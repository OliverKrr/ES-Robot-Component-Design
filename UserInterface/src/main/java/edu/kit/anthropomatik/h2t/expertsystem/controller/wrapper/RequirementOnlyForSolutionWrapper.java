package edu.kit.anthropomatik.h2t.expertsystem.controller.wrapper;

import edu.kit.anthropomatik.h2t.expertsystem.model.req.Requirement;

public class RequirementOnlyForSolutionWrapper extends RequirementWrapper {

    public RequirementOnlyForSolutionWrapper(Requirement requirement) {
        super(requirement);
    }

    @Override
    public String toString() {
        return "RequirementOnlyForSolutionWrapper{} " + super.toString();
    }
}
