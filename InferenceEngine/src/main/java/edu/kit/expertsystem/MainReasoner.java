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
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import edu.kit.expertsystem.generated.Vocabulary;
import edu.kit.expertsystem.model.Category;
import edu.kit.expertsystem.model.Component;
import edu.kit.expertsystem.model.Requirement;
import edu.kit.expertsystem.model.Result;
import edu.kit.expertsystem.model.TextFieldMinMaxRequirement;
import openllet.owlapi.OWLGenericTools;
import openllet.owlapi.OWLManagerGroup;

public class MainReasoner {

    private static final String fileEnding = ".owl";
    private static final String domainFileName = "SAC_Domain_Ontology" + fileEnding;
    private static final String reasoningFileName = "SAC_Reasoning_Ontology" + fileEnding;

    private static final String myPath = "C:\\Users\\Oliver\\Dropbox\\MyGits\\PraxisDerForschung\\KnowledgeBase\\src\\main\\resources\\";

    private static final Logger logger = LogManager.getLogger(MainReasoner.class);

    private String inferdFilePath = null;

    private OWLManagerGroup group;
    private OWLGenericTools genericTool;

    private MyOWLHelper helper;
    private ReasoningTree reasoningTree;
    private boolean isReasoningPrepared = false;

    private List<OWLClass> createdIndividualClasses = new ArrayList<>();

    public MainReasoner() {
        group = new OWLManagerGroup();
    }

    public void initialize() {
        OWLOntology basicOntology = loadOntology(domainFileName, false);
        OWLOntology ontology = loadOntology(reasoningFileName, true);
        group.getVolatileManager().addAxioms(ontology, basicOntology.axioms());

        genericTool = new OWLGenericTools(group, group.getVolatileManager(), ontology);

        logger.info("Read Ontology isConsistent: " + genericTool.getReasoner().isConsistent());
        helper = new MyOWLHelper(genericTool);
        reasoningTree = new ReasoningTree(genericTool, helper);
    }

    private OWLOntology loadOntology(String fileName, boolean setInferdFilePath) {
        try (InputStream ontoStream = readOntology(fileName, setInferdFilePath)) {
            return group.getVolatileManager().loadOntologyFromOntologyDocument(ontoStream);
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
        helper.flush();
        genericTool.getReasoner().precomputeInferences(InferenceType.values());
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
            helper.flush();
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
        genericTool.getOntology().subClassAxiomsForSubClass(componentToReasone)
                .filter(topAxiom -> topAxiom.getSuperClass().objectPropertiesInSignature()
                        .anyMatch(ob -> Vocabulary.OBJECT_PROPERTY_HASREASONINGTREEPROPERTY.equals(ob)))
                .forEach(topAxiom -> topAxiom.getSuperClass().classesInSignature().forEach(
                        clas -> genericTool.getReasoner().subClasses(clas, true).forEach(topSubClass -> {
                            createdIndividualClasses.add(topSubClass);
                            genericTool.getReasoner().subClasses(topSubClass, true).forEach(toCreate -> {
                                String name = toCreate.getIRI().getShortForm() + "Ind";
                                OWLNamedIndividual ind = genericTool.getFactory()
                                        .getOWLNamedIndividual(helper.create(name));
                                helper.addAxiom(
                                        genericTool.getFactory().getOWLClassAssertionAxiom(toCreate, ind));
                            });
                        })));
    }

    private void addRequirements(List<Requirement> requirements) {
        logger.info(requirements);
        OWLNamedIndividual requirementsInd = genericTool.getFactory()
                .getOWLNamedIndividual(helper.create("currentRequs"));
        helper.addAxiom(genericTool.getFactory()
                .getOWLClassAssertionAxiom(Vocabulary.CLASS_CURRENTREQUIREMENTS, requirementsInd));

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
        return genericTool.getFactory().getOWLDataProperty(IRI.create(iri));
    }

    private void addRequirement(OWLNamedIndividual requirementsInd, OWLDataProperty property, double value) {
        OWLDataPropertyAssertionAxiom reqAxiom = genericTool.getFactory().getOWLDataPropertyAssertionAxiom(
                property, requirementsInd,
                genericTool.getFactory().getOWLLiteral(String.valueOf(value), OWL2Datatype.XSD_DECIMAL));
        helper.addAxiom(reqAxiom);
    }

    private List<Result> reason(List<Requirement> requirements) {
        reasoningTree.makeReasoning(Vocabulary.CLASS_SACUNIT);
        return makeResults(Vocabulary.CLASS_SATISFIEDSACUNIT, requirements);
    }

    private List<Result> makeResults(OWLClass classToBuildResult, List<Requirement> requirements) {
        long startTime = System.currentTimeMillis();
        ConcurrentLinkedQueue<Result> results = new ConcurrentLinkedQueue<>();
        genericTool.getReasoner().instances(classToBuildResult).forEach(resultingComponent -> {
            Result result = new Result();
            result.components = new ArrayList<>();

            genericTool.getOntology()
                    .objectSubPropertyAxiomsForSuperProperty(Vocabulary.OBJECT_PROPERTY_ISCOMPOSEDOFDEVICE)
                    .forEach(
                            subOb -> genericTool.getReasoner()
                                    .objectPropertyValues(resultingComponent,
                                            subOb.getSubProperty().getNamedProperty())
                                    .forEach(composedComponent -> {
                                        Component component = new Component();

                                        component.nameOfComponent = helper.getNameOfComponent(
                                                subOb.getSubProperty().getNamedProperty());
                                        component.nameOfInstance = helper
                                                .getNameOfOWLNamedIndividual(composedComponent);
                                        genericTool.getReasoner()
                                                .dataPropertyValues(composedComponent,
                                                        Vocabulary.DATA_PROPERTY_HASORDERPOSITION)
                                                .findAny().ifPresent(
                                                        obProp -> component.orderPosition = parseValueToInteger(
                                                                obProp));

                                        result.components.add(component);
                                    }));

            Collections.sort(result.components, (comp1, comp2) -> comp1.orderPosition - comp2.orderPosition);

            result.requirements = copyRequirements(requirements);
            for (Requirement req : result.requirements) {
                if (req instanceof TextFieldMinMaxRequirement) {
                    TextFieldMinMaxRequirement textFieldReq = (TextFieldMinMaxRequirement) req;
                    genericTool.getReasoner()
                            .dataPropertyValues(resultingComponent, getOWLDataProperty(req.resultIRI))
                            .findAny().ifPresent(obProp -> textFieldReq.result = parseValueToDouble(obProp));
                } else {
                    throw new RuntimeException("Requirement class unknown: " + req.getClass());
                }
            }

            results.add(result);
        });

        List<Result> res = new ArrayList<>(results);
        try {
            Collections.sort(res,
                    Comparator.comparingDouble(result -> result.requirements.stream()
                            .filter(req -> Vocabulary.DATA_PROPERTY_HASDIMENSIONLENGTH_L_UNIT_MM.getIRI()
                                    .getIRIString().equals(req.resultIRI))
                            .findAny().map(req -> ((TextFieldMinMaxRequirement) req).result).get()));
        } catch (NoSuchElementException e) {
            logger.warn("Cannot sort, because weight is not available.");
        }

        // results.forEach(r -> logger.info(r));
        logger.info("Number of results: " + res.size());
        logger.info(
                "Time needed for make results: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        return res;
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
        logger.info("Reasoned ontology isConsistent: " + genericTool.getReasoner().isConsistent());
        if (inferdFilePath == null) {
            return;
        }
        OWLOntology inferOnto = genericTool.getReasoner().getRootOntology();
        try (FileOutputStream out = new FileOutputStream(new File(inferdFilePath))) {
            genericTool.getManager().saveOntology(inferOnto, out);
        }
    }

    public List<Requirement> getRequirements() {
        // TODO make methods for componentsToBuild, UI element to choose and get that
        // here
        OWLClass componentToBuild = Vocabulary.CLASS_SACUNIT;
        List<Requirement> requirements = new ArrayList<>();

        genericTool.getOntology().subClassAxiomsForSubClass(componentToBuild).forEach(topAxiom -> topAxiom
                .componentsWithoutAnnotations()
                .filter(component -> component instanceof OWLQuantifiedObjectRestriction
                        && Vocabulary.OBJECT_PROPERTY_HASREASONINGTREEPROPERTY
                                .equals(((OWLQuantifiedObjectRestriction) component).getProperty()))
                .forEach(propComponent -> genericTool.getOntology()
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
        return genericTool.getReasoner().types(owlIndividual, true).map(type -> {
            if (Vocabulary.CLASS_TEXTFIELDMINMAXREQUIREMENT.equals(type)) {
                TextFieldMinMaxRequirement textReq = new TextFieldMinMaxRequirement();
                parseCommonRequirement(textReq, owlIndividual);

                genericTool.getReasoner()
                        .dataPropertyValues(owlIndividual, Vocabulary.DATA_PROPERTY_HASENABLEFIELDMAX)
                        .findAny().ifPresent(obProp -> textReq.enableMax = obProp.parseBoolean());
                genericTool.getReasoner()
                        .dataPropertyValues(owlIndividual, Vocabulary.DATA_PROPERTY_HASENABLEFIELDMIN)
                        .findAny().ifPresent(obProp -> textReq.enableMin = obProp.parseBoolean());
                genericTool.getReasoner()
                        .dataPropertyValues(owlIndividual, Vocabulary.DATA_PROPERTY_HASSCALEFROMONTOLOGYTOUI)
                        .findAny()
                        .ifPresent(obProp -> textReq.scaleFromOntologyToUI = parseValueToDouble(obProp));

                genericTool.getOntology().dataPropertyAssertionAxioms(owlIndividual)
                        .forEach(prop -> prop.componentsWithoutAnnotations()
                                .filter(component -> component instanceof OWLDataProperty)
                                .forEach(component -> ((OWLDataProperty) component)
                                        .dataPropertiesInSignature().forEach(comp -> genericTool.getReasoner()
                                                .superDataProperties(comp).forEach(sup -> {
                                                    if (Vocabulary.DATA_PROPERTY_HASREQVALUEMIN.equals(sup)) {
                                                        textReq.minIRI = comp.getIRI().getIRIString();
                                                    } else if (Vocabulary.DATA_PROPERTY_HASREQVALUEMAX
                                                            .equals(sup)) {
                                                        textReq.maxIRI = comp.getIRI().getIRIString();
                                                    } else if (Vocabulary.DATA_PROPERTY_HASVALUE
                                                            .equals(sup)) {
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
        genericTool.getReasoner().objectPropertyValues(owlIndividual, Vocabulary.OBJECT_PROPERTY_HASCATEGORY)
                .forEach(cate -> {
                    genericTool.getReasoner()
                            .dataPropertyValues(cate, Vocabulary.DATA_PROPERTY_HASDISPLAYNAME).findAny()
                            .ifPresent(obProp -> req.category.displayName = obProp.getLiteral());
                    genericTool.getReasoner()
                            .dataPropertyValues(cate, Vocabulary.DATA_PROPERTY_HASORDERPOSITION).findAny()
                            .ifPresent(obProp -> req.category.orderPosition = parseValueToInteger(obProp));
                });

        genericTool.getReasoner().dataPropertyValues(owlIndividual, Vocabulary.DATA_PROPERTY_HASDISPLAYNAME)
                .findAny().ifPresent(obProp -> req.displayName = obProp.getLiteral());
        genericTool.getReasoner().dataPropertyValues(owlIndividual, Vocabulary.DATA_PROPERTY_HASDESCRIPTION)
                .findAny().ifPresent(obProp -> req.description = obProp.getLiteral());
        genericTool.getReasoner().dataPropertyValues(owlIndividual, Vocabulary.DATA_PROPERTY_HASUNIT)
                .findAny().ifPresent(obProp -> req.unit = obProp.getLiteral());
        genericTool.getReasoner().dataPropertyValues(owlIndividual, Vocabulary.DATA_PROPERTY_HASORDERPOSITION)
                .findAny().ifPresent(obProp -> req.orderPosition = parseValueToInteger(obProp));
    }

}
