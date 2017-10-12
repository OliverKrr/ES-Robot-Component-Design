package edu.kit.expertsystem;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

public class GUI {

    private static final Logger logger = LogManager.getLogger(GUI.class);

    public static boolean isTest = false;

    public Text minimumSpeedMIN;
    public Text minimumTorqueMIN;
    public Text minimumSpeedMAX;
    public Text minimumTorqueMAX;
    public Text weightMin;
    public Text weightMax;
    public Tree tree;

    public Shell shell;
    private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());

    private final ExecutorService pool;
    private Future<?> controllerFuture;
    AtomicBoolean isControllerCreation = new AtomicBoolean(true);
    private Controller controller;

    private AtomicBoolean isSolutionReady = new AtomicBoolean(false);

    /**
     * Launch the application.
     *
     * @param args
     * @wbp.parser.entryPoint
     */
    public static void main(String[] args) {
        try {
            GUI window = new GUI();
            window.open();
            window.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    GUI() {
        pool = Executors.newSingleThreadExecutor();
        controllerFuture = pool.submit(() -> {
            controller = new Controller(this);
            if (isTest) {
                controller.reason();
            }
            isControllerCreation.set(false);
            logger.info("Controller creation fininshed.");
        });
    }

    /**
     * Open the window.
     *
     * @wbp.parser.entryPoint
     */
    public void open() {
        if (isTest) {
            return;
        }
        Display display = Display.getDefault();
        createContents();
        startOnSecondScreenIfPossible();
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

    private void startOnSecondScreenIfPossible() {
        Monitor[] monitors = shell.getDisplay().getMonitors();
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

    private void shutdown() throws InterruptedException, ExecutionException {
        if (isTest && isControllerCreation.get()) {
            controllerFuture.get();
        } else {
            controllerFuture.cancel(true);
        }
        pool.shutdown();
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
        shell.setSize(901, 407);
        shell.setText("KIT Sensor-Actuator-Controller Unit Generator");
        shell.setLayout(null);
        shell.setImage(SWTResourceManager.getImage(GUI.class, "/H2T_logo_resized.png"));

        Label lblNewLabel = new Label(shell, SWT.NONE);
        lblNewLabel.setBounds(4, 5, 86, 39);
        lblNewLabel.setImage(SWTResourceManager.getImage(GUI.class, "/KIT_logo_resized.png"));
        formToolkit.adapt(lblNewLabel, true, true);

        TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
        tabFolder.setBounds(96, 10, 779, 348);
        formToolkit.adapt(tabFolder);
        formToolkit.paintBordersFor(tabFolder);

        TabItem tbtmRequirements = new TabItem(tabFolder, SWT.NONE);
        tbtmRequirements.setText("Requirements");

        SashForm sashForm = new SashForm(tabFolder, SWT.NONE);
        tbtmRequirements.setControl(sashForm);
        formToolkit.paintBordersFor(sashForm);

        Composite composite = new Composite(sashForm, SWT.NONE);
        formToolkit.adapt(composite);
        formToolkit.paintBordersFor(composite);

        Label lblMinimumTorque = new Label(composite, SWT.NONE);
        lblMinimumTorque.setBounds(10, 10, 182, 23);
        lblMinimumTorque.setText("Peak Torque M_max:");
        formToolkit.adapt(lblMinimumTorque, true, true);
        lblMinimumTorque.setForeground(Configs.getKitGreen70(shell.getDisplay()));
        lblMinimumTorque.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.getKitGreen70(shell.getDisplay()).dispose();
            }
        });

        Label lblMinimumSpeed = new Label(composite, SWT.NONE);
        lblMinimumSpeed.setBounds(10, 46, 182, 23);
        lblMinimumSpeed.setText("Maximal Speed n_max:");
        formToolkit.adapt(lblMinimumSpeed, true, true);
        lblMinimumSpeed.setForeground(Configs.getKitGreen70(shell.getDisplay()));
        lblMinimumSpeed.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.getKitGreen70(shell.getDisplay()).dispose();
            }
        });

        minimumSpeedMIN = new Text(composite, SWT.BORDER);
        minimumSpeedMIN.setBounds(198, 46, 40, 23);
        minimumSpeedMIN.setMessage("min");
        formToolkit.adapt(minimumSpeedMIN, true, true);

        Label lblMm = new Label(composite, SWT.NONE);
        lblMm.setBounds(244, 49, 44, 23);
        formToolkit.adapt(lblMm, true, true);
        lblMm.setText("°/s");

        Label lblNm = new Label(composite, SWT.NONE);
        lblNm.setText("Nm");
        lblNm.setBounds(244, 13, 44, 23);
        formToolkit.adapt(lblNm, true, true);

        minimumTorqueMIN = new Text(composite, SWT.BORDER);
        minimumTorqueMIN.setMessage("min");
        minimumTorqueMIN.setBounds(198, 10, 40, 23);
        formToolkit.adapt(minimumTorqueMIN, true, true);

        Label lblMin = new Label(composite, SWT.NONE);
        lblMin.setText("°/s");
        lblMin.setBounds(340, 49, 44, 23);
        formToolkit.adapt(lblMin, true, true);

        minimumSpeedMAX = new Text(composite, SWT.BORDER);
        minimumSpeedMAX.setEnabled(false);
        minimumSpeedMAX.setMessage("max");
        minimumSpeedMAX.setBounds(294, 46, 40, 23);
        formToolkit.adapt(minimumSpeedMAX, true, true);

        Label lblNm_1 = new Label(composite, SWT.NONE);
        lblNm_1.setText("Nm");
        lblNm_1.setBounds(340, 13, 44, 23);
        formToolkit.adapt(lblNm_1, true, true);

        minimumTorqueMAX = new Text(composite, SWT.BORDER);
        minimumTorqueMAX.setEnabled(false);
        minimumTorqueMAX.setMessage("max");
        minimumTorqueMAX.setBounds(294, 10, 40, 23);
        formToolkit.adapt(minimumTorqueMAX, true, true);

        Label lblWeightM = new Label(composite, SWT.NONE);
        lblWeightM.setText("Weight m:");
        lblWeightM.setBounds(10, 82, 182, 23);
        formToolkit.adapt(lblWeightM, true, true);
        lblWeightM.setForeground(Configs.getKitGreen70(shell.getDisplay()));
        lblWeightM.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.getKitGreen70(shell.getDisplay()).dispose();
            }
        });

        weightMin = new Text(composite, SWT.BORDER);
        weightMin.setEnabled(false);
        weightMin.setMessage("min");
        weightMin.setBounds(198, 82, 40, 23);
        formToolkit.adapt(weightMin, true, true);

        Label lblKg = new Label(composite, SWT.NONE);
        lblKg.setText("kg");
        lblKg.setBounds(244, 85, 44, 23);
        formToolkit.adapt(lblKg, true, true);

        weightMax = new Text(composite, SWT.BORDER);
        weightMax.setMessage("max");
        weightMax.setBounds(294, 82, 40, 23);
        formToolkit.adapt(weightMax, true, true);

        Label lblKg_1 = new Label(composite, SWT.NONE);
        lblKg_1.setText("kg");
        lblKg_1.setBounds(340, 85, 44, 23);
        formToolkit.adapt(lblKg_1, true, true);

        Button btnEnableFields = new Button(composite, SWT.CHECK);
        btnEnableFields.setBounds(307, 294, 93, 16);
        formToolkit.adapt(btnEnableFields, true, true);
        btnEnableFields.setText("Enable fields");
        btnEnableFields.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (btnEnableFields.getSelection()) {
                    btnEnableFields.setText("Disable fields");
                    weightMin.setEnabled(true);
                    minimumSpeedMAX.setEnabled(true);
                    minimumTorqueMAX.setEnabled(true);
                } else {
                    btnEnableFields.setText("Enable fields");
                    weightMin.setEnabled(false);
                    minimumSpeedMAX.setEnabled(false);
                    minimumTorqueMAX.setEnabled(false);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                // nothing to do
            }
        });

        Label label_4 = new Label(sashForm, SWT.SEPARATOR | SWT.VERTICAL);
        formToolkit.adapt(label_4, true, true);

        Composite composite2 = new Composite(sashForm, SWT.NONE);
        formToolkit.adapt(composite2);
        formToolkit.paintBordersFor(composite2);
        Label lblMinmax = new Label(composite2, SWT.NONE);
        lblMinmax.setText("min/max:");
        lblMinmax.setBounds(10, 10, 137, 34);
        formToolkit.adapt(lblMinmax, true, true);
        lblMinmax.setForeground(Configs.getKitGreen70(shell.getDisplay()));
        lblMinmax.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.getKitGreen70(shell.getDisplay()).dispose();
            }
        });
        StyledText styledText_2 = new StyledText(composite2, SWT.BORDER);
        styledText_2.setText("Desired min and max values.\r\nIf no entered, defaults are taken.");
        styledText_2.setEditable(false);
        styledText_2.setBounds(153, 10, 191, 57);
        formToolkit.adapt(styledText_2);
        formToolkit.paintBordersFor(styledText_2);
        createCommonDescriptions(composite2);
        sashForm.setWeights(new int[] { 339, 1, 291 });

        TabItem tbtmSolution = new TabItem(tabFolder, SWT.NONE);
        tbtmSolution.setText("Solution");

        SashForm sashForm_1 = new SashForm(tabFolder, SWT.NONE);
        tbtmSolution.setControl(sashForm_1);
        formToolkit.paintBordersFor(sashForm_1);

        Composite composite_1 = new Composite(sashForm_1, SWT.NONE);
        formToolkit.adapt(composite_1);
        formToolkit.paintBordersFor(composite_1);

        tree = new Tree(composite_1, SWT.NONE);
        tree.setBounds(10, 10, 385, 298);
        formToolkit.adapt(tree);
        formToolkit.paintBordersFor(tree);

        Label label_11 = new Label(sashForm_1, SWT.SEPARATOR);
        formToolkit.adapt(label_11, true, true);

        Composite composite_2 = new Composite(sashForm_1, SWT.NONE);
        formToolkit.adapt(composite_2);
        formToolkit.paintBordersFor(composite_2);
        createCommonDescriptions(composite_2);
        sashForm_1.setWeights(new int[] { 339, 1, 291 });

        tabFolder.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (tabFolder.getSelectionIndex() == 1) {
                    try {
                        controllerFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return;
                    }
                    controller.parseRequirements();
                    controllerFuture = pool.submit(() -> controller.reason());
                } else {
                    if (!isControllerCreation.get()) {
                        controllerFuture.cancel(true);
                        controllerFuture = pool.submit(() -> controller.reset());
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                // nothing to do
            }
        });
    }

    private void createCommonDescriptions(Composite composite) {
        Label maximalRotationSpeedDescription = new Label(composite, SWT.NONE);
        maximalRotationSpeedDescription.setText("Maximal Speed n_max:");
        maximalRotationSpeedDescription.setBounds(10, 161, 137, 34);
        formToolkit.adapt(maximalRotationSpeedDescription, true, true);
        maximalRotationSpeedDescription.setForeground(Configs.getKitGreen70(shell.getDisplay()));
        maximalRotationSpeedDescription.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.getKitGreen70(shell.getDisplay()).dispose();
            }
        });

        Label lblMaxMinimumTorque = new Label(composite, SWT.NONE);
        lblMaxMinimumTorque.setText("Peak Torque M_max:");
        lblMaxMinimumTorque.setBounds(10, 98, 137, 34);
        formToolkit.adapt(lblMaxMinimumTorque, true, true);
        lblMaxMinimumTorque.setForeground(Configs.getKitGreen70(shell.getDisplay()));
        lblMaxMinimumTorque.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.getKitGreen70(shell.getDisplay()).dispose();
            }
        });

        StyledText styledText = new StyledText(composite, SWT.BORDER);
        styledText.setText("Maximal output speed at nominal\r\nvoltage.");
        styledText.setEditable(false);
        styledText.setBounds(153, 161, 191, 57);
        formToolkit.adapt(styledText);
        formToolkit.paintBordersFor(styledText);

        StyledText maximalTorqueDescription = new StyledText(composite, SWT.BORDER);
        maximalTorqueDescription.setText("Repeated peak torque without\r\ndamaging the units.");
        maximalTorqueDescription.setEditable(false);
        maximalTorqueDescription.setBounds(153, 98, 191, 57);
        formToolkit.adapt(maximalTorqueDescription);
        formToolkit.paintBordersFor(maximalTorqueDescription);

        StyledText styledText_1 = new StyledText(composite, SWT.BORDER);
        styledText_1.setText("The total weight of the motor and\r\nthe gear box.");
        styledText_1.setEditable(false);
        styledText_1.setBounds(153, 224, 191, 57);
        formToolkit.adapt(styledText_1);
        formToolkit.paintBordersFor(styledText_1);

        Label lblWeightM_1 = new Label(composite, SWT.NONE);
        lblWeightM_1.setText("Weight m:");
        lblWeightM_1.setBounds(10, 224, 137, 34);
        formToolkit.adapt(lblWeightM_1, true, true);
        lblWeightM_1.setForeground(Configs.getKitGreen70(shell.getDisplay()));
        lblWeightM_1.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.getKitGreen70(shell.getDisplay()).dispose();
            }
        });
    }
}
