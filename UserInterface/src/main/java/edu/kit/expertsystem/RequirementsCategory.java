package edu.kit.expertsystem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.kit.expertsystem.controller.NavigationItem;
import edu.kit.expertsystem.controller.RequirementWrapper;
import edu.kit.expertsystem.model.Category;

public class RequirementsCategory {

    private final Composite parent;
    private final FormToolkit formToolkit;
    private final Rectangle sizeOfTab;

    private Composite requirementsOverallForm;

    private List<NavigationItem> reqNavItems = new ArrayList<>();
    private Point reqNavBarSize;

    public RequirementsCategory(Composite parent, FormToolkit formToolkit) {
        this.parent = parent;
        this.formToolkit = formToolkit;
        sizeOfTab = new Rectangle(0, 0, GUI.contentRec.width, GUI.contentRec.height);

        requirementsOverallForm = new Composite(parent, SWT.NONE);
        requirementsOverallForm.setBounds(GUI.contentRec);
        formToolkit.adapt(requirementsOverallForm);
    }

    public void createContents(List<RequirementWrapper> requirements, int[] weights) {
        Map<Category, List<RequirementWrapper>> reqPerCategory = new LinkedHashMap<>();

        for (RequirementWrapper req : requirements) {
            if (reqPerCategory.containsKey(req.requirement.category)) {
                reqPerCategory.get(req.requirement.category).add(req);
            } else {
                List<RequirementWrapper> reqs = new ArrayList<>();
                reqs.add(req);
                reqPerCategory.put(req.requirement.category, reqs);
            }
        }

        for (Entry<Category, List<RequirementWrapper>> reqsPerCat : reqPerCategory.entrySet()) {
            RequirementsTab tab = new RequirementsTab(requirementsOverallForm, formToolkit, sizeOfTab);
            tab.createContents(reqsPerCat.getValue());
            tab.getRequirementsForm().setWeights(weights);

            NavigationItem item = new NavigationItem();
            item.name = reqsPerCat.getKey().displayName;
            item.compositeToHandle = tab.getRequirementsForm();
            reqNavItems.add(item);
        }

        createReqNavigationBar();
    }

    private void createReqNavigationBar() {
        NavigationBarHelper navHelper = new NavigationBarHelper(formToolkit, parent);
        reqNavBarSize = navHelper.createVerticalNavBar(reqNavItems, 0);
        reqNavItems.get(0).item.notifyListeners(SWT.Selection, new Event());
    }

    public Composite getRequirementsOverallForm() {
        return requirementsOverallForm;
    }

    public Point getReqNavBarSize() {
        return reqNavBarSize;
    }

}
