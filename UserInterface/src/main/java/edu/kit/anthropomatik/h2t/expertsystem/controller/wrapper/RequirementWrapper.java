package edu.kit.anthropomatik.h2t.expertsystem.controller.wrapper;

import edu.kit.anthropomatik.h2t.expertsystem.model.req.Requirement;

import java.util.Objects;

public class RequirementWrapper {

    public Requirement requirement;

    RequirementWrapper(Requirement requirement) {
        this.requirement = requirement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RequirementWrapper that = (RequirementWrapper) o;
        return Objects.equals(requirement, that.requirement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requirement);
    }

    @Override
    public String toString() {
        return "RequirementWrapper{" + "requirement=" + requirement + '}';
    }
}
