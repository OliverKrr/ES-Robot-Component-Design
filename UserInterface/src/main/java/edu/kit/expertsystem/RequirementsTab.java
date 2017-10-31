package edu.kit.expertsystem;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.kit.expertsystem.controller.RequirementWrapper;

public class RequirementsTab {

    private final FormToolkit formToolkit;

    private SashForm requirementsForm;

    public RequirementsTab(Shell shell, FormToolkit formToolkit) {
        this.formToolkit = formToolkit;

        requirementsForm = new SashForm(shell, SWT.NONE);
        requirementsForm.setBounds(96, 35, 779, 348);
        formToolkit.adapt(requirementsForm);
        formToolkit.paintBordersFor(requirementsForm);
    }

    public void createContents(List<RequirementWrapper> requirements) {
        Composite leftComposite = new Composite(requirementsForm, SWT.NONE);
        formToolkit.adapt(leftComposite);
        formToolkit.paintBordersFor(leftComposite);

        RequirementsHelper requirementsHelper = new RequirementsHelper(formToolkit, leftComposite);
        for (int i = 0; i < requirements.size(); i++) {
            requirementsHelper.createRequirement(requirements.get(i), i);
        }

        Button btnEnableFields = new Button(leftComposite, SWT.CHECK);
        btnEnableFields.setBounds(307, 294, 93, 16);
        formToolkit.adapt(btnEnableFields, true, true);
        btnEnableFields.setText("Enable fields");
        btnEnableFields.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                btnEnableFields.setText(btnEnableFields.getSelection() ? "Disable fields" : "Enable fields");
                for (RequirementWrapper req : requirements) {
                    req.minValue.setEnabled(req.requirement.enableMin || !req.minValue.isEnabled());
                    req.maxValue.setEnabled(req.requirement.enableMax || !req.maxValue.isEnabled());
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                // nothing to do
            }
        });

        Label separator = new Label(requirementsForm, SWT.SEPARATOR | SWT.VERTICAL);
        formToolkit.adapt(separator, true, true);

        Composite rightComposite = new Composite(requirementsForm, SWT.NONE);
        formToolkit.adapt(rightComposite);
        formToolkit.paintBordersFor(rightComposite);

        DescriptionHelper descriptionHelper = new DescriptionHelper(formToolkit, rightComposite);
        descriptionHelper.createDescription("min/max:",
                "Desired min and max values. If no entered, defaults are taken.", 0);
        for (int i = 0; i < requirements.size(); i++) {
            descriptionHelper.createDescription(requirements.get(i).requirement.displayName,
                    requirements.get(i).requirement.description, i + 1);
        }

        requirementsForm.setWeights(new int[] { 339, 1, 291 });
    }

    public SashForm getRequirementsForm() {
        return requirementsForm;
    }

}
