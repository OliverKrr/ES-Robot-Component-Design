/*
 * Copyright 2018 Oliver Karrenbauer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation * files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, * * * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.kit.anthropomatik.h2t.expertsystem;

import edu.kit.anthropomatik.h2t.expertsystem.controller.ResultTable;
import edu.kit.anthropomatik.h2t.expertsystem.controller.ResultTreeItem;
import edu.kit.anthropomatik.h2t.expertsystem.controller.wrapper.RequirementWrapper;
import edu.kit.anthropomatik.h2t.expertsystem.controller.wrapper.ResultWrapper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
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
    private Table resultTable;
    private Label numberOfResultsLabel;
    private Button saveSolutionOntologyButton;
    private Text searchField;
    private Combo orderByCombo;
    private Combo orderByCombo2;
    private Button showOnlyDiffsCheckBox;
    private Button selectToShowButton;
    private Table selectTableToShow;
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
        saveSolutionOntologyButton.setToolTipText("Saves the solutions in an ontology");
        saveSolutionOntologyButton.setText("Save solutions");
        formToolkit.adapt(saveSolutionOntologyButton, true, true);

        searchField = new Text(leftComposite, SWT.BORDER);
        searchField.setMessage("search");
        formToolkit.adapt(searchField, true, true);
        resultWrapper.searchField = searchField;

        orderByCombo = new Combo(leftComposite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        orderByCombo.setToolTipText("First criterion to sort solutions");
        formToolkit.adapt(orderByCombo, true, true);
        orderByCombo.addModifyListener(e -> {
            updateSizeOfOrderByCombos();
            updateSizeOfSearchText();
        });
        resultWrapper.orderBy = orderByCombo;

        orderByCombo2 = new Combo(leftComposite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        orderByCombo2.setToolTipText("Second criterion to sort solutions, when the values of the first are equal");
        formToolkit.adapt(orderByCombo2, true, true);
        updateSizeOfOrderByCombos();
        orderByCombo2.addModifyListener(e -> {
            updateSizeOfOrderByCombos();
            updateSizeOfSearchText();
        });
        resultWrapper.orderBy2 = orderByCombo2;

        selectToShowButton = new Button(leftComposite, SWT.TOGGLE);
        selectToShowButton.setText("Select ... to show");
        selectToShowButton.setToolTipText("Choose what components and properties should be displayed");
        resultWrapper.selectToShowButton = selectToShowButton;
        selectTableToShow = new Table(leftComposite, SWT.CHECK | SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE);
        selectTableToShow.setVisible(false);
        selectToShowButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectTableToShow.setVisible(selectToShowButton.getSelection());
                updateSizeOfSelectShow();
            }
        });
        resultWrapper.selectTableToShow = selectTableToShow;
        updateSizeOfSelectShow();

        showOnlyDiffsCheckBox = new Button(leftComposite, SWT.CHECK);
        showOnlyDiffsCheckBox.setText("Show only differences in solutions");
        showOnlyDiffsCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                showOnlyDiffsCheckBox.setText(showOnlyDiffsCheckBox.getSelection() ? "Show all components and values"
                        + " of solutions" : "Show only differences in solutions");
            }
        });
        updateSizeOfShowOnlyDiffsCheckBox();
        formToolkit.adapt(showOnlyDiffsCheckBox, true, true);
        resultWrapper.showOnlyDiffsCheckBox = showOnlyDiffsCheckBox;

        numberOfResultsLabel = new Label(leftComposite, SWT.NONE);
        resultWrapper.numberOfResultsLabel = numberOfResultsLabel;
        updateSizeOfNumberOfResults();
        formToolkit.adapt(numberOfResultsLabel, true, true);
        numberOfResultsLabel.setForeground(Configs.KIT_GREEN_70);

        resultTree = new Tree(leftComposite, SWT.NONE);
        resultTable = new Table(leftComposite, SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE);
        resultTable.setHeaderVisible(true);
        resultTable.addListener(SWT.EraseItem, event -> {
            int index = resultTable.indexOf((TableItem) event.item);
            if (index % 2 == 0) {
                Color oldBackground = event.gc.getBackground();
                event.gc.setBackground(Configs.KIT_GREEN_30);
                event.gc.fillRectangle(0, event.y, resultTable.getClientArea().width, event.height);
                event.gc.setBackground(oldBackground);
            }
        });

        updateSizeOfResultItems();
        resultWrapper.tree = resultTree;
        resultWrapper.table = resultTable;

        switchSolutionForm = new Combo(leftComposite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        switchSolutionForm.add("Show as List");
        switchSolutionForm.add("Show as Table");
        switchSolutionForm.setToolTipText("Switch visualization of solutions");
        switchSolutionForm.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (resultTree.isDisposed() || resultTable.isDisposed()) {
                    return;
                }
                if (resultWrapper.resultShow != null) {
                    resultWrapper.resultShow.clearLastResults();
                }
                if (switchSolutionForm.getText().equals("Show as List")) {
                    resultTree.setVisible(true);
                    resultTable.setVisible(false);
                    resultWrapper.resultShow = new ResultTreeItem(formToolkit, resultWrapper);
                } else {
                    resultTree.setVisible(false);
                    resultTable.setVisible(true);
                    resultWrapper.resultShow = new ResultTable(formToolkit, resultWrapper);
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
                for (TableItem tableItem : resultTable.getItems()) {
                    String searchString = String.valueOf(tableItem.getData(SEARCH_KEY)).toLowerCase();
                    boolean contains = true;
                    for (String currentSearchString : currentSearchStrings) {
                        contains &= searchString.contains(currentSearchString);
                    }
                    if (contains) {
                        tableItem.setForeground(null);
                    } else {
                        tableItem.setForeground(Configs.KIT_GREY_30);
                    }
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
        descriptionHelper.createDescription("Search", "Filter the solutions. Anything presented can be searched. For"
                + " specific search you can do: motor:ILM115x50 HeightH:174 (or any substring ot this). Spaces AND "
                + "the terms.", rowNumber++);
        descriptionHelper.createDescription("Solution Visualisation", "Click on any solution (component or property)"
                + " to generate a composed visualization of the solution with the containing components and " +
                "calculated properties. This can be exported as PDF.", rowNumber++);
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
        updateSizeOfSelectShow();
        updateSizeOfShowOnlyDiffsCheckBox();
        updateSizeOfSwitchSolutionForm();
        updateSizeOfHorizontalSeparator();
        updateSizeOfNumberOfResults();
        updateSizeOfResultItems();
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

    private void updateSizeOfSelectShow() {
        int y = 2 * offsetY + searchTextHeight;
        Point size = GuiHelper.getSizeOfControl(selectToShowButton);
        selectToShowButton.setBounds(offsetX, y, size.x, searchTextHeight);

        int yOfTable = y + searchTextHeight;
        Point sizeTable = GuiHelper.getSizeOfControl(selectTableToShow);
        int heightTable = sizeTable.y;
        if (heightTable + yOfTable > leftComposite.getBounds().height) {
            heightTable = leftComposite.getBounds().height - yOfTable;
        }
        selectTableToShow.setBounds(offsetX, yOfTable, sizeTable.x, heightTable);
    }

    private void updateSizeOfShowOnlyDiffsCheckBox() {
        int y = 2 * offsetY + searchTextHeight;
        int x = selectToShowButton.getBounds().x + selectToShowButton.getBounds().width + offsetX;
        showOnlyDiffsCheckBox.setText("Show all components and values of solutions");
        Point size = GuiHelper.getSizeOfControl(showOnlyDiffsCheckBox);
        showOnlyDiffsCheckBox.setBounds(x, y, size.x, searchTextHeight);
        showOnlyDiffsCheckBox.setText(showOnlyDiffsCheckBox.getSelection() ? "Show all components and values" + " of"
                + " solutions" : "Show only differences in solutions");
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

    private void updateSizeOfNumberOfResults() {
        int y = 4 * offsetY + 3 * searchTextHeight;
        Point size = GuiHelper.getSizeOfControl(numberOfResultsLabel);
        numberOfResultsLabel.setBounds(offsetX, y, size.x, searchTextHeight);
    }

    private void updateSizeOfResultItems() {
        int y = 5 * offsetY + 4 * searchTextHeight;
        int width = leftComposite.getBounds().width - offsetX - offsetXEnd;
        int height = leftComposite.getBounds().height - y - offsetYEnd;

        resultTree.setBounds(offsetX, y, width, height);
        resultTable.setBounds(offsetX, y, width, height);
        formToolkit.adapt(resultTree, true, true);
        formToolkit.adapt(resultTable, true, true);
    }

    public SashForm getSolutionForm() {
        return solutionForm;
    }

    public Button getSaveSolutionOntologyButton() {
        return saveSolutionOntologyButton;
    }

}
