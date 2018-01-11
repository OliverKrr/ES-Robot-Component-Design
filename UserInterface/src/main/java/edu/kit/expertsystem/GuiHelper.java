package edu.kit.expertsystem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

public class GuiHelper {

    public static String getFontName(Font font) {
        for (FontData fontData : font.getFontData()) {
            if (fontData.getName() != null && !fontData.getName().equals("")) {
                return fontData.getName();
            }
        }
        return "Segoe UI";
    }

    public static int getFontHeight(Font font) {
        for (FontData fontData : font.getFontData()) {
            if (fontData.getHeight() != 0) {
                return fontData.getHeight();
            }
        }
        return 9;
    }

    public static Point getSizeOfControl(Control control) {
        return control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    }
}
