package edu.kit.expertsystem.controller;

import edu.kit.expertsystem.Configs;
import edu.kit.expertsystem.GuiHelper;
import edu.kit.expertsystem.SolutionTab;
import edu.kit.expertsystem.controller.wrapper.ResultWrapper;
import edu.kit.expertsystem.model.Component;
import edu.kit.expertsystem.model.Result;
import edu.kit.expertsystem.model.req.Requirement;
import edu.kit.expertsystem.model.req.TextFieldMinMaxRequirement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

public class ResultTreeItem extends ResultAbstract {

    public ResultTreeItem(FormToolkit formToolkit, ResultWrapper resultWrapper) {
        super(formToolkit, resultWrapper);
    }

    @Override
    public void clearLastSpecificResults() {
        resultWrapper.tree.removeAll();
    }

    @Override
    protected void showSolution() {
        clearLastSpecificResults();
        buildTree();
    }

    private void buildTree() {
        for (Result result : resultWrapper.results) {
            TreeItem resItem = addTreeItem(resultWrapper.tree);
            StringBuilder concatenationOfNamesBuilder = new StringBuilder();

            double maxNumberOfChars = getMaxNumberOfCharsForComp(result);

            StringBuilder builder = new StringBuilder("");
            for (Component component : result.components) {
                if (!showKeys.isEmpty() && !showKeys.get(component.nameOfComponent).showResult) {
                    continue;
                }
                String name = getNameForComponent(component, maxNumberOfChars);
                builder.append(name.replaceAll(" ", ""));
                addTreeItem(resItem, name, true, 1);
            }
            concatenationOfNamesBuilder.append(builder.toString());

            maxNumberOfChars = getMaxNumberOfCharsForReq(result);

            for (Requirement req : result.requirements) {
                if (req.resultIRI == null || (!showKeys.isEmpty() && !showKeys.get(req.displayName).showResult)) {
                    continue;
                }
                String name = getNameForReq(req, maxNumberOfChars);
                concatenationOfNamesBuilder.append(name.replaceAll(" ", ""));
                if (req instanceof TextFieldMinMaxRequirement) {
                    addTreeItem(resItem, name, false, ((TextFieldMinMaxRequirement) req)
                            .acutalSatisficationToAllowedDeviation);
                } else {
                    addTreeItem(resItem, name, false, 1);
                }
            }
            resItem.setData(SolutionTab.SEARCH_KEY, concatenationOfNamesBuilder.toString());
            resItem.setData(RESULT_KEY, result);
            resItem.setExpanded(true);
        }
    }

    private void addTreeItem(TreeItem parent, String text, boolean makeGreen, double
            acutalSatisficationToAllowedDeviation) {
        TreeItem resItem = new TreeItem(parent, SWT.WRAP);
        resItem.setText(text);
        if (makeGreen) {
            resItem.setForeground(Configs.KIT_GREEN_70);
        } else {
            if (acutalSatisficationToAllowedDeviation < 1) {
                if (acutalSatisficationToAllowedDeviation < 0.5) {
                    resItem.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
                } else {
                    resItem.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW));
                }
            }
        }
        resItem.setFont(SWTResourceManager.getFont("Courier New", GuiHelper.getFontHeight(resItem.getFont()), SWT
                .NORMAL));
    }

    private TreeItem addTreeItem(Tree parent) {
        TreeItem resItem = new TreeItem(parent, SWT.WRAP);
        resItem.setText("");
        return resItem;
    }
}
