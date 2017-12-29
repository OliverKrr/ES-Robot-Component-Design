package edu.kit.expertsystem.controller.wrapper;

import edu.kit.expertsystem.model.req.RequirementDependencyCheckbox;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

import java.util.List;

public class RequirementDependencyCheckboxWrapper {

    public RequirementDependencyCheckbox requirementDependencyCheckbox;
    public Button fromCheckbox;
    public List<Control> toControls;

    @Override
    public String toString() {
        return "RequirementDependencyCheckboxWrapper [requirementDependencyCheckbox=" + requirementDependencyCheckbox
                + ", fromCheckbox=" + fromCheckbox + ", toControls=" + toControls + "]";
    }
}
