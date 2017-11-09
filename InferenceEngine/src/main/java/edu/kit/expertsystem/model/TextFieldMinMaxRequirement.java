package edu.kit.expertsystem.model;

public class TextFieldMinMaxRequirement extends Requirement {

    public double scaleFromOntologyToUI = 1;

    public String minIRI;
    public String maxIRI;
    public boolean enableMin;
    public boolean enableMax;

    public double min = 0;
    public double max = Double.MAX_VALUE;

    public String resultIRI;
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
        min = other.min;
        max = other.max;
        resultIRI = other.resultIRI;
        result = other.result;
    }

    @Override
    public String toString() {
        return "TextFieldMinMaxRequirement [scaleFromOntologyToUI=" + scaleFromOntologyToUI + ", minIRI="
                + minIRI + ", maxIRI=" + maxIRI + ", enableMin=" + enableMin + ", enableMax=" + enableMax
                + ", min=" + min + ", max=" + max + ", resultIRI=" + resultIRI + ", result=" + result
                + ", toString()=" + super.toString() + "]";
    }

}
