package edu.kit.expertsystem;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.kit.expertsystem.controller.RequirementWrapper;
import edu.kit.expertsystem.controller.ResultWrapper;

public class SolutionTab {

    private static final int treeOffsetX = 5;
    private static final int treeOffsetY = 5;
    private static final int treeOffsetXEnd = 5;
    private static final int treeOffsetYEnd = 5;

    private final FormToolkit formToolkit;

    private SashForm solutionForm;
    private Composite leftComposite;
    private Composite rightComposite;
    private Tree resultTree;
    private DescriptionHelper descriptionHelper;

    public SolutionTab(Composite parent, FormToolkit formToolkit, Rectangle contentRec) {
        this.formToolkit = formToolkit;

        solutionForm = new SashForm(parent, SWT.NONE);
        solutionForm.setBounds(contentRec);
        formToolkit.adapt(solutionForm);
        formToolkit.paintBordersFor(solutionForm);
    }

    public void createContents(ResultWrapper resultWrapper, List<RequirementWrapper> requirements) {
        leftComposite = new Composite(solutionForm, SWT.NONE);
        formToolkit.adapt(leftComposite);

        resultTree = new Tree(leftComposite, SWT.NONE);
        updateTreeSize();
        resultWrapper.tree = resultTree;

        Label separator = new Label(solutionForm, SWT.SEPARATOR | SWT.VERTICAL);
        formToolkit.adapt(separator, false, false);

        ScrolledComposite rightScrolledComposite = new ScrolledComposite(solutionForm, SWT.V_SCROLL);
        rightScrolledComposite.setExpandVertical(true);
        rightScrolledComposite.setExpandHorizontal(true);
        formToolkit.adapt(rightScrolledComposite);

        rightComposite = new Composite(rightScrolledComposite, SWT.NONE);
        formToolkit.adapt(rightComposite);
        rightScrolledComposite.setContent(rightComposite);

        int rowNumber = 0;
        descriptionHelper = new DescriptionHelper(formToolkit, rightComposite);
        for (int i = 0; i < requirements.size(); i++) {
            if (requirements.get(i).requirement.description != null
                    && requirements.get(i).requirement.resultIRI != null) {
                descriptionHelper.createDescription(requirements.get(i).requirement.displayName,
                        requirements.get(i).requirement.description, rowNumber++);
            }
        }
        rightScrolledComposite.setMinHeight(descriptionHelper.getMaxYEnd());
    }

    public void updateSize(Rectangle contentRec) {
        solutionForm.setBounds(contentRec);
        formToolkit.adapt(solutionForm);
        formToolkit.paintBordersFor(solutionForm);
        descriptionHelper.updateSize(rightComposite.getBounds());
        updateTreeSize();
    }

    private void updateTreeSize() {
        int width = leftComposite.getSize().x - treeOffsetX - treeOffsetXEnd;
        int height = leftComposite.getSize().y - treeOffsetY - treeOffsetYEnd;
        resultTree.setBounds(treeOffsetX, treeOffsetY, width, height);
        formToolkit.adapt(resultTree, true, true);
    }

    public SashForm getSolutionForm() {
        return solutionForm;
    }

}
