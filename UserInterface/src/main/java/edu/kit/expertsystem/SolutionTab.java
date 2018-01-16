package edu.kit.expertsystem;

import edu.kit.expertsystem.controller.ResultImage;
import edu.kit.expertsystem.controller.ResultTreeItem;
import edu.kit.expertsystem.controller.wrapper.RequirementWrapper;
import edu.kit.expertsystem.controller.wrapper.ResultWrapper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.widgets.FormToolkit;

import java.util.List;

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
    private Label separatorHorizontal;
    private Tree resultTree;
    private Button saveSolutionOntologyButton;
    private Text searchField;
    private Combo orderByCombo;
    private Combo orderByCombo2;
    private Button showOnlyDiffsCheckBox;
    private Combo switchSolutionForm;
    private DescriptionHelper descriptionHelper;

    SolutionTab(Composite parent, FormToolkit formToolkit, Rectangle contentRec) {
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
        resultWrapper.searchField = searchField;

        orderByCombo = new Combo(leftComposite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        formToolkit.adapt(orderByCombo, true, true);
        orderByCombo.addModifyListener(e -> {
            updateSizeOfOrderByCombos();
            updateSizeOfSearchText();
        });
        resultWrapper.orderBy = orderByCombo;

        orderByCombo2 = new Combo(leftComposite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        formToolkit.adapt(orderByCombo2, true, true);
        updateSizeOfOrderByCombos();
        orderByCombo2.addModifyListener(e -> {
            updateSizeOfOrderByCombos();
            updateSizeOfSearchText();
        });
        resultWrapper.orderBy2 = orderByCombo2;

        showOnlyDiffsCheckBox = new Button(leftComposite, SWT.CHECK);
        showOnlyDiffsCheckBox.setText("Show only differences in results");
        showOnlyDiffsCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                showOnlyDiffsCheckBox.setText(showOnlyDiffsCheckBox.getSelection() ? "Show all components and values"
                        + " of results" : "Show only differences in results");
            }
        });
        updateSizeOfShowOnlyDiffsCheckBox();
        formToolkit.adapt(showOnlyDiffsCheckBox, true, true);
        resultWrapper.showOnlyDiffsCheckBox = showOnlyDiffsCheckBox;

        switchSolutionForm = new Combo(leftComposite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        switchSolutionForm.add("Show as List");
        switchSolutionForm.add("Show as Images");
        switchSolutionForm.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (resultWrapper.resultShow != null) {
                    resultWrapper.resultShow.clearLastResults();
                }
                if (switchSolutionForm.getText().equals("Show as List")) {
                    resultWrapper.resultShow = new ResultTreeItem(resultWrapper);
                } else {
                    resultWrapper.resultShow = new ResultImage(resultWrapper);
                }
                resultWrapper.resultShow.setResults();
            }
        });
        updateSizeOfSwitchSolutionForm();
        formToolkit.adapt(switchSolutionForm, true, true);
        switchSolutionForm.select(0);
        switchSolutionForm.notifyListeners(SWT.Selection, new Event());

        separatorHorizontal = new Label(leftComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        updateSizeOfHorizontalSeparator();
        formToolkit.adapt(separatorHorizontal, false, false);

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
        for (RequirementWrapper requirement : requirements) {
            if (requirement.requirement.description != null && requirement.requirement.resultIRI != null) {
                descriptionHelper.createDescription(requirement.requirement.displayName, requirement.requirement
                        .description, rowNumber++);
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
        updateSizeOfOrderByCombos();
        updateSizeOfSearchText();
        updateSizeOfShowOnlyDiffsCheckBox();
        updateSizeOfSwitchSolutionForm();
        updateSizeOfHorizontalSeparator();
        updateSizeOfTreeItem();
    }

    private void updateSizeOfSaveSolutionOntologyButton() {
        int width = GuiHelper.getSizeOfControl(saveSolutionOntologyButton).x + saveSolutionOntologyButtonWidthOffset;
        saveSolutionOntologyButton.setBounds(offsetX, offsetY, width, searchTextHeight);
    }

    private void updateSizeOfOrderByCombos() {
        int width = GuiHelper.getSizeOfControl(orderByCombo).x;
        int x = saveSolutionOntologyButton.getBounds().x + saveSolutionOntologyButton.getBounds().width + offsetX;
        orderByCombo.setBounds(x, offsetY, width, searchTextHeight);
        int x2 = x + width + offsetX;
        orderByCombo2.setBounds(x2, offsetY, width, searchTextHeight);
    }

    private void updateSizeOfSearchText() {
        int widthOfSearchText = Math.max(searchTextWidth, GuiHelper.getSizeOfControl(searchField).x);
        int xOfSearchText = leftComposite.getBounds().width - widthOfSearchText - offsetXEnd;
        int endXOfOther = orderByCombo2.getBounds().x + orderByCombo2.getBounds().width;
        if (xOfSearchText < endXOfOther + offsetXEnd) {
            xOfSearchText = endXOfOther + offsetXEnd;
            widthOfSearchText = leftComposite.getBounds().width - xOfSearchText - offsetXEnd;
        }
        searchField.setBounds(xOfSearchText, offsetY, widthOfSearchText, searchTextHeight);
    }

    private void updateSizeOfShowOnlyDiffsCheckBox() {
        int y = 2 * offsetY + searchTextHeight;
        showOnlyDiffsCheckBox.setText("Show all components and values of results");
        Point size = GuiHelper.getSizeOfControl(showOnlyDiffsCheckBox);
        showOnlyDiffsCheckBox.setBounds(offsetX, y, size.x, searchTextHeight);
        showOnlyDiffsCheckBox.setText(showOnlyDiffsCheckBox.getSelection() ? "Show all components and values" + " of"
                + " results" : "Show only differences in results");
    }

    private void updateSizeOfSwitchSolutionForm() {
        int y = 2 * offsetY + searchTextHeight;
        Point size = GuiHelper.getSizeOfControl(switchSolutionForm);
        int x = leftComposite.getBounds().width - size.x - offsetXEnd;
        int endXOfOther = orderByCombo2.getBounds().x + orderByCombo2.getBounds().width;
        if (x < endXOfOther + offsetXEnd) {
            x = endXOfOther + offsetXEnd;
            size.x = leftComposite.getBounds().width - x - offsetXEnd;
        }

        switchSolutionForm.setBounds(x, y, size.x, searchTextHeight);
    }

    private void updateSizeOfHorizontalSeparator() {
        int y = 3 * offsetY + 2 * searchTextHeight;
        int width = leftComposite.getBounds().width - offsetX - offsetXEnd;
        separatorHorizontal.setBounds(offsetX, y, width, searchTextHeight);
    }

    private void updateSizeOfTreeItem() {
        int yOfTree = 4 * offsetY + 3 * searchTextHeight;
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
