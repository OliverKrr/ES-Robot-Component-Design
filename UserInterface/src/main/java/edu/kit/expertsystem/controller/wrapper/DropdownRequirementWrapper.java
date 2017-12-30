package edu.kit.expertsystem.controller.wrapper;

import edu.kit.expertsystem.model.req.Requirement;
import org.eclipse.swt.widgets.Combo;

import java.util.Objects;

public class DropdownRequirementWrapper extends RequirementWrapper {

    public Combo values;

    public DropdownRequirementWrapper(Requirement requirement) {
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
        DropdownRequirementWrapper that = (DropdownRequirementWrapper) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), values);
    }

    @Override
    public String toString() {
        return "DropdownRequirementWrapper{" + "values=" + values + "} " + super.toString();
    }
}
