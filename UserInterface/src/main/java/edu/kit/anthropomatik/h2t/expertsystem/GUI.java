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

import edu.kit.anthropomatik.h2t.expertsystem.controller.Controller;
import edu.kit.anthropomatik.h2t.expertsystem.controller.NavigationItem;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * TODO: move to a readme
 * To use atlassian - jira plugin follow instructions on:
 * https://confluence.atlassian.com/kb/connecting-to-ssl-services-802171215.html
 * The pem of the self signed certificate is located in this repo: jira.pem
 * IntelliJ has its own jre (default under \AppData\Local) which has to be modified to trust the self signed certificate
 * <p>
 * Start with JVM flags: -Xms16384m -Xmx16384m -XX:-UseGCOverheadLimit
 * <p>
 * Problem with logger
 */
public class GUI {

    public static final int navBarY = 10;
    public static final int errorTextHeight = 115;
    public static final int errorTextYOffset = 2;
    private static final Point firstSizeOfShell = new Point(1000, 600);

    private static final Logger logger = LogManager.getLogger(GUI.class);
    private static final Display display = Display.getDefault();
    private final FormToolkit formToolkit = new FormToolkit(display);
    private final ExecutorService pool;
    private Shell shell;
    private RequirementsCategory requirementsCategory;
    private RequirementsCategory requirementsOptimization;
    private SolutionTab solutionTab;
    private LoadAndSaveTab loadAndSaveTab;
    private Future<?> controllerFuture;
    private Controller controller;

    private NavigationBarHelper mainNavBarHelper;
    private List<NavigationItem> mainNavBars = new ArrayList<>();
    private Combo componentToBeDesigned;
    private Button toggleDescription;
    private StyledText errorText;
    private Label kitLogo;

    private boolean showDescriptions = true;

    GUI() {
        Configs.initConfig(display);
        pool = Executors.newSingleThreadExecutor();
        // do nothing for init and no concurrent problems while creation of gui
        controllerFuture = pool.submit(() -> {
        });
    }

    /**
     * Open the window.
     *
     * @wbp.parser.entryPoint
     */
    public void open() {
        createShell();
        createErrorText();
        controller = new Controller(this);
        controller.initialize();
        createContents();

        shell.addDisposeListener(event -> shutdown());
        // startOnSecondScreenIfPossible();
        startOnTopOfScreen();
        shell.setMaximized(true);
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    @SuppressWarnings("unused")
    private void startOnSecondScreenIfPossible() {
        Monitor[] monitors = display.getMonitors();
        if (monitors.length < 2) {
            logger.debug("Do not startOnSecondScreen.");
            return;
        }
        Rectangle monitorRect = monitors[1].getBounds();
        Rectangle shellRect = shell.getBounds();
        int x = monitorRect.x + ((monitorRect.width - shellRect.width) / 2);
        int y = monitorRect.y + ((monitorRect.height - shellRect.height) / 2);
        shell.setLocation(x, y);
        logger.debug("StartOnSecondScreen");
    }

    private void startOnTopOfScreen() {
        Rectangle monitorRect = display.getPrimaryMonitor().getBounds();
        Rectangle shellRect = shell.getBounds();
        int x = monitorRect.x + ((monitorRect.width - shellRect.width) / 2);
        shell.setLocation(x, monitorRect.y);
        logger.debug("startOnTopOfScreen");
    }

    private void shutdown() {
        controller.interruptReasoning();
        controllerFuture.cancel(true);
        pool.shutdownNow();
        Configs.disposeConfig();
    }

    public void notifySolutionIsReady() {
        display.asyncExec(() -> controller.setResults());
    }

    /**
     * Create contents of the window.
     */
    private void createContents() {
        toggleDescription = new Button(shell, SWT.PUSH);
        toggleDescription.setText("Hide descriptions");
        toggleDescription.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showDescriptions = !showDescriptions;
                if (showDescriptions) {
                    toggleDescription.setText("Hide descriptions");
                } else {
                    toggleDescription.setText("Show descriptions");
                }
                updateSize();
                updateSize();
            }
        });

        componentToBeDesigned = new Combo(shell, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        componentToBeDesigned.setToolTipText("Choose component to be designed.");
        controller.getComponentsToBeDesigned().forEachOrdered(component -> componentToBeDesigned.add(component));
        componentToBeDesigned.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                controller.updateComponentsToBeDesigned(componentToBeDesigned.getText());
                if (requirementsCategory != null) {
                    requirementsCategory.disposeNavItems();
                    requirementsCategory.getRequirementsOverallForm().dispose();
                    solutionTab.getSolutionForm().dispose();
                    loadAndSaveTab.getForm().dispose();
                    requirementsOptimization.disposeNavItems();
                    requirementsOptimization.getRequirementsOverallForm().dispose();
                }
                requirementsCategory = new RequirementsCategory(shell, formToolkit, false);
                Rectangle reqNavBarRec = requirementsCategory.createNavBars(controller.getRequirementsWrapper());
                Rectangle mainNavBarRec = createNavigationBar(reqNavBarRec);
                componentToBeDesigned.setSize(0, mainNavBarRec.height);

                Rectangle recOfContent = requirementsCategory.createReqContent(controller
                        .getRequirementDependencyWrapper(), mainNavBarRec.y + mainNavBarRec.height, firstSizeOfShell);

                requirementsOptimization = new RequirementsCategory(shell, formToolkit, true);
                requirementsOptimization.createNavBars(controller.getRequirementsWrapper());
                requirementsOptimization.updateSize(recOfContent, getWeights());
                requirementsOptimization.createReqContent(controller.getRequirementDependencyWrapper(), mainNavBarRec
                        .y + mainNavBarRec.height, firstSizeOfShell);

                solutionTab = new SolutionTab(shell, formToolkit, recOfContent);
                solutionTab.createContents(controller.getResultWrapper(), controller.getRequirementsWrapper());
                solutionTab.getSaveSolutionOntologyButton().addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        controllerFuture = pool.submit(() -> controller.saveSolutionOntology());
                    }
                });

                loadAndSaveTab = new LoadAndSaveTab(shell, formToolkit, recOfContent);
                loadAndSaveTab.createContents(controller.getResultWrapper(), controller.getRequirementsWrapper());

                createKitLogo(reqNavBarRec);
                int newNavBarY = kitLogo.getBounds().y + kitLogo.getBounds().height + errorTextYOffset;
                requirementsCategory.updateNavBarY(newNavBarY);
                addNavigationBarListener();
                updateSize();
                updateSize();
            }
        });
        componentToBeDesigned.select(0);
        componentToBeDesigned.notifyListeners(SWT.Selection, new Event());

        shell.addListener(SWT.Resize, e -> {
            // have to update twice, because one does not update everything
            updateSize();
            updateSize();
        });
    }

    private void createShell() {
        shell = new Shell();
        shell.setSize(firstSizeOfShell);
        shell.setText("KIT Expert System Humanoid Robot Component Reasoner");
        shell.setImage(SWTResourceManager.getImage(GUI.class, "/logos/H2T_logo.png"));
    }

    private Rectangle createNavigationBar(Rectangle reqNavBarRec) {
        if (!mainNavBars.isEmpty()) {
            mainNavBars.forEach(navBar -> navBar.item.dispose());
            mainNavBars.clear();
        }
        NavigationItem reqItem = new NavigationItem();
        reqItem.name = "Requirements";
        mainNavBars.add(reqItem);

        NavigationItem optimizationItem = new NavigationItem();
        optimizationItem.name = "Optimization";
        mainNavBars.add(optimizationItem);

        NavigationItem solutionItem = new NavigationItem();
        solutionItem.name = "Solution";
        mainNavBars.add(solutionItem);

        NavigationItem loadAndSaveItem = new NavigationItem();
        loadAndSaveItem.name = "Load && Save";
        mainNavBars.add(loadAndSaveItem);

        mainNavBarHelper = new NavigationBarHelper(formToolkit, shell);
        return mainNavBarHelper.createHorizontalNavBar(mainNavBars, 0, RequirementsCategory.contentXOffsetStart +
                reqNavBarRec.x + reqNavBarRec.width);
    }

    private void addNavigationBarListener() {
        mainNavBars.get(0).compositeToHandle = requirementsCategory.getRequirementsOverallForm();
        mainNavBars.get(1).compositeToHandle = requirementsOptimization.getRequirementsOverallForm();
        mainNavBars.get(2).compositeToHandle = solutionTab.getSolutionForm();
        mainNavBars.get(3).compositeToHandle = loadAndSaveTab.getForm();
        mainNavBarHelper.addListener(mainNavBars);

        mainNavBars.get(0).item.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                requirementsCategory.setVisibilityOfNavItems(true);
                requirementsOptimization.setVisibilityOfNavItems(false);
                controllerFuture = pool.submit(() -> controller.reset());
            }
        });

        mainNavBars.get(1).item.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                requirementsCategory.setVisibilityOfNavItems(false);
                requirementsOptimization.setVisibilityOfNavItems(true);
                controllerFuture = pool.submit(() -> controller.reset());
            }
        });

        mainNavBars.get(2).item.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                requirementsCategory.setVisibilityOfNavItems(false);
                requirementsOptimization.setVisibilityOfNavItems(false);
                controller.parseRequirements();
                if (controller.haveRequirementChanged()) {
                    try {
                        controllerFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error(e.getMessage(), e);
                    }
                    controllerFuture = pool.submit(() -> controller.reason());
                }
            }
        });

        mainNavBars.get(3).item.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                requirementsCategory.setVisibilityOfNavItems(false);
                requirementsOptimization.setVisibilityOfNavItems(false);
            }
        });

        mainNavBars.get(0).item.notifyListeners(SWT.Selection, new Event());
    }

    private void updateSize() {
        solutionTab.getSolutionForm().setWeights(getWeights());
        formToolkit.adapt(solutionTab.getSolutionForm());

        loadAndSaveTab.getForm().setWeights(getWeights());
        formToolkit.adapt(loadAndSaveTab.getForm());

        Rectangle updatedRec = requirementsCategory.updateSize(shell.getSize(), getWeights());
        requirementsOptimization.updateSize(updatedRec, getWeights());
        solutionTab.updateSize(updatedRec);
        loadAndSaveTab.updateSize(updatedRec);

        int toggleDescriptionWidth = GuiHelper.getSizeOfControl(toggleDescription).x;
        int toggleDescriptionX = updatedRec.x + updatedRec.width - toggleDescriptionWidth;
        toggleDescription.setBounds(toggleDescriptionX, navBarY, toggleDescriptionWidth, componentToBeDesigned
                .getSize().y);
        formToolkit.adapt(toggleDescription, true, true);


        int componentsToBeDesignedComboWidth = GuiHelper.getSizeOfControl(componentToBeDesigned).x;
        int componentsToBeDesignedComboX = toggleDescriptionX - componentsToBeDesignedComboWidth - 5;
        componentToBeDesigned.setBounds(componentsToBeDesignedComboX, navBarY, componentsToBeDesignedComboWidth,
                componentToBeDesigned.getSize().y);
        formToolkit.adapt(componentToBeDesigned, true, true);

        int errorTextY = updatedRec.height + updatedRec.y + errorTextYOffset;
        errorText.setBounds(updatedRec.x, errorTextY, updatedRec.width, errorTextHeight);
        formToolkit.adapt(errorText);
    }

    private int[] getWeights() {
        //TODO make dependend on resolution -> test by matze
        float newWidthOfContent = 1f * 835 * shell.getSize().x / 1000;
        if (showDescriptions) {
            float ratioForWeights = 1f * newWidthOfContent / 835;
            return new int[]{Math.round(350 * ratioForWeights), 1, Math.round(310 / ratioForWeights)};
        } else {
            return new int[]{Math.round(newWidthOfContent), 0, 0};
        }

    }

    private void createErrorText() {
        errorText = new StyledText(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        errorText.setFont(SWTResourceManager.getFont("Courier New", GuiHelper.getFontHeight(errorText.getFont()), SWT
                .NORMAL));
        errorText.setEditable(false);
        MyAppenderForGui lastInstance = MyAppenderForGui.getLastInstance();
        if (lastInstance != null) {
            lastInstance.setGui(this);
        }
    }

    private void createKitLogo(Rectangle recToFill) {
        if (kitLogo != null) {
            kitLogo.dispose();
        }
        kitLogo = new Label(shell, SWT.CENTER);
        int logoHeight = (int) Math.round(39.0 / 86.0 * recToFill.width);
        kitLogo.setBounds(recToFill.x, navBarY, recToFill.width, logoHeight);
        kitLogo.setImage(resizeImage(SWTResourceManager.getImage(GUI.class, "/logos/KIT_logo.png"), recToFill.width,
                logoHeight));
        formToolkit.adapt(kitLogo, false, false);
    }

    private Image resizeImage(Image image, int width, int height) {
        Image scaled = new Image(display, width, height);
        GC gc = new GC(scaled);
        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.HIGH);
        gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
        gc.dispose();
        return scaled;
    }

    public void setErrorText(String message, Level level) {
        display.asyncExec(() -> {
            if (errorText == null) {
                return;
            }
            StyleRange[] oldStyleRanges = errorText.getStyleRanges();

            String newMessage = message + "\n";
            errorText.setText(newMessage + errorText.getText());

            StyleRange[] newStyleRanges = new StyleRange[oldStyleRanges.length + 1];
            newStyleRanges[0] = new StyleRange(0, newMessage.length(), matchLevelToColor(level), null, SWT.NONE);

            for (int i = 0; i < oldStyleRanges.length; ++i) {
                oldStyleRanges[i].start += newMessage.length();
                newStyleRanges[i + 1] = oldStyleRanges[i];
            }
            errorText.setStyleRanges(newStyleRanges);
        });
    }

    private Color matchLevelToColor(Level level) {
        if (Level.FATAL.equals(level) || Level.ERROR.equals(level)) {
            return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        }
        if (Level.WARN.equals(level)) {
            return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW);
        }
        // default
        return null;
    }
}
