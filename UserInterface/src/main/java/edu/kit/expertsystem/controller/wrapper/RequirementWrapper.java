package edu.kit.expertsystem.controller.wrapper;

import edu.kit.expertsystem.model.req.Requirement;

public class RequirementWrapper {

    public Requirement requirement;

    public RequirementWrapper(Requirement requirement) {
        this.requirement = requirement;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((requirement == null) ? 0 : requirement.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RequirementWrapper other = (RequirementWrapper) obj;
        if (requirement == null) {
            if (other.requirement != null) {
                return false;
            }
        } else if (!requirement.equals(other.requirement)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RequirementWrapper [requirement=" + requirement + "]";
    }

}
