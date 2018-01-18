package edu.kit.expertsystem.controller.wrapper;

import edu.kit.expertsystem.controller.ResultAbstract;
import edu.kit.expertsystem.model.Result;
import org.eclipse.swt.widgets.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ResultWrapper {

    public List<Result> results;
    public ResultAbstract resultShow;

    public Combo orderBy;
    public Combo orderBy2;
    public Text searchField;
    public Button selectToShowButton;
    public Table selectTableToShow;
    public Button showOnlyDiffsCheckBox;

    public Tree tree;
    public Table table;

    public Map<String, String> displayNameToIriMap = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ResultWrapper that = (ResultWrapper) o;
        return Objects.equals(results, that.results) && Objects.equals(resultShow, that.resultShow) && Objects.equals
                (orderBy, that.orderBy) && Objects.equals(orderBy2, that.orderBy2) && Objects.equals(searchField,
                that.searchField) && Objects.equals(selectToShowButton, that.selectToShowButton) && Objects.equals
                (selectTableToShow, that.selectTableToShow) && Objects.equals(showOnlyDiffsCheckBox, that
                .showOnlyDiffsCheckBox) && Objects.equals(tree, that.tree) && Objects.equals(table, that.table) &&
                Objects.equals(displayNameToIriMap, that.displayNameToIriMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(results, resultShow, orderBy, orderBy2, searchField, selectToShowButton,
                selectTableToShow, showOnlyDiffsCheckBox, tree, table, displayNameToIriMap);
    }

    @Override
    public String toString() {
        return "ResultWrapper{" + "results=" + results + ", resultShow=" + resultShow + ", orderBy=" + orderBy + ", "
                + "orderBy2=" + orderBy2 + ", searchField=" + searchField + ", selectToShowButton=" +
                selectToShowButton + ", selectTableToShow=" + selectTableToShow + ", showOnlyDiffsCheckBox=" +
                showOnlyDiffsCheckBox + ", tree=" + tree + ", table=" + table + ", displayNameToIriMap=" +
                displayNameToIriMap + '}';
    }
}
