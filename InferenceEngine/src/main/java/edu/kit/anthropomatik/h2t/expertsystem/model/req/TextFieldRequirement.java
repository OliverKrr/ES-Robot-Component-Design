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
