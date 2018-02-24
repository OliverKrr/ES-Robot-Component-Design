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
