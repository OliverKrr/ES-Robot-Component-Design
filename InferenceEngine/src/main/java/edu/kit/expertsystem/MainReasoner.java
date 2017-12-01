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
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
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
import openllet.core.exceptions.TimerInterruptedException;
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
    private AtomicBoolean interrupted = new AtomicBoolean(false);

    public MainReasoner() {
        group = new OWLManagerGroup();
    }

    public void initialize() {
        long startTime = System.currentTimeMillis();
        OWLOntology basicOntology = loadOntology(domainFileName, false);
        OWLOntology ontology = loadOntology(reasoningFileName, true);
        group.getVolatileManager().addAxioms(ontology, basicOntology.axioms());

        genericTool = new OWLGenericTools(group, group.getVolatileManager(), ontology);

        helper = new MyOWLHelper(genericTool);
        helper.checkConsistency();
        reasoningTree = new ReasoningTree(genericTool, helper);
        requirementHelper = new RequirementHelper(genericTool, helper);
        logger.info("Time needed for initialize: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
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

    private void createBasicIndividuals(OWLClass componentToReasone) {
        genericTool.getOntology().subClassAxiomsForSubClass(componentToReasone)
                .filter(axiomOfComponentToReason -> axiomOfComponentToReason.getSuperClass()
                        .objectPropertiesInSignature()
                        .anyMatch(obOfComponentToReason -> Vocabulary.OBJECT_PROPERTY_HASREASONINGTREEPROPERTY
                                .equals(obOfComponentToReason)))
                .forEach(filteredAxiomOfComponentToReason -> filteredAxiomOfComponentToReason.getSuperClass()
                        .classesInSignature()
                        .forEach(reasoningPropertyClass -> genericTool.getOntology()
                                .subClassAxiomsForSuperClass(reasoningPropertyClass)
                                .forEach(filteredReasoningPropertyAxiom -> filteredReasoningPropertyAxiom
                                        .getSubClass().classesInSignature()
                                        .forEach(reasoningPropertySubClass -> genericTool.getOntology()
                                                .subClassAxiomsForSuperClass(reasoningPropertySubClass)
                                                .forEach(filteredReasoningPropertySubClassAxiom -> {
                                                    OWLClass clasToCreate = filteredReasoningPropertySubClassAxiom
                                                            .getSubClass().asOWLClass();
                                                    String name = clasToCreate.getIRI().getShortForm()
                                                            + "Ind";
                                                    OWLNamedIndividual ind = genericTool.getFactory()
                                                            .getOWLNamedIndividual(helper.create(name));
                                                    helper.addAxiom(genericTool.getFactory()
                                                            .getOWLClassAssertionAxiom(clasToCreate, ind));
                                                })))));
    }

    public List<Result> startReasoning(UnitToReason unitToReason, List<Requirement> requirements) {
        long startTime = System.currentTimeMillis();
        try {
            if (!isReasoningPrepared) {
                prepareReasoning(unitToReason);
            }
            isReasoningPrepared = false;
            if (interrupted.get()) {
                return null;
            }
            addRequirements(requirements);
            if (interrupted.get()) {
                return null;
            }
            helper.flush();
            return reason(OWL.Class(unitToReason.iriOfResultUnit), requirements);
        } catch (ReasonerInterruptedException | TimerInterruptedException e) {
            // It is OK if we interrupted ourself
            if (!interrupted.get()) {
                throw e;
            }
            return null;
        } finally {
            if (interrupted.get()) {
                logger.info("Time needed for interrupted reasoning: "
                        + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
            } else {
                try {
                    saveReasonedOntology();
                } catch (OWLOntologyStorageException | IOException e) {
                    logger.error(e.getMessage(), e);
                }
                logger.info("Time needed for reasoning: " + (System.currentTimeMillis() - startTime) / 1000.0
                        + "s");
            }
        }
    }

    private void addRequirements(List<Requirement> requirements) {
        OWLNamedIndividual requirementsInd = genericTool.getFactory()
                .getOWLNamedIndividual(helper.create("currentRequs"));
        helper.addAxiom(genericTool.getFactory()
                .getOWLClassAssertionAxiom(Vocabulary.CLASS_CURRENTREQUIREMENTS, requirementsInd));

        for (Requirement req : requirements) {
            if (req instanceof TextFieldMinMaxRequirement) {
                TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req;
                addRequirement(requirementsInd, getOWLDataProperty(realReq.minIRI), realReq.min);
                addRequirement(requirementsInd, getOWLDataProperty(realReq.maxIRI), realReq.max);
                logger.info("Requirement (displayName, min, max): " + realReq.displayName + ", " + realReq.min
                        + ", " + realReq.max);
            } else if (req instanceof TextFieldRequirement) {
                TextFieldRequirement realReq = (TextFieldRequirement) req;
                addRequirement(requirementsInd, getOWLDataProperty(realReq.reqIri), realReq.value);
                logger.info(
                        "Requirement (displayName, value): " + realReq.displayName + ", " + realReq.value);
            } else if (req instanceof CheckboxRequirement) {
                CheckboxRequirement realReq = (CheckboxRequirement) req;
                addRequirement(requirementsInd, getOWLDataProperty(realReq.reqIri), realReq.value);
                logger.info(
                        "Requirement (displayName, value): " + realReq.displayName + ", " + realReq.value);
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
        if (interrupted.get()) {
            return null;
        }
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
        genericTool.getOntology().objectPropertyRangeAxioms(subOb.getSubProperty().getNamedProperty())
                .forEach(range -> component.orderPosition = helper
                        .getOrderPositionForClass(range.getRange().asOWLClass()));
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
        helper.checkConsistency();
        if (inferdFilePath == null) {
            return;
        }
        OWLOntology inferOnto = genericTool.getReasoner().getRootOntology();
        try (FileOutputStream out = new FileOutputStream(new File(inferdFilePath))) {
            genericTool.getManager().saveOntology(inferOnto, out);
        }
    }

    public List<UnitToReason> getUnitsToReason() {
        long startTime = System.currentTimeMillis();
        List<UnitToReason> units = new ArrayList<>();
        genericTool.getOntology().subClassAxiomsForSuperClass(Vocabulary.CLASS_REASONINGTREE)
                .forEach(axiomOfReasoningTree -> axiomOfReasoningTree.getSubClass().classesInSignature()
                        .filter(subClassOfReasoningTree -> genericTool.getOntology()
                                .subClassAxiomsForSubClass(subClassOfReasoningTree)
                                .anyMatch(subClassOfReasoningTreeAxiom -> subClassOfReasoningTreeAxiom
                                        .getSuperClass().objectPropertiesInSignature().anyMatch(
                                                obOfSubClassOfReasoingTree -> Vocabulary.OBJECT_PROPERTY_HASREASONINGTREEPROPERTY
                                                        .equals(obOfSubClassOfReasoingTree))))
                        .forEach(classToReason -> {
                            UnitToReason unitToReason = new UnitToReason();
                            unitToReason.displayName = classToReason.getIRI().getShortForm();
                            unitToReason.iriOfUnit = classToReason.getIRI().getIRIString();

                            genericTool.getOntology().subClassAxiomsForSuperClass(classToReason)
                                    .forEach(classToReasonAxiom -> classToReasonAxiom.getSubClass()
                                            .classesInSignature().forEach(subClassOfClassToReason -> {
                                                if (unitToReason.iriOfResultUnit == null) {
                                                    unitToReason.iriOfResultUnit = subClassOfClassToReason
                                                            .getIRI().getIRIString();
                                                } else {
                                                    throw new RuntimeException(
                                                            "UnitsToReasone are only allowed to have one child to identify resultingUnit (satisfied), found at least: "
                                                                    + unitToReason.iriOfResultUnit + " and "
                                                                    + subClassOfClassToReason.getIRI()
                                                                            .getIRIString());
                                                }
                                            }));
                            if (unitToReason.iriOfResultUnit == null) {
                                throw new RuntimeException(
                                        "UnitsToReasone must have one child to identify resultingUnit (satisfied)");
                            }
                            unitToReason.orderPosition = helper.getOrderPositionForClass(classToReason);

                            units.add(unitToReason);
                        }));

        Collections.sort(units, (unit1, unit2) -> unit1.orderPosition - unit1.orderPosition);
        logger.info("Time needed for get UnitsToReason: " + (System.currentTimeMillis() - startTime) / 1000.0
                + "s");
        return units;
    }

    public void interruptReasoning() {
        logger.info("Reasoning interrupted");
        interrupted.set(true);
        reasoningTree.interruptReasoning();
        genericTool.getReasoner().interrupt();
    }

    public List<Requirement> getRequirements(UnitToReason unitToReason) {
        return requirementHelper.getRequirements(OWL.Class(unitToReason.iriOfUnit));
    }

    public List<RequirementDependencyCheckbox> getRequirementDependencies(List<Requirement> requirements) {
        return requirementHelper.getRequirementDependencies(requirements);
    }

}
