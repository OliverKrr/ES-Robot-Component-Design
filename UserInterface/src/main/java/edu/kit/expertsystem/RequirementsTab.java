package edu.kit.expertsystem;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.kit.expertsystem.controller.RequirementWrapper;
import edu.kit.expertsystem.controller.TextFieldMinMaxRequirementWrapper;
import edu.kit.expertsystem.model.TextFieldMinMaxRequirement;

public class RequirementsTab {

    private final FormToolkit formToolkit;

    private SashForm requirementsForm;

    public RequirementsTab(Composite parent, FormToolkit formToolkit, Rectangle sizeOfForm) {
        this.formToolkit = formToolkit;

        requirementsForm = new SashForm(parent, SWT.NONE);
        requirementsForm.setBounds(sizeOfForm);
        formToolkit.adapt(requirementsForm);
        formToolkit.paintBordersFor(requirementsForm);
    }

    public void createContents(List<RequirementWrapper> requirements) {
        Composite leftComposite = new Composite(requirementsForm, SWT.NONE);
        formToolkit.adapt(leftComposite);

        boolean isAnyFieldDisabled = false;
        RequirementsHelper requirementsHelper = new RequirementsHelper(formToolkit, leftComposite);
        for (int i = 0; i < requirements.size(); i++) {
            if (requirements.get(i) instanceof TextFieldMinMaxRequirementWrapper) {
                TextFieldMinMaxRequirement textFieldReq = (TextFieldMinMaxRequirement) requirements
                        .get(i).requirement;
                isAnyFieldDisabled |= !textFieldReq.enableMin || !textFieldReq.enableMax;
            } else {
                throw new RuntimeException("Requirement class unknown: " + requirements.get(i).getClass());
            }
            requirementsHelper.createRequirement(requirements.get(i), i);
        }

        if (isAnyFieldDisabled) {
            Button btnEnableFields = new Button(leftComposite, SWT.CHECK);
            btnEnableFields.setBounds(326, 327, 93, 16);
            formToolkit.adapt(btnEnableFields, true, true);
            btnEnableFields.setText("Enable fields");
            btnEnableFields.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    btnEnableFields
                            .setText(btnEnableFields.getSelection() ? "Disable fields" : "Enable fields");
                    for (RequirementWrapper req : requirements) {
                        if (req instanceof TextFieldMinMaxRequirementWrapper) {
                            TextFieldMinMaxRequirementWrapper textFieldReqWrapper = (TextFieldMinMaxRequirementWrapper) req;
                            TextFieldMinMaxRequirement textFieldReq = (TextFieldMinMaxRequirement) req.requirement;
                            textFieldReqWrapper.minValue.setEnabled(
                                    textFieldReq.enableMin || !textFieldReqWrapper.minValue.isEnabled());
                            textFieldReqWrapper.maxValue.setEnabled(
                                    textFieldReq.enableMax || !textFieldReqWrapper.maxValue.isEnabled());
                        } else {
                            throw new RuntimeException("Requirement class unknown: " + req.getClass());
                        }
                    }
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent event) {
                    // nothing to do
                }
            });
        }

        Label separator = new Label(requirementsForm, SWT.SEPARATOR | SWT.VERTICAL);
        formToolkit.adapt(separator, false, false);

        ScrolledComposite rightScrolledComposite = new ScrolledComposite(requirementsForm, SWT.V_SCROLL);
        rightScrolledComposite.setExpandVertical(true);
        rightScrolledComposite.setExpandHorizontal(true);
        formToolkit.adapt(rightScrolledComposite);

        Composite rightComposite = new Composite(rightScrolledComposite, SWT.NONE);
        formToolkit.adapt(rightComposite);
        rightScrolledComposite.setContent(rightComposite);

        int rowNumber = 0;
        DescriptionHelper descriptionHelper = new DescriptionHelper(formToolkit, rightComposite);
        descriptionHelper.createDescription("min/max:",
                "Desired min and max values. If no entered, defaults are taken.", rowNumber++);
        for (int i = 0; i < requirements.size(); i++) {
            if (requirements.get(i).requirement.description != null) {
                descriptionHelper.createDescription(requirements.get(i).requirement.displayName,
                        requirements.get(i).requirement.description, rowNumber++);
            }
        }
        rightScrolledComposite.setMinHeight(descriptionHelper.getMaxYEnd());
    }

    public SashForm getRequirementsForm() {
        return requirementsForm;
    }

}
