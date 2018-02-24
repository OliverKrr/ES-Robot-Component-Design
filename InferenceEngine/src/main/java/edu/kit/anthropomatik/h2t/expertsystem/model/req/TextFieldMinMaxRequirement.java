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

public class TextFieldMinMaxRequirement extends Requirement {

    public double scaleFromOntologyToUI = 1;

    public String minIRI;
    public String maxIRI;
    public boolean enableMin = true;
    public boolean enableMax = true;

    public boolean isIntegerValue = false;

    public double defaultMin = 0;
    public double defaultMax = Double.MAX_VALUE;

    public double min = defaultMin;
    public double max = defaultMax;

    public double result = -1;
    public double acutalSatisficationToAllowedDeviation = 1;

    public boolean allowOptimization = false;
    public double deviationPercentage = 0;
    public int userWeight = 3;


    public TextFieldMinMaxRequirement() {
        super();
    }

    public TextFieldMinMaxRequirement(TextFieldMinMaxRequirement other) {
        super(other);
        scaleFromOntologyToUI = other.scaleFromOntologyToUI;
        minIRI = other.minIRI;
        maxIRI = other.maxIRI;
        enableMin = other.enableMin;
        enableMax = other.enableMax;
        isIntegerValue = other.isIntegerValue;
        defaultMin = other.defaultMin;
        defaultMax = other.defaultMax;
        min = other.min;
        max = other.max;
        result = other.result;
        acutalSatisficationToAllowedDeviation = other.acutalSatisficationToAllowedDeviation;
        allowOptimization = other.allowOptimization;
        deviationPercentage = other.deviationPercentage;
        userWeight = other.userWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        TextFieldMinMaxRequirement that = (TextFieldMinMaxRequirement) o;
        return Double.compare(that.scaleFromOntologyToUI, scaleFromOntologyToUI) == 0 && enableMin == that.enableMin
                && enableMax == that.enableMax && isIntegerValue == that.isIntegerValue && Double.compare(that
                .defaultMin, defaultMin) == 0 && Double.compare(that.defaultMax, defaultMax) == 0 && Double.compare
                (that.min, min) == 0 && Double.compare(that.max, max) == 0 && Double.compare(that.result, result) ==
                0 && Double.compare(that.acutalSatisficationToAllowedDeviation,
                acutalSatisficationToAllowedDeviation) == 0 && allowOptimization == that.allowOptimization && Double
                .compare(that.deviationPercentage, deviationPercentage) == 0 && userWeight == that.userWeight &&
                Objects.equals(minIRI, that.minIRI) && Objects.equals(maxIRI, that.maxIRI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), scaleFromOntologyToUI, minIRI, maxIRI, enableMin, enableMax,
                isIntegerValue, defaultMin, defaultMax, min, max, result, acutalSatisficationToAllowedDeviation,
                allowOptimization, deviationPercentage, userWeight);
    }

    @Override
    public String toString() {
        return "TextFieldMinMaxRequirement{" + "scaleFromOntologyToUI=" + scaleFromOntologyToUI + ", minIRI='" +
                minIRI + '\'' + ", maxIRI='" + maxIRI + '\'' + ", enableMin=" + enableMin + ", enableMax=" +
                enableMax + ", isIntegerValue=" + isIntegerValue + ", defaultMin=" + defaultMin + ", defaultMax=" +
                defaultMax + ", min=" + min + ", max=" + max + ", result=" + result + ", " +
                "acutalSatisficationToAllowedDeviation=" + acutalSatisficationToAllowedDeviation + ", " +
                "allowOptimization=" + allowOptimization + ", deviationPercentage=" + deviationPercentage + ", " +
                "userWeight=" + userWeight + "} " + super.toString();
    }
}
