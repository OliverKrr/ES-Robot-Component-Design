package edu.kit.expertsystem.model;

public class RequirementDependencyCheckbox {

    public Requirement from;
    public Requirement to;
    public boolean displayOnValue;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (displayOnValue ? 1231 : 1237);
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
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
        RequirementDependencyCheckbox other = (RequirementDependencyCheckbox) obj;
        if (displayOnValue != other.displayOnValue) {
            return false;
        }
        if (from == null) {
            if (other.from != null) {
                return false;
            }
        } else if (!from.equals(other.from)) {
            return false;
        }
        if (to == null) {
            if (other.to != null) {
                return false;
            }
        } else if (!to.equals(other.to)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RequirementDependencyCheckbox [from=" + from + ", to=" + to + ", displayOnValue="
                + displayOnValue + "]";
    }

}
