package edu.kit.expertsystem.model;

public class Line {

    private double m;
    private double b;

    public Line(double x1, double y1, double x2, double y2) {
        double deviator = x2 - x1;
        if (deviator == 0) {
            m = 0;
            b = Math.max(y1, y2);
        } else {
            m = (y2 - y1) / deviator;
            b = (y1 * x2 - y2 * x1) / deviator;
        }
    }

    public double getY(double x) {
        return m * x + b;
    }
}
