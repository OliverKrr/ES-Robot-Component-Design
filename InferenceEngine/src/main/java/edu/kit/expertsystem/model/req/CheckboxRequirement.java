package edu.kit.expertsystem.model.req;

public class CheckboxRequirement extends Requirement {

    public String reqIri;
    public boolean enable = true;

    public boolean defaultValue = true;
    public boolean value = defaultValue;
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
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (defaultValue ? 1231 : 1237);
        result = prime * result + (enable ? 1231 : 1237);
        result = prime * result + ((reqIri == null) ? 0 : reqIri.hashCode());
        result = prime * result + (this.result ? 1231 : 1237);
        result = prime * result + (value ? 1231 : 1237);
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
        CheckboxRequirement other = (CheckboxRequirement) obj;
        if (defaultValue != other.defaultValue) {
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
        if (result != other.result) {
            return false;
        }
        return value == other.value;
    }

    @Override
    public String toString() {
        return "CheckboxRequirement [reqIri=" + reqIri + ", enable=" + enable + ", defaultValue=" + defaultValue + "," +
                " value=" + value + ", result=" + result + ", toString()=" + super.toString() + "]";
    }

}
