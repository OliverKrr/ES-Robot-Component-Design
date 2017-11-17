package edu.kit.expertsystem.model;

public class TextFieldRequirement extends Requirement {

    public double scaleFromOntologyToUI = 1;

    public String reqIri;
    public boolean enable = true;
    public RequirementType requirementType = RequirementType.EXACT;

    public double defaultValue = 0;
    public double value = defaultValue;
    public double result = -1;

    public TextFieldRequirement() {
        super();
    }

    public TextFieldRequirement(TextFieldRequirement other) {
        super(other);
        scaleFromOntologyToUI = other.scaleFromOntologyToUI;
        reqIri = other.reqIri;
        enable = other.enable;
        requirementType = other.requirementType;
        defaultValue = other.defaultValue;
        value = other.value;
        result = other.result;
    }

    @Override
    public String toString() {
        return "TextFieldRequirement [scaleFromOntologyToUI=" + scaleFromOntologyToUI + ", reqIri=" + reqIri
                + ", enable=" + enable + ", requirementType=" + requirementType + ", defaultValue="
                + defaultValue + ", value=" + value + ", result=" + result + ", toString()="
                + super.toString() + "]";
    }

}
