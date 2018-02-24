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
package edu.kit.anthropomatik.h2t.expertsystem.controller.wrapper;

import edu.kit.anthropomatik.h2t.expertsystem.controller.ResultAbstract;
import edu.kit.anthropomatik.h2t.expertsystem.model.Result;
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

    public Label numberOfResultsLabel;
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
                .showOnlyDiffsCheckBox) && Objects.equals(numberOfResultsLabel, that.numberOfResultsLabel) && Objects
                .equals(tree, that.tree) && Objects.equals(table, that.table) && Objects.equals(displayNameToIriMap,
                that.displayNameToIriMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(results, resultShow, orderBy, orderBy2, searchField, selectToShowButton,
                selectTableToShow, showOnlyDiffsCheckBox, numberOfResultsLabel, tree, table, displayNameToIriMap);
    }

    @Override
    public String toString() {
        return "ResultWrapper{" + "results=" + results + ", resultShow=" + resultShow + ", orderBy=" + orderBy + ", "
                + "orderBy2=" + orderBy2 + ", searchField=" + searchField + ", selectToShowButton=" +
                selectToShowButton + ", selectTableToShow=" + selectTableToShow + ", showOnlyDiffsCheckBox=" +
                showOnlyDiffsCheckBox + ", numberOfResultsLabel=" + numberOfResultsLabel + ", tree=" + tree + ", " +
                "table=" + table + ", displayNameToIriMap=" + displayNameToIriMap + '}';
    }
}
