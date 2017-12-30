package edu.kit.expertsystem.model.req;

import java.util.Objects;

public class RequirementDependencyCheckbox {

    public Requirement from;
    public Requirement to;
    public boolean displayOnValue;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RequirementDependencyCheckbox that = (RequirementDependencyCheckbox) o;
        return displayOnValue == that.displayOnValue && Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, displayOnValue);
    }

    @Override
    public String toString() {
        return "RequirementDependencyCheckbox{" + "from=" + from + ", to=" + to + ", displayOnValue=" +
                displayOnValue + '}';
    }
}
