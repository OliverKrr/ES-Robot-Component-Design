package edu.kit.expertsystem.model;

import java.util.List;

public class Result {
    public Motor motor = new Motor();
    public GearBox gearBox = new GearBox();

    public List<Requirement> requirements;

    @Override
    public String toString() {
        return "Result [motor=" + motor + ", gearBox=" + gearBox + ", requirements=" + requirements + "]";
    }
}
