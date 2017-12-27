package edu.kit.expertsystem.controller.wrapper;

import edu.kit.expertsystem.model.req.Requirement;
import org.eclipse.swt.widgets.Text;

public class TextFieldRequirementWrapper extends RequirementWrapper {

    public Text value;

    public TextFieldRequirementWrapper(Requirement requirement) {
        super(requirement);
    }

    @Override
    public String toString() {
        return "TextFieldRequirementWrapper [value=" + value + ", toString()=" + super.toString() + "]";
    }

}
