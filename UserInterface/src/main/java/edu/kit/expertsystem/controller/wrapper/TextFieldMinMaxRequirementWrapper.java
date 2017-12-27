package edu.kit.expertsystem.controller.wrapper;

import edu.kit.expertsystem.model.req.Requirement;
import org.eclipse.swt.widgets.Text;

public class TextFieldMinMaxRequirementWrapper extends RequirementWrapper {

    public Text minValue;
    public Text maxValue;

    public TextFieldMinMaxRequirementWrapper(Requirement requirement) {
        super(requirement);
    }

    @Override
    public String toString() {
        return "TextFieldMinMaxRequirementWrapper [minValue=" + minValue + ", maxValue=" + maxValue
                + ", toString()=" + super.toString() + "]";
    }

}
