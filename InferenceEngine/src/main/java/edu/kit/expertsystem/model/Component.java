package edu.kit.expertsystem.model;

import java.util.Objects;

public class Component {

    public String nameOfComponent = "";
    public String nameOfInstance = "";
    public int orderPosition = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Component component = (Component) o;
        return orderPosition == component.orderPosition && Objects.equals(nameOfComponent, component.nameOfComponent)
                && Objects.equals(nameOfInstance, component.nameOfInstance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameOfComponent, nameOfInstance, orderPosition);
    }

    @Override
    public String toString() {
        return "Component{" + "nameOfComponent='" + nameOfComponent + '\'' + ", nameOfInstance='" + nameOfInstance +
                '\'' + ", orderPosition=" + orderPosition + '}';
    }
}
