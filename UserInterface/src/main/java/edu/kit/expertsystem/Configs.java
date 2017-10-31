package edu.kit.expertsystem;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class Configs {

    public static Color KIT_GREEN_100;
    public static Color KIT_GREEN_70;
    public static Color KIT_GREEN_50;
    public static Color KIT_GREEN_30;
    public static Color KIT_GREEN_15;

    public static Color KIT_GREY_100;
    public static Color KIT_GREY_70;
    public static Color KIT_GREY_50;
    public static Color KIT_GREY_30;
    public static Color KIT_GREY_15;

    public static void initConfig(Display display) {
        KIT_GREEN_100 = new Color(display, 0, 150, 130);
        KIT_GREEN_70 = new Color(display, 76, 181, 167);
        KIT_GREEN_50 = new Color(display, 127, 202, 192);
        KIT_GREEN_30 = new Color(display, 178, 223, 217);
        KIT_GREEN_15 = new Color(display, 217, 239, 236);

        KIT_GREY_100 = new Color(display, 0, 0, 0);
        KIT_GREY_70 = new Color(display, 77, 77, 77);
        KIT_GREY_50 = new Color(display, 128, 128, 128);
        KIT_GREY_30 = new Color(display, 179, 179, 179);
        KIT_GREY_15 = new Color(display, 217, 217, 217);
    }
}
