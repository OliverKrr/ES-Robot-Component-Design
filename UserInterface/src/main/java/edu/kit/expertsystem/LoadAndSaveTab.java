package edu.kit.expertsystem;

import edu.kit.expertsystem.controller.wrapper.RequirementWrapper;
import edu.kit.expertsystem.controller.wrapper.ResultWrapper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import java.util.Date;
import java.util.List;

public class LoadAndSaveTab {
    private final FormToolkit formToolkit;

    private SashForm solutionForm;
    private Composite leftComposite;
    private Composite rightComposite;
    private DescriptionHelper descriptionHelper;

    LoadAndSaveTab(Composite parent, FormToolkit formToolkit, Rectangle contentRec) {
        this.formToolkit = formToolkit;

        solutionForm = new SashForm(parent, SWT.NONE);
        solutionForm.setBounds(contentRec);
        formToolkit.adapt(solutionForm);
        formToolkit.paintBordersFor(solutionForm);
    }

    public void createContents(ResultWrapper resultWrapper, List<RequirementWrapper> requirements) {
        leftComposite = new Composite(solutionForm, SWT.NONE);
        formToolkit.adapt(leftComposite);

        // TODO make loadAndSaveTab
        // read from logs
        // pack to list with requirements/solutions (each with file/date/solutionCount)
        // Display in a table
        // by clicking it will be loaded
        //  -> requirements in req
        //  -> solution in solution
        //  -> reasoning should only start, when changes
        //  -> make common functions (move them to requirements)
        //  make saveButton above to save requirements/solutions (like the log) in a separat file -> open browser)
        //  -> also save (and log): orderBys, selectedDisplay, search, displayView (table/tree)
        //  (when load from log -> only take last)
        //  extra loadButton to search for a file, because of unknown location

        //TODO but how handle backwards compatibility?


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
        descriptionHelper.createDescription("Saving", "Save current Requirements and Solutions.", rowNumber++);
        descriptionHelper.createDescription("Loading", "Requirements and Solutions will be loaded. There is no " +
                "guarantee that all requirements are loaded, because of version (naming) changes.", rowNumber++);
        rightScrolledComposite.setMinHeight(descriptionHelper.getMaxYEnd());
    }

    public void updateSize(Rectangle contentRec) {
        solutionForm.setBounds(contentRec);
        formToolkit.adapt(solutionForm);
        formToolkit.paintBordersFor(solutionForm);
        descriptionHelper.updateSize(rightComposite.getBounds());
    }

    public SashForm getForm() {
        return solutionForm;
    }

    private static class LoadData{
        String fileName;
        Date date;
    }
}
