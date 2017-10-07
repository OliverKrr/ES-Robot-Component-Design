package edu.ontology.sac.model;

public class Unit {

    public double min = 0;
    public double max = Double.MAX_VALUE;

    public Unit() {
    }

    public Unit(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String toString() {
        return "Unit [min=" + min + ", max=" + max + "]";
    }

}
