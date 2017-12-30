package edu.kit.expertsystem.controller;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import java.util.Objects;

public class NavigationItem {

    public String name;
    public Composite compositeToHandle;
    public Button item;
    public int defaultFontHeight;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NavigationItem that = (NavigationItem) o;
        return defaultFontHeight == that.defaultFontHeight && Objects.equals(name, that.name) && Objects.equals
                (compositeToHandle, that.compositeToHandle) && Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, compositeToHandle, item, defaultFontHeight);
    }

    @Override
    public String toString() {
        return "NavigationItem{" + "name='" + name + '\'' + ", compositeToHandle=" + compositeToHandle + ", item=" +
                item + ", defaultFontHeight=" + defaultFontHeight + '}';
    }
}
