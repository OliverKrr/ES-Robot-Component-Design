package edu.kit.expertsystem.controller.wrapper;

import org.eclipse.swt.widgets.Button;

import edu.kit.expertsystem.model.req.Requirement;

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
