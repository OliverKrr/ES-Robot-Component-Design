package edu.kit.expertsystem.model;

import java.util.Objects;

public class ComponentToBeDesigned {

    public String displayName;
    public String iriOfUnit;
    public String iriOfResultUnit;
    public int orderPosition;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ComponentToBeDesigned that = (ComponentToBeDesigned) o;
        return orderPosition == that.orderPosition && Objects.equals(displayName, that.displayName) && Objects.equals
                (iriOfUnit, that.iriOfUnit) && Objects.equals(iriOfResultUnit, that.iriOfResultUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, iriOfUnit, iriOfResultUnit, orderPosition);
    }

    @Override
    public String toString() {
        return "ComponentToBeDesigned{" + "displayName='" + displayName + '\'' + ", iriOfUnit='" + iriOfUnit + '\'' + ", " +
                "iriOfResultUnit='" + iriOfResultUnit + '\'' + ", orderPosition=" + orderPosition + '}';
    }
}
