package edu.kit.expertsystem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class DescriptionHelper {

    private static final int labelWidth = 80;
    private static final int descriptionWidth = 248;

    private static final int height = 60;

    private static final int labelX = 5;
    private static final int descriptionX = labelX + labelWidth + 5;

    private static final int basisY = 10;
    private static final int offsetY = height + 5;

    private final FormToolkit formToolkit;
    private final Composite composite;

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
        label.setBounds(labelX, y, labelWidth, height);
        formToolkit.adapt(label, false, false);
        label.setForeground(Configs.KIT_GREEN_70);
        label.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.KIT_GREEN_70.dispose();
            }
        });

        StyledText description = new StyledText(composite, SWT.BORDER | SWT.WRAP);
        description.setText(descriptionText);
        description.setEditable(false);
        description.setBounds(descriptionX, y, descriptionWidth, height);
        formToolkit.adapt(description);
        formToolkit.paintBordersFor(description);
    }

    public int getMaxYEnd() {
        return maxYEnd;
    }

}
