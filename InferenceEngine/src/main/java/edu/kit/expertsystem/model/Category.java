package edu.kit.expertsystem.model;

public class Category {

    public String displayName;
    public int orderPosition;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
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
        Category other = (Category) obj;
        if (displayName == null) {
            if (other.displayName != null) {
                return false;
            }
        } else if (!displayName.equals(other.displayName)) {
            return false;
        }
        if (orderPosition != other.orderPosition) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Category [displayName=" + displayName + ", orderPosition=" + orderPosition + "]";
    }

}
