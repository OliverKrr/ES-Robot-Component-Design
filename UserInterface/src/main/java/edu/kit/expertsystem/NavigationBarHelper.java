package edu.kit.expertsystem;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.kit.expertsystem.controller.NavigationItem;

public class NavigationBarHelper {

    private static final int navItemWidthOffset = 20;
    private static final int navItemHeightOffset = 5;

    private static final int horizontalX = GUI.contentX;
    private static final int horizontalY = GUI.navBarY;

    private static final int verticalX = 5;
    private static final int verticalBasisY = 70;

    private final FormToolkit formToolkit;
    private final Composite composite;

    public NavigationBarHelper(FormToolkit formToolkit, Composite composite) {
        this.formToolkit = formToolkit;
        this.composite = composite;
    }

    public Point createHorizontalNavBar(List<NavigationItem> navItems, int level) {
        return createNavBar(navItems, level, true);
    }

    public Point createVerticalNavBar(List<NavigationItem> navItems, int level) {
        return createNavBar(navItems, level, false);
    }

    private Point createNavBar(List<NavigationItem> navItems, int level, boolean isHorizontal) {
        int maxWidth = 0;
        int maxHeight = 0;

        for (NavigationItem item : navItems) {
            item.item = new Button(composite, SWT.NONE);
            // calculate max width and height
            item.item.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
            GC gc = new GC(item.item);
            Point size = gc.textExtent(item.name);
            gc.dispose();
            maxWidth = Math.max(maxWidth, size.x);
            maxHeight = Math.max(maxHeight, size.y);
        }

        int navItemWidth = maxWidth + navItemWidthOffset;
        int navItemHeight = maxHeight + navItemHeightOffset;

        for (int i = 0; i < navItems.size(); i++) {
            NavigationItem item = navItems.get(i);
            item.item.setText(item.name);

            if (isHorizontal) {
                item.item.setBounds(horizontalX + i * navItemWidth, horizontalY + level * navItemHeight,
                        navItemWidth, navItemHeight);
            } else {
                item.item.setBounds(verticalX + level * navItemWidth, verticalBasisY + i * navItemHeight,
                        navItemWidth, navItemHeight);
            }

            item.item.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    item.item.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
                    item.item.setBackground(Configs.KIT_GREY_30);
                    item.compositeToHandle.setVisible(true);

                    for (NavigationItem otherItem : navItems) {
                        if (item != otherItem) {
                            otherItem.item.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
                            otherItem.item.setBackground(Configs.KIT_GREY_15);
                            otherItem.compositeToHandle.setVisible(false);
                        }
                    }
                }
            });
            formToolkit.adapt(item.item, true, true);
            item.item.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent e) {
                    Configs.KIT_GREY_15.dispose();
                    Configs.KIT_GREY_30.dispose();
                }
            });
        }
        return new Point(navItemWidth, navItemHeight);
    }

}
