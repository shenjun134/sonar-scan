package com.sonar.component;

import com.sonar.model.*;

import java.util.Map;

public interface MeasureComponent {

    /**
     * @param projectDO
     * @return
     */
    ProjectSnapshot getSnapshot(ProjectDO projectDO);


    /**
     * @param projectDO
     * @return
     */
    MeasurementResult getMeasurement(ProjectDO projectDO);


    /**
     * @return
     */
    Map<String, MetricsDef> getMetricsDef();


    /**
     * @param projectDO
     * @return
     */
    MeasurementReport getReport(ProjectDO projectDO);
}
