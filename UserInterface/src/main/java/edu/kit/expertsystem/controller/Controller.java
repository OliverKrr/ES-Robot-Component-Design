package edu.kit.expertsystem.controller;

import edu.kit.expertsystem.GUI;
import edu.kit.expertsystem.MainReasoner;
import edu.kit.expertsystem.controller.wrapper.*;
import edu.kit.expertsystem.model.UnitToReason;
import edu.kit.expertsystem.model.req.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Controller {

    public static final double EPSILON = 0.00001;

    private static final Logger logger = LogManager.getLogger(Controller.class);

    private GUI gui;
    private MainReasoner reasoner;

    private List<RequirementWrapper> requirementsWrapper;
    private List<RequirementDependencyCheckboxWrapper> requirementDependencyMappers;
    private List<UnitToReason> unitsToReason;
    private UnitToReason currentUnitToReason;
    private ResultWrapper resultWrapper;

    private boolean haveRequirementChanged = true;
    private boolean isReseted = false;

    public Controller(GUI gui) {
        this.gui = gui;
        reasoner = new MainReasoner();
    }

    public void initialize() {
        reasoner.initialize();
        unitsToReason = reasoner.getUnitsToReason();
        if (unitsToReason.isEmpty()) {
            throw new RuntimeException("There are no units to reasone in the ontology!");
        }
        currentUnitToReason = unitsToReason.get(0);
        resultWrapper = new ResultWrapper();
    }

    private void initRequirements() {
        List<Requirement> requirements = reasoner.getRequirements(currentUnitToReason);
        requirementsWrapper = new ArrayList<>(requirements.size());
        requirements.forEach(req -> {
            if (req instanceof TextFieldMinMaxRequirement) {
                requirementsWrapper.add(new TextFieldMinMaxRequirementWrapper(req));
            } else if (req instanceof TextFieldRequirement) {
                requirementsWrapper.add(new TextFieldRequirementWrapper(req));
            } else if (req instanceof CheckboxRequirement) {
                requirementsWrapper.add(new CheckboxRequirementWrapper(req));
            } else if (req instanceof DropdownRequirement) {
                requirementsWrapper.add(new DropdownRequirementWrapper(req));
            } else if (req instanceof RequirementOnlyForSolution) {
                requirementsWrapper.add(new RequirementOnlyForSolutionWrapper(req));
            } else {
                throw new RuntimeException("Requirement class unknown: " + req.getClass());
            }
        });

        List<RequirementDependencyCheckbox> requirementDependencies = reasoner.getRequirementDependencies(requirements);
        requirementDependencyMappers = new ArrayList<>(requirementDependencies.size());
        requirementDependencies.forEach(reqDep -> {
            RequirementDependencyCheckboxWrapper wrapper = new RequirementDependencyCheckboxWrapper();
            wrapper.requirementDependencyCheckbox = reqDep;
            requirementDependencyMappers.add(wrapper);
        });
    }

    public void interruptReasoning() {
        reasoner.interruptReasoning();
    }

    public void saveSolutionOntology() {
        reasoner.saveInferredOntology();
    }

    public void reset() {
        if (!isReseted) {
            reasoner.prepareReasoning(currentUnitToReason);
            isReseted = true;
        }
    }

    private List<Requirement> parseToRequirements() {
        List<Requirement> requirements = new ArrayList<>(requirementsWrapper.size());
        for (RequirementWrapper req : requirementsWrapper) {
            requirements.add(req.requirement);
        }
        return requirements;
    }

    public void parseRequirements() {
        for (RequirementWrapper req : requirementsWrapper) {
            if (req instanceof TextFieldMinMaxRequirementWrapper) {
                TextFieldMinMaxRequirementWrapper reqWrapper = (TextFieldMinMaxRequirementWrapper) req;
                TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req.requirement;

                double oldMin = realReq.min;
                double oldMax = realReq.max;
                if (realReq.isIntegerValue) {
                    realReq.min = parseInteger(reqWrapper.minValue, realReq.defaultMin) / realReq.scaleFromOntologyToUI;
                    realReq.max = parseInteger(reqWrapper.maxValue, realReq.defaultMax) / realReq.scaleFromOntologyToUI;
                } else {
                    realReq.min = parseDouble(reqWrapper.minValue, realReq.defaultMin) / realReq.scaleFromOntologyToUI;
                    realReq.max = parseDouble(reqWrapper.maxValue, realReq.defaultMax) / realReq.scaleFromOntologyToUI;
                }
                haveRequirementChanged |= Math.abs(oldMin - realReq.min) > EPSILON || Math.abs(oldMax - realReq.max)
                        > EPSILON;

                if (realReq.allowOptimization) {
                    double oldDeviationPercentage = realReq.deviationPercentage;
                    int oldUserWeight = realReq.userWeight;
                    realReq.deviationPercentage = reqWrapper.deviation.getSelection() / Math.pow(10,
                            TextFieldMinMaxRequirementWrapper.digitsDeviation);
                    realReq.userWeight = Integer.parseInt(reqWrapper.userWeighting.getText());

                    haveRequirementChanged |= Math.abs(oldDeviationPercentage - realReq.deviationPercentage) >
                            EPSILON || oldUserWeight != realReq.userWeight;
                }
            } else if (req instanceof TextFieldRequirementWrapper) {
                TextFieldRequirementWrapper reqWrapper = (TextFieldRequirementWrapper) req;
                TextFieldRequirement realReq = (TextFieldRequirement) req.requirement;

                double oldValue = realReq.value;
                if (realReq.isIntegerValue) {
                    realReq.value = parseInteger(reqWrapper.value, realReq.defaultValue) / realReq
                            .scaleFromOntologyToUI;
                } else {
                    realReq.value = parseDouble(reqWrapper.value, realReq.defaultValue) / realReq.scaleFromOntologyToUI;
                }
                haveRequirementChanged |= Math.abs(oldValue - realReq.value) > EPSILON;
            } else if (req instanceof CheckboxRequirementWrapper) {
                CheckboxRequirementWrapper reqWrapper = (CheckboxRequirementWrapper) req;
                CheckboxRequirement realReq = (CheckboxRequirement) req.requirement;

                boolean oldValue = realReq.value;
                realReq.value = reqWrapper.value.getSelection();
                haveRequirementChanged |= oldValue != realReq.value;
            } else if (req instanceof DropdownRequirementWrapper) {
                DropdownRequirementWrapper reqWrapper = (DropdownRequirementWrapper) req;
                DropdownRequirement realReq = (DropdownRequirement) req.requirement;

                String oldSelectedValue = realReq.selectedValue;
                realReq.selectedValue = reqWrapper.values.getText();
                haveRequirementChanged |= !oldSelectedValue.equals(realReq.selectedValue);
            } else if (req instanceof RequirementOnlyForSolutionWrapper) {
                // RequirementOnlyForSolution have no value
            } else {
                throw new RuntimeException("Requirement class unknown: " + req.getClass());
            }
        }
    }

    private double parseDouble(Text textToParse, double defaultValue) {
        if (!textToParse.getText().isEmpty()) {
            try {
                return Double.parseDouble(textToParse.getText());
            } catch (NumberFormatException e) {
                String message = "Could not parse <" + textToParse.getText() + "> to double. Default value <" +
                        defaultValue + "> will be taken!";
                logger.error(message);
            }
        }
        return defaultValue;
    }

    private double parseInteger(Text textToParse, double defaultValue) {
        if (!textToParse.getText().isEmpty()) {
            try {
                return Integer.parseInt(textToParse.getText());
            } catch (NumberFormatException e) {
                String message = "Could not parse <" + textToParse.getText() + "> to int. Default value <" + Math
                        .round(defaultValue) + "> will be taken!";
                logger.error(message);
            }
        }
        return Math.round(defaultValue);
    }

    public void reason() {
        if (haveRequirementChanged) {
            haveRequirementChanged = false;
            isReseted = false;
            resultWrapper.results = reasoner.startReasoning(currentUnitToReason, parseToRequirements());
            if (resultWrapper.results != null) {
                gui.notifySolutionIsReady();
            }
        }
    }

    public boolean haveRequirementChanged() {
        return haveRequirementChanged;
    }

    public void setResults() {
        resultWrapper.resultShow.setResults();
    }

    public void updateUnitToReason(String unitToReason) {
        unitsToReason.stream().filter(unit -> unit.displayName.equals(unitToReason)).forEach(unit ->
                currentUnitToReason = unit);
        initRequirements();
    }

    public Stream<String> getUnitsToReason() {
        return unitsToReason.stream().map(unit -> unit.displayName);
    }

    public List<RequirementWrapper> getRequirementsWrapper() {
        return requirementsWrapper;
    }

    public List<RequirementDependencyCheckboxWrapper> getRequirementDependencyWrapper() {
        return requirementDependencyMappers;
    }

    public ResultWrapper getResultWrapper() {
        return resultWrapper;
    }
}
