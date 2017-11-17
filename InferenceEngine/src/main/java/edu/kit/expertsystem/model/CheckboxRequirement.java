package edu.kit.expertsystem.model;

public class CheckboxRequirement extends Requirement {

    public String reqIri;
    public boolean enable = true;

    public boolean defaultValue = true;
    public boolean value = defaultValue;

    public CheckboxRequirement() {
        super();
    }

    public CheckboxRequirement(CheckboxRequirement other) {
        super(other);
        reqIri = other.reqIri;
        enable = other.enable;
        defaultValue = other.defaultValue;
        value = other.value;
    }

}
