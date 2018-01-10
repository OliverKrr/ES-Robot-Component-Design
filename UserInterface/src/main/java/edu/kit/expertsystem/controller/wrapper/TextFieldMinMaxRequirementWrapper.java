package edu.kit.expertsystem.controller.wrapper;

import edu.kit.expertsystem.model.req.Requirement;
import org.eclipse.swt.widgets.Text;

import java.util.Objects;

public class TextFieldMinMaxRequirementWrapper extends RequirementWrapper {

    public Text minValue;
    public Text minValueOptimization;
    public Text maxValue;
    public Text maxValueOptimization;

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
        return Objects.equals(minValue, that.minValue) && Objects.equals(minValueOptimization, that
                .minValueOptimization) && Objects.equals(maxValue, that.maxValue) && Objects.equals
                (maxValueOptimization, that.maxValueOptimization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), minValue, minValueOptimization, maxValue, maxValueOptimization);
    }

    @Override
    public String toString() {
        return "TextFieldMinMaxRequirementWrapper{" + "minValue=" + minValue + ", minValueOptimization=" +
                minValueOptimization + ", maxValue=" + maxValue + ", maxValueOptimization=" + maxValueOptimization +
                "} " + super.toString();
    }

}
