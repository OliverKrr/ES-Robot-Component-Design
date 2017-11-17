package edu.kit.expertsystem.controller;

import org.eclipse.swt.widgets.Button;

import edu.kit.expertsystem.model.Requirement;

public class CheckboxRequirementWrapper extends RequirementWrapper {

    public Button value;

    public CheckboxRequirementWrapper(Requirement requirement) {
        super(requirement);
    }

    @Override
    public String toString() {
        return "CheckboxRequirementWrapper [value=" + value + ", toString()=" + super.toString() + "]";
    }

}
