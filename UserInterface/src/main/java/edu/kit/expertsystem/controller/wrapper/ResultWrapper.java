package edu.kit.expertsystem.controller.wrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import edu.kit.expertsystem.model.Result;

public class ResultWrapper {

    public List<Result> results;
    public Tree tree;
    public Combo orderBy;
    public Text searchField;

    public Map<String, String> displayNameToIriMap = new HashMap<>();

    @Override
    public String toString() {
        return "ResultWrapper [results=" + results + "]";
    }

}
