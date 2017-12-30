package edu.kit.expertsystem.model.req;

import java.util.Objects;

public class Requirement {

    public String displayName;
    public String description;
    public String unit;
    public Category category;
    public int orderPosition;

    public String individualIRI;
    public String resultIRI;

    Requirement() {
    }

    Requirement(Requirement other) {
        displayName = other.displayName;
        description = other.description;
        unit = other.unit;
        category = other.category;
        individualIRI = other.individualIRI;
        resultIRI = other.resultIRI;
        orderPosition = other.orderPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Requirement that = (Requirement) o;
        return orderPosition == that.orderPosition && Objects.equals(displayName, that.displayName) && Objects.equals
                (description, that.description) && Objects.equals(unit, that.unit) && Objects.equals(category, that
                .category) && Objects.equals(individualIRI, that.individualIRI) && Objects.equals(resultIRI, that
                .resultIRI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, description, unit, category, orderPosition, individualIRI, resultIRI);
    }

    @Override
    public String toString() {
        return "Requirement{" + "displayName='" + displayName + '\'' + ", description='" + description + '\'' + ", "
                + "unit='" + unit + '\'' + ", category=" + category + ", orderPosition=" + orderPosition + ", " +
                "individualIRI='" + individualIRI + '\'' + ", resultIRI='" + resultIRI + '\'' + '}';
    }
}
