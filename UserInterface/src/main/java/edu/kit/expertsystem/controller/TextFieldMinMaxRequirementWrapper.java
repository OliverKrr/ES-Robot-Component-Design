package edu.kit.expertsystem.controller;

import org.eclipse.swt.widgets.Text;

import edu.kit.expertsystem.model.Requirement;

public class TextFieldMinMaxRequirementWrapper extends RequirementWrapper {

    public Text minValue;
    public Text maxValue;

    public TextFieldMinMaxRequirementWrapper(Requirement requirement) {
        super(requirement);
    }

    @Override
    public String toString() {
        return "TextFieldMinMaxRequirementWrapper [minValue=" + minValue + ", maxValue=" + maxValue
                + ", toString()=" + super.toString() + "]";
    }

}
