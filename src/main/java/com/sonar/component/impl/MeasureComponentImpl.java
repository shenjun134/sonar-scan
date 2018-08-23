package com.sonar.component.impl;

import com.sonar.component.MeasureComponent;
import com.sonar.constant.WebApi;
import com.sonar.convert.ComponentResultConverter;
import com.sonar.convert.SnapshotConverter;
import com.sonar.model.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sonar.wsclient.Host;
import org.sonar.wsclient.JdkUtils;
import org.sonar.wsclient.connectors.HttpClient4Connector;

import java.util.Map;

public class MeasureComponentImpl extends AbstractComponent implements MeasureComponent {

    private static final Logger logger = Logger.getLogger(MeasureComponentImpl.class);

    public MeasureComponentImpl() {
    }

    public MeasureComponentImpl(String webUser, String webHost, String webPassword) {
        this.webUser = webUser;
        this.webHost = webHost;
        this.webPassword = webPassword;
        util = new JdkUtils();
    }

    @Override
    public ProjectSnapshot getSnapshot(ProjectDO projectDO) {
        try {
            this.check(projectDO);
            return snapshot(projectDO);
        } catch (Exception e) {
            logger.error("getSnapshot error - " + projectDO, e);
            throw new RuntimeException("getSnapshot error", e);
        }
    }

    private ProjectSnapshot snapshot(ProjectDO projectDO) {
        HttpClient4Connector connector = new HttpClient4Connector(new Host(webHost, webUser, webPassword));
        try {
            String baseUrl = this.webHost + StringUtils.replace(WebApi.SNAPSHOT_BY_KEE, "#componentKey#", projectDO.getKee());

            String jsonResp = this.httpGet(baseUrl, connector);

            logger.info("snapshot response: " + jsonResp);

            ProjectSnapshot snapshot = SnapshotConverter.convert2Snapshot(jsonResp);
            return snapshot;
        } finally {
            connector.close();
        }

    }

    @Override
    public MeasurementResult getMeasurement(ProjectDO projectDO) {
        try {
            this.check(projectDO);
            return measurement(projectDO);
        } catch (Exception e) {
            logger.error("getMeasurement error - " + projectDO, e);
            throw new RuntimeException("getMeasurement error", e);
        }
    }

    @Override
    public Map<String, MetricsDef> getMetricsDef() {
        HttpClient4Connector connector = new HttpClient4Connector(new Host(webHost, webUser, webPassword));
        try {
            String baseUrl = this.webHost + WebApi.METRICS_DEF;
            String jsonResp = this.httpGet(baseUrl, connector);
            logger.info("getMetricsDef response: " + jsonResp);
            return ComponentResultConverter.convert2MetricsMap(jsonResp);
        } finally {
            connector.close();
        }
    }

    @Override
    public MeasurementReport getReport(ProjectDO projectDO) {
        MeasurementReport report = new MeasurementReport();
        report.setResult(this.getMeasurement(projectDO));
        report.setSnapshot(this.getSnapshot(projectDO));
        report.setMetricsDefMap(this.getMetricsDef());
        return report;
    }

    private MeasurementResult measurement(ProjectDO projectDO) {
        HttpClient4Connector connector = new HttpClient4Connector(new Host(webHost, webUser, webPassword));
        try {
            String baseUrl = this.webHost + StringUtils.replace(WebApi.MEASUEMENT_BY_KEE, "#componentKey#", projectDO.getKee());
            String url = StringUtils.replace(baseUrl, "#metricKeys#", WebApi.METRICS_KEY_LIST);
            String jsonResp = this.httpGet(url, connector);
            logger.info("snapshot response: " + jsonResp);

            ComponentResult componentResult = ComponentResultConverter.convert2Result(jsonResp);

            MeasurementResult result = new MeasurementResult();
            result.setComponentResult(componentResult);
            return result;
        } finally {
            connector.close();
        }
    }
}
