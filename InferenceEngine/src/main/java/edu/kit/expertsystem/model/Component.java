package edu.kit.expertsystem.model;

public class Component {

    public String nameOfComponent = "";
    public String nameOfInstance = "";
    public int orderPosition = 0;

    @Override
    public String toString() {
        return "Component [nameOfComponent=" + nameOfComponent + ", nameOfInstance=" + nameOfInstance
                + ", orderPosition=" + orderPosition + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nameOfComponent == null) ? 0 : nameOfComponent.hashCode());
        result = prime * result + ((nameOfInstance == null) ? 0 : nameOfInstance.hashCode());
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
        Component other = (Component) obj;
        if (nameOfComponent == null) {
            if (other.nameOfComponent != null) {
                return false;
            }
        } else if (!nameOfComponent.equals(other.nameOfComponent)) {
            return false;
        }
        if (nameOfInstance == null) {
            if (other.nameOfInstance != null) {
                return false;
            }
        } else if (!nameOfInstance.equals(other.nameOfInstance)) {
            return false;
        }
        return orderPosition == other.orderPosition;
    }

}
