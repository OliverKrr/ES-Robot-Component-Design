package edu.kit.expertsystem;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.kit.expertsystem.controller.RequirementWrapper;
import edu.kit.expertsystem.controller.ResultWrapper;

public class SolutionTab {

    private final FormToolkit formToolkit;

    private SashForm solutionForm;

    public SolutionTab(Composite parent, FormToolkit formToolkit) {
        this.formToolkit = formToolkit;

        solutionForm = new SashForm(parent, SWT.NONE);
        solutionForm.setBounds(GUI.contentRec);
        formToolkit.adapt(solutionForm);
        formToolkit.paintBordersFor(solutionForm);
    }

    public void createContents(ResultWrapper resultWrapper, List<RequirementWrapper> requirements) {
        Composite leftComposite = new Composite(solutionForm, SWT.NONE);
        formToolkit.adapt(leftComposite);
        formToolkit.paintBordersFor(leftComposite);

        resultWrapper.tree = new Tree(leftComposite, SWT.NONE);
        resultWrapper.tree.setBounds(10, 10, 385, 298);
        formToolkit.adapt(resultWrapper.tree);
        formToolkit.paintBordersFor(resultWrapper.tree);

        Label separator = new Label(solutionForm, SWT.SEPARATOR | SWT.VERTICAL);
        formToolkit.adapt(separator, true, true);

        Composite rightComposite = new Composite(solutionForm, SWT.NONE);
        formToolkit.adapt(rightComposite);
        formToolkit.paintBordersFor(rightComposite);

        DescriptionHelper descriptionHelper = new DescriptionHelper(formToolkit, rightComposite);
        for (int i = 0; i < requirements.size(); i++) {
            descriptionHelper.createDescription(requirements.get(i).requirement.displayName,
                    requirements.get(i).requirement.description, i + 1);
        }
    }

    public SashForm getSolutionForm() {
        return solutionForm;
    }

}
