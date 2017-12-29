package edu.kit.expertsystem.controller.wrapper;

import edu.kit.expertsystem.model.req.Requirement;
import org.eclipse.swt.widgets.Text;

import java.util.Objects;

public class TextFieldMinMaxRequirementWrapper extends RequirementWrapper {

    public Text minValue;
    public Text maxValue;

    public TextFieldMinMaxRequirementWrapper(Requirement requirement) {
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
        TextFieldMinMaxRequirementWrapper that = (TextFieldMinMaxRequirementWrapper) o;
        return Objects.equals(minValue, that.minValue) && Objects.equals(maxValue, that.maxValue);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), minValue, maxValue);
    }

    @Override
    public String toString() {
        return "TextFieldMinMaxRequirementWrapper{" + "minValue=" + minValue + ", maxValue=" + maxValue + "} " +
                super.toString();
    }

}
