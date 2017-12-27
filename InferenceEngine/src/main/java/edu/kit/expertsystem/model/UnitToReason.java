package edu.kit.expertsystem.model;

public class UnitToReason {

    public String displayName;
    public String iriOfUnit;
    public String iriOfResultUnit;
    public int orderPosition;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
        result = prime * result + ((iriOfResultUnit == null) ? 0 : iriOfResultUnit.hashCode());
        result = prime * result + ((iriOfUnit == null) ? 0 : iriOfUnit.hashCode());
        result = prime * result + orderPosition;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UnitToReason other = (UnitToReason) obj;
        if (displayName == null) {
            if (other.displayName != null) {
                return false;
            }
        } else if (!displayName.equals(other.displayName)) {
            return false;
        }
        if (iriOfResultUnit == null) {
            if (other.iriOfResultUnit != null) {
                return false;
            }
        } else if (!iriOfResultUnit.equals(other.iriOfResultUnit)) {
            return false;
        }
        if (iriOfUnit == null) {
            if (other.iriOfUnit != null) {
                return false;
            }
        } else if (!iriOfUnit.equals(other.iriOfUnit)) {
            return false;
        }
        return orderPosition == other.orderPosition;
    }

    @Override
    public String toString() {
        return "UnitToReason [displayName=" + displayName + ", iriOfUnit=" + iriOfUnit + ", iriOfResultUnit="
                + iriOfResultUnit + ", orderPosition=" + orderPosition + "]";
    }

}
