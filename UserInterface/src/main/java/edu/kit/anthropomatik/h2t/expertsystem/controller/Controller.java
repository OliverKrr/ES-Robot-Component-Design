/*
 * Copyright 2018 Oliver Karrenbauer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation * files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, * * * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.kit.anthropomatik.h2t.expertsystem.controller;

import edu.kit.anthropomatik.h2t.expertsystem.GUI;
import edu.kit.anthropomatik.h2t.expertsystem.MainReasoner;
import edu.kit.anthropomatik.h2t.expertsystem.controller.wrapper.*;
import edu.kit.anthropomatik.h2t.expertsystem.model.ComponentToBeDesigned;
import edu.kit.anthropomatik.h2t.expertsystem.model.req.*;
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
    private List<ComponentToBeDesigned> componentsToBeDesigned;
    private ComponentToBeDesigned currentComponentToBeDesigned;
    private ResultWrapper resultWrapper;

    private boolean haveRequirementChanged = true;
    private boolean isReseted = false;

    public Controller(GUI gui) {
        this.gui = gui;
        reasoner = new MainReasoner();
    }

    public void initialize() {
        reasoner.initialize();
        componentsToBeDesigned = reasoner.getUnitsToReason();
        if (componentsToBeDesigned.isEmpty()) {
            throw new RuntimeException("There are no units to reasone in the ontology!");
        }
        currentComponentToBeDesigned = componentsToBeDesigned.get(0);
        resultWrapper = new ResultWrapper();
    }

    private void initRequirements() {
        List<Requirement> requirements = reasoner.getRequirements(currentComponentToBeDesigned);
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
            reasoner.prepareReasoning(currentComponentToBeDesigned);
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
            resultWrapper.componentToBeDesigned = currentComponentToBeDesigned.displayName;
            resultWrapper.results = reasoner.startReasoning(currentComponentToBeDesigned, parseToRequirements());
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

    public void updateComponentsToBeDesigned(String componentToBeDesigned) {
        componentsToBeDesigned.stream().filter(unit -> unit.displayName.equals(componentToBeDesigned)).forEach(unit
                -> currentComponentToBeDesigned = unit);
        initRequirements();
    }

    public Stream<String> getComponentsToBeDesigned() {
        return componentsToBeDesigned.stream().map(unit -> unit.displayName);
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
