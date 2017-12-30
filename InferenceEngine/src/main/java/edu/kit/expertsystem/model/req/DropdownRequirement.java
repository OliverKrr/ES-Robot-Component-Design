package edu.kit.expertsystem.model.req;

import java.util.Arrays;
import java.util.Objects;

public class DropdownRequirement extends Requirement {

    public String reqIri;
    public boolean enable = true;

    public String separator = ";";
    public String defaultValue = "";
    public String[] values = {defaultValue};
    public String selectedValue = defaultValue;
    public String result = defaultValue;

    public DropdownRequirement() {
        super();
    }

    public DropdownRequirement(DropdownRequirement other) {
        super(other);
        reqIri = other.reqIri;
        enable = other.enable;
        separator = other.separator;
        defaultValue = other.defaultValue;
        values = other.values;
        selectedValue = other.selectedValue;
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
        DropdownRequirement that = (DropdownRequirement) o;
        return enable == that.enable && Objects.equals(reqIri, that.reqIri) && Objects.equals(separator, that
                .separator) && Objects.equals(defaultValue, that.defaultValue) && Arrays.equals(values, that.values)
                && Objects.equals(selectedValue, that.selectedValue) && Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        int result1 = Objects.hash(super.hashCode(), reqIri, enable, separator, defaultValue, selectedValue, result);
        result1 = 31 * result1 + Arrays.hashCode(values);
        return result1;
    }

    @Override
    public String toString() {
        return "DropdownRequirement{" + "reqIri='" + reqIri + '\'' + ", enable=" + enable + ", separator='" +
                separator + '\'' + ", defaultValue='" + defaultValue + '\'' + ", values=" + Arrays.toString(values) +
                ", selectedValue='" + selectedValue + '\'' + ", result='" + result + '\'' + "} " + super.toString();
    }
}
