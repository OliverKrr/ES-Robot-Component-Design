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
package edu.kit.anthropomatik.h2t.expertsystem.model.req;

import java.util.Objects;

public class Requirement {

    public String displayName;
    public String description;
    public String unit;
    public Category category;
    public int orderPosition;
    public boolean showDefaultInResults = true;

    public String individualIRI;
    public String resultIRI;

    Requirement() {
    }

    Requirement(Requirement other) {
        displayName = other.displayName;
        description = other.description;
        unit = other.unit;
        category = other.category;
        individualIRI = other.individualIRI;
        resultIRI = other.resultIRI;
        orderPosition = other.orderPosition;
        showDefaultInResults = other.showDefaultInResults;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Requirement that = (Requirement) o;
        return orderPosition == that.orderPosition && showDefaultInResults == that.showDefaultInResults && Objects
                .equals(displayName, that.displayName) && Objects.equals(description, that.description) && Objects
                .equals(unit, that.unit) && Objects.equals(category, that.category) && Objects.equals(individualIRI,
                that.individualIRI) && Objects.equals(resultIRI, that.resultIRI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, description, unit, category, orderPosition, showDefaultInResults,
                individualIRI, resultIRI);
    }

    @Override
    public String toString() {
        return "Requirement{" + "displayName='" + displayName + '\'' + ", description='" + description + '\'' + ", "
                + "unit='" + unit + '\'' + ", category=" + category + ", orderPosition=" + orderPosition + ", " +
                "showDefaultInResults=" + showDefaultInResults + ", individualIRI='" + individualIRI + '\'' + ", " +
                "resultIRI='" + resultIRI + '\'' + '}';
    }
}
