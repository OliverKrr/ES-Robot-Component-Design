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

}
