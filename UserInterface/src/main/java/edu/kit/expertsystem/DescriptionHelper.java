package edu.kit.expertsystem;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class DescriptionHelper {

    private static final float widthRatio = 0.27f;

    private static final int height = 55;

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

    public DescriptionHelper(FormToolkit formToolkit, Composite composite) {
        this.formToolkit = formToolkit;
        this.composite = composite;
    }

    public void createDescription(String labelText, String descriptionText, int rowNumber) {
        int y = basisY + offsetY * rowNumber;
        maxYEnd = Math.max(maxYEnd, y + height);

        Label label = new Label(composite, SWT.WRAP);
        label.setText(labelText);
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
