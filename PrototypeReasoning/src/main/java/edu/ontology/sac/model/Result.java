package edu.ontology.sac.model;

public class Result {
    public Motor motor = new Motor();
    public GearBox gearBox = new GearBox();

    public double maximalTorque;
    public double maximalRotationSpeed;
    public double weight;

    @Override
    public String toString() {
        return "Result [motor=" + motor + ", gearBox=" + gearBox + ", maximalTorque=" + maximalTorque
                + ", maximalRotationSpeed=" + maximalRotationSpeed + ", weight=" + weight + "]";
    }
}
