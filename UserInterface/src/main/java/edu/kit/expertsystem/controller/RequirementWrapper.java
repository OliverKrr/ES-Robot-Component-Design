package edu.kit.expertsystem.controller;

import edu.kit.expertsystem.model.Requirement;

public class RequirementWrapper {

    public Requirement requirement;

    public RequirementWrapper(Requirement requirement) {
        this.requirement = requirement;
    }

    @Override
    public String toString() {
        return "RequirementWrapper [requirement=" + requirement + "]";
    }

}
