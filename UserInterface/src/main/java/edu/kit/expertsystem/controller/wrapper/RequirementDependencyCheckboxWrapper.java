package edu.kit.expertsystem.controller.wrapper;

import edu.kit.expertsystem.model.req.RequirementDependencyCheckbox;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

import java.util.List;
import java.util.Objects;

public class RequirementDependencyCheckboxWrapper {

    public RequirementDependencyCheckbox requirementDependencyCheckbox;
    public Button fromCheckbox;
    public List<Control> toControls;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RequirementDependencyCheckboxWrapper that = (RequirementDependencyCheckboxWrapper) o;
        return Objects.equals(requirementDependencyCheckbox, that.requirementDependencyCheckbox) && Objects.equals
                (fromCheckbox, that.fromCheckbox) && Objects.equals(toControls, that.toControls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requirementDependencyCheckbox, fromCheckbox, toControls);
    }

    @Override
    public String toString() {
        return "RequirementDependencyCheckboxWrapper{" + "requirementDependencyCheckbox=" +
                requirementDependencyCheckbox + ", fromCheckbox=" + fromCheckbox + ", toControls=" + toControls + '}';
    }
}
