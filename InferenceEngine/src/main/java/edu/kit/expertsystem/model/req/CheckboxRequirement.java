package edu.kit.expertsystem.model.req;

import java.util.Objects;

public class CheckboxRequirement extends Requirement {

    public String reqIri;
    public boolean enable = true;

    public boolean defaultValue = true;
    public boolean value = true;
    public boolean result = false;

    public CheckboxRequirement() {
        super();
    }

    public CheckboxRequirement(CheckboxRequirement other) {
        super(other);
        reqIri = other.reqIri;
        enable = other.enable;
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
        CheckboxRequirement that = (CheckboxRequirement) o;
        return enable == that.enable && defaultValue == that.defaultValue && value == that.value && result == that
                .result && Objects.equals(reqIri, that.reqIri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), reqIri, enable, defaultValue, value, result);
    }

    @Override
    public String toString() {
        return "CheckboxRequirement{" + "reqIri='" + reqIri + '\'' + ", enable=" + enable + ", defaultValue=" +
                defaultValue + ", value=" + value + ", selectedValue=" + result + "} " + super.toString();
    }
}
