package edu.kit.expertsystem.model;

import edu.kit.expertsystem.model.req.Requirement;

import java.util.List;

public class Result {

    public List<Component> components;
    public List<Requirement> requirements;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((components == null) ? 0 : components.hashCode());
        result = prime * result + ((requirements == null) ? 0 : requirements.hashCode());
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
        Result other = (Result) obj;
        if (components == null) {
            if (other.components != null) {
                return false;
            }
        } else if (!components.equals(other.components)) {
            return false;
        }
        if (requirements == null) {
            return other.requirements == null;
        } else
            return requirements.equals(other.requirements);
    }

    @Override
    public String toString() {
        return "Result [components=" + components + ", requirements=" + requirements + "]";
    }

}
