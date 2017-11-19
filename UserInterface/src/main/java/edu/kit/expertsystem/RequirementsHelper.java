package edu.kit.expertsystem;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.kit.expertsystem.controller.CheckboxRequirementWrapper;
import edu.kit.expertsystem.controller.RequirementDependencyCheckboxWrapper;
import edu.kit.expertsystem.controller.RequirementWrapper;
import edu.kit.expertsystem.controller.TextFieldMinMaxRequirementWrapper;
import edu.kit.expertsystem.controller.TextFieldRequirementWrapper;
import edu.kit.expertsystem.model.CheckboxRequirement;
import edu.kit.expertsystem.model.TextFieldMinMaxRequirement;
import edu.kit.expertsystem.model.TextFieldRequirement;

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

    private List<Control> createdControls;
    private Button createdButton;

    private int y1;
    private int y2;

    public RequirementsHelper(FormToolkit formToolkit, Composite composite) {
        this.formToolkit = formToolkit;
        this.composite = composite;
    }

    public void createRequirement(RequirementWrapper requirementWrapper,
            List<RequirementDependencyCheckboxWrapper> requirementDependencyWrappers, int rowNumber) {
        createdControls = new ArrayList<>();
        createdButton = null;
        y1 = basisY1 + offsetY * rowNumber;
        y2 = basisY2 + offsetY * rowNumber;
        createCommonRequirement(requirementWrapper);

        if (requirementWrapper instanceof TextFieldMinMaxRequirementWrapper) {
            createTextFieldMinMaxRequirement((TextFieldMinMaxRequirementWrapper) requirementWrapper);
        } else if (requirementWrapper instanceof TextFieldRequirementWrapper) {
            createTextFieldRequirement((TextFieldRequirementWrapper) requirementWrapper);
        } else if (requirementWrapper instanceof CheckboxRequirementWrapper) {
            createCheckboxRequirement((CheckboxRequirementWrapper) requirementWrapper);
        } else {
            throw new RuntimeException("Requirement class unknown: " + requirementWrapper.getClass());
        }

        requirementDependencyWrappers.forEach(reqDep -> {
            if (reqDep.requirementDependencyCheckbox.from.equals(requirementWrapper.requirement)) {
                reqDep.fromCheckbox = createdButton;
            }
            if (reqDep.requirementDependencyCheckbox.to.equals(requirementWrapper.requirement)) {
                reqDep.toControls = createdControls;
            }
        });
    }

    private void createCommonRequirement(RequirementWrapper requirementWrapper) {
        // \t funktioniert hier -> mehr siehe Arbeitsblatt
        Label displayName = new Label(composite, SWT.NONE);
        displayName.setBounds(displayNameX, y1, displayNameWidth, height);
        displayName.setText(requirementWrapper.requirement.displayName);
        formToolkit.adapt(displayName, false, false);
        displayName.setForeground(Configs.KIT_GREEN_70);
        createdControls.add(displayName);
    }

    private void createTextFieldMinMaxRequirement(TextFieldMinMaxRequirementWrapper requirementWrapper) {
        TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) requirementWrapper.requirement;

        requirementWrapper.minValue = new Text(composite, SWT.BORDER);
        requirementWrapper.minValue.setEnabled(realReq.enableMin);
        requirementWrapper.minValue.setMessage("min");
        requirementWrapper.minValue.setToolTipText("min");
        if (realReq.defaultMin != 0) {
            requirementWrapper.minValue.setText(String.valueOf(realReq.defaultMin));
        }
        requirementWrapper.minValue.setBounds(minX, y1, minMaxWidth, height);
        formToolkit.adapt(requirementWrapper.minValue, true, true);
        createdControls.add(requirementWrapper.minValue);

        if (requirementWrapper.requirement.unit != null) {
            Label unitForMin = new Label(composite, SWT.NONE);
            unitForMin.setText(requirementWrapper.requirement.unit);
            unitForMin.setBounds(unitForMinX, y2, unitWidth, height);
            formToolkit.adapt(unitForMin, false, false);
            createdControls.add(unitForMin);
        }

        requirementWrapper.maxValue = new Text(composite, SWT.BORDER);
        requirementWrapper.maxValue.setEnabled(realReq.enableMax);
        requirementWrapper.maxValue.setMessage("max");
        requirementWrapper.maxValue.setToolTipText("max");
        if (realReq.defaultMax != Double.MAX_VALUE) {
            requirementWrapper.maxValue.setText(String.valueOf(realReq.defaultMax));
        }
        requirementWrapper.maxValue.setBounds(maxX, y1, minMaxWidth, height);
        formToolkit.adapt(requirementWrapper.maxValue, true, true);
        createdControls.add(requirementWrapper.maxValue);

        if (requirementWrapper.requirement.unit != null) {
            Label unitForMax = new Label(composite, SWT.NONE);
            unitForMax.setText(requirementWrapper.requirement.unit);
            unitForMax.setBounds(unitForMaxX, y2, unitWidth, height);
            formToolkit.adapt(unitForMax, false, false);
            createdControls.add(unitForMax);
        }
    }

    private void createTextFieldRequirement(TextFieldRequirementWrapper requirementWrapper) {
        TextFieldRequirement realReq = (TextFieldRequirement) requirementWrapper.requirement;

        requirementWrapper.value = new Text(composite, SWT.BORDER);
        requirementWrapper.value.setEnabled(realReq.enable);

        String type = realReq.requirementType.toString().toLowerCase();
        requirementWrapper.value.setMessage(type);
        requirementWrapper.value.setToolTipText(type);
        if (realReq.defaultValue != 0) {
            requirementWrapper.value.setText(String.valueOf(realReq.defaultValue));
        }
        requirementWrapper.value.setBounds(minX, y1, minMaxWidth, height);
        formToolkit.adapt(requirementWrapper.value, true, true);
        createdControls.add(requirementWrapper.value);

        if (requirementWrapper.requirement.unit != null) {
            Label unitForMin = new Label(composite, SWT.NONE);
            unitForMin.setText(requirementWrapper.requirement.unit);
            unitForMin.setBounds(unitForMinX, y2, unitWidth, height);
            formToolkit.adapt(unitForMin, false, false);
            createdControls.add(unitForMin);
        }
    }

    private void createCheckboxRequirement(CheckboxRequirementWrapper requirementWrapper) {
        CheckboxRequirement realReq = (CheckboxRequirement) requirementWrapper.requirement;

        requirementWrapper.value = new Button(composite, SWT.CHECK);
        requirementWrapper.value.setEnabled(realReq.enable);
        requirementWrapper.value.setSelection(realReq.defaultValue);
        requirementWrapper.value.setBounds(minX, y1, minMaxWidth, height);
        formToolkit.adapt(requirementWrapper.value, true, true);
        createdControls.add(requirementWrapper.value);
        createdButton = requirementWrapper.value;

        if (requirementWrapper.requirement.unit != null) {
            Label unitForMin = new Label(composite, SWT.NONE);
            unitForMin.setText(requirementWrapper.requirement.unit);
            unitForMin.setBounds(unitForMinX, y2, unitWidth, height);
            formToolkit.adapt(unitForMin, false, false);
            createdControls.add(unitForMin);
        }
    }
}
