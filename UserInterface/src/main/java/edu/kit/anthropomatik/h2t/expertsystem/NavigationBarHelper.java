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

import edu.kit.anthropomatik.h2t.expertsystem.controller.NavigationItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

import java.util.List;

public class NavigationBarHelper {

    private static final int horizontalY = GUI.navBarY;

    private static final int verticalX = 5;

    private final FormToolkit formToolkit;
    private final Composite composite;

    private int horizontalX;
    private Rectangle lastRectangle;

    NavigationBarHelper(FormToolkit formToolkit, Composite composite) {
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
        int navItemWidth = 0;
        int navItemHeight = 0;

        for (NavigationItem item : navItems) {
            item.item = new Button(composite, SWT.PUSH);
            item.defaultFontHeight = GuiHelper.getFontHeight(item.item.getFont()) + 2;
            item.item.setFont(SWTResourceManager.getFont(GuiHelper.getFontName(item.item.getFont()), item
                    .defaultFontHeight + 2, SWT.BOLD));
            item.item.setText(item.name);
            // calculate max width and height
            Point size = GuiHelper.getSizeOfControl(item.item);
            navItemWidth = Math.max(navItemWidth, size.x);
            navItemHeight = Math.max(navItemHeight, size.y);
        }

        lastRectangle = new Rectangle(0, 0, navItemWidth, navItemHeight);

        for (int i = 0; i < navItems.size(); i++) {
            if (isHorizontal) {
                lastRectangle.x = horizontalX + i * navItemWidth;
                lastRectangle.y = horizontalY + level * navItemHeight;
            } else {
                lastRectangle.x = verticalX + level * navItemWidth;
                lastRectangle.y = i * navItemHeight;
            }
            NavigationItem item = navItems.get(i);
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
                    item.item.setFont(SWTResourceManager.getFont(GuiHelper.getFontName(item.item.getFont()), item
                            .defaultFontHeight + 2, SWT.BOLD));
                    item.compositeToHandle.setVisible(true);

                    for (NavigationItem otherItem : navItems) {
                        if (item != otherItem) {
                            otherItem.item.setFont(SWTResourceManager.getFont(GuiHelper.getFontName(otherItem.item
                                    .getFont()), item.defaultFontHeight, SWT.NORMAL));
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

    public void updateY(List<NavigationItem> reqNavItems, int y) {
        boolean isFirst = true;
        int yDelta = 0;
        for (NavigationItem navItem : reqNavItems) {
            Rectangle bounds = navItem.item.getBounds();
            if (isFirst) {
                isFirst = false;
                yDelta = y - bounds.y;
            }
            bounds.y = bounds.y + yDelta;
            navItem.item.setBounds(bounds);
        }
    }
}
