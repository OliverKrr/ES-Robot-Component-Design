package edu.kit.expertsystem;

import edu.kit.expertsystem.generated.Vocabulary;
import edu.kit.expertsystem.io.OntologyReadAndWriteHelper;
import edu.kit.expertsystem.model.Component;
import edu.kit.expertsystem.model.ComponentToBeDesigned;
import edu.kit.expertsystem.model.Line;
import edu.kit.expertsystem.model.Result;
import edu.kit.expertsystem.model.req.*;
import edu.kit.expertsystem.reasoning.ReasoningTree;
import openllet.core.OpenlletOptions;
import openllet.owlapi.OWL;
import openllet.owlapi.OWLGenericTools;
import openllet.owlapi.OWLManagerGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MainReasoner {

    private static final Logger logger = LogManager.getLogger(MainReasoner.class);

    private OWLManagerGroup group;
    private OWLGenericTools genericTool;

    private MyOWLHelper helper;
    private ReasoningTree reasoningTree;
    private RequirementHelper requirementHelper;
    private OntologyReadAndWriteHelper ontologyReadAndWriteHelper;

    private List<OWLNamedIndividual> constances;
    private List<OWLNamedIndividual> basicIndividuals = new ArrayList<>();

    private boolean isReasoningPrepared = false;
    private AtomicBoolean interrupted = new AtomicBoolean(false);

    public MainReasoner() {
        try {
            OpenlletOptions.load(MainReasoner.class.getResource("/myOpenllet.properties"));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        group = new OWLManagerGroup();
    }

    public void initialize() {
        long startTime = System.currentTimeMillis();
        ontologyReadAndWriteHelper = new OntologyReadAndWriteHelper(group);
        OWLOntology ontology = ontologyReadAndWriteHelper.loadOntologies();

        genericTool = new OWLGenericTools(group, group.getVolatileManager(), ontology);

        helper = new MyOWLHelper(genericTool);
        ontologyReadAndWriteHelper.setGenericToolAndHelper(genericTool, helper);
        helper.checkConsistency();
        reasoningTree = new ReasoningTree(genericTool, helper);
        requirementHelper = new RequirementHelper(genericTool, helper);

        constances = genericTool.getReasoner().instances(Vocabulary.CLASS_CONSTANTS).collect(Collectors.toList());
        reasoningTree.setConstances(constances);

        logger.debug("Time needed for initialize: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
    }

    public void prepareReasoning(ComponentToBeDesigned componentToBeDesigned) {
        long startTime = System.currentTimeMillis();
        helper.clearGeneratedAxioms();
        createBasicIndividuals(OWL.Class(componentToBeDesigned.iriOfUnit));
        helper.flush();
        genericTool.getReasoner().precomputeInferences(InferenceType.values());
        isReasoningPrepared = true;
        logger.debug("Time needed for preparation: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
    }

    private void createBasicIndividuals(OWLClass componentToReasone) {
        basicIndividuals.clear();
        reasoningTree.resetDeviceToIndividual();
        genericTool.getOntology().subClassAxiomsForSubClass(componentToReasone).filter(axiomOfComponentToReason ->
                axiomOfComponentToReason.getSuperClass().objectPropertiesInSignature().anyMatch(Vocabulary
                        .OBJECT_PROPERTY_HASREASONINGTREEPROPERTY::equals)).forEach(filteredAxiomOfComponentToReason
                -> filteredAxiomOfComponentToReason.getSuperClass().classesInSignature().forEach
                (reasoningPropertyClass -> genericTool.getOntology().subClassAxiomsForSuperClass
                        (reasoningPropertyClass).forEach(basicAxiom -> createIndividualsRecursive(basicAxiom
                        .getSubClass().asOWLClass(), basicAxiom))));
    }

    private void createIndividualsRecursive(OWLClass basicClass, OWLSubClassOfAxiom reasoningPropertyAxiom) {
        if (genericTool.getOntology().subClassAxiomsForSuperClass(reasoningPropertyAxiom.getSubClass().asOWLClass())
                .count() == 0) {
            createIndividualFor(basicClass, reasoningPropertyAxiom.getSubClass());
        } else {
            genericTool.getOntology().subClassAxiomsForSuperClass(reasoningPropertyAxiom.getSubClass().asOWLClass())
                    .forEach(reasoningPropertyAxiom1 -> createIndividualsRecursive(basicClass,
                            reasoningPropertyAxiom1));
        }
    }

    private void createIndividualFor(OWLClass parent, OWLClassExpression clasExpres) {
        OWLClass clasToCreate = clasExpres.asOWLClass();
        String name = clasToCreate.getIRI().getShortForm() + "Ind";
        OWLNamedIndividual ind = genericTool.getFactory().getOWLNamedIndividual(helper.create(name));
        helper.addAxiom(genericTool.getFactory().getOWLClassAssertionAxiom(clasToCreate, ind));

        constances.forEach(con -> helper.addAxiom(genericTool.getFactory().getOWLObjectPropertyAssertionAxiom
                (Vocabulary.OBJECT_PROPERTY_HASCONSTANT, ind, con)));
        basicIndividuals.add(ind);

        reasoningTree.addDeviceToIndividual(parent, ind);
    }

    public List<Result> startReasoning(ComponentToBeDesigned componentToBeDesigned, List<Requirement> requirements) {
        long startTime = System.currentTimeMillis();
        try {
            if (!isReasoningPrepared) {
                prepareReasoning(componentToBeDesigned);
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
            return reason(OWL.Class(componentToBeDesigned.iriOfResultUnit), requirements);
        } catch (Throwable e) {
            // It is expected if we interrupted ourself
            if (!interrupted.get()) {
                logger.error(e.getMessage(), e);
                throw e;
            }
            return null;
        } finally {
            logger.info("Time needed for reasoning: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        }
    }

    private void addRequirements(List<Requirement> requirements) {
        OWLNamedIndividual requirementsInd = genericTool.getFactory().getOWLNamedIndividual(helper.create
                ("currentRequs"));
        helper.addAxiom(genericTool.getFactory().getOWLClassAssertionAxiom(Vocabulary.CLASS_CURRENTREQUIREMENTS,
                requirementsInd));

        for (Requirement req : requirements) {
            if (req instanceof TextFieldMinMaxRequirement) {
                TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req;
                double minValue = realReq.min - (realReq.deviationPercentage / 100.0 * realReq.min);
                double maxValue = realReq.max + (realReq.deviationPercentage / 100.0 * realReq.max);
                if (!Double.isFinite(maxValue)) {
                    maxValue = Double.MAX_VALUE;
                }

                addRequirement(requirementsInd, getOWLDataProperty(realReq.minIRI), String.valueOf(minValue));
                addRequirement(requirementsInd, getOWLDataProperty(realReq.maxIRI), String.valueOf(maxValue));
                logger.debug("Requirement (displayName, min, max): " + realReq.displayName + ", " + realReq.min + ","
                        + " " + realReq.max);
                logger.debug("Requirement (displayName, minIn, maxIn): " + realReq.displayName + "," + " " + minValue
                        + ", " + maxValue);
                logger.debug("Requirement (displayName, deviation, userWeight): " + realReq.displayName + ", " +
                        realReq.deviationPercentage + ", " + realReq.userWeight);
            } else if (req instanceof TextFieldRequirement) {
                TextFieldRequirement realReq = (TextFieldRequirement) req;
                addRequirement(requirementsInd, getOWLDataProperty(realReq.reqIri), String.valueOf(realReq.value));
                logger.debug("Requirement (displayName, value): " + realReq.displayName + ", " + realReq.value);
            } else if (req instanceof CheckboxRequirement) {
                CheckboxRequirement realReq = (CheckboxRequirement) req;
                addRequirement(requirementsInd, getOWLDataProperty(realReq.reqIri), String.valueOf(realReq.value));
                logger.debug("Requirement (displayName, value): " + realReq.displayName + ", " + realReq.value);
            } else if (req instanceof DropdownRequirement) {
                DropdownRequirement realReq = (DropdownRequirement) req;
                addRequirement(requirementsInd, getOWLDataProperty(realReq.reqIri), realReq.selectedValue);
                logger.debug("Requirement (displayName, value): " + realReq.displayName + ", " + realReq.selectedValue);
            } else if (req instanceof RequirementOnlyForSolution) {
                // RequirementOnlyForSolution have no value
            } else {
                throw new RuntimeException("Requirement class unknown: " + req.getClass());
            }
        }
        reasoningTree.setCurrentRequirement(requirementsInd);
        basicIndividuals.forEach(ind -> helper.addAxiom(genericTool.getFactory().getOWLObjectPropertyAssertionAxiom
                (Vocabulary.OBJECT_PROPERTY_HASCURRENTREQUIREMENT, ind, requirementsInd)));
    }

    private OWLDataProperty getOWLDataProperty(String iri) {
        return genericTool.getFactory().getOWLDataProperty(IRI.create(iri));
    }

    private void addRequirement(OWLNamedIndividual requirementsInd, OWLDataProperty property, String value) {
        OWLDataPropertyAssertionAxiom reqAxiom = genericTool.getFactory().getOWLDataPropertyAssertionAxiom(property,
                requirementsInd, genericTool.getFactory().getOWLLiteral(value, genericTool.getOntology()
                        .dataPropertyRangeAxioms(property).findAny().orElseThrow(() -> new RuntimeException("Specify"
                                + " rang for property of a requirement!")).getRange().asOWLDatatype()));
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

            genericTool.getOntology().objectSubPropertyAxiomsForSuperProperty(Vocabulary
                    .OBJECT_PROPERTY_ISCOMPOSEDOFDEVICE).forEach(subOb -> genericTool.getReasoner()
                    .objectPropertyValues(resultingComponent, subOb.getSubProperty().getNamedProperty()).forEach
                            (composedComponent -> result.components.add(parseComponent(subOb, composedComponent))));
            result.components.sort(Comparator.comparingInt(comp -> comp.orderPosition));

            result.requirements = copyRequirements(requirements);
            for (Requirement req : result.requirements) {
                if (req.resultIRI != null) {
                    setResult(resultingComponent, req);
                }
            }

            results.add(result);
        });

        handleDeviations(results);
        helper.checkConsistency();
        logger.debug("Number of results: " + results.size());
        logger.debug("Time needed for make results: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        return results;
    }

    private Component parseComponent(OWLSubObjectPropertyOfAxiom subOb, OWLNamedIndividual composedComponent) {
        Component component = new Component();
        component.nameOfComponent = helper.getNameOfComponent(subOb.getSubProperty().getNamedProperty());
        component.nameOfInstance = helper.getNameOfOWLNamedIndividual(composedComponent);
        if (component.nameOfInstance.contains(ReasoningTree.PermutationSeparator)) {
            component.nameOfInstance = component.nameOfInstance.substring(0, component.nameOfInstance.indexOf
                    (ReasoningTree.PermutationSeparator));
            if (component.nameOfInstance.contains(Vocabulary.CLASS_LINEAR.getIRI().getShortForm())) {
                component.nameOfInstance = Vocabulary.CLASS_LINEAR.getIRI().getShortForm();
            } else if (component.nameOfInstance.contains(Vocabulary.CLASS_COMPRESSED.getIRI().getShortForm())) {
                component.nameOfInstance = Vocabulary.CLASS_COMPRESSED.getIRI().getShortForm();
            } else if (component.nameOfInstance.contains(Vocabulary.CLASS_TWOSIDED.getIRI().getShortForm())) {
                component.nameOfInstance = Vocabulary.CLASS_TWOSIDED.getIRI().getShortForm();
            }
        }
        genericTool.getOntology().objectPropertyRangeAxioms(subOb.getSubProperty().getNamedProperty()).forEach(range
                -> component.orderPosition = helper.getOrderPositionForClass(range.getRange().asOWLClass()));
        genericTool.getOntology().objectPropertyRangeAxioms(subOb.getSubProperty().getNamedProperty()).forEach(range
                -> component.showDefaultInResults = helper.getShowDefaultInResultsForClass(range.getRange()
                .asOWLClass()));
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
            } else if (req instanceof DropdownRequirement) {
                copyReqs.add(new DropdownRequirement((DropdownRequirement) req));
            } else if (req instanceof RequirementOnlyForSolution) {
                copyReqs.add(new RequirementOnlyForSolution((RequirementOnlyForSolution) req));
            } else {
                throw new RuntimeException("Requirement class unknown: " + req.getClass());
            }
        }
        return copyReqs;
    }

    private void setResult(OWLNamedIndividual resultingComponent, Requirement req) {
        if (req instanceof TextFieldMinMaxRequirement) {
            TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req;
            genericTool.getReasoner().dataPropertyValues(resultingComponent, getOWLDataProperty(req.resultIRI))
                    .findAny().ifPresent(obProp -> realReq.result = helper.parseValueToDouble(obProp));
        } else if (req instanceof TextFieldRequirement) {
            TextFieldRequirement realReq = (TextFieldRequirement) req;
            genericTool.getReasoner().dataPropertyValues(resultingComponent, getOWLDataProperty(req.resultIRI))
                    .findAny().ifPresent(obProp -> realReq.result = helper.parseValueToDouble(obProp));
        } else if (req instanceof CheckboxRequirement) {
            CheckboxRequirement realReq = (CheckboxRequirement) req;
            genericTool.getReasoner().dataPropertyValues(resultingComponent, getOWLDataProperty(req.resultIRI))
                    .findAny().ifPresent(obProp -> realReq.result = obProp.parseBoolean());
        } else if (req instanceof DropdownRequirement) {
            DropdownRequirement realReq = (DropdownRequirement) req;
            genericTool.getReasoner().dataPropertyValues(resultingComponent, getOWLDataProperty(req.resultIRI))
                    .findAny().ifPresent(obProp -> realReq.result = obProp.getLiteral());
        } else if (req instanceof RequirementOnlyForSolution) {
            RequirementOnlyForSolution realReq = (RequirementOnlyForSolution) req;
            genericTool.getReasoner().dataPropertyValues(resultingComponent, getOWLDataProperty(req.resultIRI))
                    .findAny().ifPresent(obProp -> realReq.result = helper.parseValueToDouble(obProp));
        } else {
            throw new RuntimeException("Requirement class unknown: " + req.getClass());
        }
    }

    private void handleDeviations(List<Result> results) {
        for (Result result : results) {
            double weightSumForNRMSD = 0;
            double sumForNRMSD = 0;
            double weightSumForPerformance = 0;
            double sumForPerformance = 0;
            for (Requirement req : result.requirements) {
                if (req instanceof TextFieldMinMaxRequirement) {
                    TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req;
                    double weightInSumForNRMSD = 0;
                    double weightInSumForPerformance = 0;

                    boolean skipedMin = false;
                    if (realReq.min != 0) {
                        Line minLine = new Line(0, 0, realReq.min, 1.0);
                        double devMin = minLine.getY(realReq.result);
                        if (devMin <= 1) {
                            weightInSumForNRMSD = (1 - devMin) * (1 - devMin) * realReq.userWeight;
                            double deviationMin = realReq.min - (realReq.deviationPercentage / 100.0 * realReq.min);
                            minLine = new Line(deviationMin, 0, realReq.min, 1.0);
                            realReq.acutalSatisficationToAllowedDeviation = minLine.getY(realReq.result);
                        } else {
                            weightInSumForNRMSD = 0;
                        }
                        weightInSumForPerformance = devMin * realReq.userWeight;
                    } else {
                        skipedMin = true;
                    }

                    boolean skipedMax = false;
                    if (realReq.max * realReq.scaleFromOntologyToUI < Double.MAX_VALUE) {
                        Line maxLine = new Line(realReq.max, 1.0, 2 * realReq.max, 0);
                        double devMax = maxLine.getY(realReq.result);
                        if (devMax <= 1) {
                            weightInSumForNRMSD = Math.max(weightInSumForNRMSD, (1 - devMax) * (1 - devMax) * realReq
                                    .userWeight);
                            double deviationMax = realReq.max + (realReq.deviationPercentage / 100.0 * realReq.max);
                            maxLine = new Line(realReq.max, 1.0, deviationMax, 0);
                            realReq.acutalSatisficationToAllowedDeviation = Math.min(realReq
                                    .acutalSatisficationToAllowedDeviation, maxLine.getY(realReq.result));
                        } else {
                            weightInSumForNRMSD = Math.max(weightInSumForNRMSD, 0);
                        }
                        weightInSumForPerformance = Math.min(weightInSumForPerformance, devMax * realReq.userWeight);
                        if (skipedMin) {
                            weightInSumForPerformance = devMax * realReq.userWeight;
                        }
                    } else {
                        skipedMax = true;
                    }


                    if (!skipedMin || !skipedMax) {
                        sumForNRMSD += weightInSumForNRMSD;
                        weightSumForNRMSD += realReq.userWeight;
                        sumForPerformance += weightInSumForPerformance;
                        weightSumForPerformance += realReq.userWeight;
                    }
                }
            }

            //TODO make this general
            double height = result.requirements.stream().filter(req -> Vocabulary
                    .DATA_PROPERTY_HASDIMENSIONHEIGHT_H_UNIT_MM.getIRI().getIRIString().equals(req.resultIRI) && req
                    instanceof RequirementOnlyForSolution).findAny().map(req -> ((RequirementOnlyForSolution) req)
                    .result).orElse(0.0);
            double allowedOuterDiameter = result.requirements.stream().filter(req -> Vocabulary
                    .DATA_PROPERTY_HASDIMENSIONOUTERDIAMETER_D_UNIT_MM.getIRI().getIRIString().equals(req.resultIRI)
                    && req instanceof TextFieldMinMaxRequirement).findAny().map(req -> ((TextFieldMinMaxRequirement)
                    req).max).orElse(0.0);
            if (height > 0.0 && allowedOuterDiameter < Double.MAX_VALUE) {
                double additionalHeight = result.requirements.stream().filter(req -> req instanceof
                        TextFieldRequirement && Vocabulary.DATA_PROPERTY_HASMAXIMUMADDITIONALHEIGHTPREQMAX_H_UNIT_MM
                        .getIRI().getIRIString().equals(((TextFieldRequirement) req).reqIri)).findAny().map(req -> (
                                (TextFieldRequirement) req).value).orElse(0.0);
                int userWeight = result.requirements.stream().filter(req -> Vocabulary
                        .DATA_PROPERTY_HASDIMENSIONOUTERDIAMETER_D_UNIT_MM.getIRI().getIRIString().equals(req
                                .resultIRI) && req instanceof TextFieldMinMaxRequirement).findAny().map(req -> (
                                        (TextFieldMinMaxRequirement) req).userWeight).orElse(0);

                double allowedHeight = allowedOuterDiameter + additionalHeight;

                double weightInSumForNRMSD;
                double weightInSumForPerformance;
                Line maxLine = new Line(allowedHeight, 1.0, 2 * allowedHeight, 0);
                double devMax = maxLine.getY(height);
                if (devMax <= 1) {
                    weightInSumForNRMSD = (1 - devMax) * (1 - devMax) * userWeight;
                } else {
                    weightInSumForNRMSD = 0;
                }
                weightInSumForPerformance = devMax * userWeight;

                sumForNRMSD += weightInSumForNRMSD;
                weightSumForNRMSD += userWeight;
                sumForPerformance += weightInSumForPerformance;
                weightSumForPerformance += userWeight;
            }


            double devNRMSD = Math.sqrt(1.0 / weightSumForNRMSD * sumForNRMSD);
            if (Double.isNaN(devNRMSD)) {
                devNRMSD = 1;
            }
            double devPerformance = 1.0 / weightSumForPerformance * sumForPerformance;
            if (Double.isNaN(devPerformance)) {
                devPerformance = 1;
            }

            double finalDevNRMSD = devNRMSD;
            result.requirements.stream().filter(req -> Vocabulary.DATA_PROPERTY_HASNRMSD.getIRI().getIRIString()
                    .equals(req.resultIRI) && req instanceof RequirementOnlyForSolution).forEach(req -> (
                            (RequirementOnlyForSolution) req).result = finalDevNRMSD);
            double finalDevPerformance = devPerformance;
            result.requirements.stream().filter(req -> Vocabulary.DATA_PROPERTY_HASPERFORMANCEINDEX_PX.getIRI()
                    .getIRIString().equals(req.resultIRI) && req instanceof RequirementOnlyForSolution).forEach(req
                    -> ((RequirementOnlyForSolution) req).result = finalDevPerformance);
        }
    }

    public List<ComponentToBeDesigned> getUnitsToReason() {
        long startTime = System.currentTimeMillis();
        List<ComponentToBeDesigned> units = new ArrayList<>();
        genericTool.getOntology().subClassAxiomsForSuperClass(Vocabulary.CLASS_REASONINGTREE).filter
                (axiomOfReasoningTree -> genericTool.getOntology().subClassAxiomsForSubClass(axiomOfReasoningTree
                        .getSubClass().asOWLClass()).anyMatch(subClassOfReasoningTreeAxiom ->
                        subClassOfReasoningTreeAxiom.getSuperClass().objectPropertiesInSignature().anyMatch
                                (Vocabulary.OBJECT_PROPERTY_HASREASONINGTREEPROPERTY::equals))).forEach
                (filteredAxiomOfReasoningTree -> {
            ComponentToBeDesigned componentToBeDesigned = new ComponentToBeDesigned();
            componentToBeDesigned.displayName = filteredAxiomOfReasoningTree.getSubClass().asOWLClass().getIRI().getShortForm();
            componentToBeDesigned.iriOfUnit = filteredAxiomOfReasoningTree.getSubClass().asOWLClass().getIRI().getIRIString();

            genericTool.getOntology().subClassAxiomsForSuperClass(filteredAxiomOfReasoningTree.getSubClass()
                    .asOWLClass()).forEach(classToReasonAxiom -> {
                if (componentToBeDesigned.iriOfResultUnit == null) {
                    componentToBeDesigned.iriOfResultUnit = classToReasonAxiom.getSubClass().asOWLClass().getIRI()
                            .getIRIString();
                } else {
                    throw new RuntimeException("UnitsToReasone are only allowed to have one child to identify " +
                            "resultingUnit (satisfied), found at least: " + componentToBeDesigned.iriOfResultUnit + " and " +
                            classToReasonAxiom.getSubClass().asOWLClass().getIRI().getIRIString());
                }
            });
            if (componentToBeDesigned.iriOfResultUnit == null) {
                throw new RuntimeException("UnitsToReasone must have one child to identify resultingUnit (satisfied)");
            }
            componentToBeDesigned.orderPosition = helper.getOrderPositionForClass(filteredAxiomOfReasoningTree.getSubClass()
                    .asOWLClass());

            units.add(componentToBeDesigned);
        });

        units.sort(Comparator.comparingInt(unit -> unit.orderPosition));
        logger.debug("Time needed for get UnitsToReason: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        return units;
    }

    public void interruptReasoning() {
        logger.debug("Reasoning interrupted");
        interrupted.set(true);
        ontologyReadAndWriteHelper.interruptReasoning();
        reasoningTree.interruptReasoning();
        genericTool.getReasoner().interrupt();
    }

    public List<Requirement> getRequirements(ComponentToBeDesigned componentToBeDesigned) {
        return requirementHelper.getRequirements(OWL.Class(componentToBeDesigned.iriOfUnit));
    }

    public List<RequirementDependencyCheckbox> getRequirementDependencies(List<Requirement> requirements) {
        return requirementHelper.getRequirementDependencies(requirements);
    }

    public void saveInferredOntology() {
        ontologyReadAndWriteHelper.saveInferredOntology();
    }

}
