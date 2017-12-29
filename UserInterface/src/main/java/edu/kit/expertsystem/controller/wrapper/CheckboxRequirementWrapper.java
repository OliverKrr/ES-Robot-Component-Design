package edu.kit.expertsystem.controller.wrapper;

import edu.kit.expertsystem.model.req.Requirement;
import org.eclipse.swt.widgets.Button;

import java.util.Objects;

public class CheckboxRequirementWrapper extends RequirementWrapper {

    public Button value;

    public CheckboxRequirementWrapper(Requirement requirement) {
        super(requirement);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        CheckboxRequirementWrapper that = (CheckboxRequirementWrapper) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), value);
    }

    @Override
    public String toString() {
        return "CheckboxRequirementWrapper{" + "value=" + value + "} " + super.toString();
    }

}
