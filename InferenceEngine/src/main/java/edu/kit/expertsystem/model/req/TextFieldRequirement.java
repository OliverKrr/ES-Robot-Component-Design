package edu.kit.expertsystem.model.req;

public class TextFieldRequirement extends Requirement {

    public double scaleFromOntologyToUI = 1;

    public String reqIri;
    public boolean enable = true;
    public RequirementType requirementType = RequirementType.EXACT;

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
        defaultValue = other.defaultValue;
        value = other.value;
        result = other.result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(defaultValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (enable ? 1231 : 1237);
        result = prime * result + ((reqIri == null) ? 0 : reqIri.hashCode());
        result = prime * result + ((requirementType == null) ? 0 : requirementType.hashCode());
        temp = Double.doubleToLongBits(this.result);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(scaleFromOntologyToUI);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(value);
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
        TextFieldRequirement other = (TextFieldRequirement) obj;
        if (Double.doubleToLongBits(defaultValue) != Double.doubleToLongBits(other.defaultValue)) {
            return false;
        }
        if (enable != other.enable) {
            return false;
        }
        if (reqIri == null) {
            if (other.reqIri != null) {
                return false;
            }
        } else if (!reqIri.equals(other.reqIri)) {
            return false;
        }
        if (requirementType != other.requirementType) {
            return false;
        }
        if (Double.doubleToLongBits(result) != Double.doubleToLongBits(other.result)) {
            return false;
        }
        if (Double.doubleToLongBits(scaleFromOntologyToUI) != Double
                .doubleToLongBits(other.scaleFromOntologyToUI)) {
            return false;
        }
        return Double.doubleToLongBits(value) == Double.doubleToLongBits(other.value);
    }

    @Override
    public String toString() {
        return "TextFieldRequirement [scaleFromOntologyToUI=" + scaleFromOntologyToUI + ", reqIri=" + reqIri
                + ", enable=" + enable + ", requirementType=" + requirementType + ", defaultValue="
                + defaultValue + ", value=" + value + ", result=" + result + ", toString()="
                + super.toString() + "]";
    }

}
