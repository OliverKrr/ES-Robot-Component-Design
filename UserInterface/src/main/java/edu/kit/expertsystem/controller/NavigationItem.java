package edu.kit.expertsystem.controller;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class NavigationItem {

    public String name;
    public Composite compositeToHandle;
    public Button item;
    public int defaultFontHeight;

    @Override
    public String toString() {
        return "NavigationItem [name=" + name + ", compositeToHandle=" + compositeToHandle + ", item=" + item + ", " +
                "defaultFontHeight=" + defaultFontHeight + "]";
    }

}
