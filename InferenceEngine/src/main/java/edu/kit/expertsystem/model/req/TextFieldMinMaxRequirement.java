package edu.kit.expertsystem.model.req;

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
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(defaultMax);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(defaultMin);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (enableMax ? 1231 : 1237);
        result = prime * result + (enableMin ? 1231 : 1237);
        temp = Double.doubleToLongBits(max);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((maxIRI == null) ? 0 : maxIRI.hashCode());
        temp = Double.doubleToLongBits(min);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((minIRI == null) ? 0 : minIRI.hashCode());
        temp = Double.doubleToLongBits(this.result);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(scaleFromOntologyToUI);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TextFieldMinMaxRequirement other = (TextFieldMinMaxRequirement) obj;
        if (Double.doubleToLongBits(defaultMax) != Double.doubleToLongBits(other.defaultMax)) {
            return false;
        }
        if (Double.doubleToLongBits(defaultMin) != Double.doubleToLongBits(other.defaultMin)) {
            return false;
        }
        if (enableMax != other.enableMax) {
            return false;
        }
        if (enableMin != other.enableMin) {
            return false;
        }
        if (Double.doubleToLongBits(max) != Double.doubleToLongBits(other.max)) {
            return false;
        }
        if (maxIRI == null) {
            if (other.maxIRI != null) {
                return false;
            }
        } else if (!maxIRI.equals(other.maxIRI)) {
            return false;
        }
        if (Double.doubleToLongBits(min) != Double.doubleToLongBits(other.min)) {
            return false;
        }
        if (minIRI == null) {
            if (other.minIRI != null) {
                return false;
            }
        } else if (!minIRI.equals(other.minIRI)) {
            return false;
        }
        if (Double.doubleToLongBits(result) != Double.doubleToLongBits(other.result)) {
            return false;
        }
        return Double.doubleToLongBits(scaleFromOntologyToUI) == Double
                .doubleToLongBits(other.scaleFromOntologyToUI);
    }

    @Override
    public String toString() {
        return "TextFieldMinMaxRequirement [scaleFromOntologyToUI=" + scaleFromOntologyToUI + ", minIRI="
                + minIRI + ", maxIRI=" + maxIRI + ", enableMin=" + enableMin + ", enableMax=" + enableMax
                + ", defaultMin=" + defaultMin + ", defaultMax=" + defaultMax + ", min=" + min + ", max="
                + max + ", result=" + result + ", toString()=" + super.toString() + "]";
    }

}
