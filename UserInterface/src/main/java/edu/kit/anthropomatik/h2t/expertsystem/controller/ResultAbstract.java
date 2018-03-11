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

import edu.kit.anthropomatik.h2t.expertsystem.GuiHelper;
import edu.kit.anthropomatik.h2t.expertsystem.controller.wrapper.ResultWrapper;
import edu.kit.anthropomatik.h2t.expertsystem.model.Component;
import edu.kit.anthropomatik.h2t.expertsystem.model.Result;
import edu.kit.anthropomatik.h2t.expertsystem.model.req.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public abstract class ResultAbstract {

    static final String RESULT_KEY = "ResultKey";
    private static final Logger logger = LogManager.getLogger(ResultAbstract.class);
    private static final int MAXIMAL_NEEDED_SPACES = 9;
    private final FormToolkit formToolkit;
    ResultWrapper resultWrapper;
    Map<String, ShowResult> showKeys = new HashMap<>();
    private DecimalFormat df = new DecimalFormat("#.####");
    private SelectionAdapter adaptSolutionListener;
    private SelectionAdapter showResultInNewWindowListener;
    private ResultWindow resultWindow;


    ResultAbstract(FormToolkit formToolkit, ResultWrapper resultWrapper) {
        this.formToolkit = formToolkit;
        this.resultWrapper = resultWrapper;
        df.setRoundingMode(RoundingMode.CEILING);
        resultWindow = new ResultWindow(formToolkit);
    }

    public final synchronized void clearLastResults() {
        if (adaptSolutionListener != null) {
            resultWrapper.orderBy.removeSelectionListener(adaptSolutionListener);
            resultWrapper.orderBy2.removeSelectionListener(adaptSolutionListener);
            resultWrapper.selectToShowButton.removeSelectionListener(adaptSolutionListener);
            resultWrapper.showOnlyDiffsCheckBox.removeSelectionListener(adaptSolutionListener);
            adaptSolutionListener = null;
        }
        if (showResultInNewWindowListener != null) {
            resultWrapper.table.removeSelectionListener(showResultInNewWindowListener);
            resultWrapper.tree.removeSelectionListener(showResultInNewWindowListener);
            showResultInNewWindowListener = null;
        }
        clearLastSpecificResults();
    }

    protected abstract void clearLastSpecificResults();

    public final synchronized void setResults() {
        if (resultWrapper.results == null) {
            return;
        }
        int oldSelectionOfOrderBy = resultWrapper.orderBy.getSelectionIndex();
        int oldSelectionOfOrderBy2 = resultWrapper.orderBy2.getSelectionIndex();
        Map<String, Boolean> selectShowMap = new HashMap<>();
        Arrays.stream(resultWrapper.selectTableToShow.getItems()).forEach(item -> selectShowMap.put(item.getText(),
                item.getChecked()));
        reset();
        logSolutionAndAddOrderByItems();
        selectOrderBy(oldSelectionOfOrderBy, oldSelectionOfOrderBy2);
        addSelectToShow(selectShowMap);

        adaptSolutionListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (resultWrapper.selectToShowButton.getSelection()) {
                    return;
                }
                sortSolution();
                setShowKeys();
                setNumberOfResults();
                showSolution();
                resultWrapper.numberOfResultsLabel.forceFocus();

                Event reloadSearchE = new Event();
                reloadSearchE.type = SWT.KeyUp;
                reloadSearchE.keyCode = ' ';
                resultWrapper.searchField.notifyListeners(SWT.KeyUp, reloadSearchE);
            }
        };

        resultWrapper.orderBy.addSelectionListener(adaptSolutionListener);
        resultWrapper.orderBy2.addSelectionListener(adaptSolutionListener);
        resultWrapper.selectToShowButton.addSelectionListener(adaptSolutionListener);
        resultWrapper.showOnlyDiffsCheckBox.addSelectionListener(adaptSolutionListener);
        resultWrapper.orderBy.notifyListeners(SWT.Selection, new Event());

        showResultInNewWindowListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (TableItem item : resultWrapper.table.getSelection()) {
                    openNewShell(item);
                }
                for (TreeItem item : resultWrapper.tree.getSelection()) {
                    if (item.getParentItem() != null) {
                        openNewShell(item.getParentItem());
                    } else {
                        openNewShell(item);
                    }
                }
            }

            private void openNewShell(Item item) {
                Result result = (Result) item.getData(RESULT_KEY);
                if (result == null) {
                    return;
                }
                resultWindow.showWindow(resultWrapper.componentToBeDesigned, result);
            }
        };
        resultWrapper.table.addSelectionListener(showResultInNewWindowListener);
        resultWrapper.tree.addSelectionListener(showResultInNewWindowListener);
    }

    private void setNumberOfResults() {
        resultWrapper.numberOfResultsLabel.setText("Number of results: " + resultWrapper.results.size());
        Rectangle oldBounds = resultWrapper.numberOfResultsLabel.getBounds();
        Point size = GuiHelper.getSizeOfControl(resultWrapper.numberOfResultsLabel);
        resultWrapper.numberOfResultsLabel.setBounds(oldBounds.x, oldBounds.y, size.x, oldBounds.height);
    }

    protected abstract void showSolution();

    private void sortSolution() {
        String currentSelection = resultWrapper.orderBy.getText();
        String currentSelection2 = resultWrapper.orderBy2.getText();

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

                if (Math.abs(valueFirst1 - valueSecond1) > Controller.EPSILON) {
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
    }

    private void selectOrderBy(int oldSelectionOfOrderBy, int oldSelectionOfOrderBy2) {
        if (resultWrapper.orderBy.getItemCount() > oldSelectionOfOrderBy) {
            if (oldSelectionOfOrderBy == -1) {
                oldSelectionOfOrderBy = 1;
            }
            resultWrapper.orderBy.select(oldSelectionOfOrderBy);
        }
        if (resultWrapper.orderBy2.getItemCount() > oldSelectionOfOrderBy2) {
            if (oldSelectionOfOrderBy2 == -1) {
                if (resultWrapper.orderBy2.getItemCount() > 2) {
                    oldSelectionOfOrderBy2 = 2;
                } else {
                    oldSelectionOfOrderBy2 = 0;
                }
            }
            resultWrapper.orderBy2.select(oldSelectionOfOrderBy2);
        }
    }

    private void addSelectToShow(Map<String, Boolean> selectShowMap) {
        Set<String> addedItems = new HashSet<>();
        for (Result result : resultWrapper.results) {
            for (Component component : result.components) {
                if (!addedItems.contains(component.nameOfComponent)) {
                    addedItems.add(component.nameOfComponent);
                    TableItem item = new TableItem(resultWrapper.selectTableToShow, SWT.NONE);
                    item.setText(component.nameOfComponent);
                    if (selectShowMap.containsKey(component.nameOfComponent)) {
                        item.setChecked(selectShowMap.get(component.nameOfComponent));
                    } else {
                        item.setChecked(component.showDefaultInResults);
                    }
                }
            }
            for (Requirement req : result.requirements) {
                if (req.resultIRI != null && !addedItems.contains(req.displayName)) {
                    addedItems.add(req.displayName);
                    TableItem item = new TableItem(resultWrapper.selectTableToShow, SWT.NONE);
                    item.setText(req.displayName);
                    if (selectShowMap.containsKey(req.displayName)) {
                        item.setChecked(selectShowMap.get(req.displayName));
                    } else {
                        item.setChecked(req.showDefaultInResults);
                    }
                }
            }
        }
    }

    private void logSolutionAndAddOrderByItems() {
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
    }

    private void reset() {
        resultWrapper.orderBy.removeAll();
        resultWrapper.orderBy2.removeAll();
        resultWrapper.displayNameToIriMap.clear();
        resultWrapper.selectTableToShow.removeAll();
        clearLastResults();
    }

    double getMaxNumberOfCharsForComp(Result result) {
        return result.components.stream().max(Comparator.comparingInt(val -> val.nameOfComponent.length())).orElseGet
                (Component::new).nameOfComponent.length();
    }

    String getNameForComponent(Component component, double maxNumberOfChars) {
        return "".equals(component.nameOfComponent) ? component.nameOfInstance : component.nameOfComponent + ": " +
                getSpacesForDisplayName(component.nameOfComponent, maxNumberOfChars) + component.nameOfInstance;
    }

    double getMaxNumberOfCharsForReq(Result result) {
        return result.requirements.stream().filter(req -> req.resultIRI != null).max(Comparator.comparingInt(val ->
                val.displayName.length())).orElseGet(CheckboxRequirement::new).displayName.length();
    }

    String getNameForReq(Requirement req, double maxNumberOfChars) {
        String resultValue = getResultValue(req);
        String unit = req.unit == null ? "" : req.unit;
        return req.displayName + ": " + getSpacesForDisplayName(req.displayName, maxNumberOfChars) + resultValue +
                getSpacesForResultValue(resultValue) + unit;
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

    String getResultValue(Requirement req) {
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

    private String getSpacesForDisplayName(String currentDisplayName, double maxNumberOfChars) {
        StringBuilder builder = new StringBuilder("");
        for (int i = currentDisplayName.length(); i <= maxNumberOfChars; ++i) {
            builder.append(" ");
        }
        return builder.toString();
    }

    public static String getSpacesForResultValue(String value) {
        StringBuilder builder = new StringBuilder("");
        for (int i = value.length(); i <= MAXIMAL_NEEDED_SPACES; ++i) {
            builder.append(" ");
        }
        return builder.toString();
    }

    private void setShowKeys() {
        showKeys.clear();
        if (resultWrapper.showOnlyDiffsCheckBox.getSelection()) {
            for (Result result : resultWrapper.results) {
                for (Component component : result.components) {
                    handleShow(component.nameOfComponent, component.nameOfInstance);
                }
                for (Requirement req : result.requirements) {
                    if (req.resultIRI != null) {
                        handleShow(req.displayName, getResultValue(req));
                    }
                }
            }
        }
        for (TableItem item : resultWrapper.selectTableToShow.getItems()) {
            if (showKeys.containsKey(item.getText())) {
                showKeys.get(item.getText()).showResult &= item.getChecked();
            } else {
                showKeys.put(item.getText(), new ShowResult("", item.getChecked()));
            }
        }
    }

    private void handleShow(String key, String value) {
        if (!showKeys.containsKey(key)) {
            showKeys.put(key, new ShowResult(value, false));
        }
        if (!showKeys.get(key).firstValue.equals(value)) {
            showKeys.get(key).showResult = true;
        }
    }

    protected static class ShowResult {
        String firstValue;
        boolean showResult;

        ShowResult(String firstValue, boolean showResult) {
            this.firstValue = firstValue;
            this.showResult = showResult;
        }
    }
}
