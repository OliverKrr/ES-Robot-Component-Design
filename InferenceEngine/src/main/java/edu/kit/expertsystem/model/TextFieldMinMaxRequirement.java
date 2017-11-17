package edu.kit.expertsystem.model;

public class TextFieldMinMaxRequirement extends Requirement {

    public double scaleFromOntologyToUI = 1;

    public String minIRI;
    public String maxIRI;
    public boolean enableMin = true;
    public boolean enableMax = true;

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
        defaultMin = other.defaultMin;
        defaultMax = other.defaultMax;
        min = other.min;
        max = other.max;
        result = other.result;
    }

    @Override
    public String toString() {
        return "TextFieldMinMaxRequirement [scaleFromOntologyToUI=" + scaleFromOntologyToUI + ", minIRI="
                + minIRI + ", maxIRI=" + maxIRI + ", enableMin=" + enableMin + ", enableMax=" + enableMax
                + ", defaultMin=" + defaultMin + ", defaultMax=" + defaultMax + ", min=" + min + ", max="
                + max + ", result=" + result + ", toString()=" + super.toString() + "]";
    }

}
