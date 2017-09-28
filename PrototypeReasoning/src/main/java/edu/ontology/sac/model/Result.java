package edu.ontology.sac.model;

public class Result {
    public Engine engine = new Engine();
    public Gear gear = new Gear();

    public double maximalTorque;
    public double maximalRotationSpeed;
    public double weight;

    @Override
    public String toString() {
        return "Result [engine=" + engine + ", gear=" + gear + ", maximalTorque=" + maximalTorque
                + ", maximalRotationSpeed=" + maximalRotationSpeed + ", weight=" + weight + "]";
    }
}
