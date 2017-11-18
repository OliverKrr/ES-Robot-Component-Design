package edu.kit.expertsystem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.kit.expertsystem.controller.NavigationItem;
import edu.kit.expertsystem.controller.RequirementDependencyCheckboxWrapper;
import edu.kit.expertsystem.controller.RequirementWrapper;
import edu.kit.expertsystem.model.Category;

public class RequirementsCategory {

    public static final int contentXOffsetStart = 5;
    private static final int contentXOffsetEnd = 27;
    private static final int contentYOffsetStart = 2;
    private static final int contentYOffsetEnd = 48;

    private final Composite parent;
    private final FormToolkit formToolkit;

    private Composite requirementsOverallForm;
    private Rectangle sizeOfTab;
    private Rectangle conentRec;
    private int contentY;

    private NavigationBarHelper navHelper;
    private Map<Category, List<RequirementWrapper>> reqPerCategory = new LinkedHashMap<>();
    private List<RequirementsTab> reqTabs = new ArrayList<>();
    private List<NavigationItem> reqNavItems = new ArrayList<>();

    public RequirementsCategory(Composite parent, FormToolkit formToolkit) {
        this.parent = parent;
        this.formToolkit = formToolkit;
        navHelper = new NavigationBarHelper(formToolkit, parent);
    }

    public Rectangle createNavBars(List<RequirementWrapper> requirements) {
        for (RequirementWrapper req : requirements) {
            if (reqPerCategory.containsKey(req.requirement.category)) {
                reqPerCategory.get(req.requirement.category).add(req);
            } else {
                List<RequirementWrapper> reqs = new ArrayList<>();
                reqs.add(req);
                reqPerCategory.put(req.requirement.category, reqs);

                NavigationItem item = new NavigationItem();
                item.name = req.requirement.category.displayName;
                reqNavItems.add(item);
            }
        }
        return navHelper.createVerticalNavBar(reqNavItems, 0);
    }

    public Rectangle createReqContent(
            List<RequirementDependencyCheckboxWrapper> requirementDependencyWrappers, int[] weights,
            int contentY, Point sizeOfShell) {
        this.contentY = contentY + contentYOffsetStart;
        initSizes(sizeOfShell, weights);
        for (Entry<Category, List<RequirementWrapper>> reqsPerCat : reqPerCategory.entrySet()) {
            RequirementsTab tab = new RequirementsTab(requirementsOverallForm, formToolkit, sizeOfTab);
            tab.createContents(reqsPerCat.getValue(), requirementDependencyWrappers);
            tab.getRequirementsForm().setWeights(weights);
            reqTabs.add(tab);

            reqNavItems.stream().filter(reqNavItem -> reqNavItem.name.equals(reqsPerCat.getKey().displayName))
                    .forEach(reqNavItem -> reqNavItem.compositeToHandle = tab.getRequirementsForm());
        }
        navHelper.addListener(reqNavItems);
        handleDependencies(requirementDependencyWrappers);
        return conentRec;
    }

    private void initSizes(Point sizeOfShell, int[] newContentWeights) {
        requirementsOverallForm = new Composite(parent, SWT.NONE);
        updateSize(sizeOfShell, newContentWeights);
    }

    public Rectangle updateSize(Point sizeOfShell, int[] newContentWeights) {
        int contentX = navHelper.getLastRectangle().x + navHelper.getLastRectangle().width
                + contentXOffsetStart;
        int contentWidth = sizeOfShell.x - contentX - contentXOffsetEnd;
        int contentHeight = sizeOfShell.y - contentY - GUI.errorTextHeight - GUI.errorTextYOffset
                - contentYOffsetEnd;
        conentRec = new Rectangle(contentX, contentY, contentWidth, contentHeight);
        sizeOfTab = new Rectangle(0, 0, conentRec.width, conentRec.height);

        requirementsOverallForm.setBounds(conentRec);
        formToolkit.adapt(requirementsOverallForm);
        for (RequirementsTab tab : reqTabs) {
            tab.getRequirementsForm().setWeights(newContentWeights);
            formToolkit.adapt(tab.getRequirementsForm());
            tab.updateSize(sizeOfTab);
        }
        return conentRec;
    }

    private void handleDependencies(
            List<RequirementDependencyCheckboxWrapper> requirementDependencyWrappers) {
        reqNavItems.get(0).item.notifyListeners(SWT.Selection, new Event());

        requirementDependencyWrappers.forEach(reqDep -> {
            reqDep.fromCheckbox.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    reqDep.toControls.forEach(control -> control.setVisible(
                            reqDep.requirementDependencyCheckbox.displayOnValue == reqDep.fromCheckbox
                                    .getSelection()));
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent event) {
                    // nothing to do
                }
            });
            reqDep.fromCheckbox.notifyListeners(SWT.Selection, new Event());
        });
    }

    public Composite getRequirementsOverallForm() {
        return requirementsOverallForm;
    }
}
