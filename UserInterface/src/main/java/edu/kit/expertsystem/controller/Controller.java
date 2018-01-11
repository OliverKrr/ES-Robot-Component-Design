package edu.kit.expertsystem.controller;

import edu.kit.expertsystem.*;
import edu.kit.expertsystem.controller.wrapper.*;
import edu.kit.expertsystem.model.Component;
import edu.kit.expertsystem.model.Result;
import edu.kit.expertsystem.model.UnitToReason;
import edu.kit.expertsystem.model.req.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Stream;

public class Controller {

    private static final int MAXIMAL_NEEDED_SPACES = 13;

    private static final Logger logger = LogManager.getLogger(Controller.class);

    private GUI gui;
    private MainReasoner reasoner;

    private List<RequirementWrapper> requirementsWrapper;
    private List<RequirementDependencyCheckboxWrapper> requirementDependencyMappers;
    private List<UnitToReason> unitsToReason;
    private UnitToReason currentUnitToReason;
    private ResultWrapper resultWrapper;

    private DecimalFormat df = new DecimalFormat("#.####");

    boolean haveRequirementChanged = true;
    boolean isReseted = false;

    public Controller(GUI gui) {
        this.gui = gui;
        df.setRoundingMode(RoundingMode.CEILING);
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
                haveRequirementChanged |= Math.abs(oldMin - realReq.min) > 0.000001 || Math.abs(oldMax - realReq.max)
                        > 0.000001;

                if (realReq.allowOptimization) {
                    double oldDeviationPercentage = realReq.deviationPercentage;
                    int oldUserWeight = realReq.userWeight;
                    realReq.deviationPercentage = reqWrapper.deviation.getSelection() / Math.pow(10,
                            TextFieldMinMaxRequirementWrapper.digitsDeviation);
                    realReq.userWeight = Integer.parseInt(reqWrapper.userWeighting.getText());

                    haveRequirementChanged |= Math.abs(oldDeviationPercentage - realReq.deviationPercentage) >
                            0.000001 || oldUserWeight != realReq.userWeight;
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
                haveRequirementChanged |= Math.abs(oldValue - realReq.value) > 0.000001;
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
        int oldSelectionOfOrderBy = resultWrapper.orderBy.getSelectionIndex();
        int oldSelectionOfOrderBy2 = resultWrapper.orderBy2.getSelectionIndex();
        resultWrapper.orderBy.removeAll();
        resultWrapper.orderBy2.removeAll();
        resultWrapper.tree.removeAll();
        resultWrapper.displayNameToIriMap.clear();

        for (Result result : resultWrapper.results) {
            double maxNumberOfCharsForComp = getMaxNumberOfCharsForComp(result);
            logger.debug("Solution:");
            result.components.forEach(comp -> logger.debug("Component: " + getNameForComponent(comp,
                    maxNumberOfCharsForComp)));

            double maxNumberOfCharsForReq = getMaxNumberOfCharsForReq(result);
            for (Requirement req : result.requirements) {
                if (req.resultIRI != null) {
                    logger.debug("Requirement: " + getNameForReq(req, maxNumberOfCharsForReq));

                    if (!resultWrapper.displayNameToIriMap.containsKey(req.displayName)) {
                        resultWrapper.displayNameToIriMap.put(req.displayName, req.resultIRI);
                        resultWrapper.orderBy.add(req.displayName + " \u25BC");
                        resultWrapper.orderBy.add(req.displayName + " \u25B2");
                        resultWrapper.orderBy2.add(req.displayName + " \u25BC");
                        resultWrapper.orderBy2.add(req.displayName + " \u25B2");
                    }
                }
            }
        }

        SelectionAdapter listener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                resultWrapper.tree.forceFocus();
                String currentSelection = resultWrapper.orderBy.getText();
                String currentSelection2 = resultWrapper.orderBy.getText();
                if (currentSelection.length() > 2 && currentSelection2.length() > 2) {
                    String displayName = currentSelection.substring(0, currentSelection.length() - 2);
                    String displayName2 = currentSelection2.substring(0, currentSelection2.length() - 2);

                    resultWrapper.results.sort((first, second) -> {
                        double valueFirst1 = 0;
                        double valueFirst2 = 0;
                        for (Requirement req : first.requirements) {
                            if (resultWrapper.displayNameToIriMap.get(displayName).equals(req.resultIRI)) {
                                valueFirst1 = getResultValueAsDouble(req);
                            }
                            if (resultWrapper.displayNameToIriMap.get(displayName2).equals(req.resultIRI)) {
                                valueFirst2 = getResultValueAsDouble(req);
                            }
                        }
                        double valueSecond1 = 0;
                        double valueSecond2 = 0;
                        for (Requirement req : second.requirements) {
                            if (resultWrapper.displayNameToIriMap.get(displayName).equals(req.resultIRI)) {
                                valueSecond1 = getResultValueAsDouble(req);
                            }
                            if (resultWrapper.displayNameToIriMap.get(displayName2).equals(req.resultIRI)) {
                                valueSecond2 = getResultValueAsDouble(req);
                            }
                        }

                        if (Math.abs(valueFirst1 - valueSecond1) > 0.000001) {
                            if (currentSelection.endsWith("\u25B2")) {
                                return Double.compare(valueFirst1, valueSecond1);
                            } else {
                                return Double.compare(valueSecond1, valueFirst1);
                            }
                        } else {
                            if (currentSelection2.endsWith("\u25B2")) {
                                return Double.compare(valueFirst2, valueSecond2);
                            } else {
                                return Double.compare(valueSecond2, valueFirst2);
                            }
                        }
                    });
                }

                resultWrapper.tree.removeAll();
                buildTree();

                Event reloadSearchE = new Event();
                reloadSearchE.type = SWT.KeyUp;
                reloadSearchE.keyCode = ' ';
                resultWrapper.searchField.notifyListeners(SWT.KeyUp, reloadSearchE);
            }
        };
        if (resultWrapper.orderBy.getItemCount() > oldSelectionOfOrderBy) {
            if (oldSelectionOfOrderBy == -1) {
                oldSelectionOfOrderBy = 0;
            }
            resultWrapper.orderBy.select(oldSelectionOfOrderBy);
        }
        if (resultWrapper.orderBy2.getItemCount() > oldSelectionOfOrderBy2) {
            if (oldSelectionOfOrderBy2 == -1) {
                oldSelectionOfOrderBy2 = 0;
            }
            resultWrapper.orderBy2.select(oldSelectionOfOrderBy2);
        }

        resultWrapper.orderBy.addSelectionListener(listener);
        resultWrapper.orderBy2.addSelectionListener(listener);
        resultWrapper.showOnlyDiffsCheckBox.addSelectionListener(listener);
        resultWrapper.orderBy.notifyListeners(SWT.Selection, new Event());
    }

    private void buildTree() {
        addTreeItem(resultWrapper.tree, "Number of results: " + resultWrapper.results.size());

        Map<String, ShowResult> showKeys = new HashMap<>();
        if (resultWrapper.showOnlyDiffsCheckBox.getSelection()) {
            for (Result result : resultWrapper.results) {
                for (Component component : result.components) {
                    handleShow(showKeys, component.nameOfComponent, component.nameOfInstance, true);
                }
                for (Requirement req : result.requirements) {
                    handleShow(showKeys, req.displayName, getResultValue(req), false);
                }
            }
        }

        for (Result result : resultWrapper.results) {
            TreeItem resItem = addTreeItem(resultWrapper.tree, "");
            StringBuilder concatenationOfNamesBuilder = new StringBuilder();

            double maxNumberOfChars = getMaxNumberOfCharsForComp(result);

            StringBuilder builder = new StringBuilder("");
            for (Component component : result.components) {
                if (resultWrapper.showOnlyDiffsCheckBox.getSelection() && !showKeys.get(component.nameOfComponent)
                        .showResult) {
                    continue;
                }
                String name = getNameForComponent(component, maxNumberOfChars);
                builder.append(name.replaceAll(" ", ""));
                addTreeItem(resItem, name, true);
            }
            concatenationOfNamesBuilder.append(builder.toString());

            maxNumberOfChars = getMaxNumberOfCharsForReq(result);

            for (Requirement req : result.requirements) {
                if (req.resultIRI == null || (resultWrapper.showOnlyDiffsCheckBox.getSelection() && !showKeys.get(req
                        .displayName).showResult)) {
                    continue;
                }
                String name = getNameForReq(req, maxNumberOfChars);
                concatenationOfNamesBuilder.append(name.replaceAll(" ", ""));
                addTreeItem(resItem, name, false);
            }
            resItem.setData(SolutionTab.SEARCH_KEY, concatenationOfNamesBuilder.toString());
            resItem.setExpanded(true);
        }
    }

    private double getResultValueAsDouble(Requirement req) {
        if (req instanceof TextFieldMinMaxRequirement) {
            TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req;
            return realReq.result * realReq.scaleFromOntologyToUI;
        } else if (req instanceof TextFieldRequirement) {
            TextFieldRequirement realReq = (TextFieldRequirement) req;
            return realReq.result * realReq.scaleFromOntologyToUI;
        } else if (req instanceof CheckboxRequirement) {
            CheckboxRequirement realReq = (CheckboxRequirement) req;
            return realReq.result ? 1 : 0;
        } else if (req instanceof DropdownRequirement) {
            // DropdownRequirement have no results
            return Double.MAX_VALUE;
        } else if (req instanceof RequirementOnlyForSolution) {
            RequirementOnlyForSolution realReq = (RequirementOnlyForSolution) req;
            return realReq.result * realReq.scaleFromOntologyToUI;
        } else {
            throw new RuntimeException("Requirement class unknown: " + req.getClass());
        }
    }

    private String getResultValue(Requirement req) {
        if (req instanceof TextFieldMinMaxRequirement) {
            TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req;
            return df.format(realReq.result * realReq.scaleFromOntologyToUI);
        } else if (req instanceof TextFieldRequirement) {
            TextFieldRequirement realReq = (TextFieldRequirement) req;
            return df.format(realReq.result * realReq.scaleFromOntologyToUI);
        } else if (req instanceof CheckboxRequirement) {
            CheckboxRequirement realReq = (CheckboxRequirement) req;
            return String.valueOf(realReq.result);
        } else if (req instanceof DropdownRequirement) {
            DropdownRequirement realReq = (DropdownRequirement) req;
            return realReq.result;
        } else if (req instanceof RequirementOnlyForSolution) {
            RequirementOnlyForSolution realReq = (RequirementOnlyForSolution) req;
            return df.format(realReq.result * realReq.scaleFromOntologyToUI);
        } else {
            throw new RuntimeException("Requirement class unknown: " + req.getClass());
        }
    }

    private void handleShow(Map<String, ShowResult> showKeys, String key, String value, boolean isComponent) {
        if (!showKeys.containsKey(key)) {
            showKeys.put(key, new ShowResult(value, false, isComponent));
        }
        if (!showKeys.get(key).firstValue.equals(value)) {
            showKeys.get(key).showResult = true;
        }
    }

    private double getMaxNumberOfCharsForComp(Result result) {
        return result.components.stream().max(Comparator.comparingInt(val -> val.nameOfComponent.length())).get()
                .nameOfComponent.length();
    }

    private String getNameForComponent(Component component, double maxNumberOfChars) {
        return "".equals(component.nameOfComponent) ? component.nameOfInstance : component.nameOfComponent + ": " +
                getSpacesForDisplayName(component.nameOfComponent, maxNumberOfChars) + component.nameOfInstance;
    }

    private double getMaxNumberOfCharsForReq(Result result) {
        return result.requirements.stream().filter(req -> req.resultIRI != null).max(Comparator.comparingInt(val ->
                val.displayName.length())).get().displayName.length();
    }

    private String getNameForReq(Requirement req, double maxNumberOfChars) {
        String resultValue = getResultValue(req);
        String unit = req.unit == null ? "" : req.unit;
        return req.displayName + ": " + getSpacesForDisplayName(req.displayName, maxNumberOfChars) + resultValue +
                getSpacesForResultValue(resultValue) + unit;
    }

    private void addTreeItem(TreeItem parent, String text, boolean makeGreen) {
        TreeItem resItem = new TreeItem(parent, SWT.WRAP);
        resItem.setText(text);
        if (makeGreen) {
            resItem.setForeground(Configs.KIT_GREEN_70);
        }
        resItem.setFont(SWTResourceManager.getFont("Courier New", GuiHelper.getFontHeight(resItem.getFont()), SWT
                .NORMAL));
    }

    private TreeItem addTreeItem(Tree parent, String text) {
        TreeItem resItem = new TreeItem(parent, SWT.WRAP);
        resItem.setText(text);
        resItem.setForeground(Configs.KIT_GREEN_70);
        return resItem;
    }

    private String getSpacesForDisplayName(String currentDisplayName, double maxNumberOfChars) {
        StringBuilder builder = new StringBuilder("");
        for (int i = currentDisplayName.length(); i <= maxNumberOfChars; ++i) {
            builder.append(" ");
        }
        return builder.toString();
    }

    private String getSpacesForResultValue(String value) {
        StringBuilder builder = new StringBuilder("");
        for (int i = value.length(); i <= MAXIMAL_NEEDED_SPACES; ++i) {
            builder.append(" ");
        }
        return builder.toString();
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


    private static class ShowResult {
        String firstValue;
        boolean showResult;
        boolean isComponent;

        ShowResult(String firstValue, boolean showResult, boolean isComponent) {
            this.firstValue = firstValue;
            this.showResult = showResult;
            this.isComponent = isComponent;
        }
    }
}
