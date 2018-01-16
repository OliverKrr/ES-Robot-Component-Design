package edu.kit.expertsystem.controller;

import edu.kit.expertsystem.Configs;
import edu.kit.expertsystem.GuiHelper;
import edu.kit.expertsystem.SolutionTab;
import edu.kit.expertsystem.controller.wrapper.ResultWrapper;
import edu.kit.expertsystem.model.Component;
import edu.kit.expertsystem.model.Result;
import edu.kit.expertsystem.model.req.Requirement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import java.util.HashMap;
import java.util.Map;

public class ResultTreeItem extends ResultAbstract {

    public ResultTreeItem(ResultWrapper resultWrapper) {
        super(resultWrapper);
    }

    @Override
    public void clearLastSpecificResults() {
        resultWrapper.tree.removeAll();
    }

    @Override
    protected void showSolution() {
        resultWrapper.tree.removeAll();
        buildTree();
        resultWrapper.tree.forceFocus();
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

    private void handleShow(Map<String, ShowResult> showKeys, String key, String value, boolean isComponent) {
        if (!showKeys.containsKey(key)) {
            showKeys.put(key, new ShowResult(value, false, isComponent));
        }
        if (!showKeys.get(key).firstValue.equals(value)) {
            showKeys.get(key).showResult = true;
        }
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
