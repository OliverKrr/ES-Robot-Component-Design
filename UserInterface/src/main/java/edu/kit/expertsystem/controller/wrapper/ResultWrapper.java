package edu.kit.expertsystem.controller.wrapper;

import edu.kit.expertsystem.model.Result;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ResultWrapper {

    public List<Result> results;
    public Tree tree;
    public Combo orderBy;
    public Combo orderBy2;
    public Text searchField;
    public Button showOnlyDiffsCheckBox;

    public Map<String, String> displayNameToIriMap = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ResultWrapper that = (ResultWrapper) o;
        return Objects.equals(results, that.results) && Objects.equals(tree, that.tree) && Objects.equals(orderBy,
                that.orderBy) && Objects.equals(orderBy2, that.orderBy2) && Objects.equals(searchField, that
                .searchField) && Objects.equals(showOnlyDiffsCheckBox, that.showOnlyDiffsCheckBox) && Objects.equals
                (displayNameToIriMap, that.displayNameToIriMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(results, tree, orderBy, orderBy2, searchField, showOnlyDiffsCheckBox, displayNameToIriMap);
    }

    @Override
    public String toString() {
        return "ResultWrapper{" + "results=" + results + ", tree=" + tree + ", orderBy=" + orderBy + ", orderBy2=" +
                orderBy2 + ", searchField=" + searchField + ", showOnlyDiffsCheckBox=" + showOnlyDiffsCheckBox + ", "
                + "displayNameToIriMap=" + displayNameToIriMap + '}';
    }
}
