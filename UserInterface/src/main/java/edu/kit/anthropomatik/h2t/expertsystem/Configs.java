/*
 * Copyright 2018 Oliver Karrenbauer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation * files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, * * * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.kit.anthropomatik.h2t.expertsystem;

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

    public static void disposeConfig() {
        KIT_GREEN_100.dispose();
        KIT_GREEN_70.dispose();
        KIT_GREEN_50.dispose();
        KIT_GREEN_30.dispose();
        KIT_GREEN_15.dispose();

        KIT_GREY_100.dispose();
        KIT_GREY_70.dispose();
        KIT_GREY_50.dispose();
        KIT_GREY_30.dispose();
        KIT_GREY_15.dispose();
    }
}
