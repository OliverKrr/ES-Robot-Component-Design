package edu.kit.expertsystem.controller.wrapper;

import edu.kit.expertsystem.model.req.Requirement;
import org.eclipse.swt.widgets.Button;

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
