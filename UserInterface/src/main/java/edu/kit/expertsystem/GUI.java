package edu.kit.expertsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.kit.expertsystem.controller.Controller;
import edu.kit.expertsystem.controller.NavigationItem;

public class GUI {

    private static final Logger logger = LogManager.getLogger(GUI.class);

    private Shell shell;
    private static final Display display = Display.getDefault();
    private final FormToolkit formToolkit = new FormToolkit(display);

    private RequirementsTab requirementsTab;
    private SolutionTab solutionTab;

    private final ExecutorService pool;
    private Future<?> controllerFuture;
    private Controller controller;

    private AtomicBoolean isSolutionReady = new AtomicBoolean(false);

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

        controllerFuture = pool.submit(() -> {
            controller.initialStartForCaching();
        });

        shell.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent arg0) {
                shutdown();
            }
        });
        // startOnSecondScreenIfPossible();
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (isSolutionReady.get()) {
                controller.setResults();
                isSolutionReady.set(false);
            }
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
        int x = monitorRect.x + (monitorRect.width - shellRect.width) / 2;
        int y = monitorRect.y + (monitorRect.height - shellRect.height) / 2;
        shell.setLocation(x, y);
        logger.info("StartOnSecondScreen.");
    }

    private void shutdown() {
        controllerFuture.cancel(true);
        pool.shutdownNow();
    }

    public void notifySolutionIsReady() {
        isSolutionReady.set(true);
        shell.dispose();
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shell = new Shell();
        shell.setSize(1000, 600);
        shell.setText("KIT Sensor-Actuator-Controller Unit Generator");
        shell.setLayout(null);
        shell.setImage(SWTResourceManager.getImage(GUI.class, "/H2T_logo_resized.png"));

        errorText = new StyledText(shell, SWT.BORDER);
        errorText.setBounds(100, 389, 779, 83);
        errorText.setEditable(false);
        formToolkit.adapt(errorText);
        formToolkit.paintBordersFor(errorText);

        Label kitLogo = new Label(shell, SWT.NONE);
        kitLogo.setBounds(5, 10, 86, 39);
        kitLogo.setImage(SWTResourceManager.getImage(GUI.class, "/KIT_logo_resized.png"));
        formToolkit.adapt(kitLogo, true, true);

        Rectangle sizeOfForms = new Rectangle(100, 35, 779, 348);
        requirementsTab = new RequirementsTab(shell, formToolkit, sizeOfForms);
        solutionTab = new SolutionTab(shell, formToolkit, sizeOfForms);

        Button defaultNavItem = createNavigationBar();

        requirementsTab.createContents(controller.getRequirementsWrapper());
        int[] weights = new int[] { 339, 1, 291 };
        requirementsTab.getRequirementsForm().setWeights(weights);
        solutionTab.createContents(controller.getResultWrapper(), controller.getRequirementsWrapper());
        solutionTab.getSolutionForm().setWeights(weights);

        defaultNavItem.notifyListeners(SWT.Selection, new Event());
    }

    private Button createNavigationBar() {
        List<NavigationItem> mainBar = new ArrayList<>();

        NavigationItem reqItem = new NavigationItem();
        reqItem.name = "Requirements";
        reqItem.compositeToHandle = requirementsTab.getRequirementsForm();
        mainBar.add(reqItem);

        NavigationItem solutionItem = new NavigationItem();
        solutionItem.name = "Solution";
        solutionItem.compositeToHandle = solutionTab.getSolutionForm();
        mainBar.add(solutionItem);

        NavigationBarHelper navHelper = new NavigationBarHelper(formToolkit, shell);
        navHelper.createHorizontalNavBar(mainBar, 0);

        mainBar.get(0).item.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                controllerFuture.cancel(true);
                controllerFuture = pool.submit(() -> controller.reset());
            }
        });

        mainBar.get(1).item.addSelectionListener(new SelectionAdapter() {

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

        return mainBar.get(0).item;
    }

}
