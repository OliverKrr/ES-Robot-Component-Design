package edu.kit.expertsystem.model.req;

import java.util.Objects;

public class TextFieldRequirement extends Requirement {

    public double scaleFromOntologyToUI = 1;

    public String reqIri;
    public boolean enable = true;
    public RequirementType requirementType = RequirementType.EXACT;
    public boolean isIntegerValue = false;

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
        isIntegerValue = other.isIntegerValue;
        defaultValue = other.defaultValue;
        value = other.value;
        result = other.result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        TextFieldRequirement that = (TextFieldRequirement) o;
        return Double.compare(that.scaleFromOntologyToUI, scaleFromOntologyToUI) == 0 && enable == that.enable &&
                isIntegerValue == that.isIntegerValue && Double.compare(that.defaultValue, defaultValue) == 0 &&
                Double.compare(that.value, value) == 0 && Double.compare(that.result, result) == 0 && Objects.equals
                (reqIri, that.reqIri) && requirementType == that.requirementType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), scaleFromOntologyToUI, reqIri, enable, requirementType, isIntegerValue,
                defaultValue, value, result);
    }

    @Override
    public String toString() {
        return "TextFieldRequirement{" + "scaleFromOntologyToUI=" + scaleFromOntologyToUI + ", reqIri='" + reqIri +
                '\'' + ", enable=" + enable + ", requirementType=" + requirementType + ", isIntegerValue=" +
                isIntegerValue + ", defaultValue=" + defaultValue + ", value=" + value + ", result=" + result + "} "
                + super.toString();
    }
}
