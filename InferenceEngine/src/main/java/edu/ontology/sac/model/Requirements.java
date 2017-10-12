package edu.ontology.sac.model;

public class Requirements {

    public Unit maximalTorque = new Unit();
    public Unit maximalRotationSpeed = new Unit();
    public Unit weight = new Unit();

    @Override
    public String toString() {
        return "Requirements [maximalTorque=" + maximalTorque + ", maximalRotationSpeed="
                + maximalRotationSpeed + ", weight=" + weight + "]";
    }
}
