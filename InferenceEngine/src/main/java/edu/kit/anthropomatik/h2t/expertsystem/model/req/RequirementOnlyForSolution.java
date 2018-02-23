package edu.kit.anthropomatik.h2t.expertsystem.model.req;

import java.util.Objects;

public class RequirementOnlyForSolution extends Requirement {

    public double scaleFromOntologyToUI = 1;

    public double result = -1;

    public RequirementOnlyForSolution() {
        super();
    }

    public RequirementOnlyForSolution(RequirementOnlyForSolution other) {
        super(other);
        scaleFromOntologyToUI = other.scaleFromOntologyToUI;
        result = other.result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        RequirementOnlyForSolution that = (RequirementOnlyForSolution) o;
        return Double.compare(that.scaleFromOntologyToUI, scaleFromOntologyToUI) == 0 && Double.compare(that.result,
                result) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), scaleFromOntologyToUI, result);
    }

    @Override
    public String toString() {
        return "RequirementOnlyForSolution{" + "scaleFromOntologyToUI=" + scaleFromOntologyToUI + ", result=" +
                result + "} " + super.toString();
    }
}
