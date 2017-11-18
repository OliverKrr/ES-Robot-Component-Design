package edu.kit.expertsystem;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.kit.expertsystem.controller.NavigationItem;

public class NavigationBarHelper {

    private static final int navItemWidthOffset = 20;
    private static final int navItemHeightOffset = 5;

    private static final int horizontalY = GUI.navBarY;

    private static final int verticalX = 5;
    private static final int verticalBasisY = 70;

    private final FormToolkit formToolkit;
    private final Composite composite;

    private int horizontalX;
    private Rectangle lastRectangle;

    public NavigationBarHelper(FormToolkit formToolkit, Composite composite) {
        this.formToolkit = formToolkit;
        this.composite = composite;
    }

    public Rectangle createHorizontalNavBar(List<NavigationItem> navItems, int level, int horizontalX) {
        this.horizontalX = horizontalX;
        return createNavBar(navItems, level, true);
    }

    public Rectangle createVerticalNavBar(List<NavigationItem> navItems, int level) {
        return createNavBar(navItems, level, false);
    }

    private Rectangle createNavBar(List<NavigationItem> navItems, int level, boolean isHorizontal) {
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
        lastRectangle = new Rectangle(0, 0, navItemWidth, navItemHeight);

        for (int i = 0; i < navItems.size(); i++) {
            NavigationItem item = navItems.get(i);
            item.item.setText(item.name);

            if (isHorizontal) {
                lastRectangle.x = horizontalX + i * navItemWidth;
                lastRectangle.y = horizontalY + level * navItemHeight;
            } else {
                lastRectangle.x = verticalX + level * navItemWidth;
                lastRectangle.y = verticalBasisY + i * navItemHeight;
            }
            item.item.setBounds(lastRectangle);
            formToolkit.adapt(item.item, true, true);
        }
        return lastRectangle;
    }

    public void addListener(List<NavigationItem> navItems) {
        for (NavigationItem item : navItems) {
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
        }
    }

    public Rectangle getLastRectangle() {
        return lastRectangle;
    }

}
