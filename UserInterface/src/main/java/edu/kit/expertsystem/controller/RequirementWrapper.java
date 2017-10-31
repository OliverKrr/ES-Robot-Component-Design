package edu.kit.expertsystem.controller;

import org.eclipse.swt.widgets.Text;

import edu.kit.expertsystem.model.Requirement;

public class RequirementWrapper {

    public Requirement requirement;
    public Text minValue;
    public Text maxValue;

    public RequirementWrapper(Requirement requirement) {
        this.requirement = requirement;
    }

    @Override
    public String toString() {
        return "RequirementWrapper [requirement=" + requirement + "]";
    }

}
