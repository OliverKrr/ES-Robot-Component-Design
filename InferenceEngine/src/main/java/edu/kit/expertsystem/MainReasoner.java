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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import edu.kit.expertsystem.generated.Vocabulary;
import edu.kit.expertsystem.model.CheckboxRequirement;
import edu.kit.expertsystem.model.Component;
import edu.kit.expertsystem.model.Requirement;
import edu.kit.expertsystem.model.RequirementDependencyCheckbox;
import edu.kit.expertsystem.model.Result;
import edu.kit.expertsystem.model.TextFieldMinMaxRequirement;
import edu.kit.expertsystem.model.TextFieldRequirement;
import edu.kit.expertsystem.model.UnitToReason;
import openllet.owlapi.OWL;
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
    private RequirementHelper requirementHelper;
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
        requirementHelper = new RequirementHelper(genericTool, helper);
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

    public void prepareReasoning(UnitToReason unitToReason) {
        long startTime = System.currentTimeMillis();
        helper.clearGeneratedAxioms();
        createBasicIndividuals(OWL.Class(unitToReason.iriOfUnit));
        helper.flush();
        genericTool.getReasoner().precomputeInferences(InferenceType.values());
        isReasoningPrepared = true;
        logger.info(
                "Time needed for preparation: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
    }

    public List<Result> startReasoning(UnitToReason unitToReason, List<Requirement> requirements) {
        long startTime = System.currentTimeMillis();
        try {
            if (!isReasoningPrepared) {
                prepareReasoning(unitToReason);
            }
            isReasoningPrepared = false;
            addRequirements(requirements);
            helper.flush();
            return reason(OWL.Class(unitToReason.iriOfResultUnit), requirements);
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
                TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req;
                addRequirement(requirementsInd, getOWLDataProperty(realReq.minIRI), realReq.min);
                addRequirement(requirementsInd, getOWLDataProperty(realReq.maxIRI), realReq.max);
            } else if (req instanceof TextFieldRequirement) {
                TextFieldRequirement realReq = (TextFieldRequirement) req;
                addRequirement(requirementsInd, getOWLDataProperty(realReq.reqIri), realReq.value);
            } else if (req instanceof CheckboxRequirement) {
                CheckboxRequirement realReq = (CheckboxRequirement) req;
                addRequirement(requirementsInd, getOWLDataProperty(realReq.reqIri), realReq.value);
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

    private void addRequirement(OWLNamedIndividual requirementsInd, OWLDataProperty property, boolean value) {
        OWLDataPropertyAssertionAxiom reqAxiom = genericTool.getFactory().getOWLDataPropertyAssertionAxiom(
                property, requirementsInd,
                genericTool.getFactory().getOWLLiteral(String.valueOf(value), OWL2Datatype.XSD_BOOLEAN));
        helper.addAxiom(reqAxiom);
    }

    private List<Result> reason(OWLClass resultingUnit, List<Requirement> requirements) {
        reasoningTree.makeReasoning();
        return makeResults(resultingUnit, requirements);
    }

    private List<Result> makeResults(OWLClass classToBuildResult, List<Requirement> requirements) {
        long startTime = System.currentTimeMillis();
        List<Result> results = new ArrayList<>();
        genericTool.getReasoner().instances(classToBuildResult).forEach(resultingComponent -> {
            Result result = new Result();
            result.components = new ArrayList<>();

            genericTool.getOntology()
                    .objectSubPropertyAxiomsForSuperProperty(Vocabulary.OBJECT_PROPERTY_ISCOMPOSEDOFDEVICE)
                    .forEach(subOb -> genericTool.getReasoner()
                            .objectPropertyValues(resultingComponent,
                                    subOb.getSubProperty().getNamedProperty())
                            .forEach(composedComponent -> result.components
                                    .add(parseComponent(subOb, composedComponent))));
            Collections.sort(result.components, (comp1, comp2) -> comp1.orderPosition - comp2.orderPosition);

            result.requirements = copyRequirements(requirements);
            for (Requirement req : result.requirements) {
                if (req.resultIRI == null) {
                    continue;
                }
                if (req instanceof TextFieldMinMaxRequirement) {
                    TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req;
                    genericTool.getReasoner()
                            .dataPropertyValues(resultingComponent, getOWLDataProperty(req.resultIRI))
                            .findAny()
                            .ifPresent(obProp -> realReq.result = helper.parseValueToDouble(obProp));
                } else if (req instanceof TextFieldRequirement) {
                    TextFieldRequirement realReq = (TextFieldRequirement) req;
                    genericTool.getReasoner()
                            .dataPropertyValues(resultingComponent, getOWLDataProperty(req.resultIRI))
                            .findAny()
                            .ifPresent(obProp -> realReq.result = helper.parseValueToDouble(obProp));
                } else if (req instanceof CheckboxRequirement) {
                    CheckboxRequirement realReq = (CheckboxRequirement) req;
                    genericTool.getReasoner()
                            .dataPropertyValues(resultingComponent, getOWLDataProperty(req.resultIRI))
                            .findAny().ifPresent(obProp -> realReq.result = obProp.parseBoolean());
                } else {
                    throw new RuntimeException("Requirement class unknown: " + req.getClass());
                }
            }

            results.add(result);
        });

        try {
            Collections.sort(results,
                    Comparator.comparingDouble(result -> result.requirements.stream()
                            .filter(req -> Vocabulary.DATA_PROPERTY_HASDIMENSIONLENGTH_L_UNIT_MM.getIRI()
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

    private Component parseComponent(OWLSubObjectPropertyOfAxiom subOb,
            OWLNamedIndividual composedComponent) {
        Component component = new Component();
        component.nameOfComponent = helper.getNameOfComponent(subOb.getSubProperty().getNamedProperty());
        component.nameOfInstance = helper.getNameOfOWLNamedIndividual(composedComponent);

        // Because we can infer the same screw for multiple units, we have to
        // find the orderPosition the hard way
        genericTool.getOntology().objectPropertyRangeAxioms(subOb.getSubProperty().getNamedProperty())
                .forEach(range -> genericTool.getOntology()
                        .subClassAxiomsForSubClass(range.getRange().asOWLClass())
                        .forEach(axiom -> axiom.componentsWithoutAnnotations()
                                .filter(comp -> comp instanceof OWLDataHasValue
                                        && Vocabulary.DATA_PROPERTY_HASORDERPOSITION
                                                .equals(((OWLDataHasValue) comp).getProperty()))
                                .findAny().ifPresent(comp -> component.orderPosition = helper
                                        .parseValueToInteger(((OWLDataHasValue) comp).getFiller()))));
        return component;
    }

    private List<Requirement> copyRequirements(List<Requirement> requirements) {
        List<Requirement> copyReqs = new ArrayList<>(requirements.size());
        for (Requirement req : requirements) {
            if (req instanceof TextFieldMinMaxRequirement) {
                copyReqs.add(new TextFieldMinMaxRequirement((TextFieldMinMaxRequirement) req));
            } else if (req instanceof TextFieldRequirement) {
                copyReqs.add(new TextFieldRequirement((TextFieldRequirement) req));
            } else if (req instanceof CheckboxRequirement) {
                copyReqs.add(new CheckboxRequirement((CheckboxRequirement) req));
            } else {
                throw new RuntimeException("Requirement class unknown: " + req.getClass());
            }
        }
        return copyReqs;
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

    public List<UnitToReason> getUnitsToReason() {
        List<UnitToReason> units = new ArrayList<>();
        // TODO getRealList
        UnitToReason unitToReason = new UnitToReason();
        unitToReason.displayName = "SacUnit";
        unitToReason.iriOfUnit = Vocabulary.CLASS_SACUNIT.getIRI().getIRIString();
        unitToReason.iriOfResultUnit = Vocabulary.CLASS_SATISFIEDSACUNIT.getIRI().getIRIString();
        units.add(unitToReason);

        UnitToReason unitToReason2 = new UnitToReason();
        unitToReason2.displayName = "MotorGearBoxMatch";
        unitToReason2.iriOfUnit = Vocabulary.CLASS_MOTORGEARBOXMATCH.getIRI().getIRIString();
        unitToReason2.iriOfResultUnit = Vocabulary.CLASS_SATISFIEDMOTORGEARBOXMATCH.getIRI().getIRIString();
        units.add(unitToReason2);

        return units;
    }

    public List<Requirement> getRequirements(UnitToReason unitToReason) {
        return requirementHelper.getRequirements(OWL.Class(unitToReason.iriOfUnit));
    }

    public List<RequirementDependencyCheckbox> getRequirementDependencies(List<Requirement> requirements) {
        return requirementHelper.getRequirementDependencies(requirements);
    }

}
