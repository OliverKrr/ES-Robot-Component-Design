package edu.ontology.sac;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.ontology.sac.model.Requirements;
import edu.ontology.sac.model.Result;
import edu.ontology.sac.model.Unit;

public class Controller {

    private static final int MAXIMAL_NEEDED_SPACES = 6;
    private GUI gui;
    private MyReasonerV3 reasoner;

    private Requirements currentRequirements;
    private List<Result> lastResults;

    Controller(GUI gui) {
        this.gui = gui;
        reasoner = new MyReasonerV3();
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

    public void reason() {
        if (GUI.isTest) {
            reset();
            currentRequirements = new Requirements();
        }
        lastResults = reasoner.startReasoning(currentRequirements);
        if (!GUI.isTest) {
            gui.notifySolutionIsReady();
        }
    }

    private Unit parseDoubles(Text minValue, Text maxValue) {
        Unit unit = new Unit();
        if (!minValue.getText().isEmpty()) {
            try {
                unit.min = Double.parseDouble(minValue.getText());
            } catch (NumberFormatException e) {
                System.err.println("Could not parse to double: " + minValue.getText()
                        + ". Default value be taken: " + unit.min);
            }
        }
        if (!maxValue.getText().isEmpty()) {
            try {
                unit.max = Double.parseDouble(maxValue.getText());
            } catch (NumberFormatException e) {
                System.err.println("Could not parse to double: " + maxValue.getText()
                        + ". Default value be taken: " + unit.max);
            }
        }
        return unit;
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
                Configs.getKitGreen70(gui.shell.getDisplay()).dispose();
            }
        });
        return resItem;
    }

    private TreeItem addTreeItem(Tree parent, String text) {
        TreeItem resItem = new TreeItem(parent, SWT.NONE);
        resItem.setText(text);
        resItem.setForeground(Configs.getKitGreen100(gui.shell.getDisplay()));
        resItem.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Configs.getKitGreen70(gui.shell.getDisplay()).dispose();
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
