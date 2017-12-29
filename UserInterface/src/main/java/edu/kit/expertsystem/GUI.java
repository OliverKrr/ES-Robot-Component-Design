package edu.kit.expertsystem;

import edu.kit.expertsystem.controller.Controller;
import edu.kit.expertsystem.controller.NavigationItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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

public class GUI {

    public static final int navBarY = 10;
    public static final int errorTextHeight = 115;
    public static final int errorTextYOffset = 2;
    public static final int comboOffsetWidth = 24;
    private static final Point firstSizeOfShell = new Point(1000, 600);

    private static final Logger logger = LogManager.getLogger(GUI.class);

    private Shell shell;
    private static final Display display = Display.getDefault();
    private final FormToolkit formToolkit = new FormToolkit(display);

    private RequirementsCategory requirementsCategory;
    private SolutionTab solutionTab;

    private final ExecutorService pool;
    private Future<?> controllerFuture;
    private Controller controller;

    private NavigationBarHelper mainNavBarHelper;
    private List<NavigationItem> mainNavBars = new ArrayList<>();
    private Combo unitsToReasonCombo;
    private StyledText errorText;
    private Label kitLogo;

    /**
     * Launch the application.
     *
     * @param args
     * @wbp.parser.entryPoint
     */
    public static void main(String[] args) {
        try {
            Configs.initConfig(display);
            GUI window = new GUI();
            window.open();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public GUI() {
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

        shell.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent event) {
                shutdown();
            }
        });
        // startOnSecondScreenIfPossible();
        startOnTopOfScreen();
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
    protected void createContents() {
        unitsToReasonCombo = new Combo(shell, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        controller.getUnitsToReason().forEachOrdered(unit -> unitsToReasonCombo.add(unit));
        unitsToReasonCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                controller.updateUnitToReason(unitsToReasonCombo.getText());
                if (requirementsCategory != null) {
                    requirementsCategory.disposeNavItems();
                    requirementsCategory.getRequirementsOverallForm().dispose();
                    solutionTab.getSolutionForm().dispose();
                }
                requirementsCategory = new RequirementsCategory(shell, formToolkit);
                Rectangle reqNavBarRec = requirementsCategory.createNavBars(controller.getRequirementsWrapper());
                Rectangle mainNavBarRec = createNavigationBar(reqNavBarRec);
                unitsToReasonCombo.setSize(0, mainNavBarRec.height);

                Rectangle recOfContent = requirementsCategory.createReqContent(controller
                        .getRequirementDependencyWrapper(), mainNavBarRec.y + mainNavBarRec.height, firstSizeOfShell);

                solutionTab = new SolutionTab(shell, formToolkit, recOfContent);
                solutionTab.createContents(controller.getResultWrapper(), controller.getRequirementsWrapper());
                solutionTab.getSaveSolutionOntologyButton().addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        controllerFuture = pool.submit(() -> controller.saveSolutionOntology());
                    }
                });

                createKitLogo(reqNavBarRec);
                addNavigationBarListener();
                updateSize();
            }
        });
        unitsToReasonCombo.select(0);
        unitsToReasonCombo.notifyListeners(SWT.Selection, new Event());

        shell.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event e) {
                // have to update twice, because one does not update everything
                updateSize();
                updateSize();
            }
        });
    }

    private void createShell() {
        shell = new Shell();
        shell.setSize(firstSizeOfShell);
        shell.setText("KIT Sensor-Actuator-Controller Unit Generator");
        shell.setImage(SWTResourceManager.getImage(GUI.class, "/H2T_logo_resized.png"));
    }

    private Rectangle createNavigationBar(Rectangle reqNavBarRec) {
        if (!mainNavBars.isEmpty()) {
            mainNavBars.forEach(navBar -> navBar.item.dispose());
            mainNavBars.clear();
        }
        NavigationItem reqItem = new NavigationItem();
        reqItem.name = "Requirements";
        mainNavBars.add(reqItem);

        NavigationItem solutionItem = new NavigationItem();
        solutionItem.name = "Solution";
        mainNavBars.add(solutionItem);

        mainNavBarHelper = new NavigationBarHelper(formToolkit, shell);
        return mainNavBarHelper.createHorizontalNavBar(mainNavBars, 0, RequirementsCategory.contentXOffsetStart +
                reqNavBarRec.x + reqNavBarRec.width);
    }

    private void addNavigationBarListener() {
        mainNavBars.get(0).compositeToHandle = requirementsCategory.getRequirementsOverallForm();
        mainNavBars.get(1).compositeToHandle = solutionTab.getSolutionForm();
        mainNavBarHelper.addListener(mainNavBars);

        mainNavBars.get(0).item.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                requirementsCategory.setVisibilityOfNavItems(true);
                controllerFuture = pool.submit(() -> controller.reset());
            }
        });

        mainNavBars.get(1).item.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                requirementsCategory.setVisibilityOfNavItems(false);
                try {
                    controllerFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(e.getMessage(), e);
                    return;
                }
                controller.parseRequirements();
                controllerFuture = pool.submit(() -> controller.reason());
            }
        });
        mainNavBars.get(0).item.notifyListeners(SWT.Selection, new Event());
    }

    private void updateSize() {
        solutionTab.getSolutionForm().setWeights(getWeights());
        formToolkit.adapt(solutionTab.getSolutionForm());

        Rectangle updatedRec = requirementsCategory.updateSize(shell.getSize(), getWeights());
        solutionTab.updateSize(updatedRec);

        int unitsToReasonComboWidth = getUnitsToReasoneComboWidth();
        int unitToReasonComboX = updatedRec.x + updatedRec.width - unitsToReasonComboWidth;
        unitsToReasonCombo.setBounds(unitToReasonComboX, navBarY, unitsToReasonComboWidth, unitsToReasonCombo.getSize
                ().y);
        formToolkit.adapt(unitsToReasonCombo, true, true);

        int errorTextY = updatedRec.height + updatedRec.y + errorTextYOffset;
        errorText.setBounds(updatedRec.x, errorTextY, updatedRec.width, errorTextHeight);
        formToolkit.adapt(errorText);
    }

    private int[] getWeights() {
        float newWidthOfContent = 1f * 835 * shell.getSize().x / 1000;
        float ratioForWeights = 1f * newWidthOfContent / 835;
        int[] newContentWeights = {Math.round(350 * ratioForWeights), 1, Math.round(280 / ratioForWeights)};
        return newContentWeights;
    }

    private int getUnitsToReasoneComboWidth() {
        int maxWidth = 0;
        for (String unit : unitsToReasonCombo.getItems()) {
            Point size = GuiHelper.getSizeOfText(unitsToReasonCombo, unit);
            maxWidth = Math.max(maxWidth, size.x);
        }
        return maxWidth + comboOffsetWidth;
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
        kitLogo.setImage(resizeImage(SWTResourceManager.getImage(GUI.class, "/KIT_logo.png"), recToFill.width,
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

    public void setErrorText(String message, Color color) {
        display.asyncExec(() -> {
            if (errorText == null) {
                return;
            }
            StyleRange[] oldStyleRanges = errorText.getStyleRanges();

            String newMessage = message + "\n";
            errorText.setText(newMessage + errorText.getText());

            StyleRange[] newStyleRanges = new StyleRange[oldStyleRanges.length + 1];
            newStyleRanges[0] = new StyleRange(0, newMessage.length(), color, null, SWT.NONE);

            for (int i = 0; i < oldStyleRanges.length; ++i) {
                oldStyleRanges[i].start += newMessage.length();
                newStyleRanges[i + 1] = oldStyleRanges[i];
            }
            errorText.setStyleRanges(newStyleRanges);
        });
    }
}
