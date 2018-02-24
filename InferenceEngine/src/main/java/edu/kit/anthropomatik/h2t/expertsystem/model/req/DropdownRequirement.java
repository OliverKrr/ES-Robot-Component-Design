/*
 * Copyright 2018 Oliver Karrenbauer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation * files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, * * * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.kit.anthropomatik.h2t.expertsystem.model.req;

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
