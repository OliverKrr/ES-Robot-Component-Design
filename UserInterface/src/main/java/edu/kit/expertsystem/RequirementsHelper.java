package edu.kit.expertsystem;

import edu.kit.expertsystem.controller.wrapper.*;
import edu.kit.expertsystem.model.req.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

import java.util.ArrayList;
import java.util.List;

public class RequirementsHelper {

    private static final int displayNameWidth = 172;
    private static final int minMaxWidth = 40;
    private static final int unitWidth = 30;

    private static final int height = 23;
    private static final int heightForLabels = height + 18;

    private static final int displayNameX = 10;
    private static final int minX = displayNameX + displayNameWidth + 16;
    private static final int unitForMinX = minX + minMaxWidth + 6;
    private static final int maxX = unitForMinX + unitWidth + 6;
    private static final int unitForMaxX = maxX + minMaxWidth + 6;
    private static final int deviationSpinnerX = unitForMaxX + unitWidth + 35;
    private static final int deviationScaleOffsetX = 4;
    private static final int userWeightingOffsetX = 45;

    private static final int optimizationYOffset = 20;
    private static final int appliedYOffset = 3;
    private static final int basisY = 61;
    private static final int basisY1 = basisY + Math.round(heightForLabels / 2.f) - Math.round(height / 2.f);
    private static final int basisY2 = basisY1 + 3;
    private static final int offsetY = 57;

    private final FormToolkit formToolkit;
    private final Composite composite;
    private final boolean isOptimization;

    private Label topicLabel;
    private Label deviationLabel;
    private Label userWeightingLabel;
    private List<Control> createdControls;
    private Button createdButton;

    private int maxXEnd;
    private int maxYEnd;

    private int y;
    private int y1;
    private int y2;

    RequirementsHelper(FormToolkit formToolkit, Composite composite, Category category, boolean isOptimization) {
        this.formToolkit = formToolkit;
        this.composite = composite;
        this.isOptimization = isOptimization;

        topicLabel = new Label(composite, SWT.WRAP);
        if (isOptimization) {
            topicLabel.setText("Optimization");
        } else {
            topicLabel.setText(category.topic);
        }
        topicLabel.setFont(SWTResourceManager.getFont(GuiHelper.getFontName(topicLabel.getFont()), GuiHelper
                .getFontHeight(topicLabel.getFont()) + 7, SWT.BOLD));
        formToolkit.adapt(topicLabel, false, false);
        topicLabel.setForeground(Configs.KIT_GREEN_100);

        if (isOptimization) {
            deviationLabel = new Label(composite, SWT.WRAP);
            deviationLabel.setText("Allowed deviation in %");
            deviationLabel.setFont(SWTResourceManager.getFont(GuiHelper.getFontName(deviationLabel.getFont()),
                    GuiHelper.getFontHeight(deviationLabel.getFont()) + 3, SWT.BOLD));

            userWeightingLabel = new Label(composite, SWT.WRAP);
            userWeightingLabel.setText("Priority");
            userWeightingLabel.setFont(SWTResourceManager.getFont(GuiHelper.getFontName(userWeightingLabel.getFont())
                    , GuiHelper.getFontHeight(userWeightingLabel.getFont()) + 3, SWT.BOLD));
        }
    }

    public boolean createRequirement(RequirementWrapper requirementWrapper,
                                     List<RequirementDependencyCheckboxWrapper> requirementDependencyWrappers, int
                                             rowNumber) {
        if (requirementWrapper instanceof RequirementOnlyForSolutionWrapper) {
            // RequirementOnlyForSolution will not be displayed
            return false;
        }
        if (isOptimization) {
            if (!(requirementWrapper instanceof TextFieldMinMaxRequirementWrapper)) {
                return false;
            }
            if (!((TextFieldMinMaxRequirement) ((TextFieldMinMaxRequirementWrapper) requirementWrapper).requirement)
                    .allowOptimization) {
                return false;
            }
        }

        createdControls = new ArrayList<>();
        createdButton = null;
        y = basisY + offsetY * rowNumber + (isOptimization ? optimizationYOffset : 0);
        y1 = basisY1 + offsetY * rowNumber + (isOptimization ? optimizationYOffset : 0);
        y2 = basisY2 + offsetY * rowNumber + (isOptimization ? optimizationYOffset : 0);
        maxYEnd = Math.max(maxYEnd, y2 + heightForLabels);
        createCommonRequirement(requirementWrapper);

        if (requirementWrapper instanceof TextFieldMinMaxRequirementWrapper) {
            createTextFieldMinMaxRequirement((TextFieldMinMaxRequirementWrapper) requirementWrapper);
        } else if (requirementWrapper instanceof TextFieldRequirementWrapper) {
            createTextFieldRequirement((TextFieldRequirementWrapper) requirementWrapper);
        } else if (requirementWrapper instanceof CheckboxRequirementWrapper) {
            createCheckboxRequirement((CheckboxRequirementWrapper) requirementWrapper);
        } else if (requirementWrapper instanceof DropdownRequirementWrapper) {
            createDropdownRequirement((DropdownRequirementWrapper) requirementWrapper);
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
        return true;
    }

    private void createCommonRequirement(RequirementWrapper requirementWrapper) {
        // \t funktioniert hier -> mehr siehe Arbeitsblatt
        Label displayName = new Label(composite, SWT.WRAP);
        displayName.setBounds(displayNameX, y, displayNameWidth, heightForLabels);
        displayName.setText(requirementWrapper.requirement.displayName + ":");
        formToolkit.adapt(displayName, false, false);
        displayName.setForeground(Configs.KIT_GREEN_70);
        createdControls.add(displayName);
    }

    private void createTextFieldMinMaxRequirement(TextFieldMinMaxRequirementWrapper requirementWrapper) {
        TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) requirementWrapper.requirement;

        Text min;
        if (isOptimization) {
            requirementWrapper.minValueOptimization = new Text(composite, SWT.BORDER);
            min = requirementWrapper.minValueOptimization;
            requirementWrapper.minValue.addModifyListener(e -> requirementWrapper.minValueOptimization.setText
                    (requirementWrapper.minValue.getText()));

            requirementWrapper.minApplied = new Label(composite, SWT.NONE);
            int minAppliedY = y1 + height + appliedYOffset;
            requirementWrapper.minApplied.setBounds(minX, minAppliedY, minMaxWidth, height);
            formToolkit.adapt(requirementWrapper.minApplied, true, true);
            requirementWrapper.minApplied.setForeground(Configs.KIT_GREEN_50);
        } else {
            requirementWrapper.minValue = new Text(composite, SWT.BORDER);
            min = requirementWrapper.minValue;
        }
        min.setEnabled(realReq.enableMin && !isOptimization);
        min.setMessage("min");
        min.setToolTipText("min");
        if (realReq.defaultMin != 0) {
            if (realReq.isIntegerValue) {
                min.setText(String.valueOf(Math.round(realReq.defaultMin)));
            } else {
                min.setText(String.valueOf(realReq.defaultMin));
            }
        }
        min.setBounds(minX, y1, minMaxWidth, height);
        formToolkit.adapt(min, true, true);
        createdControls.add(min);

        addUnit(requirementWrapper, unitForMinX);

        Text max;
        if (isOptimization) {
            requirementWrapper.maxValueOptimization = new Text(composite, SWT.BORDER);
            max = requirementWrapper.maxValueOptimization;
            requirementWrapper.maxValue.addModifyListener(e -> requirementWrapper.maxValueOptimization.setText
                    (requirementWrapper.maxValue.getText()));

            requirementWrapper.maxApplied = new Label(composite, SWT.NONE);
            int maxAppliedY = y1 + height + appliedYOffset;
            requirementWrapper.maxApplied.setBounds(maxX, maxAppliedY, minMaxWidth, height);
            formToolkit.adapt(requirementWrapper.maxApplied, true, true);
            requirementWrapper.maxApplied.setForeground(Configs.KIT_GREEN_50);
        } else {
            requirementWrapper.maxValue = new Text(composite, SWT.BORDER);
            max = requirementWrapper.maxValue;
        }
        max.setEnabled(realReq.enableMax && !isOptimization);
        max.setMessage("max");
        max.setToolTipText("max");
        if (realReq.defaultMax != Double.MAX_VALUE) {
            if (realReq.isIntegerValue) {
                max.setText(String.valueOf(Math.round(realReq.defaultMax)));
            } else {
                max.setText(String.valueOf(realReq.defaultMax));
            }
        }

        max.setBounds(maxX, y1, minMaxWidth, height);
        formToolkit.adapt(max, true, true);
        createdControls.add(max);
        maxXEnd = Math.max(maxXEnd, maxX + minMaxWidth);

        addUnit(requirementWrapper, unitForMaxX);

        if (isOptimization) {
            createOptimizationComponents(requirementWrapper);
        }
    }

    private void createOptimizationComponents(TextFieldMinMaxRequirementWrapper requirementWrapper) {
        int spinnerFactor = (int) Math.round(Math.pow(10, TextFieldMinMaxRequirementWrapper.digitsDeviation));
        int spinnerMaximum = TextFieldMinMaxRequirementWrapper.maxDeviation * spinnerFactor;
        Spinner spinnerDeviation = new Spinner(composite, SWT.WRAP | SWT.BORDER);
        requirementWrapper.deviation = spinnerDeviation;
        spinnerDeviation.setValues(0, 0, spinnerMaximum, TextFieldMinMaxRequirementWrapper.digitsDeviation,
                spinnerMaximum / 100, 5 * spinnerMaximum / 100);

        Scale scaleDeviation = new Scale(composite, SWT.WRAP | SWT.BORDER);
        scaleDeviation.setSelection(0);
        scaleDeviation.setMinimum(0);
        scaleDeviation.setMaximum(TextFieldMinMaxRequirementWrapper.maxDeviation);
        scaleDeviation.setIncrement(1);
        scaleDeviation.setPageIncrement(5);
        scaleDeviation.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                spinnerDeviation.setSelection(scaleDeviation.getSelection() * spinnerFactor);
            }
        });
        spinnerDeviation.addModifyListener(e -> scaleDeviation.setSelection(Math.round(1f * spinnerDeviation
                .getSelection() / spinnerFactor)));
        Runnable update = () -> {
            try {
                if (!"".equals(requirementWrapper.minValue.getText())) {
                    double minValue = Double.parseDouble(requirementWrapper.minValue.getText());
                    double minAppliedDeviation = minValue - (1.0 * spinnerDeviation.getSelection() / spinnerFactor /
                            100.0 * minValue);
                    requirementWrapper.minApplied.setText("(" + String.valueOf(minAppliedDeviation) + ")");
                } else {
                    requirementWrapper.minApplied.setText("");
                }
            } catch (NumberFormatException e) {
                requirementWrapper.minApplied.setText("(NAN)");
            }
            try {
                if (!"".equals(requirementWrapper.maxValue.getText())) {
                    double maxValue = Double.parseDouble(requirementWrapper.maxValue.getText());
                    double maxAppliedDeviation = maxValue + (1.0 * spinnerDeviation.getSelection() / spinnerFactor /
                            100.0 * maxValue);
                    requirementWrapper.maxApplied.setText("(" + String.valueOf(maxAppliedDeviation) + ")");
                } else {
                    requirementWrapper.maxApplied.setText("");
                }
            } catch (NumberFormatException e) {
                requirementWrapper.maxApplied.setText("(NAN)");
            }
        };
        spinnerDeviation.addModifyListener(e -> update.run());
        scaleDeviation.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                update.run();
            }
        });
        requirementWrapper.minValue.addModifyListener(e -> update.run());
        requirementWrapper.maxValue.addModifyListener(e -> update.run());


        Point spinnerSize = GuiHelper.getSizeOfControl(spinnerDeviation);
        Point scaleSize = GuiHelper.getSizeOfControl(scaleDeviation);
        int scaleHeight = Math.min(scaleSize.y, heightForLabels);
        int scaleX = deviationSpinnerX + spinnerSize.x + deviationScaleOffsetX;
        int scaleY = y + Math.round(heightForLabels / 2.f) - Math.round(scaleHeight / 2.f);

        spinnerDeviation.setBounds(deviationSpinnerX, y1, spinnerSize.x, height);
        formToolkit.adapt(spinnerDeviation, true, true);
        scaleDeviation.setBounds(scaleX, scaleY, scaleSize.x, scaleHeight);
        formToolkit.adapt(scaleDeviation, true, true);

        Point deviationSize = GuiHelper.getSizeOfControl(deviationLabel);
        int deviationX = deviationSpinnerX + Math.round((spinnerSize.x + scaleSize.x + deviationScaleOffsetX) / 2.f)
                - Math.round(deviationSize.x / 2.f);

        deviationLabel.setBounds(deviationX, 0, deviationSize.x, deviationSize.y);
        formToolkit.adapt(deviationLabel, false, false);
        deviationLabel.setForeground(Configs.KIT_GREEN_70);


        Combo userWeighting = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        requirementWrapper.userWeighting = userWeighting;
        for (int i = TextFieldMinMaxRequirementWrapper.minUserWeighting; i <= TextFieldMinMaxRequirementWrapper
                .maxUserWeighting; ++i) {
            userWeighting.add(String.valueOf(i));
        }
        userWeighting.select(((TextFieldMinMaxRequirement) requirementWrapper.requirement).userWeight);
        Point userWeightingSize = GuiHelper.getSizeOfControl(userWeighting);
        int userWeightingX = scaleX + scaleSize.x + userWeightingOffsetX;
        userWeighting.setBounds(userWeightingX, y1, userWeightingSize.x, height);
        formToolkit.adapt(userWeighting, true, true);

        Point userWeightingLabelSize = GuiHelper.getSizeOfControl(userWeightingLabel);
        int userWeightingLabelX = userWeightingX + Math.round(userWeightingSize.x / 2.f) - Math.round
                (userWeightingLabelSize.x / 2.f);

        userWeightingLabel.setBounds(userWeightingLabelX, 0, userWeightingLabelSize.x, userWeightingLabelSize.y);
        formToolkit.adapt(userWeightingLabel, false, false);
        userWeightingLabel.setForeground(Configs.KIT_GREEN_70);
        maxXEnd = Math.max(maxXEnd, userWeightingLabelX + userWeightingLabelSize.x);
    }

    private void createTextFieldRequirement(TextFieldRequirementWrapper requirementWrapper) {
        TextFieldRequirement realReq = (TextFieldRequirement) requirementWrapper.requirement;

        requirementWrapper.value = new Text(composite, SWT.BORDER);
        requirementWrapper.value.setEnabled(realReq.enable);

        String type = realReq.requirementType.toString().toLowerCase();
        requirementWrapper.value.setMessage(type);
        requirementWrapper.value.setToolTipText(type);
        if (realReq.isIntegerValue) {
            requirementWrapper.value.setText(String.valueOf(Math.round(realReq.defaultValue)));
        } else {
            requirementWrapper.value.setText(String.valueOf(realReq.defaultValue));
        }
        requirementWrapper.value.setBounds(minX, y1, minMaxWidth, height);
        formToolkit.adapt(requirementWrapper.value, true, true);
        createdControls.add(requirementWrapper.value);
        maxXEnd = Math.max(maxXEnd, minX + minMaxWidth);

        addUnit(requirementWrapper, unitForMinX);
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
        maxXEnd = Math.max(maxXEnd, minX + minMaxWidth);

        addUnit(requirementWrapper, unitForMinX);
    }

    private void createDropdownRequirement(DropdownRequirementWrapper requirementWrapper) {
        DropdownRequirement realReq = (DropdownRequirement) requirementWrapper.requirement;

        requirementWrapper.values = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        requirementWrapper.values.setEnabled(realReq.enable);
        int selectionIndex = 0;
        for (int i = 0; i < realReq.values.length; i++) {
            requirementWrapper.values.add(realReq.values[i]);
            if (realReq.values[i].equals(realReq.defaultValue)) {
                selectionIndex = i;
            }
        }
        int maxWidth = GuiHelper.getSizeOfControl(requirementWrapper.values).x;
        requirementWrapper.values.select(selectionIndex);
        requirementWrapper.values.setBounds(minX, y1, maxWidth, height);
        formToolkit.adapt(requirementWrapper.values, true, true);
        createdControls.add(requirementWrapper.values);
        maxXEnd = Math.max(maxXEnd, minX + maxWidth);

        addUnit(requirementWrapper, unitForMinX);
    }

    private void addUnit(RequirementWrapper wrapper, int unit) {
        if (wrapper.requirement.unit != null) {
            Label unitForMin = new Label(composite, SWT.NONE);
            unitForMin.setText(wrapper.requirement.unit);
            unitForMin.setBounds(unit, y2, unitWidth, heightForLabels);
            formToolkit.adapt(unitForMin, false, false);
            createdControls.add(unitForMin);
            maxXEnd = Math.max(maxXEnd, unit + unitWidth);
        }
    }

    public void updateSize(Rectangle bounds) {
        updateTopicSize(bounds);
    }

    private void updateTopicSize(Rectangle bounds) {
        int y = basisY + offsetY * -1;
        Point sizeOfText = GuiHelper.getSizeOfControl(topicLabel);
        int width = sizeOfText.x;
        int x = (bounds.width - width) / 2;
        if (x < 0) {
            x = 0;
        }
        topicLabel.setBounds(x, y, width, sizeOfText.y);
        maxXEnd = Math.max(maxXEnd, x + width);

        if (isOptimization) {
            int secondY = y + sizeOfText.y + Math.round((basisY - y - sizeOfText.y) / 2.f);
            Rectangle deviationBounds = deviationLabel.getBounds();
            deviationLabel.setBounds(deviationBounds.x, secondY, deviationBounds.width, deviationBounds.height);

            Rectangle userWeightingBounds = userWeightingLabel.getBounds();
            userWeightingLabel.setBounds(userWeightingBounds.x, secondY, userWeightingBounds.width,
                    userWeightingBounds.height);
            maxXEnd = Math.max(maxXEnd, userWeightingBounds.x + userWeightingBounds.width);
        }
    }

    public int getMaxXEnd() {
        return maxXEnd;
    }

    public int getMaxYEnd() {
        return maxYEnd;
    }
}
