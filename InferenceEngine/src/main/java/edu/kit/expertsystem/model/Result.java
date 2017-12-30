package edu.kit.expertsystem.model;

import edu.kit.expertsystem.model.req.Requirement;

import java.util.List;
import java.util.Objects;

public class Result {

    public List<Component> components;
    public List<Requirement> requirements;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Result result = (Result) o;
        return Objects.equals(components, result.components) && Objects.equals(requirements, result.requirements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(components, requirements);
    }

    @Override
    public String toString() {
        return "Result{" + "components=" + components + ", requirements=" + requirements + '}';
    }
}
