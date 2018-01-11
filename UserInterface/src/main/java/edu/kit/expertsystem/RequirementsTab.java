package edu.kit.expertsystem;

import edu.kit.expertsystem.controller.wrapper.*;
import edu.kit.expertsystem.model.req.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import java.util.List;

public class RequirementsTab {

    private static final int btnEnalbeFieldOffsetXEnd = 20;
    private static final int btnEnalbeFieldOffsetYEnd = 7;

    private final FormToolkit formToolkit;
    private final boolean isOptimization;

    private SashForm requirementsForm;
    private RequirementsHelper requirementsHelper;

    private ScrolledComposite leftScrolledComposite;
    private Composite leftComposite;
    private Composite rightComposite;
    private Button btnEnableFields;
    private DescriptionHelper descriptionHelper;

    RequirementsTab(Composite parent, FormToolkit formToolkit, Rectangle sizeOfForm, boolean isOptimization) {
        this.formToolkit = formToolkit;
        this.isOptimization = isOptimization;

        requirementsForm = new SashForm(parent, SWT.NONE);
        requirementsForm.setBounds(sizeOfForm);
        formToolkit.adapt(requirementsForm);
        formToolkit.paintBordersFor(requirementsForm);
    }

    public void createContents(Category category, List<RequirementWrapper> requirements,
                               List<RequirementDependencyCheckboxWrapper> requirementDependencyWrappers) {
        leftScrolledComposite = new ScrolledComposite(requirementsForm, SWT.V_SCROLL | SWT.H_SCROLL);
        leftScrolledComposite.setExpandVertical(true);
        leftScrolledComposite.setExpandHorizontal(true);
        formToolkit.adapt(leftScrolledComposite);

        leftComposite = new Composite(leftScrolledComposite, SWT.NONE);
        formToolkit.adapt(leftComposite);
        leftScrolledComposite.setContent(leftComposite);

        boolean isAnyFieldDisabled = false;
        requirementsHelper = new RequirementsHelper(formToolkit, leftComposite, category, isOptimization);
        int lastReqOrderPosition = -1;
        int rowNumber = 0;
        for (RequirementWrapper requirement1 : requirements) {
            if (requirement1 instanceof TextFieldMinMaxRequirementWrapper) {
                TextFieldMinMaxRequirement req = (TextFieldMinMaxRequirement) requirement1.requirement;
                isAnyFieldDisabled |= !req.enableMin || !req.enableMax;
            } else if (requirement1 instanceof TextFieldRequirementWrapper) {
                TextFieldRequirement req = (TextFieldRequirement) requirement1.requirement;
                isAnyFieldDisabled |= !req.enable;
            } else if (requirement1 instanceof CheckboxRequirementWrapper) {
                CheckboxRequirement req = (CheckboxRequirement) requirement1.requirement;
                isAnyFieldDisabled |= !req.enable;
            } else if (requirement1 instanceof DropdownRequirementWrapper) {
                DropdownRequirement req = (DropdownRequirement) requirement1.requirement;
                isAnyFieldDisabled |= !req.enable;
            } else if (requirement1 instanceof RequirementOnlyForSolutionWrapper) {
                // RequirementOnlyForSolution will not be displayed
            } else {
                throw new RuntimeException("Requirement class unknown: " + requirement1.getClass());
            }
            if (lastReqOrderPosition == requirement1.requirement.orderPosition && !isOptimization) {
                rowNumber--;
            }
            lastReqOrderPosition = requirement1.requirement.orderPosition;
            if (requirementsHelper.createRequirement(requirement1, requirementDependencyWrappers, rowNumber)) {
                ++rowNumber;
            }
        }

        if (isAnyFieldDisabled && !isOptimization) {
            btnEnableFields = new Button(leftComposite, SWT.CHECK);
            updateEnableField();
            btnEnableFields.setText("Enable fields");
            btnEnableFields.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    btnEnableFields.setText(btnEnableFields.getSelection() ? "Disable fields" : "Enable fields");
                    for (RequirementWrapper req : requirements) {
                        if (req instanceof TextFieldMinMaxRequirementWrapper) {
                            TextFieldMinMaxRequirementWrapper reqWrapper = (TextFieldMinMaxRequirementWrapper) req;
                            TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req.requirement;
                            reqWrapper.minValue.setEnabled(realReq.enableMin || !reqWrapper.minValue.isEnabled());
                            reqWrapper.maxValue.setEnabled(realReq.enableMax || !reqWrapper.maxValue.isEnabled());
                        } else if (req instanceof TextFieldRequirementWrapper) {
                            TextFieldRequirementWrapper reqWrapper = (TextFieldRequirementWrapper) req;
                            TextFieldRequirement realReq = (TextFieldRequirement) req.requirement;
                            reqWrapper.value.setEnabled(realReq.enable || !reqWrapper.value.isEnabled());
                        } else if (req instanceof CheckboxRequirementWrapper) {
                            CheckboxRequirementWrapper reqWrapper = (CheckboxRequirementWrapper) req;
                            CheckboxRequirement realReq = (CheckboxRequirement) req.requirement;
                            reqWrapper.value.setEnabled(realReq.enable || !reqWrapper.value.isEnabled());
                        } else if (req instanceof DropdownRequirementWrapper) {
                            DropdownRequirementWrapper reqWrapper = (DropdownRequirementWrapper) req;
                            DropdownRequirement realReq = (DropdownRequirement) req.requirement;
                            reqWrapper.values.setEnabled(realReq.enable || !reqWrapper.values.isEnabled());
                        } else if (req instanceof RequirementOnlyForSolutionWrapper) {
                            // RequirementOnlyForSolution will not be displayed
                        } else {
                            throw new RuntimeException("Requirement class unknown: " + req.getClass());
                        }
                    }
                }
            });
        }

        Label separator = new Label(requirementsForm, SWT.SEPARATOR | SWT.VERTICAL);
        formToolkit.adapt(separator, false, false);

        ScrolledComposite rightScrolledComposite = new ScrolledComposite(requirementsForm, SWT.V_SCROLL);
        rightScrolledComposite.setExpandVertical(true);
        rightScrolledComposite.setExpandHorizontal(true);
        formToolkit.adapt(rightScrolledComposite);

        rightComposite = new Composite(rightScrolledComposite, SWT.NONE);
        formToolkit.adapt(rightComposite);
        rightScrolledComposite.setContent(rightComposite);

        rowNumber = 0;
        descriptionHelper = new DescriptionHelper(formToolkit, rightComposite);
        if (isOptimization) {
            descriptionHelper.createDescription("Deviation", "The amount of deviation the result can differ from.",
                    rowNumber++);
            descriptionHelper.createDescription("Priority", "Influences the quality index depending on the actual " +
                    "deviation.", rowNumber++);
        } else {
            descriptionHelper.createDescription("min/max", "Desired min and max values. If no entered, defaults are"
                    + " taken: min=0 and max=infinite.", rowNumber++);
        }
        for (RequirementWrapper requirement : requirements) {
            if (requirement.requirement.description != null && !(requirement instanceof
                    RequirementOnlyForSolutionWrapper)) {
                if (isOptimization) {
                    if (!(requirement instanceof TextFieldMinMaxRequirementWrapper)) {
                        continue;
                    }
                    if (!((TextFieldMinMaxRequirement) ((TextFieldMinMaxRequirementWrapper) requirement).requirement)
                            .allowOptimization) {
                        continue;
                    }
                }
                descriptionHelper.createDescription(requirement.requirement.displayName, requirement.requirement
                        .description, rowNumber++);
            }
        }
        rightScrolledComposite.setMinHeight(descriptionHelper.getMaxYEnd());
    }

    public void updateSize(Rectangle sizeOfForm) {
        if (btnEnableFields != null) {
            // set to o so it will not extend leftComposite.width
            // it will be set back later on accordingly to leftComposite
            btnEnableFields.setBounds(0, 0, 0, 0);
        }
        requirementsForm.setBounds(sizeOfForm);
        formToolkit.adapt(requirementsForm);
        formToolkit.paintBordersFor(requirementsForm);
        requirementsHelper.updateSize(leftComposite.getBounds());
        leftScrolledComposite.setMinWidth(requirementsHelper.getMaxXEnd());
        leftScrolledComposite.setMinHeight(requirementsHelper.getMaxYEnd());
        descriptionHelper.updateSize(rightComposite.getBounds());
        updateEnableField();
    }

    private void updateEnableField() {
        if (btnEnableFields != null) {
            Point size = GuiHelper.getSizeOfText(btnEnableFields, "Disable fields");
            // Offset for width, to include the checkbox size
            int realWidth = size.x + btnEnalbeFieldOffsetXEnd;
            int xCord = leftComposite.getBounds().width - realWidth;
            int yCord = leftComposite.getBounds().height - size.y - btnEnalbeFieldOffsetYEnd;
            btnEnableFields.setBounds(xCord, yCord, realWidth, size.y);
            formToolkit.adapt(btnEnableFields, true, true);
        }
    }

    public SashForm getRequirementsForm() {
        return requirementsForm;
    }

}
