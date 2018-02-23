package edu.kit.anthropomatik.h2t.expertsystem.model.req;

import java.util.Objects;

public class Category {

    public String displayName;
    public String topic;
    public int orderPosition;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Category category = (Category) o;
        return orderPosition == category.orderPosition && Objects.equals(displayName, category.displayName) &&
                Objects.equals(topic, category.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, topic, orderPosition);
    }

    @Override
    public String toString() {
        return "Category{" + "displayName='" + displayName + '\'' + ", topic='" + topic + '\'' + ", orderPosition=" +
                orderPosition + '}';
    }
}
