package com.sonar.model;

import java.util.ArrayList;
import java.util.List;

public class MeasurementResult {

    private ComponentResult componentResult;

    private List<PeriodVO> periods = new ArrayList<>();


    public ComponentResult getComponentResult() {
        return componentResult;
    }

    public void setComponentResult(ComponentResult componentResult) {
        this.componentResult = componentResult;
    }

    public List<PeriodVO> getPeriods() {
        return periods;
    }

    public void setPeriods(List<PeriodVO> periods) {
        this.periods = periods;
    }

    @Override
    public String toString() {
        return "MeasurementResult{" +
                "componentResult=" + componentResult +
                ", periods=" + periods +
                '}';
    }
}
