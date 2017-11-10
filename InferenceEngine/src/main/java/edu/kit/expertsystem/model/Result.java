package edu.kit.expertsystem.model;

import java.util.List;

public class Result {

    public List<Component> components;
    public List<Requirement> requirements;

    @Override
    public String toString() {
        return "Result [components=" + components + ", requirements=" + requirements + "]";
    }

}
