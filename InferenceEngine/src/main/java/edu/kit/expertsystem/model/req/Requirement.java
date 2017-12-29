package edu.kit.expertsystem.model.req;

public class Requirement {

    public String displayName;
    public String description;
    public String unit;
    public Category category;
    public int orderPosition;

    public String individualIRI;
    public String resultIRI;

    public Requirement() {
    }

    public Requirement(Requirement other) {
        displayName = other.displayName;
        description = other.description;
        unit = other.unit;
        category = other.category;
        individualIRI = other.individualIRI;
        resultIRI = other.resultIRI;
        orderPosition = other.orderPosition;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
        result = prime * result + ((individualIRI == null) ? 0 : individualIRI.hashCode());
        result = prime * result + orderPosition;
        result = prime * result + ((resultIRI == null) ? 0 : resultIRI.hashCode());
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
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
        Requirement other = (Requirement) obj;
        if (category == null) {
            if (other.category != null) {
                return false;
            }
        } else if (!category.equals(other.category)) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (displayName == null) {
            if (other.displayName != null) {
                return false;
            }
        } else if (!displayName.equals(other.displayName)) {
            return false;
        }
        if (individualIRI == null) {
            if (other.individualIRI != null) {
                return false;
            }
        } else if (!individualIRI.equals(other.individualIRI)) {
            return false;
        }
        if (orderPosition != other.orderPosition) {
            return false;
        }
        if (resultIRI == null) {
            if (other.resultIRI != null) {
                return false;
            }
        } else if (!resultIRI.equals(other.resultIRI)) {
            return false;
        }
        if (unit == null) {
            return other.unit == null;
        } else
            return unit.equals(other.unit);
    }

    @Override
    public String toString() {
        return "Requirement [displayName=" + displayName + ", description=" + description + ", unit=" + unit + ", " +
                "category=" + category + ", orderPosition=" + orderPosition + ", individualIRI=" + individualIRI + "," +
                " resultIRI=" + resultIRI + "]";
    }
}
