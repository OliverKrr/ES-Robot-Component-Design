package edu.kit.expertsystem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.kit.expertsystem.controller.RequirementWrapper;

public class RequirementsHelper {

    private static final int displayNameWidth = 182;
    private static final int minMaxWidth = 40;
    private static final int unitWidth = 44;

    private static final int height = 23;

    private static final int displayNameX = 10;
    private static final int minX = 198;
    private static final int unitForMinX = 244;
    private static final int maxX = 294;
    private static final int unitForMaxX = 340;

    private static final int basisY1 = 36;
    private static final int basisY2 = 39;
    private static final int offsetY = 36;

    private final FormToolkit formToolkit;
    private final Composite composite;

    public RequirementsHelper(FormToolkit formToolkit, Composite composite) {
        this.formToolkit = formToolkit;
        this.composite = composite;
    }

    public void createRequirement(RequirementWrapper requirementWrapper, int rowNumber) {
        int y1 = basisY1 + offsetY * rowNumber;
        int y2 = basisY2 + offsetY * rowNumber;

        Label displayName = new Label(composite, SWT.NONE);
        displayName.setBounds(displayNameX, y1, displayNameWidth, height);
        displayName.setText(requirementWrapper.requirement.displayName);
        formToolkit.adapt(displayName, false, false);
        displayName.setForeground(Configs.KIT_GREEN_70);

        requirementWrapper.minValue = new Text(composite, SWT.BORDER);
        requirementWrapper.minValue.setEnabled(requirementWrapper.requirement.enableMin);
        requirementWrapper.minValue.setMessage("min");
        requirementWrapper.minValue.setBounds(minX, y1, minMaxWidth, height);
        formToolkit.adapt(requirementWrapper.minValue, true, true);

        Label unitForMin = new Label(composite, SWT.NONE);
        unitForMin.setText(requirementWrapper.requirement.unit);
        unitForMin.setBounds(unitForMinX, y2, unitWidth, height);
        formToolkit.adapt(unitForMin, false, false);

        requirementWrapper.maxValue = new Text(composite, SWT.BORDER);
        requirementWrapper.maxValue.setEnabled(requirementWrapper.requirement.enableMax);
        requirementWrapper.maxValue.setMessage("max");
        requirementWrapper.maxValue.setBounds(maxX, y1, minMaxWidth, height);
        formToolkit.adapt(requirementWrapper.maxValue, true, true);

        Label unitForMax = new Label(composite, SWT.NONE);
        unitForMax.setText(requirementWrapper.requirement.unit);
        unitForMax.setBounds(unitForMaxX, y2, unitWidth, height);
        formToolkit.adapt(unitForMax, false, false);
    }

}
