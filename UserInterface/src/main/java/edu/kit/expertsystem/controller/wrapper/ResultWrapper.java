package edu.kit.expertsystem.controller.wrapper;

import java.util.List;

import org.eclipse.swt.widgets.Tree;

import edu.kit.expertsystem.model.Result;

public class ResultWrapper {

    public List<Result> results;
    public Tree tree;

    @Override
    public String toString() {
        return "ResultWrapper [results=" + results + "]";
    }

}
