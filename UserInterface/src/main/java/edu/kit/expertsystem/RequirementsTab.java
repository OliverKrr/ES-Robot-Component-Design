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

    private SashForm requirementsForm;
    private RequirementsHelper requirementsHelper;

    private Composite leftComposite;
    private Composite rightComposite;
    private Button btnEnableFields;
    private DescriptionHelper descriptionHelper;

    public RequirementsTab(Composite parent, FormToolkit formToolkit, Rectangle sizeOfForm) {
        this.formToolkit = formToolkit;

        requirementsForm = new SashForm(parent, SWT.NONE);
        requirementsForm.setBounds(sizeOfForm);
        formToolkit.adapt(requirementsForm);
        formToolkit.paintBordersFor(requirementsForm);
    }

    public void createContents(Category category, List<RequirementWrapper> requirements,
                               List<RequirementDependencyCheckboxWrapper> requirementDependencyWrappers) {
        leftComposite = new Composite(requirementsForm, SWT.NONE);
        formToolkit.adapt(leftComposite);

        boolean isAnyFieldDisabled = false;
        requirementsHelper = new RequirementsHelper(formToolkit, leftComposite, category);
        int lastReqOrderPosition = -1;
        int rowNumber = 0;
        for (int i = 0; i < requirements.size(); i++) {
            if (requirements.get(i) instanceof TextFieldMinMaxRequirementWrapper) {
                TextFieldMinMaxRequirement req = (TextFieldMinMaxRequirement) requirements.get(i).requirement;
                isAnyFieldDisabled |= !req.enableMin || !req.enableMax;
            } else if (requirements.get(i) instanceof TextFieldRequirementWrapper) {
                TextFieldRequirement req = (TextFieldRequirement) requirements.get(i).requirement;
                isAnyFieldDisabled |= !req.enable;
            } else if (requirements.get(i) instanceof CheckboxRequirementWrapper) {
                CheckboxRequirement req = (CheckboxRequirement) requirements.get(i).requirement;
                isAnyFieldDisabled |= !req.enable;
            } else if (requirements.get(i) instanceof DropdownRequirementWrapper) {
                DropdownRequirement req = (DropdownRequirement) requirements.get(i).requirement;
                isAnyFieldDisabled |= !req.enable;
            } else {
                throw new RuntimeException("Requirement class unknown: " + requirements.get(i).getClass());
            }
            if (lastReqOrderPosition == requirements.get(i).requirement.orderPosition) {
                rowNumber--;
            }
            lastReqOrderPosition = requirements.get(i).requirement.orderPosition;
            requirementsHelper.createRequirement(requirements.get(i), requirementDependencyWrappers, rowNumber++);
        }

        if (isAnyFieldDisabled) {
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
        descriptionHelper.createDescription("min/max:", "Desired min and max values. If no entered, defaults are " +
                "taken: min=0 and max=infinite.", rowNumber++);
        for (int i = 0; i < requirements.size(); i++) {
            if (requirements.get(i).requirement.description != null) {
                descriptionHelper.createDescription(requirements.get(i).requirement.displayName, requirements.get(i)
                        .requirement.description, rowNumber++);
            }
        }
        rightScrolledComposite.setMinHeight(descriptionHelper.getMaxYEnd());
    }

    public void updateSize(Rectangle sizeOfForm) {
        requirementsForm.setBounds(sizeOfForm);
        formToolkit.adapt(requirementsForm);
        formToolkit.paintBordersFor(requirementsForm);
        requirementsHelper.updateSize(leftComposite.getBounds());
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
