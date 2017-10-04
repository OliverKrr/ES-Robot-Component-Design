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
    }

    public void parseRequirements() {
        gui.tree.removeAll();
        currentRequirements = new Requirements();
        currentRequirements.maximalTorque = new Unit(parseDouble(gui.minimumTorqueMIN, 0),
                parseDouble(gui.minimumTorqueMAX, Double.MAX_VALUE));
        currentRequirements.maximalRotationSpeed = new Unit(parseDouble(gui.minimumSpeedMIN, 0),
                parseDouble(gui.minimumSpeedMAX, Double.MAX_VALUE));
        currentRequirements.weight = new Unit(parseDouble(gui.weightMin, 0),
                parseDouble(gui.weightMax, Double.MAX_VALUE));

        currentRequirements.maximalRotationSpeed.min /= 6;
        currentRequirements.maximalRotationSpeed.max /= 6;
    }

    public void reason() {
        if (GUI.isTest) {
            gui.tree.removeAll();
            currentRequirements = new Requirements();
            currentRequirements.maximalTorque = new Unit(0, Double.MAX_VALUE);
            currentRequirements.maximalRotationSpeed = new Unit(0, Double.MAX_VALUE);
            currentRequirements.weight = new Unit(0, Double.MAX_VALUE);
        }
        lastResults = reasoner.startReasoning(currentRequirements);
        if (!GUI.isTest) {
            gui.notifySolutionIsReady();
        }
    }

    private double parseDouble(Text toParse, double defaultValue) {
        if (toParse.getText().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(toParse.getText());
        } catch (NumberFormatException e) {
            System.err.println("Could not parse to double: " + toParse.getText()
                    + ". Default value be taken: " + defaultValue);
            return defaultValue;
        }
    }

    public void setResults() {
        addTreeItem(gui.tree, "Number of results: " + lastResults.size());
        for (Result result : lastResults) {
            TreeItem resItem = addTreeItem(gui.tree,
                    "Motor: " + result.motor.name + " & Gear box: " + result.gearBox.name);
            addTreeItem(resItem, "Maximal Torque M_max:         " + result.maximalTorque
                    + getSpaces(result.maximalTorque) + "Nm");
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
