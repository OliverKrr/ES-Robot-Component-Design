package edu.kit.anthropomatik.h2t.expertsystem.model;

public class Line {

    private double m;
    private double b;

    public Line(double x1, double y1, double x2, double y2) {
        double divisor = x2 - x1;
        if (divisor == 0) {
            m = 0;
            b = Math.max(y1, y2);
        } else {
            m = (y2 - y1) / divisor;
            b = (y1 * x2 - y2 * x1) / divisor;
        }
    }

    public double getY(double x) {
        return m * x + b;
    }
}
