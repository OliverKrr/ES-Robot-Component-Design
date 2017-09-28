package edu.ontology.sac.model;

public class Unit {

    public double min;
    public double max;

    public Unit(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String toString() {
        return "Unit [min=" + min + ", max=" + max + "]";
    }

}
