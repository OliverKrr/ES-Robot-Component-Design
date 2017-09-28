package edu.ontology.sac.model;

public class Requirements {

    public Unit maximalTorque;
    public Unit maximalRotationSpeed;
    public Unit weight;

    @Override
    public String toString() {
        return "Requirements [maximalTorque=" + maximalTorque + ", maximalRotationSpeed="
                + maximalRotationSpeed + ", weight=" + weight + "]";
    }
}
