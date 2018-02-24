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

import edu.kit.anthropomatik.h2t.expertsystem.SolutionTab;
import edu.kit.anthropomatik.h2t.expertsystem.controller.wrapper.ResultWrapper;
import edu.kit.anthropomatik.h2t.expertsystem.model.Component;
import edu.kit.anthropomatik.h2t.expertsystem.model.Result;
import edu.kit.anthropomatik.h2t.expertsystem.model.req.Requirement;
import edu.kit.anthropomatik.h2t.expertsystem.model.req.TextFieldMinMaxRequirement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.FormToolkit;

import java.util.*;

public class ResultTable extends ResultAbstract {

    private Map<String, TableColumn> tableColumnMap = new HashMap<>();

    public ResultTable(FormToolkit formToolkit, ResultWrapper resultWrapper) {
        super(formToolkit, resultWrapper);
    }

    @Override
    public void clearLastSpecificResults() {
        resultWrapper.table.removeAll();
        Arrays.stream(resultWrapper.table.getColumns()).forEach(Widget::dispose);
        tableColumnMap.clear();
    }

    @Override
    protected void showSolution() {
        clearLastSpecificResults();
        buildTable();
    }

    private void buildTable() {
        for (Result result : resultWrapper.results) {
            List<String> tableRow = new ArrayList<>();
            StringBuilder concatenationOfNamesBuilder = new StringBuilder();
            Map<Integer, Color> foregroundColor = new HashMap<>();

            StringBuilder builder = new StringBuilder("");
            for (Component component : result.components) {
                if (!showKeys.isEmpty() && !showKeys.get(component.nameOfComponent).showResult) {
                    continue;
                }
                builder.append(getNameForComponent(component, 0).replaceAll(" ", ""));
                tableRow.add(component.nameOfInstance);

                if (!tableColumnMap.containsKey(component.nameOfComponent)) {
                    TableColumn tableColumn = new TableColumn(resultWrapper.table, SWT.NONE);
                    tableColumn.setText(component.nameOfComponent);
                    tableColumnMap.put(component.nameOfComponent, tableColumn);
                }
            }
            concatenationOfNamesBuilder.append(builder.toString());

            for (Requirement req : result.requirements) {
                if (req.resultIRI == null || (!showKeys.isEmpty() && !showKeys.get(req.displayName).showResult)) {
                    continue;
                }
                concatenationOfNamesBuilder.append(getNameForReq(req, 0).replaceAll(" ", ""));
                tableRow.add(getResultValue(req));

                String nameOfColumn = req.displayName;
                nameOfColumn += req.unit == null ? "" : " (" + req.unit + ")";
                if (!tableColumnMap.containsKey(nameOfColumn)) {
                    TableColumn tableColumn = new TableColumn(resultWrapper.table, SWT.NONE);
                    tableColumn.setText(nameOfColumn);
                    tableColumnMap.put(nameOfColumn, tableColumn);
                }
                if (req instanceof TextFieldMinMaxRequirement) {
                    TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req;
                    if (realReq.acutalSatisficationToAllowedDeviation < 1) {
                        if (realReq.acutalSatisficationToAllowedDeviation < 0.5) {
                            foregroundColor.put(tableRow.size() - 1, Display.getCurrent().getSystemColor(SWT
                                    .COLOR_RED));
                        } else {
                            foregroundColor.put(tableRow.size() - 1, Display.getCurrent().getSystemColor(SWT
                                    .COLOR_DARK_YELLOW));
                        }
                    }
                }
            }

            TableItem item = new TableItem(resultWrapper.table, SWT.NONE);
            item.setText(tableRow.toArray(new String[0]));
            item.setData(SolutionTab.SEARCH_KEY, concatenationOfNamesBuilder.toString());
            item.setData(RESULT_KEY, result);
            foregroundColor.forEach(item::setForeground);
        }
        tableColumnMap.values().forEach(TableColumn::pack);
    }

}
