package edu.kit.expertsystem.controller.wrapper;

import edu.kit.expertsystem.model.req.Requirement;

public class RequirementOnlyForSolutionWrapper extends RequirementWrapper {

    public RequirementOnlyForSolutionWrapper(Requirement requirement) {
        super(requirement);
    }

    @Override
    public String toString() {
        return "RequirementOnlyForSolutionWrapper{} " + super.toString();
    }
}
