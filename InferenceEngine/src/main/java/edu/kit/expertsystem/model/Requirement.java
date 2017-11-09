package edu.kit.expertsystem.model;

public class Requirement {

    public String displayName;
    public String description;
    public String unit;
    public Category category;

    public String resultIRI;

    public Requirement() {
    }

    public Requirement(Requirement other) {
        displayName = other.displayName;
        description = other.description;
        unit = other.unit;
        category = other.category;
        resultIRI = other.resultIRI;
    }

    @Override
    public String toString() {
        return "Requirement [displayName=" + displayName + ", description=" + description + ", unit=" + unit
                + ", category=" + category + ", resultIRI=" + resultIRI + "]";
    }
}
