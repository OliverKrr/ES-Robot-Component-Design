package edu.kit.expertsystem.model;

public class Requirement {

    public String category;
    public String displayName;
    public String unit;
    public String description;

    public boolean enableMin;
    public boolean enableMax;

    public double min = 0.0;
    public double max = Double.MAX_VALUE;

    public Requirement() {
    }

    @Override
    public String toString() {
        return "Requirement [category=" + category + ", displayName=" + displayName + ", unit=" + unit
                + ", description=" + description + ", min=" + min + ", max=" + max + "]";
    }

}
