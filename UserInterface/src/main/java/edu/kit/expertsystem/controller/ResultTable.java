package edu.kit.expertsystem.controller;

import edu.kit.expertsystem.SolutionTab;
import edu.kit.expertsystem.controller.wrapper.ResultWrapper;
import edu.kit.expertsystem.model.Component;
import edu.kit.expertsystem.model.Result;
import edu.kit.expertsystem.model.req.Requirement;
import org.eclipse.swt.SWT;
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
        resultWrapper.table.forceFocus();
    }

    private void buildTable() {
        for (Result result : resultWrapper.results) {
            List<String> tableRow = new ArrayList<>();
            StringBuilder concatenationOfNamesBuilder = new StringBuilder();


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
            }

            TableItem item = new TableItem(resultWrapper.table, SWT.NONE);
            item.setText(tableRow.toArray(new String[0]));
            item.setData(SolutionTab.SEARCH_KEY, concatenationOfNamesBuilder.toString());
            item.setData(SolutionTab.COLOR_KEY, item.getForeground());
            item.setData(RESULT_KEY, result);
        }
        tableColumnMap.values().forEach(TableColumn::pack);
    }

}
