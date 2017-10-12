package edu.ontology.sac;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class Configs {

    private static Color KIT_GREEN_100 = null;
    private static Color KIT_GREEN_70 = null;
    private static Color KIT_GREEN_50 = null;
    private static Color KIT_GREEN_30 = null;
    private static Color KIT_GREEN_15 = null;

    static Color getKitGreen100(Display display) {
        if (KIT_GREEN_100 == null) {
            KIT_GREEN_100 = new Color(display, 0, 150, 130);
        }
        return KIT_GREEN_100;
    }

    static Color getKitGreen70(Display display) {
        if (KIT_GREEN_70 == null) {
            KIT_GREEN_70 = new Color(display, 76, 181, 167);
        }
        return KIT_GREEN_70;
    }

    static Color getKitGreen50(Display display) {
        if (KIT_GREEN_50 == null) {
            KIT_GREEN_50 = new Color(display, 127, 202, 192);
        }
        return KIT_GREEN_50;
    }

    static Color getKitGreen30(Display display) {
        if (KIT_GREEN_30 == null) {
            KIT_GREEN_30 = new Color(display, 178, 223, 217);
        }
        return KIT_GREEN_30;
    }

    static Color getKitGreen15(Display display) {
        if (KIT_GREEN_15 == null) {
            KIT_GREEN_15 = new Color(display, 217, 239, 236);
        }
        return KIT_GREEN_15;
    }

    private static Color KIT_GREY_100 = null;
    private static Color KIT_GREY_70 = null;
    private static Color KIT_GREY_50 = null;
    private static Color KIT_GREY_30 = null;
    private static Color KIT_GREY_15 = null;

    static Color getKitGrey100(Display display) {
        if (KIT_GREY_100 == null) {
            KIT_GREY_100 = new Color(display, 0, 0, 0);
        }
        return KIT_GREY_100;
    }

    static Color getKitGrey70(Display display) {
        if (KIT_GREY_70 == null) {
            KIT_GREY_70 = new Color(display, 77, 77, 77);
        }
        return KIT_GREY_70;
    }

    static Color getKitGrey50(Display display) {
        if (KIT_GREY_50 == null) {
            KIT_GREY_50 = new Color(display, 128, 128, 128);
        }
        return KIT_GREY_50;
    }

    static Color getKitGrey30(Display display) {
        if (KIT_GREY_30 == null) {
            KIT_GREY_30 = new Color(display, 179, 179, 179);
        }
        return KIT_GREY_30;
    }

    static Color getKitGrey15(Display display) {
        if (KIT_GREY_15 == null) {
            KIT_GREY_15 = new Color(display, 217, 217, 217);
        }
        return KIT_GREY_15;
    }
}
