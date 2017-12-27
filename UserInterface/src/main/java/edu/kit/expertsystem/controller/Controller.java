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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Controller {

    private static final int MAXIMAL_NEEDED_SPACES = 7;

    private static final Logger logger = LogManager.getLogger(Controller.class);

    private GUI gui;
    private MainReasoner reasoner;

    private List<RequirementWrapper> requirementsWrapper;
    private List<RequirementDependencyCheckboxWrapper> requirementDependencyMappers;
    private List<UnitToReason> unitsToReason;
    private UnitToReason currentUnitToReason;
    private ResultWrapper resultWrapper;

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
            } else {
                throw new RuntimeException("Requirement class unknown: " + req.getClass());
            }
        });

        List<RequirementDependencyCheckbox> requirementDependencies = reasoner
                .getRequirementDependencies(requirements);
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
        for (RequirementWrapper req : requirementsWrapper) {
            if (req instanceof TextFieldMinMaxRequirementWrapper) {
                TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req.requirement;
                realReq.min = realReq.defaultMin;
                realReq.max = realReq.defaultMax;
                realReq.result = -1;
            } else if (req instanceof TextFieldRequirementWrapper) {
                TextFieldRequirement realReq = (TextFieldRequirement) req.requirement;
                realReq.value = realReq.defaultValue;
                realReq.result = -1;
            } else if (req instanceof CheckboxRequirementWrapper) {
                CheckboxRequirement realReq = (CheckboxRequirement) req.requirement;
                realReq.value = realReq.defaultValue;
                realReq.result = false;
            } else {
                throw new RuntimeException("Requirement class unknown: " + req.getClass());
            }
        }
        reasoner.prepareReasoning(currentUnitToReason);
    }

    private List<Requirement> parseToRequirements() {
        List<Requirement> requirements = new ArrayList<>(requirementsWrapper.size());
        for (RequirementWrapper req : requirementsWrapper) {
            requirements.add(req.requirement);
        }
        return requirements;
    }

    public void parseRequirements() {
        resultWrapper.orderBy.removeAll();
        resultWrapper.tree.removeAll();

        for (RequirementWrapper req : requirementsWrapper) {
            if (req instanceof TextFieldMinMaxRequirementWrapper) {
                TextFieldMinMaxRequirementWrapper reqWrapper = (TextFieldMinMaxRequirementWrapper) req;
                TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req.requirement;

                realReq.min = parseDouble(reqWrapper.minValue, realReq.defaultMin)
                        / realReq.scaleFromOntologyToUI;
                realReq.max = parseDouble(reqWrapper.maxValue, realReq.defaultMax)
                        / realReq.scaleFromOntologyToUI;
            } else if (req instanceof TextFieldRequirementWrapper) {
                TextFieldRequirementWrapper reqWrapper = (TextFieldRequirementWrapper) req;
                TextFieldRequirement realReq = (TextFieldRequirement) req.requirement;

                realReq.value = parseDouble(reqWrapper.value, realReq.defaultValue)
                        / realReq.scaleFromOntologyToUI;
            } else if (req instanceof CheckboxRequirementWrapper) {
                CheckboxRequirementWrapper reqWrapper = (CheckboxRequirementWrapper) req;
                CheckboxRequirement realReq = (CheckboxRequirement) req.requirement;

                realReq.value = reqWrapper.value.getSelection();
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
                String message = "Could not parse <" + textToParse.getText() + "> to double. Default value <"
                        + defaultValue + "> will be taken!";
                logger.error(message);
            }
        }
        return defaultValue;
    }

    public void reason() {
        resultWrapper.results = reasoner.startReasoning(currentUnitToReason, parseToRequirements());
        if (resultWrapper.results != null) {
            gui.notifySolutionIsReady();
        }
    }

    public void setResults() {
        resultWrapper.displayNameToIriMap.clear();
        for (Result result : resultWrapper.results) {
            double maxNumberOfCharsForComp = getMaxNumberOfCharsForComp(result);
            logger.debug("Solution:");
            result.components.forEach(
                    comp -> logger.debug("Component: " + getNameForComponent(comp, maxNumberOfCharsForComp)));

            double maxNumberOfCharsForReq = getMaxNumberOfCharsForReq(result);
            for (Requirement req : result.requirements) {
                if (req.resultIRI != null) {
                    logger.debug("Requirement: " + getNameForReq(req, maxNumberOfCharsForReq));

                    if (!resultWrapper.displayNameToIriMap.containsKey(req.displayName)) {
                        resultWrapper.displayNameToIriMap.put(req.displayName, req.resultIRI);
                        resultWrapper.orderBy.add(req.displayName + " \u25BC");
                        resultWrapper.orderBy.add(req.displayName + " \u25B2");
                    }
                }
            }
        }

        resultWrapper.orderBy.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                String currentSelection = resultWrapper.orderBy.getText();
                if (currentSelection.length() > 2) {
                    String displayName = currentSelection.substring(0, currentSelection.length() - 2);

                    Collections.sort(resultWrapper.results,
                            Comparator.comparingDouble(result -> result.requirements.stream()
                                    .filter(req -> resultWrapper.displayNameToIriMap.get(displayName)
                                            .equals(req.resultIRI))
                                    .findAny().map(req -> -((TextFieldMinMaxRequirement) req).result)
                                    .orElse(-Double.MAX_VALUE)));

                    if (currentSelection.endsWith("\u25B2")) {
                        Collections.reverse(resultWrapper.results);
                    }
                }

                resultWrapper.tree.removeAll();
                buildTree();

                Event reloadSearchE = new Event();
                reloadSearchE.type = SWT.KeyUp;
                reloadSearchE.keyCode = ' ';
                resultWrapper.searchField.notifyListeners(SWT.KeyUp, reloadSearchE);
            }
        });
        resultWrapper.orderBy.select(0);
        resultWrapper.orderBy.notifyListeners(SWT.Selection, new Event());
    }

    private void buildTree() {
        addTreeItem(resultWrapper.tree, "Number of results: " + resultWrapper.results.size());
        for (Result result : resultWrapper.results) {
            TreeItem resItem = addTreeItem(resultWrapper.tree, "");
            String concatenationOfNames = "";

            double maxNumberOfChars = getMaxNumberOfCharsForComp(result);

            for (Component component : result.components) {
                String name = getNameForComponent(component, maxNumberOfChars);
                concatenationOfNames += name.replaceAll(" ", "");
                addTreeItem(resItem, name, true);
            }

            maxNumberOfChars = getMaxNumberOfCharsForReq(result);

            for (Requirement req : result.requirements) {
                if (req.resultIRI == null) {
                    continue;
                }
                String name = getNameForReq(req, maxNumberOfChars);
                concatenationOfNames += name.replaceAll(" ", "");
                addTreeItem(resItem, name, false);
            }
            resItem.setData(SolutionTab.SEARCH_KEY, concatenationOfNames);
            resItem.setExpanded(true);
        }
    }

    private double getMaxNumberOfCharsForComp(Result result) {
        return result.components.stream()
                .max((val1, val2) -> val1.nameOfComponent.length() - val2.nameOfComponent.length())
                .get().nameOfComponent.length();
    }

    private String getNameForComponent(Component component, double maxNumberOfChars) {
        return "".equals(component.nameOfComponent) ? component.nameOfInstance
                : component.nameOfComponent + ": "
                + getSpacesForDisplayName(component.nameOfComponent, maxNumberOfChars)
                + component.nameOfInstance;
    }

    private double getMaxNumberOfCharsForReq(Result result) {
        return result.requirements.stream().filter(req -> req.resultIRI != null)
                .max((val1, val2) -> val1.displayName.length() - val2.displayName.length()).get().displayName
                .length();
    }

    private String getNameForReq(Requirement req, double maxNumberOfChars) {
        String resultValue = "";
        if (req instanceof TextFieldMinMaxRequirement) {
            TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req;
            resultValue = String.valueOf(realReq.result * realReq.scaleFromOntologyToUI);
        } else if (req instanceof TextFieldRequirement) {
            TextFieldRequirement realReq = (TextFieldRequirement) req;
            resultValue = String.valueOf(realReq.result * realReq.scaleFromOntologyToUI);
        } else if (req instanceof CheckboxRequirement) {
            CheckboxRequirement realReq = (CheckboxRequirement) req;
            resultValue = String.valueOf(realReq.result);
        } else {
            throw new RuntimeException("Requirement class unknown: " + req.getClass());
        }
        String unit = req.unit == null ? "" : req.unit;
        return req.displayName + ": " + getSpacesForDisplayName(req.displayName, maxNumberOfChars)
                + resultValue + getSpacesForResultValue(resultValue) + unit;
    }

    private TreeItem addTreeItem(TreeItem parent, String text, boolean makeGreen) {
        TreeItem resItem = new TreeItem(parent, SWT.WRAP);
        resItem.setText(text);
        if (makeGreen) {
            resItem.setForeground(Configs.KIT_GREEN_70);
        }
        resItem.setFont(SWTResourceManager.getFont("Courier New", GuiHelper.getFontHeight(resItem.getFont()),
                SWT.NORMAL));
        return resItem;
    }

    private TreeItem addTreeItem(Tree parent, String text) {
        TreeItem resItem = new TreeItem(parent, SWT.WRAP);
        resItem.setText(text);
        resItem.setForeground(Configs.KIT_GREEN_70);
        return resItem;
    }

    private String getSpacesForDisplayName(String currentDisplayName, double maxNumberOfChars) {
        String ret = "";
        for (int i = currentDisplayName.length(); i <= maxNumberOfChars; ++i) {
            ret += " ";
        }
        return ret;
    }

    private String getSpacesForResultValue(String value) {
        String ret = "";
        for (int i = value.length(); i <= MAXIMAL_NEEDED_SPACES; ++i) {
            ret += " ";
        }
        return ret;
    }

    public void updateUnitToReason(String unitToReason) {
        unitsToReason.stream().filter(unit -> unit.displayName.equals(unitToReason))
                .forEach(unit -> currentUnitToReason = unit);
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
