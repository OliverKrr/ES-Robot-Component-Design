package edu.kit.expertsystem;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.kit.expertsystem.model.Requirements;
import edu.kit.expertsystem.model.Result;
import edu.kit.expertsystem.model.Unit;

public class Controller {

    private static final int MAXIMAL_NEEDED_SPACES = 6;

    private static final Logger logger = LogManager.getLogger(Controller.class);

    private GUI gui;
    private MainReasoner reasoner;

    private Requirements currentRequirements;
    private List<Result> lastResults;

    Controller(GUI gui) {
        this.gui = gui;
        reasoner = new MainReasoner();
        reset();
        initialStartForCaching();
    }

    public void reset() {
        reasoner.prepareReasoning();
    }

    private void initialStartForCaching() {
        reasoner.startReasoning(new Requirements());
    }

    public void parseRequirements() {
        gui.tree.removeAll();
        currentRequirements = new Requirements();
        currentRequirements.maximalTorque = parseDoubles(gui.minimumTorqueMIN, gui.minimumTorqueMAX);
        currentRequirements.maximalRotationSpeed = parseDoubles(gui.minimumSpeedMIN, gui.minimumSpeedMAX);
        currentRequirements.weight = parseDoubles(gui.weightMin, gui.weightMax);

        // convert from rpm to °/s
        currentRequirements.maximalRotationSpeed.min /= 6;
        currentRequirements.maximalRotationSpeed.max /= 6;
    }

    private Unit parseDoubles(Text minValue, Text maxValue) {
        Unit unit = new Unit();
        if (!minValue.getText().isEmpty()) {
            try {
                unit.min = Double.parseDouble(minValue.getText());
            } catch (NumberFormatException e) {
                logger.error("Could not parse to double: " + minValue.getText() + ". Default value be taken: "
                        + unit.min);
            }
        }
        if (!maxValue.getText().isEmpty()) {
            try {
                unit.max = Double.parseDouble(maxValue.getText());
            } catch (NumberFormatException e) {
                logger.error("Could not parse to double: " + maxValue.getText() + ". Default value be taken: "
                        + unit.max);
            }
        }
        return unit;
    }

    public void reason() {
        lastResults = reasoner.startReasoning(currentRequirements);
        gui.notifySolutionIsReady();
    }

    public void setResults() {
        addTreeItem(gui.tree, "Number of results: " + lastResults.size());
        for (Result result : lastResults) {
            TreeItem resItem = addTreeItem(gui.tree,
                    "Motor: " + result.motor.name + " & Gear box: " + result.gearBox.name);
            addTreeItem(resItem, "Maximal Torque M_max:         " + result.maximalTorque
                    + getSpaces(result.maximalTorque) + "Nm");
            // convert from °/s to rpm
            addTreeItem(resItem, "Maximal Rotation Speed n_max: " + result.maximalRotationSpeed * 6
                    + getSpaces(result.maximalRotationSpeed * 6) + "°/s");
            addTreeItem(resItem,
                    "Weight m:                     " + result.weight + getSpaces(result.weight) + "kg");
            resItem.setExpanded(true);
        }
    }

    private TreeItem addTreeItem(TreeItem parent, String text) {
        TreeItem resItem = new TreeItem(parent, SWT.NONE);
        resItem.setText(text);
        resItem.setFont(SWTResourceManager.getFont("Courier New", 9, SWT.NORMAL));
        resItem.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.KIT_GREEN_70.dispose();
            }
        });
        return resItem;
    }

    private TreeItem addTreeItem(Tree parent, String text) {
        TreeItem resItem = new TreeItem(parent, SWT.NONE);
        resItem.setText(text);
        resItem.setForeground(Configs.KIT_GREEN_100);
        resItem.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.KIT_GREEN_70.dispose();
            }
        });
        return resItem;
    }

    private String getSpaces(double value) {
        String ret = "";
        String str = String.valueOf(value);
        for (int i = str.length(); i <= MAXIMAL_NEEDED_SPACES; ++i) {
            ret += " ";
        }
        return ret;
    }

}
