/*
 * Copyright 2018 Oliver Karrenbauer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation * files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, * * * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.kit.anthropomatik.h2t.expertsystem.controller.wrapper;

import edu.kit.anthropomatik.h2t.expertsystem.model.req.Requirement;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import java.util.Objects;

public class TextFieldMinMaxRequirementWrapper extends RequirementWrapper {

    public static final int maxDeviation = 100;
    public static final int digitsDeviation = 1;
    public static final int minUserWeighting = 0;
    public static final int maxUserWeighting = 5;

    public Text minValue;
    public Text minValueOptimization;
    public Label minApplied;
    public Text maxValue;
    public Text maxValueOptimization;
    public Label maxApplied;
    public Spinner deviation;
    public Combo userWeighting;

    public TextFieldMinMaxRequirementWrapper(Requirement requirement) {
        super(requirement);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        TextFieldMinMaxRequirementWrapper that = (TextFieldMinMaxRequirementWrapper) o;
        return Objects.equals(minValue, that.minValue) && Objects.equals(minValueOptimization, that
                .minValueOptimization) && Objects.equals(minApplied, that.minApplied) && Objects.equals(maxValue,
                that.maxValue) && Objects.equals(maxValueOptimization, that.maxValueOptimization) && Objects.equals
                (maxApplied, that.maxApplied) && Objects.equals(deviation, that.deviation) && Objects.equals
                (userWeighting, that.userWeighting);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), minValue, minValueOptimization, minApplied, maxValue,
                maxValueOptimization, maxApplied, deviation, userWeighting);
    }

    @Override
    public String toString() {
        return "TextFieldMinMaxRequirementWrapper{" + "minValue=" + minValue + ", minValueOptimization=" +
                minValueOptimization + ", minApplied=" + minApplied + ", maxValue=" + maxValue + ", " +
                "maxValueOptimization=" + maxValueOptimization + ", maxApplied=" + maxApplied + ", deviation=" +
                deviation + ", userWeighting=" + userWeighting + "} " + super.toString();
    }

}
