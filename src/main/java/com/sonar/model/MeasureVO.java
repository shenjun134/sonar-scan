package com.sonar.model;

import com.sonar.constant.MetricsEnum;

import java.util.ArrayList;
import java.util.List;

public class MeasureVO {

    private String metric;

    private String value;

    private MetricsEnum metricEnum;

    private List<PeriodVO> periods = new ArrayList<>();

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public MetricsEnum getMetricEnum() {
        return metricEnum;
    }

    public void setMetricEnum(MetricsEnum metricEnum) {
        this.metricEnum = metricEnum;
    }

    public List<PeriodVO> getPeriods() {
        return periods;
    }

    public void setPeriods(List<PeriodVO> periods) {
        this.periods = periods;
    }

    @Override
    public String toString() {
        return "MeasureVO{" +
                "metric='" + metric + '\'' +
                ", value='" + value + '\'' +
                ", metricEnum=" + metricEnum +
                '}';
    }
}
