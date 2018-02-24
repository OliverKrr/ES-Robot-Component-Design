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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import java.util.ArrayList;
import java.util.List;

public class DescriptionHelper {

    private static final float widthRatio = 0.27f;

    private static final int height = 82;

    private static final int labelX = 1;
    private static final int descriptionXOffset = 3;
    private static final int descriptionOffsetXEnd = 9;

    private static final int basisY = 8;
    private static final int offsetY = height + 3;

    private final FormToolkit formToolkit;
    private final Composite composite;

    private List<Label> createdLabels = new ArrayList<>();
    private List<StyledText> createdDescriptions = new ArrayList<>();

    private int maxYEnd;

    DescriptionHelper(FormToolkit formToolkit, Composite composite) {
        this.formToolkit = formToolkit;
        this.composite = composite;
    }

    public void createDescription(String labelText, String descriptionText, int rowNumber) {
        int y = basisY + offsetY * rowNumber;
        maxYEnd = Math.max(maxYEnd, y + height);

        Label label = new Label(composite, SWT.WRAP);
        label.setText(labelText + ":");
        label.setBounds(labelX, y, 0, height);
        formToolkit.adapt(label, false, false);
        label.setForeground(Configs.KIT_GREEN_70);
        createdLabels.add(label);

        StyledText description = new StyledText(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        description.setText(descriptionText);
        description.setEditable(false);
        description.setBounds(0, y, 0, height);
        formToolkit.adapt(description, false, false);
        formToolkit.paintBordersFor(description);
        createdDescriptions.add(description);
    }

    public void updateSize(Rectangle bounds) {
        int labelWidth = Math.round(bounds.width * widthRatio);
        int descriptionWidth = Math.round(bounds.width * (1 - widthRatio)) - descriptionOffsetXEnd;
        int descriptionX = labelX + labelWidth + descriptionXOffset;

        for (Label label : createdLabels) {
            Rectangle oldBounds = label.getBounds();
            label.setBounds(oldBounds.x, oldBounds.y, labelWidth, oldBounds.height);
        }
        for (StyledText description : createdDescriptions) {
            Rectangle oldBounds = description.getBounds();
            description.setBounds(descriptionX, oldBounds.y, descriptionWidth, oldBounds.height);
        }
    }

    public int getMaxYEnd() {
        return maxYEnd;
    }

}
