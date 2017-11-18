package edu.kit.expertsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.kit.expertsystem.controller.Controller;
import edu.kit.expertsystem.controller.NavigationItem;

public class GUI {

    public static final int navBarY = 10;
    public static final int errorTextHeight = 83;
    public static final int errorTextYOffset = 2;
    private static final int[] contentWeights = new int[] { 339, 1, 291 };
    private Point sizeOfShell = new Point(1000, 600);

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
    private StyledText errorText;

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
        controller = new Controller(this);
        controller.initialize();
    }

    /**
     * Open the window.
     *
     * @wbp.parser.entryPoint
     */
    public void open() {
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
            logger.info("Do not startOnSecondScreen.");
            return;
        }
        Rectangle monitorRect = monitors[1].getBounds();
        Rectangle shellRect = shell.getBounds();
        int x = monitorRect.x + ((monitorRect.width - shellRect.width) / 2);
        int y = monitorRect.y + ((monitorRect.height - shellRect.height) / 2);
        shell.setLocation(x, y);
        logger.info("StartOnSecondScreen");
    }

    private void startOnTopOfScreen() {
        Rectangle monitorRect = display.getPrimaryMonitor().getBounds();
        Rectangle shellRect = shell.getBounds();
        int x = monitorRect.x + ((monitorRect.width - shellRect.width) / 2);
        shell.setLocation(x, monitorRect.y);
        logger.info("startOnTopOfScreen");
    }

    private void shutdown() {
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
        createShell();

        requirementsCategory = new RequirementsCategory(shell, formToolkit);
        Rectangle reqNavBarRec = requirementsCategory.createNavBars(controller.getRequirementsWrapper());
        Rectangle mainNavBarRec = createNavigationBar(reqNavBarRec);

        Rectangle contentRec = requirementsCategory.createReqContent(
                controller.getRequirementDependencyWrapper(), contentWeights,
                mainNavBarRec.y + mainNavBarRec.height, sizeOfShell);

        solutionTab = new SolutionTab(shell, formToolkit, contentRec);
        solutionTab.createContents(controller.getResultWrapper(), controller.getRequirementsWrapper());
        solutionTab.getSolutionForm().setWeights(contentWeights);

        createErrorText(contentRec);
        createKitLogo(reqNavBarRec);
        addNavigationBarListener();

        shell.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event e) {
                sizeOfShell = shell.getSize();
                Rectangle updatedRec = requirementsCategory.updateSize(sizeOfShell);
                solutionTab.updateSize(updatedRec);

                int errorTextY = updatedRec.height + updatedRec.y + errorTextYOffset;
                errorText.setBounds(updatedRec.x, errorTextY, updatedRec.width, errorTextHeight);
                formToolkit.adapt(errorText);
                formToolkit.paintBordersFor(errorText);
            }
        });
    }

    private void createShell() {
        shell = new Shell();
        shell.setSize(sizeOfShell);
        shell.setText("KIT Sensor-Actuator-Controller Unit Generator");
        shell.setImage(SWTResourceManager.getImage(GUI.class, "/H2T_logo_resized.png"));
    }

    private Rectangle createNavigationBar(Rectangle reqNavBarRec) {
        NavigationItem reqItem = new NavigationItem();
        reqItem.name = "Requirements";
        mainNavBars.add(reqItem);

        NavigationItem solutionItem = new NavigationItem();
        solutionItem.name = "Solution";
        mainNavBars.add(solutionItem);

        mainNavBarHelper = new NavigationBarHelper(formToolkit, shell);
        return mainNavBarHelper.createHorizontalNavBar(mainNavBars, 0,
                RequirementsCategory.contentXOffsetStart + reqNavBarRec.x + reqNavBarRec.width);
    }

    private void addNavigationBarListener() {
        mainNavBars.get(0).compositeToHandle = requirementsCategory.getRequirementsOverallForm();
        mainNavBars.get(1).compositeToHandle = solutionTab.getSolutionForm();
        mainNavBarHelper.addListener(mainNavBars);

        mainNavBars.get(0).item.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                controllerFuture.cancel(true);
                controllerFuture = pool.submit(() -> controller.reset());
            }
        });

        mainNavBars.get(1).item.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                try {
                    controllerFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(e.getMessage(), e);
                    errorText.setText(e.getLocalizedMessage());
                    return;
                }
                controller.parseRequirements();
                controllerFuture = pool.submit(() -> controller.reason());
            }
        });
        mainNavBars.get(0).item.notifyListeners(SWT.Selection, new Event());
    }

    private void createErrorText(Rectangle contentRec) {
        errorText = new StyledText(shell, SWT.BORDER | SWT.WRAP);
        int errorTextY = contentRec.height + contentRec.y + errorTextYOffset;
        errorText.setBounds(contentRec.x, errorTextY, contentRec.width, errorTextHeight);
        errorText.setEditable(false);
        formToolkit.adapt(errorText);
        formToolkit.paintBordersFor(errorText);
    }

    private void createKitLogo(Rectangle recToFill) {
        Label kitLogo = new Label(shell, SWT.CENTER);
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
        image.dispose();
        return scaled;
    }

    public void setErrorText(String message) {
        errorText.setText(message);
    }
}
