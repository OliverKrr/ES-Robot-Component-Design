package edu.kit.expertsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import edu.kit.expertsystem.generated.Vocabulary;
import edu.kit.expertsystem.model.Category;
import edu.kit.expertsystem.model.Component;
import edu.kit.expertsystem.model.Requirement;
import edu.kit.expertsystem.model.Result;
import edu.kit.expertsystem.model.TextFieldMinMaxRequirement;
import openllet.owlapi.PelletReasoner;

public class MainReasoner {

    private static final String fileEnding = ".owl";
    private static final String domainFileName = "SAC_Domain_Ontology" + fileEnding;
    private static final String reasoningFileName = "SAC_Reasoning_Ontology" + fileEnding;

    private static final String myPath = "C:\\Users\\Oliver\\Dropbox\\MyGits\\PraxisDerForschung\\KnowledgeBase\\src\\main\\resources\\";

    private static final Logger logger = LogManager.getLogger(MainReasoner.class);

    private String inferdFilePath = null;

    private OWLOntologyManager manager;
    private OWLDataFactory dataFac;
    private OWLOntology ontology;
    private PelletReasoner reasoner;

    private MyOWLHelper helper;
    private ReasoningTree reasoningTree;
    private boolean isReasoningPrepared = false;

    private List<OWLClass> createdIndividualClasses = new ArrayList<>();

    public MainReasoner() {
        manager = OWLManager.createOWLOntologyManager();
        dataFac = manager.getOWLDataFactory();
    }

    public void initialize() {
        OWLOntology basicOntology = loadOntology(domainFileName, false);
        ontology = loadOntology(reasoningFileName, true);
        ontology.addAxioms(basicOntology.axioms());

        reasoner = new PelletReasoner(ontology, BufferingMode.BUFFERING);
        logger.info("Read Ontology isConsistent: " + reasoner.isConsistent());
        helper = new MyOWLHelper(manager, ontology);
        reasoningTree = new ReasoningTree(dataFac, ontology, reasoner, helper);
    }

    private OWLOntology loadOntology(String fileName, boolean setInferdFilePath) {
        try (InputStream ontoStream = readOntology(fileName, setInferdFilePath)) {
            return manager.loadOntologyFromOntologyDocument(ontoStream);
        } catch (OWLOntologyCreationException | IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Loading the ontology failed");
        }
    }

    private InputStream readOntology(String fileName, boolean setInferdFilePath) throws IOException {
        String path = myPath + fileName;
        if (setInferdFilePath) {
            inferdFilePath = path.substring(0, path.length() - fileEnding.length()) + "Inf" + fileEnding;
        }
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e1) {
            String localPath = Paths.get("").toAbsolutePath() + "/" + fileName;
            if (setInferdFilePath) {
                inferdFilePath = localPath.substring(0, localPath.length() - fileEnding.length()) + "Inf"
                        + fileEnding;
            }
            try {
                return new FileInputStream(localPath);
            } catch (FileNotFoundException e2) {
                return getClass().getResourceAsStream("/" + fileName);
            }
        }
    }

    public void prepareReasoning() {
        long startTime = System.currentTimeMillis();
        if (isReasoningPrepared) {
            return;
        }
        helper.clearGeneratedAxioms();
        createBasicIndividuals(Vocabulary.CLASS_SACUNIT);
        reasoner.flush();
        reasoner.precomputeInferences(InferenceType.values());
        isReasoningPrepared = true;
        logger.info(
                "Time needed for preparation: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
    }

    public List<Result> startReasoning(List<Requirement> requirements) {
        long startTime = System.currentTimeMillis();
        try {
            if (!isReasoningPrepared) {
                prepareReasoning();
            }
            isReasoningPrepared = false;
            addRequirements(requirements);
            reasoner.flush();
            return reason(requirements);
        } finally {
            try {
                saveReasonedOntology();
            } catch (OWLOntologyStorageException | IOException e) {
                logger.error(e.getMessage(), e);
            }
            logger.info("Time needed: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        }
    }

    private void createBasicIndividuals(OWLClass componentToReasone) {
        createdIndividualClasses.clear();
        ontology.subClassAxiomsForSubClass(componentToReasone).forEach(topAxiom -> topAxiom
                .componentsWithoutAnnotations()
                .filter(component -> component instanceof OWLQuantifiedObjectRestriction
                        && Vocabulary.OBJECT_PROPERTY_HASREASONINGTREEPROPERTY
                                .equals(((OWLQuantifiedObjectRestriction) component).getProperty()))
                .forEach(propComponent -> reasoner
                        .subClasses(((OWLQuantifiedObjectRestriction) propComponent).getFiller().asOWLClass(),
                                true)
                        .forEach(topSubClass -> {
                            createdIndividualClasses.add(topSubClass);
                            reasoner.subClasses(topSubClass, true).forEach(toCreate -> {
                                String name = toCreate.getIRI().getShortForm() + "Ind";
                                OWLNamedIndividual ind = dataFac.getOWLNamedIndividual(helper.create(name));
                                helper.addAxiom(dataFac.getOWLClassAssertionAxiom(toCreate, ind));
                            });
                        })));
    }

    private void addRequirements(List<Requirement> requirements) {
        logger.info(requirements);
        OWLNamedIndividual requirementsInd = dataFac.getOWLNamedIndividual(helper.create("currentRequs"));
        helper.addAxiom(
                dataFac.getOWLClassAssertionAxiom(Vocabulary.CLASS_CURRENTREQUIREMENTS, requirementsInd));

        for (Requirement req : requirements) {
            if (req instanceof TextFieldMinMaxRequirement) {
                TextFieldMinMaxRequirement textFieldReq = (TextFieldMinMaxRequirement) req;
                addRequirement(requirementsInd, getOWLDataProperty(textFieldReq.minIRI), textFieldReq.min);
                addRequirement(requirementsInd, getOWLDataProperty(textFieldReq.maxIRI), textFieldReq.max);
            } else {
                throw new RuntimeException("Requirement class unknown: " + req.getClass());
            }
        }
    }

    private OWLDataProperty getOWLDataProperty(String iri) {
        return dataFac.getOWLDataProperty(IRI.create(iri));
    }

    private void addRequirement(OWLNamedIndividual requirementsInd, OWLDataProperty property, double value) {
        OWLDataPropertyAssertionAxiom reqAxiom = dataFac.getOWLDataPropertyAssertionAxiom(property,
                requirementsInd, dataFac.getOWLLiteral(String.valueOf(value), OWL2Datatype.XSD_DECIMAL));
        helper.addAxiom(reqAxiom);
    }

    private List<Result> reason(List<Requirement> requirements) {
        reasoningTree.makeReasoning(Vocabulary.CLASS_SACUNIT);
        return makeResults(Vocabulary.CLASS_SATISFIEDSACUNIT, requirements);
    }

    private List<Result> makeResults(OWLClass classToBuildResult, List<Requirement> requirements) {
        long startTime = System.currentTimeMillis();
        List<Result> results = new ArrayList<>();
        reasoner.instances(classToBuildResult).forEach(resultingComponent -> {
            Result result = new Result();
            result.components = new ArrayList<>();

            List<OWLNamedIndividual> childrendToSearch = new ArrayList<>();
            childrendToSearch.add(resultingComponent);
            List<OWLNamedIndividual> createdChildren = new ArrayList<>();
            do {
                List<OWLNamedIndividual> childrendToSearchNext = new ArrayList<>();
                for (OWLNamedIndividual childToSearch : childrendToSearch) {
                    getChildrenOfTreeItems(childToSearch, childrendToSearchNext, createdChildren);
                }
                childrendToSearch = childrendToSearchNext;
            } while (!childrendToSearch.isEmpty());

            createdChildren.forEach(createdChild -> {
                Component component = new Component();
                reasoner.types(createdChild)
                        .filter(type -> ontology.subClassAxiomsForSuperClass(Vocabulary.CLASS_DEVICE)
                                .anyMatch(subDevice -> subDevice.getSubClass().equals(type)))
                        .findAny()
                        .ifPresent(type -> component.nameOfComponent = type.getIRI().getShortForm());
                component.nameOfInstance = helper.getNameOfOWLNamedIndividual(createdChild);
                result.components.add(component);
            });

            Collections.sort(result.components, (comp1, comp2) -> String.CASE_INSENSITIVE_ORDER.reversed()
                    .compare(comp1.nameOfComponent, comp2.nameOfComponent));

            result.requirements = copyRequirements(requirements);
            for (Requirement req : result.requirements) {
                if (req instanceof TextFieldMinMaxRequirement) {
                    TextFieldMinMaxRequirement textFieldReq = (TextFieldMinMaxRequirement) req;
                    reasoner.dataPropertyValues(resultingComponent, getOWLDataProperty(req.resultIRI))
                            .findAny().ifPresent(obProp -> textFieldReq.result = parseValueToDouble(obProp));
                } else {
                    throw new RuntimeException("Requirement class unknown: " + req.getClass());
                }
            }

            results.add(result);
        });

        try {
            Collections.sort(results,
                    Comparator.comparingDouble(result -> result.requirements.stream()
                            .filter(req -> Vocabulary.DATA_PROPERTY_HASWEIGHT_M_UNIT_KG.getIRI()
                                    .getIRIString().equals(req.resultIRI))
                            .findAny().map(req -> ((TextFieldMinMaxRequirement) req).result).get()));
        } catch (NoSuchElementException e) {
            logger.warn("Cannot sort, because weight is not available.");
        }

        // results.forEach(r -> logger.info(r));
        logger.info("Number of results: " + results.size());
        logger.info(
                "Time needed for make results: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        return results;
    }

    private void getChildrenOfTreeItems(OWLNamedIndividual treeIndividual,
            List<OWLNamedIndividual> childrendToSearchNext, List<OWLNamedIndividual> createdChildren) {
        reasoner.objectPropertyValues(treeIndividual, Vocabulary.OBJECT_PROPERTY_HASCHILD)
                .forEach(childIndividual -> {
                    if (reasoner.types(childIndividual).anyMatch(type -> createdIndividualClasses.stream()
                            .anyMatch(createdIndi -> createdIndi.equals(type)))) {
                        createdChildren.add(childIndividual);
                    } else {
                        childrendToSearchNext.add(childIndividual);
                    }
                });
    }

    private List<Requirement> copyRequirements(List<Requirement> requirements) {
        List<Requirement> copyReqs = new ArrayList<>(requirements.size());
        for (Requirement req : requirements) {
            if (req instanceof TextFieldMinMaxRequirement) {
                copyReqs.add(new TextFieldMinMaxRequirement((TextFieldMinMaxRequirement) req));
            } else {
                throw new RuntimeException("Requirement class unknown: " + req.getClass());
            }
        }
        return copyReqs;
    }

    private double parseValueToDouble(OWLLiteral obProp) {
        if (obProp.isInteger()) {
            return obProp.parseInteger();
        }
        return obProp.parseDouble();
    }

    private int parseValueToInteger(OWLLiteral obProp) {
        if (obProp.isInteger()) {
            return obProp.parseInteger();
        }
        return Math.round(obProp.parseFloat());
    }

    private void saveReasonedOntology() throws IOException, OWLOntologyStorageException {
        logger.info("Reasoned ontology isConsistent: " + reasoner.isConsistent());
        if (inferdFilePath == null) {
            return;
        }
        OWLOntology inferOnto = reasoner.getRootOntology();
        try (FileOutputStream out = new FileOutputStream(new File(inferdFilePath))) {
            manager.saveOntology(inferOnto, out);
        }
    }

    public List<Requirement> getRequirements() {
        // TODO make methods for componentsToBuild, UI element to choose and get that
        // here
        OWLClass componentToBuild = Vocabulary.CLASS_SACUNIT;
        List<Requirement> requirements = new ArrayList<>();

        ontology.subClassAxiomsForSubClass(componentToBuild).forEach(topAxiom -> topAxiom
                .componentsWithoutAnnotations()
                .filter(component -> component instanceof OWLQuantifiedObjectRestriction
                        && Vocabulary.OBJECT_PROPERTY_HASREASONINGTREEPROPERTY
                                .equals(((OWLQuantifiedObjectRestriction) component).getProperty()))
                .forEach(propComponent -> ontology
                        .subClassAxiomsForSubClass(
                                ((OWLQuantifiedObjectRestriction) propComponent).getFiller().asOWLClass())
                        .forEach(axiom -> axiom.componentsWithoutAnnotations()
                                .filter(component -> component instanceof OWLObjectHasValue
                                        && Vocabulary.OBJECT_PROPERTY_HASREQUIREMENT
                                                .equals(((OWLObjectHasValue) component).getProperty()))
                                .forEach(component -> requirements
                                        .add(parseRequirement(((OWLObjectHasValue) component).getFiller()
                                                .asOWLNamedIndividual()))))));

        Collections.sort(requirements, (req1, req2) -> {
            int sortCat = req1.category.orderPosition - req2.category.orderPosition;
            if (sortCat == 0) {
                return req1.orderPosition - req2.orderPosition;
            }
            return sortCat;
        });
        return requirements;
    }

    private Requirement parseRequirement(OWLNamedIndividual owlIndividual) {
        return reasoner.types(owlIndividual, true).map(type -> {
            if (Vocabulary.CLASS_TEXTFIELDMINMAXREQUIREMENT.equals(type)) {
                TextFieldMinMaxRequirement textReq = new TextFieldMinMaxRequirement();
                parseCommonRequirement(textReq, owlIndividual);

                reasoner.dataPropertyValues(owlIndividual, Vocabulary.DATA_PROPERTY_HASENABLEFIELDMAX)
                        .findAny().ifPresent(obProp -> textReq.enableMax = obProp.parseBoolean());
                reasoner.dataPropertyValues(owlIndividual, Vocabulary.DATA_PROPERTY_HASENABLEFIELDMIN)
                        .findAny().ifPresent(obProp -> textReq.enableMin = obProp.parseBoolean());
                reasoner.dataPropertyValues(owlIndividual, Vocabulary.DATA_PROPERTY_HASSCALEFROMONTOLOGYTOUI)
                        .findAny()
                        .ifPresent(obProp -> textReq.scaleFromOntologyToUI = parseValueToDouble(obProp));

                ontology.dataPropertyAssertionAxioms(owlIndividual)
                        .forEach(prop -> prop.componentsWithoutAnnotations()
                                .filter(component -> component instanceof OWLDataProperty)
                                .forEach(component -> ((OWLDataProperty) component)
                                        .dataPropertiesInSignature()
                                        .forEach(comp -> reasoner.superDataProperties(comp).forEach(sup -> {
                                            if (Vocabulary.DATA_PROPERTY_HASREQVALUEMIN.equals(sup)) {
                                                textReq.minIRI = comp.getIRI().getIRIString();
                                            } else if (Vocabulary.DATA_PROPERTY_HASREQVALUEMAX.equals(sup)) {
                                                textReq.maxIRI = comp.getIRI().getIRIString();
                                            } else if (Vocabulary.DATA_PROPERTY_HASVALUE.equals(sup)) {
                                                textReq.resultIRI = comp.getIRI().getIRIString();
                                            }
                                        }))));

                return textReq;
            } else {
                throw new RuntimeException("Requirement type unknown: " + type);
            }
        }).findAny().get();
    }

    private void parseCommonRequirement(Requirement req, OWLNamedIndividual owlIndividual) {
        req.category = new Category();
        reasoner.objectPropertyValues(owlIndividual, Vocabulary.OBJECT_PROPERTY_HASCATEGORY).forEach(cate -> {
            reasoner.dataPropertyValues(cate, Vocabulary.DATA_PROPERTY_HASDISPLAYNAME).findAny()
                    .ifPresent(obProp -> req.category.displayName = obProp.getLiteral());
            reasoner.dataPropertyValues(cate, Vocabulary.DATA_PROPERTY_HASORDERPOSITION).findAny()
                    .ifPresent(obProp -> req.category.orderPosition = parseValueToInteger(obProp));
        });

        reasoner.dataPropertyValues(owlIndividual, Vocabulary.DATA_PROPERTY_HASDISPLAYNAME).findAny()
                .ifPresent(obProp -> req.displayName = obProp.getLiteral());
        reasoner.dataPropertyValues(owlIndividual, Vocabulary.DATA_PROPERTY_HASDESCRIPTION).findAny()
                .ifPresent(obProp -> req.description = obProp.getLiteral());
        reasoner.dataPropertyValues(owlIndividual, Vocabulary.DATA_PROPERTY_HASUNIT).findAny()
                .ifPresent(obProp -> req.unit = obProp.getLiteral());
        reasoner.dataPropertyValues(owlIndividual, Vocabulary.DATA_PROPERTY_HASORDERPOSITION).findAny()
                .ifPresent(obProp -> req.orderPosition = parseValueToInteger(obProp));
    }

}
