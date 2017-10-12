package edu.ontology.sac.model;

public class Result {
    public Motor motor = new Motor();
    public GearBox gearBox = new GearBox();

    public double maximalTorque = -1;
    public double maximalRotationSpeed = -1;
    public double weight = -1;

    @Override
    public String toString() {
        return "Result [motor=" + motor + ", gearBox=" + gearBox + ", maximalTorque=" + maximalTorque
                + ", maximalRotationSpeed=" + maximalRotationSpeed + ", weight=" + weight + "]";
    }
}
