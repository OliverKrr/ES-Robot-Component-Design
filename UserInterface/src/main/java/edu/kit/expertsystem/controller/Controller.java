package edu.kit.expertsystem.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.kit.expertsystem.Configs;
import edu.kit.expertsystem.GUI;
import edu.kit.expertsystem.MainReasoner;
import edu.kit.expertsystem.model.Component;
import edu.kit.expertsystem.model.Requirement;
import edu.kit.expertsystem.model.Result;
import edu.kit.expertsystem.model.TextFieldMinMaxRequirement;
import edu.kit.expertsystem.model.TextFieldRequirement;

public class Controller {

    private static final int MAXIMAL_NEEDED_SPACES = 6;

    private static final Logger logger = LogManager.getLogger(Controller.class);

    private GUI gui;
    private MainReasoner reasoner;

    private List<RequirementWrapper> requirementsWrapper;
    private ResultWrapper resultWrapper;

    public Controller(GUI gui) {
        this.gui = gui;
        reasoner = new MainReasoner();
    }

    public void initialize() {
        reasoner.initialize();

        List<Requirement> requirements = reasoner.getRequirements();
        requirementsWrapper = new ArrayList<>(requirements.size());
        for (Requirement req : requirements) {
            if (req instanceof TextFieldMinMaxRequirement) {
                requirementsWrapper.add(new TextFieldMinMaxRequirementWrapper(req));
            } else if (req instanceof TextFieldRequirement) {
                requirementsWrapper.add(new TextFieldRequirementWrapper(req));
            } else {
                throw new RuntimeException("Requirement class unknown: " + req.getClass());
            }
        }
        resultWrapper = new ResultWrapper();
    }

    public void reset() {
        for (RequirementWrapper req : requirementsWrapper) {
            if (req instanceof TextFieldMinMaxRequirementWrapper) {
                TextFieldMinMaxRequirement textFieldReq = (TextFieldMinMaxRequirement) req.requirement;
                textFieldReq.min = textFieldReq.defaultMin;
                textFieldReq.max = textFieldReq.defaultMax;
                textFieldReq.result = -1;
            } else if (req instanceof TextFieldRequirementWrapper) {
                TextFieldRequirement textFieldReq = (TextFieldRequirement) req.requirement;
                textFieldReq.value = textFieldReq.defaultValue;
                textFieldReq.result = -1;
            } else {
                throw new RuntimeException("Requirement class unknown: " + req.getClass());
            }
        }
        reasoner.prepareReasoning();
    }

    public void initialStartForCaching() {
        reasoner.startReasoning(parseToRequirements());
    }

    private List<Requirement> parseToRequirements() {
        List<Requirement> requirements = new ArrayList<>(requirementsWrapper.size());
        for (RequirementWrapper req : requirementsWrapper) {
            requirements.add(req.requirement);
        }
        return requirements;
    }

    public void parseRequirements() {
        resultWrapper.tree.removeAll();

        for (RequirementWrapper req : requirementsWrapper) {
            if (req instanceof TextFieldMinMaxRequirementWrapper) {
                TextFieldMinMaxRequirementWrapper textFieldReqWrapper = (TextFieldMinMaxRequirementWrapper) req;
                TextFieldMinMaxRequirement textFieldReq = (TextFieldMinMaxRequirement) req.requirement;

                textFieldReq.min = parseDouble(textFieldReqWrapper.minValue, textFieldReq.defaultMin)
                        / textFieldReq.scaleFromOntologyToUI;
                textFieldReq.max = parseDouble(textFieldReqWrapper.maxValue, textFieldReq.defaultMax)
                        / textFieldReq.scaleFromOntologyToUI;
            } else if (req instanceof TextFieldRequirementWrapper) {
                TextFieldRequirementWrapper textFieldReqWrapper = (TextFieldRequirementWrapper) req;
                TextFieldRequirement textFieldReq = (TextFieldRequirement) req.requirement;

                textFieldReq.value = parseDouble(textFieldReqWrapper.value, textFieldReq.defaultValue)
                        / textFieldReq.scaleFromOntologyToUI;
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
                gui.setErrorText(message);
                logger.error(message);
            }
        }
        return defaultValue;
    }

    public void reason() {
        resultWrapper.results = reasoner.startReasoning(parseToRequirements());
        gui.notifySolutionIsReady();
    }

    public void setResults() {
        addTreeItem(resultWrapper.tree, "Number of results: " + resultWrapper.results.size());
        for (Result result : resultWrapper.results) {
            TreeItem resItem = addTreeItem(resultWrapper.tree, "");

            double maxNumberOfChars = 0;
            for (Component component : result.components) {
                maxNumberOfChars = Math.max(maxNumberOfChars, component.nameOfComponent.length());
            }

            for (Component component : result.components) {
                String name = "".equals(component.nameOfComponent) ? component.nameOfInstance
                        : component.nameOfComponent + ":"
                                + getSpacesForDisplayName(component.nameOfComponent, maxNumberOfChars)
                                + component.nameOfInstance;
                addTreeItem(resItem, name, true);
            }

            maxNumberOfChars = 0;
            for (Requirement req : result.requirements) {
                maxNumberOfChars = Math.max(maxNumberOfChars, req.displayName.length());
            }

            for (Requirement req : result.requirements) {
                double resultValue = -1;
                if (req instanceof TextFieldMinMaxRequirement) {
                    TextFieldMinMaxRequirement textFieldReq = (TextFieldMinMaxRequirement) req;
                    resultValue = textFieldReq.result * textFieldReq.scaleFromOntologyToUI;
                } else if (req instanceof TextFieldRequirement) {
                    TextFieldRequirement textFieldReq = (TextFieldRequirement) req;
                    resultValue = textFieldReq.result * textFieldReq.scaleFromOntologyToUI;
                } else {
                    throw new RuntimeException("Requirement class unknown: " + req.getClass());
                }
                addTreeItem(resItem,
                        req.displayName + getSpacesForDisplayName(req.displayName, maxNumberOfChars) + " "
                                + resultValue + getSpacesForResultValue(resultValue) + req.unit,
                        false);
            }
            resItem.setExpanded(true);
        }
    }

    private TreeItem addTreeItem(TreeItem parent, String text, boolean makeGreen) {
        TreeItem resItem = new TreeItem(parent, SWT.WRAP);
        resItem.setText(text);
        if (makeGreen) {
            resItem.setForeground(Configs.KIT_GREEN_70);
        }
        resItem.setFont(SWTResourceManager.getFont("Courier New", 9, SWT.NORMAL));
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

    private String getSpacesForResultValue(double value) {
        String ret = "";
        String str = String.valueOf(value);
        for (int i = str.length(); i <= MAXIMAL_NEEDED_SPACES; ++i) {
            ret += " ";
        }
        return ret;
    }

    public List<RequirementWrapper> getRequirementsWrapper() {
        return requirementsWrapper;
    }

    public ResultWrapper getResultWrapper() {
        return resultWrapper;
    }

}
