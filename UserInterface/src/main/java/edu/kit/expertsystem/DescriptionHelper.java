package edu.kit.expertsystem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class DescriptionHelper {

    private static final int labelWidth = 137;
    private static final int labelHeight = 34;
    private static final int descriptionWidth = 191;
    private static final int descriptionHeight = 57;

    private static final int labelX = 10;
    private static final int descriptionX = 153;

    private static final int basisY = 161;
    private static final int offsetY = 63;

    private final FormToolkit formToolkit;
    private final Composite composite;

    public DescriptionHelper(FormToolkit formToolkit, Composite composite) {
        this.formToolkit = formToolkit;
        this.composite = composite;
    }

    public void createDescription(String labelText, String descriptionText, int rowNumber) {
        int y = basisY + offsetY * rowNumber;

        Label label = new Label(composite, SWT.NONE);
        label.setText(labelText);
        label.setBounds(labelX, y, labelWidth, labelHeight);
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
        description.setBounds(descriptionX, y, descriptionWidth, descriptionHeight);
        formToolkit.adapt(description);
        formToolkit.paintBordersFor(description);
    }

}
