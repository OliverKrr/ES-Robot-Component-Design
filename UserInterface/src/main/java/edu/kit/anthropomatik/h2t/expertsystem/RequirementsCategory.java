package edu.kit.anthropomatik.h2t.expertsystem;

import edu.kit.anthropomatik.h2t.expertsystem.controller.NavigationItem;
import edu.kit.anthropomatik.h2t.expertsystem.controller.wrapper.RequirementDependencyCheckboxWrapper;
import edu.kit.anthropomatik.h2t.expertsystem.controller.wrapper.RequirementWrapper;
import edu.kit.anthropomatik.h2t.expertsystem.model.req.Category;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.forms.widgets.FormToolkit;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RequirementsCategory {

    public static final int contentXOffsetStart = 5;
    private static final int contentXOffsetEnd = 27;
    private static final int contentYOffsetStart = 2;
    private static final int contentYOffsetEnd = 48;

    private final Composite parent;
    private final FormToolkit formToolkit;
    private final boolean isOptimization;

    private Composite requirementsOverallForm;
    private Rectangle sizeOfTab;
    private Rectangle conentRec;
    private int contentY;

    private NavigationBarHelper navHelper;
    private Map<Category, List<RequirementWrapper>> reqPerCategory = new LinkedHashMap<>();
    private List<RequirementsTab> reqTabs = new ArrayList<>();
    private List<NavigationItem> reqNavItems = new ArrayList<>();

    RequirementsCategory(Composite parent, FormToolkit formToolkit, boolean isOptimization) {
        this.parent = parent;
        this.formToolkit = formToolkit;
        this.isOptimization = isOptimization;
        navHelper = new NavigationBarHelper(formToolkit, parent);
    }

    public Rectangle createNavBars(List<RequirementWrapper> requirements) {
        Category optiCat = new Category();
        if (isOptimization) {
            reqPerCategory.put(optiCat, new ArrayList<>());
        }
        for (RequirementWrapper req : requirements) {
            if (isOptimization) {
                reqPerCategory.get(optiCat).add(req);
            } else if (reqPerCategory.containsKey(req.requirement.category)) {
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
        if (isOptimization) {
            return new Rectangle(0, 0, 0, 0);
        }
        return navHelper.createVerticalNavBar(reqNavItems, 0);
    }

    public Rectangle createReqContent(List<RequirementDependencyCheckboxWrapper> requirementDependencyWrappers, int
            contentY, Point sizeOfShell) {
        this.contentY = contentY + contentYOffsetStart;
        requirementsOverallForm = new Composite(parent, SWT.NONE);
        if (!isOptimization) {
            initSizes(sizeOfShell);
        }
        for (Entry<Category, List<RequirementWrapper>> reqsPerCat : reqPerCategory.entrySet()) {
            RequirementsTab tab = new RequirementsTab(requirementsOverallForm, formToolkit, sizeOfTab, isOptimization);
            tab.createContents(reqsPerCat.getKey(), reqsPerCat.getValue(), requirementDependencyWrappers);
            reqTabs.add(tab);

            if (!isOptimization) {
                reqNavItems.stream().filter(reqNavItem -> reqNavItem.name.equals(reqsPerCat.getKey().displayName))
                        .forEach(reqNavItem -> reqNavItem.compositeToHandle = tab.getRequirementsForm());
            }
        }
        if (!isOptimization) {
            navHelper.addListener(reqNavItems);
            handleDependencies(requirementDependencyWrappers);
        }
        return conentRec;
    }

    private void initSizes(Point sizeOfShell) {
        updateSize(sizeOfShell, null);
    }

    public Rectangle updateSize(Point sizeOfShell, int[] newContentWeights) {
        int contentX = navHelper.getLastRectangle().x + navHelper.getLastRectangle().width + contentXOffsetStart;
        int contentWidth = sizeOfShell.x - contentX - contentXOffsetEnd;
        int contentHeight = sizeOfShell.y - contentY - GUI.errorTextHeight - GUI.errorTextYOffset - contentYOffsetEnd;
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

    public void updateSize(Rectangle conentRec, int[] newContentWeights) {
        this.conentRec = conentRec;
        sizeOfTab = new Rectangle(0, 0, conentRec.width, conentRec.height);

        if (requirementsOverallForm != null) {
            requirementsOverallForm.setBounds(conentRec);
            formToolkit.adapt(requirementsOverallForm);
        }
        for (RequirementsTab tab : reqTabs) {
            tab.getRequirementsForm().setWeights(newContentWeights);
            formToolkit.adapt(tab.getRequirementsForm());
            tab.updateSize(sizeOfTab);
        }
    }

    private void handleDependencies(List<RequirementDependencyCheckboxWrapper> requirementDependencyWrappers) {
        reqNavItems.get(0).item.notifyListeners(SWT.Selection, new Event());

        requirementDependencyWrappers.forEach(reqDep -> {
            reqDep.fromCheckbox.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    reqDep.toControls.forEach(control -> control.setVisible(reqDep.requirementDependencyCheckbox
                            .displayOnValue == reqDep.fromCheckbox.getSelection()));
                }
            });
            reqDep.fromCheckbox.notifyListeners(SWT.Selection, new Event());
        });
    }

    public void setVisibilityOfNavItems(boolean isVisible) {
        for (NavigationItem navItem : reqNavItems) {
            navItem.item.setVisible(isVisible);
        }
    }

    public void disposeNavItems() {
        reqNavItems.forEach(navItem -> navItem.item.dispose());
    }

    public Composite getRequirementsOverallForm() {
        return requirementsOverallForm;
    }

    public void updateNavBarY(int y) {
        navHelper.updateY(reqNavItems, y);
    }
}
