package edu.kit.expertsystem.controller;

import org.eclipse.swt.widgets.Text;

import edu.kit.expertsystem.model.Requirement;

public class TextFieldRequirementWrapper extends RequirementWrapper {

    public Text value;

    public TextFieldRequirementWrapper(Requirement requirement) {
        super(requirement);
    }

    @Override
    public String toString() {
        return "TextFieldRequirementWrapper [value=" + value + ", toString()=" + super.toString() + "]";
    }

}
