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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

public class GUI {

    private static final Logger logger = LogManager.getLogger(GUI.class);

    public Text minimumSpeedMIN;
    public Text minimumTorqueMIN;
    public Text minimumSpeedMAX;
    public Text minimumTorqueMAX;
    public Text weightMin;
    public Text weightMax;
    public Tree tree;

    private Shell shell;
    private static final Display display = Display.getDefault();
    private final FormToolkit formToolkit = new FormToolkit(display);

    private final ExecutorService pool;
    private Future<?> controllerFuture;
    AtomicBoolean isControllerCreation = new AtomicBoolean(true);
    private Controller controller;

    private AtomicBoolean isSolutionReady = new AtomicBoolean(false);

    private SashForm requirementsForm;

    private SashForm solutionForm;

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

    GUI() {
        pool = Executors.newSingleThreadExecutor();
        controllerFuture = pool.submit(() -> {
            controller = new Controller(this);
            controller.initialize();
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
        createContents();
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
        shell.setSize(895, 511);
        shell.setText("KIT Sensor-Actuator-Controller Unit Generator");
        shell.setLayout(null);
        shell.setImage(SWTResourceManager.getImage(GUI.class, "/H2T_logo_resized.png"));

        errorText = new StyledText(shell, SWT.BORDER);
        errorText.setBounds(96, 389, 779, 83);
        errorText.setEditable(false);
        formToolkit.adapt(errorText);
        formToolkit.paintBordersFor(errorText);

        Label lblNewLabel = new Label(shell, SWT.NONE);
        lblNewLabel.setBounds(5, 10, 86, 39);
        lblNewLabel.setImage(SWTResourceManager.getImage(GUI.class, "/KIT_logo_resized.png"));
        formToolkit.adapt(lblNewLabel, true, true);

        Button btnSolution = new Button(shell, SWT.NONE);
        Button btnRequirements = new Button(shell, SWT.NONE);
        btnRequirements.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
        btnRequirements.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                btnRequirements.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
                btnSolution.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
                btnRequirements.setBackground(Configs.KIT_GREY_50);
                btnSolution.setBackground(Configs.KIT_GREY_15);
                requirementsForm.setVisible(true);
                solutionForm.setVisible(false);

                if (!isControllerCreation.get()) {
                    controllerFuture.cancel(true);
                    controllerFuture = pool.submit(() -> controller.reset());
                }

            }
        });
        btnRequirements.setBounds(96, 10, 120, 25);
        formToolkit.adapt(btnRequirements, true, true);
        btnRequirements.setText("Requirements");
        btnRequirements.setBackground(Configs.KIT_GREY_50);
        btnRequirements.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.KIT_GREY_50.dispose();
            }
        });

        btnSolution.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        btnSolution.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                btnRequirements.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
                btnSolution.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
                btnRequirements.setBackground(Configs.KIT_GREY_15);
                btnSolution.setBackground(Configs.KIT_GREY_50);
                requirementsForm.setVisible(false);
                solutionForm.setVisible(true);

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
        btnSolution.setText("Solution");
        btnSolution.setBounds(212, 10, 120, 25);
        formToolkit.adapt(btnSolution, true, true);
        btnSolution.setBackground(Configs.KIT_GREY_15);
        btnSolution.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.KIT_GREY_15.dispose();
            }
        });

        createRequirementsForm();
        createSolutionPage();
        solutionForm.setVisible(false);
    }

    private void createRequirementsForm() {
        requirementsForm = new SashForm(shell, SWT.NONE);
        requirementsForm.setBounds(96, 35, 779, 348);
        formToolkit.adapt(requirementsForm);
        formToolkit.paintBordersFor(requirementsForm);

        Composite composite = new Composite(requirementsForm, SWT.NONE);
        formToolkit.adapt(composite);
        formToolkit.paintBordersFor(composite);

        Label lblMinimumTorque = new Label(composite, SWT.NONE);
        lblMinimumTorque.setBounds(10, 36, 182, 23);
        lblMinimumTorque.setText("Peak Torque M_max:");
        formToolkit.adapt(lblMinimumTorque, true, true);
        lblMinimumTorque.setForeground(Configs.KIT_GREEN_70);
        lblMinimumTorque.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.KIT_GREEN_70.dispose();
            }
        });

        Label lblMinimumSpeed = new Label(composite, SWT.NONE);
        lblMinimumSpeed.setBounds(10, 72, 182, 23);
        lblMinimumSpeed.setText("Maximal Speed n_max:");
        formToolkit.adapt(lblMinimumSpeed, true, true);
        lblMinimumSpeed.setForeground(Configs.KIT_GREEN_70);
        lblMinimumSpeed.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.KIT_GREEN_70.dispose();
            }
        });

        minimumSpeedMIN = new Text(composite, SWT.BORDER);
        minimumSpeedMIN.setBounds(198, 72, 40, 23);
        minimumSpeedMIN.setMessage("min");
        formToolkit.adapt(minimumSpeedMIN, true, true);

        Label lblMm = new Label(composite, SWT.NONE);
        lblMm.setBounds(244, 75, 44, 23);
        formToolkit.adapt(lblMm, true, true);
        lblMm.setText("°/s");

        Label lblNm = new Label(composite, SWT.NONE);
        lblNm.setText("Nm");
        lblNm.setBounds(244, 39, 44, 23);
        formToolkit.adapt(lblNm, true, true);

        minimumTorqueMIN = new Text(composite, SWT.BORDER);
        minimumTorqueMIN.setMessage("min");
        minimumTorqueMIN.setBounds(198, 36, 40, 23);
        formToolkit.adapt(minimumTorqueMIN, true, true);

        Label lblMin = new Label(composite, SWT.NONE);
        lblMin.setText("°/s");
        lblMin.setBounds(340, 75, 44, 23);
        formToolkit.adapt(lblMin, true, true);

        minimumSpeedMAX = new Text(composite, SWT.BORDER);
        minimumSpeedMAX.setEnabled(false);
        minimumSpeedMAX.setMessage("max");
        minimumSpeedMAX.setBounds(294, 72, 40, 23);
        formToolkit.adapt(minimumSpeedMAX, true, true);

        Label lblNm_1 = new Label(composite, SWT.NONE);
        lblNm_1.setText("Nm");
        lblNm_1.setBounds(340, 39, 44, 23);
        formToolkit.adapt(lblNm_1, true, true);

        minimumTorqueMAX = new Text(composite, SWT.BORDER);
        minimumTorqueMAX.setEnabled(false);
        minimumTorqueMAX.setMessage("max");
        minimumTorqueMAX.setBounds(294, 36, 40, 23);
        formToolkit.adapt(minimumTorqueMAX, true, true);

        Label lblWeightM = new Label(composite, SWT.NONE);
        lblWeightM.setText("Weight m:");
        lblWeightM.setBounds(10, 108, 182, 23);
        formToolkit.adapt(lblWeightM, true, true);
        lblWeightM.setForeground(Configs.KIT_GREEN_70);
        lblWeightM.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.KIT_GREEN_70.dispose();
            }
        });

        weightMin = new Text(composite, SWT.BORDER);
        weightMin.setEnabled(false);
        weightMin.setMessage("min");
        weightMin.setBounds(198, 108, 40, 23);
        formToolkit.adapt(weightMin, true, true);

        Label lblKg = new Label(composite, SWT.NONE);
        lblKg.setText("kg");
        lblKg.setBounds(244, 111, 44, 23);
        formToolkit.adapt(lblKg, true, true);

        weightMax = new Text(composite, SWT.BORDER);
        weightMax.setMessage("max");
        weightMax.setBounds(294, 108, 40, 23);
        formToolkit.adapt(weightMax, true, true);

        Label lblKg_1 = new Label(composite, SWT.NONE);
        lblKg_1.setText("kg");
        lblKg_1.setBounds(340, 111, 44, 23);
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

        Label label_4 = new Label(requirementsForm, SWT.SEPARATOR | SWT.VERTICAL);
        formToolkit.adapt(label_4, true, true);

        Composite composite2 = new Composite(requirementsForm, SWT.NONE);
        formToolkit.adapt(composite2);
        formToolkit.paintBordersFor(composite2);
        Label lblMinmax = new Label(composite2, SWT.NONE);
        lblMinmax.setText("min/max:");
        lblMinmax.setBounds(10, 10, 137, 34);
        formToolkit.adapt(lblMinmax, true, true);
        lblMinmax.setForeground(Configs.KIT_GREEN_70);
        lblMinmax.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.KIT_GREEN_70.dispose();
            }
        });
        StyledText styledText_2 = new StyledText(composite2, SWT.BORDER);
        styledText_2.setText("Desired min and max values.\r\nIf no entered, defaults are taken.");
        styledText_2.setEditable(false);
        styledText_2.setBounds(153, 10, 191, 57);
        formToolkit.adapt(styledText_2);
        formToolkit.paintBordersFor(styledText_2);
        createCommonDescriptions(composite2);
        requirementsForm.setWeights(new int[] { 339, 1, 291 });
    }

    private void createSolutionPage() {
        solutionForm = new SashForm(shell, SWT.NONE);
        solutionForm.setBounds(96, 35, 779, 348);
        formToolkit.adapt(solutionForm);
        formToolkit.paintBordersFor(solutionForm);

        Composite composite_1 = new Composite(solutionForm, SWT.NONE);
        formToolkit.adapt(composite_1);
        formToolkit.paintBordersFor(composite_1);

        tree = new Tree(composite_1, SWT.NONE);
        tree.setBounds(10, 10, 385, 298);
        formToolkit.adapt(tree);
        formToolkit.paintBordersFor(tree);

        Label label_11 = new Label(solutionForm, SWT.SEPARATOR);
        formToolkit.adapt(label_11, true, true);

        Composite composite_2 = new Composite(solutionForm, SWT.NONE);
        formToolkit.adapt(composite_2);
        formToolkit.paintBordersFor(composite_2);
        createCommonDescriptions(composite_2);
        solutionForm.setWeights(new int[] { 339, 1, 291 });
    }

    private void createCommonDescriptions(Composite composite) {
        Label maximalRotationSpeedDescription = new Label(composite, SWT.NONE);
        maximalRotationSpeedDescription.setText("Maximal Speed n_max:");
        maximalRotationSpeedDescription.setBounds(10, 161, 137, 34);
        formToolkit.adapt(maximalRotationSpeedDescription, true, true);
        maximalRotationSpeedDescription.setForeground(Configs.KIT_GREEN_70);
        maximalRotationSpeedDescription.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.KIT_GREEN_70.dispose();
            }
        });

        Label lblMaxMinimumTorque = new Label(composite, SWT.NONE);
        lblMaxMinimumTorque.setText("Peak Torque M_max:");
        lblMaxMinimumTorque.setBounds(10, 98, 137, 34);
        formToolkit.adapt(lblMaxMinimumTorque, true, true);
        lblMaxMinimumTorque.setForeground(Configs.KIT_GREEN_70);
        lblMaxMinimumTorque.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.KIT_GREEN_70.dispose();
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
        lblWeightM_1.setForeground(Configs.KIT_GREEN_70);
        lblWeightM_1.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.KIT_GREEN_70.dispose();
            }
        });
    }
}
