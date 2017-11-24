package edu.kit.expertsystem;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

public class GuiHelper {

    public static String getFontName(Font font) {
        for (FontData fontData : font.getFontData()) {
            if (fontData.getName() != null && fontData.getName() != "") {
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

    public static Point getSizeOfText(Control control, String text) {
        GC gc = new GC(control);
        gc.setFont(control.getFont());
        Point size = gc.textExtent(text);
        gc.dispose();
        return size;
    }
}
