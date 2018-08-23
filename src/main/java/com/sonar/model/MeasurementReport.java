package com.sonar.model;

import java.util.HashMap;
import java.util.Map;

public class MeasurementReport {

    private MeasurementResult result;

    private ProjectSnapshot snapshot;

    private Map<String, MetricsDef> metricsDefMap = new HashMap<>();

    public MeasurementResult getResult() {
        return result;
    }

    public void setResult(MeasurementResult result) {
        this.result = result;
    }

    public ProjectSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(ProjectSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public Map<String, MetricsDef> getMetricsDefMap() {
        return metricsDefMap;
    }

    public void setMetricsDefMap(Map<String, MetricsDef> metricsDefMap) {
        this.metricsDefMap = metricsDefMap;
    }

    @Override
    public String toString() {
        return "MeasurementReport{" +
                "result=" + result +
                ", snapshot=" + snapshot +
                ", metricsDefMap=" + metricsDefMap +
                '}';
    }
}
