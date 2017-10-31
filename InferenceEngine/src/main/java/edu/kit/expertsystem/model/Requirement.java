package edu.kit.expertsystem.model;

public class Requirement {

    public String category;
    public String displayName;
    public String unit;
    public String description;

    public double scaleFromOntologyToUI = 1;

    public boolean enableMin;
    public boolean enableMax;

    public double min = 0;
    public double max = Double.MAX_VALUE;

    public double result = -1;

    public Requirement() {
    }

    public Requirement(Requirement other) {
        category = other.category;
        displayName = other.displayName;
        unit = other.unit;
        description = other.description;
        scaleFromOntologyToUI = other.scaleFromOntologyToUI;
        enableMin = other.enableMin;
        enableMax = other.enableMax;
        min = other.min;
        max = other.max;
        result = other.result;
    }

    @Override
    public String toString() {
        return "Requirement [category=" + category + ", displayName=" + displayName + ", unit=" + unit
                + ", description=" + description + ", scaleFromOntologyToUI=" + scaleFromOntologyToUI
                + ", enableMin=" + enableMin + ", enableMax=" + enableMax + ", min=" + min + ", max=" + max
                + ", result=" + result + "]";
    }

}
