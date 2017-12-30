package edu.kit.expertsystem.model.req;

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
                0 && Objects.equals(minIRI, that.minIRI) && Objects.equals(maxIRI, that.maxIRI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), scaleFromOntologyToUI, minIRI, maxIRI, enableMin, enableMax,
                isIntegerValue, defaultMin, defaultMax, min, max, result);
    }

    @Override
    public String toString() {
        return "TextFieldMinMaxRequirement{" + "scaleFromOntologyToUI=" + scaleFromOntologyToUI + ", minIRI='" +
                minIRI + '\'' + ", maxIRI='" + maxIRI + '\'' + ", enableMin=" + enableMin + ", enableMax=" +
                enableMax + ", isIntegerValue=" + isIntegerValue + ", defaultMin=" + defaultMin + ", defaultMax=" +
                defaultMax + ", min=" + min + ", max=" + max + ", result=" + result + "} " + super.toString();
    }
}
