package edu.kit.expertsystem.controller.wrapper;

import java.util.List;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

import edu.kit.expertsystem.model.req.RequirementDependencyCheckbox;

public class RequirementDependencyCheckboxWrapper {

    public RequirementDependencyCheckbox requirementDependencyCheckbox;
    public Button fromCheckbox;
    public List<Control> toControls;

    @Override
    public String toString() {
        return "RequirementDependencyCheckboxWrapper [requirementDependencyCheckbox="
                + requirementDependencyCheckbox + ", fromCheckbox=" + fromCheckbox + ", toControls="
                + toControls + "]";
    }
}
