package edu.kit.expertsystem.controller.wrapper;

import edu.kit.expertsystem.model.req.Requirement;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import java.util.Objects;

public class TextFieldMinMaxRequirementWrapper extends RequirementWrapper {

    public static final int maxDeviation = 50;
    public static final int digitsDeviation = 1;
    public static final int minUserWeighting = 0;
    public static final int defaultUserWeighting = 3;
    public static final int maxUserWeighting = 5;

    public Text minValue;
    public Text minValueOptimization;
    public Label minApplied;
    public Text maxValue;
    public Text maxValueOptimization;
    public Label maxApplied;
    public Spinner deviation;
    public Combo userWeighting;

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
                .minValueOptimization) && Objects.equals(minApplied, that.minApplied) && Objects.equals(maxValue,
                that.maxValue) && Objects.equals(maxValueOptimization, that.maxValueOptimization) && Objects.equals
                (maxApplied, that.maxApplied) && Objects.equals(deviation, that.deviation) && Objects.equals
                (userWeighting, that.userWeighting);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), minValue, minValueOptimization, minApplied, maxValue,
                maxValueOptimization, maxApplied, deviation, userWeighting);
    }

    @Override
    public String toString() {
        return "TextFieldMinMaxRequirementWrapper{" + "minValue=" + minValue + ", minValueOptimization=" +
                minValueOptimization + ", minApplied=" + minApplied + ", maxValue=" + maxValue + ", " +
                "maxValueOptimization=" + maxValueOptimization + ", maxApplied=" + maxApplied + ", deviation=" +
                deviation + ", userWeighting=" + userWeighting + "} " + super.toString();
    }

}
