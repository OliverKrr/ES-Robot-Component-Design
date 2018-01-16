package edu.kit.expertsystem.controller;

import edu.kit.expertsystem.controller.wrapper.ResultWrapper;
import edu.kit.expertsystem.model.Component;
import edu.kit.expertsystem.model.Result;
import edu.kit.expertsystem.model.req.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Comparator;

public abstract class ResultAbstract {

    protected static final Logger logger = LogManager.getLogger(ResultAbstract.class);

    private static final int MAXIMAL_NEEDED_SPACES = 9;

    protected DecimalFormat df = new DecimalFormat("#.####");

    protected ResultWrapper resultWrapper;
    private SelectionAdapter listener;


    public ResultAbstract(ResultWrapper resultWrapper) {
        this.resultWrapper = resultWrapper;
        df.setRoundingMode(RoundingMode.CEILING);
    }

    public final synchronized void clearLastResults() {
        if (listener != null){
            resultWrapper.orderBy.removeSelectionListener(listener);
            resultWrapper.orderBy2.removeSelectionListener(listener);
            resultWrapper.showOnlyDiffsCheckBox.removeSelectionListener(listener);
            listener = null;
        }
        clearLastSpecificResults();
    }

    protected abstract void clearLastSpecificResults();

    public final synchronized void setResults() {
        if (resultWrapper.results == null) {
            return;
        }
        int oldSelectionOfOrderBy = resultWrapper.orderBy.getSelectionIndex();
        int oldSelectionOfOrderBy2 = resultWrapper.orderBy2.getSelectionIndex();
        reset();
        logSolutionAndAddOrderByItems();
        selectOrderBy(oldSelectionOfOrderBy, oldSelectionOfOrderBy2);

        listener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                sortSolution();
                showSolution();

                Event reloadSearchE = new Event();
                reloadSearchE.type = SWT.KeyUp;
                reloadSearchE.keyCode = ' ';
                resultWrapper.searchField.notifyListeners(SWT.KeyUp, reloadSearchE);
            }
        };

        resultWrapper.orderBy.addSelectionListener(listener);
        resultWrapper.orderBy2.addSelectionListener(listener);
        resultWrapper.showOnlyDiffsCheckBox.addSelectionListener(listener);
        resultWrapper.orderBy.notifyListeners(SWT.Selection, new Event());
    }

    protected abstract void showSolution();

    private void sortSolution() {
        String currentSelection = resultWrapper.orderBy.getText();
        String currentSelection2 = resultWrapper.orderBy2.getText();

        if (currentSelection.length() > 2 && currentSelection2.length() > 2) {
            String displayName = currentSelection.substring(0, currentSelection.length() - 2);
            String displayName2 = currentSelection2.substring(0, currentSelection2.length() - 2);

            resultWrapper.results.sort((first, second) -> {
                double valueFirst1 = 0;
                double valueFirst2 = 0;
                for (Requirement req : first.requirements) {
                    if (resultWrapper.displayNameToIriMap.get(displayName).equals(req.resultIRI)) {
                        valueFirst1 = getResultValueAsDouble(req);
                    }
                    if (resultWrapper.displayNameToIriMap.get(displayName2).equals(req.resultIRI)) {
                        valueFirst2 = getResultValueAsDouble(req);
                    }
                }
                double valueSecond1 = 0;
                double valueSecond2 = 0;
                for (Requirement req : second.requirements) {
                    if (resultWrapper.displayNameToIriMap.get(displayName).equals(req.resultIRI)) {
                        valueSecond1 = getResultValueAsDouble(req);
                    }
                    if (resultWrapper.displayNameToIriMap.get(displayName2).equals(req.resultIRI)) {
                        valueSecond2 = getResultValueAsDouble(req);
                    }
                }

                if (Math.abs(valueFirst1 - valueSecond1) > Controller.EPSILON) {
                    if (currentSelection.endsWith("\u25B2")) {
                        return Double.compare(valueFirst1, valueSecond1);
                    } else {
                        return Double.compare(valueSecond1, valueFirst1);
                    }
                } else {
                    if (currentSelection2.endsWith("\u25B2")) {
                        return Double.compare(valueFirst2, valueSecond2);
                    } else {
                        return Double.compare(valueSecond2, valueFirst2);
                    }
                }
            });
        }
    }

    private void selectOrderBy(int oldSelectionOfOrderBy, int oldSelectionOfOrderBy2) {
        if (resultWrapper.orderBy.getItemCount() > oldSelectionOfOrderBy) {
            if (oldSelectionOfOrderBy == -1) {
                oldSelectionOfOrderBy = 0;
            }
            resultWrapper.orderBy.select(oldSelectionOfOrderBy);
        }
        if (resultWrapper.orderBy2.getItemCount() > oldSelectionOfOrderBy2) {
            if (oldSelectionOfOrderBy2 == -1) {
                if (resultWrapper.orderBy2.getItemCount() > 2) {
                    oldSelectionOfOrderBy2 = 2;
                } else {
                    oldSelectionOfOrderBy2 = 0;
                }
            }
            resultWrapper.orderBy2.select(oldSelectionOfOrderBy2);
        }
    }

    private void logSolutionAndAddOrderByItems() {
        for (Result result : resultWrapper.results) {
            double maxNumberOfCharsForComp = getMaxNumberOfCharsForComp(result);
            logger.debug("Solution:");
            result.components.forEach(comp -> logger.debug("Component: " + getNameForComponent(comp,
                    maxNumberOfCharsForComp)));

            double maxNumberOfCharsForReq = getMaxNumberOfCharsForReq(result);
            for (Requirement req : result.requirements) {
                if (req.resultIRI != null) {
                    logger.debug("Requirement: " + getNameForReq(req, maxNumberOfCharsForReq));

                    if (!resultWrapper.displayNameToIriMap.containsKey(req.displayName)) {
                        resultWrapper.displayNameToIriMap.put(req.displayName, req.resultIRI);
                        resultWrapper.orderBy.add(req.displayName + " \u25BC");
                        resultWrapper.orderBy.add(req.displayName + " \u25B2");
                        resultWrapper.orderBy2.add(req.displayName + " \u25BC");
                        resultWrapper.orderBy2.add(req.displayName + " \u25B2");
                    }
                }
            }
        }
    }

    private void reset() {
        resultWrapper.orderBy.removeAll();
        resultWrapper.orderBy2.removeAll();
        resultWrapper.displayNameToIriMap.clear();
        clearLastResults();
    }

    protected double getMaxNumberOfCharsForComp(Result result) {
        return result.components.stream().max(Comparator.comparingInt(val -> val.nameOfComponent.length())).get()
                .nameOfComponent.length();
    }

    protected String getNameForComponent(Component component, double maxNumberOfChars) {
        return "".equals(component.nameOfComponent) ? component.nameOfInstance : component.nameOfComponent + ": " +
                getSpacesForDisplayName(component.nameOfComponent, maxNumberOfChars) + component.nameOfInstance;
    }

    protected double getMaxNumberOfCharsForReq(Result result) {
        return result.requirements.stream().filter(req -> req.resultIRI != null).max(Comparator.comparingInt(val ->
                val.displayName.length())).get().displayName.length();
    }

    protected String getNameForReq(Requirement req, double maxNumberOfChars) {
        String resultValue = getResultValue(req);
        String unit = req.unit == null ? "" : req.unit;
        return req.displayName + ": " + getSpacesForDisplayName(req.displayName, maxNumberOfChars) + resultValue +
                getSpacesForResultValue(resultValue) + unit;
    }

    private double getResultValueAsDouble(Requirement req) {
        if (req instanceof TextFieldMinMaxRequirement) {
            TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req;
            return realReq.result * realReq.scaleFromOntologyToUI;
        } else if (req instanceof TextFieldRequirement) {
            TextFieldRequirement realReq = (TextFieldRequirement) req;
            return realReq.result * realReq.scaleFromOntologyToUI;
        } else if (req instanceof CheckboxRequirement) {
            CheckboxRequirement realReq = (CheckboxRequirement) req;
            return realReq.result ? 1 : 0;
        } else if (req instanceof DropdownRequirement) {
            // DropdownRequirement have no results
            return Double.MAX_VALUE;
        } else if (req instanceof RequirementOnlyForSolution) {
            RequirementOnlyForSolution realReq = (RequirementOnlyForSolution) req;
            return realReq.result * realReq.scaleFromOntologyToUI;
        } else {
            throw new RuntimeException("Requirement class unknown: " + req.getClass());
        }
    }

    protected String getResultValue(Requirement req) {
        if (req instanceof TextFieldMinMaxRequirement) {
            TextFieldMinMaxRequirement realReq = (TextFieldMinMaxRequirement) req;
            return df.format(realReq.result * realReq.scaleFromOntologyToUI);
        } else if (req instanceof TextFieldRequirement) {
            TextFieldRequirement realReq = (TextFieldRequirement) req;
            return df.format(realReq.result * realReq.scaleFromOntologyToUI);
        } else if (req instanceof CheckboxRequirement) {
            CheckboxRequirement realReq = (CheckboxRequirement) req;
            return String.valueOf(realReq.result);
        } else if (req instanceof DropdownRequirement) {
            DropdownRequirement realReq = (DropdownRequirement) req;
            return realReq.result;
        } else if (req instanceof RequirementOnlyForSolution) {
            RequirementOnlyForSolution realReq = (RequirementOnlyForSolution) req;
            return df.format(realReq.result * realReq.scaleFromOntologyToUI);
        } else {
            throw new RuntimeException("Requirement class unknown: " + req.getClass());
        }
    }

    private String getSpacesForDisplayName(String currentDisplayName, double maxNumberOfChars) {
        StringBuilder builder = new StringBuilder("");
        for (int i = currentDisplayName.length(); i <= maxNumberOfChars; ++i) {
            builder.append(" ");
        }
        return builder.toString();
    }

    private String getSpacesForResultValue(String value) {
        StringBuilder builder = new StringBuilder("");
        for (int i = value.length(); i <= MAXIMAL_NEEDED_SPACES; ++i) {
            builder.append(" ");
        }
        return builder.toString();
    }
}
