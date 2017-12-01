package edu.kit.expertsystem;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.kit.expertsystem.controller.wrapper.RequirementWrapper;
import edu.kit.expertsystem.controller.wrapper.ResultWrapper;

public class SolutionTab {

    public static final String SEARCH_KEY = "SearchString";

    private static final int offsetX = 5;
    private static final int offsetY = 5;
    private static final int offsetXEnd = 5;
    private static final int offsetYEnd = 5;

    private static final int saveSolutionOntologyButtonWidthOffset = 10;

    private static final int searchTextWidth = 120;
    private static final int searchTextHeight = 23;

    private final FormToolkit formToolkit;

    private SashForm solutionForm;
    private Composite leftComposite;
    private Composite rightComposite;
    private Tree resultTree;
    private Button saveSolutionOntologyButton;
    private Text searchField;
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

        saveSolutionOntologyButton = new Button(leftComposite, SWT.PUSH);
        saveSolutionOntologyButton.setToolTipText("Saves the solution of the reasoning in an ontology");
        saveSolutionOntologyButton.setText("Save solution");
        formToolkit.adapt(saveSolutionOntologyButton, true, true);

        searchField = new Text(leftComposite, SWT.BORDER);
        searchField.setMessage("search");
        searchField.setToolTipText("search");
        formToolkit.adapt(searchField, true, true);

        resultTree = new Tree(leftComposite, SWT.NONE);
        updateSizeOfTreeItem();
        resultWrapper.tree = resultTree;

        searchField.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                updateSizeOfSearchText();
                String[] currentSearchStrings = searchField.getText().toLowerCase().split(" ");
                for (TreeItem treeItem : resultTree.getItems()) {
                    String searchString = String.valueOf(treeItem.getData(SEARCH_KEY)).toLowerCase();
                    boolean contains = true;
                    for (String currentSearchString : currentSearchStrings) {
                        contains &= searchString.contains(currentSearchString);
                    }
                    treeItem.setExpanded(contains);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // nothing to do
            }
        });

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
        updateSizeOfSaveSolutionOntologyButton();
        updateSizeOfSearchText();
        updateSizeOfTreeItem();
    }

    private void updateSizeOfSaveSolutionOntologyButton() {
        int width = GuiHelper.getSizeOfText(saveSolutionOntologyButton,
                saveSolutionOntologyButton.getText()).x + saveSolutionOntologyButtonWidthOffset;
        saveSolutionOntologyButton.setBounds(offsetX, offsetY, width, searchTextHeight);
    }

    private void updateSizeOfSearchText() {
        int widthOfSearchText = Math.max(searchTextWidth,
                GuiHelper.getSizeOfText(searchField, searchField.getText()).x);
        int xOfSearchText = leftComposite.getBounds().width - widthOfSearchText - offsetXEnd;
        int saveSolutionXEnd = saveSolutionOntologyButton.getBounds().x
                + saveSolutionOntologyButton.getBounds().width;
        if (xOfSearchText < saveSolutionXEnd + offsetXEnd) {
            xOfSearchText = saveSolutionXEnd + offsetXEnd;
            widthOfSearchText = leftComposite.getBounds().width - xOfSearchText - offsetXEnd;
        }
        searchField.setBounds(xOfSearchText, offsetY, widthOfSearchText, searchTextHeight);
    }

    private void updateSizeOfTreeItem() {
        int yOfTree = offsetY + searchTextHeight + offsetY;
        int widthOfTree = leftComposite.getBounds().width - offsetX - offsetXEnd;
        int heightOfTree = leftComposite.getBounds().height - yOfTree - offsetYEnd;
        resultTree.setBounds(offsetX, yOfTree, widthOfTree, heightOfTree);
        formToolkit.adapt(resultTree, true, true);
    }

    public SashForm getSolutionForm() {
        return solutionForm;
    }

    public Button getSaveSolutionOntologyButton() {
        return saveSolutionOntologyButton;
    }

}
