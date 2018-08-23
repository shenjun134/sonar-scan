package com.sonar.service;

import com.sonar.component.MeasureComponent;
import com.sonar.component.impl.MeasureComponentImpl;
import com.sonar.convert.ComponentResultConverter;
import com.sonar.dao.SonarDao;
import com.sonar.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Map;

public class MeasurementService {
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(ReportService.class);

    //August 13, 2018 12:18 PM
    private static final String dateFormat = "";

    private static final String NUMBER_FORMAT = "#0.0";

    private static final DecimalFormat decimalFormat = new DecimalFormat(NUMBER_FORMAT);

    private MeasureComponent measureComponent;

    private SonarDao sonarDao;

    private SonarProperties sonarProperties;

    private boolean pass = false;

    private static final String preFix = "measure-";

    private static final String valueEnd = "-value";

    private static final String styleEnd = "-style";


    /**
     * @param measureReq
     */
    public void process(MeasureReq measureReq) {
        init();
        before(measureReq);
        doProcess(measureReq);
        after(measureReq);
    }


    protected void before(MeasureReq measureReq) {
        if (StringUtils.isBlank(measureReq.getProjectName())) {
            throw new IllegalArgumentException("no project found");
        }
        measureComponent = new MeasureComponentImpl(sonarProperties.getWebUser(), sonarProperties.getWebHost(), sonarProperties.getWebPassword());
    }


    /**
     * generate report
     *
     * @param measureReq
     */
    protected void after(MeasureReq measureReq) {
    }


    /**
     * @param measureReq
     */
    protected void doProcess(MeasureReq measureReq) {
        logger.info("begin do process with - " + measureReq);
        Map<String, ProjectDO> allProject = sonarDao.getAllProject();
        if (allProject == null || allProject.size() == 0) {
            return;
        }
        ProjectDO project = allProject.get(measureReq.getProjectName());
        if (project == null) {
            System.out.println("###################################################################################");
            System.out.println("########################### No project found in sonar Database!");
            System.out.println("###################################################################################");
            throw new IllegalArgumentException("No project found in sonar Database!");
        }

        MeasurementReport report = measureComponent.getReport(project);
        String version = "unknow";
        String date = "unknow";
        if (report.getSnapshot() != null) {
            version = report.getSnapshot().getVersion();
            date = report.getSnapshot().getSnapshotDate() != null ? report.getSnapshot().getSnapshotDate().toString() : "unknow";
        }
        /**
         * reliability_rating
         *
         * security_rating
         *
         * sqale_rating
         *
         */

        RatingEnum reliability_rating = null;
        RatingEnum security_rating = null;
        RatingEnum sqale_rating = null;

        if (report.getResult() != null && CollectionUtils.isNotEmpty(report.getResult().getComponentResult().getList())) {
            for (MeasureVO measureVO : report.getResult().getComponentResult().getList()) {
                if (StringUtils.equalsIgnoreCase(measureVO.getMetric(), "reliability_rating")) {
                    double value = NumberUtils.toDouble(measureVO.getValue(), -1);
                    reliability_rating = RatingEnum.rateOf(value);
                    measureVO.setValue(reliability_rating.getCode());
                } else if (StringUtils.equalsIgnoreCase(measureVO.getMetric(), "security_rating")) {
                    double value = NumberUtils.toDouble(measureVO.getValue(), -1);
                    security_rating = RatingEnum.rateOf(value);
                    measureVO.setValue(security_rating.getCode());
                } else if (StringUtils.equalsIgnoreCase(measureVO.getMetric(), "sqale_rating")) {
                    double value = NumberUtils.toDouble(measureVO.getValue(), -1);
                    sqale_rating = RatingEnum.rateOf(value);
                    measureVO.setValue(sqale_rating.getCode());
                }
            }
        }
        if (reliability_rating == null) {
            reliability_rating = RatingEnum.RATING_UNKNOWN;
        }
        if (security_rating == null) {
            security_rating = RatingEnum.RATING_UNKNOWN;
        }
        if (sqale_rating == null) {
            sqale_rating = RatingEnum.RATING_UNKNOWN;
        }

        String path = System.getProperty("user.dir");
        BufferedReader htmlTemplate = null;
        StringBuilder template = new StringBuilder();
        try {
            String templateName = measureReq.getTemplateName();
            if (StringUtils.isBlank(templateName)) {
                templateName = SeverityHtmlService.Constant.template;
            }
            htmlTemplate = new BufferedReader(new FileReader(path + "/" + templateName));
            String temp;
            while ((temp = htmlTemplate.readLine()) != null) {
                template.append(temp);
            }
        } catch (FileNotFoundException e) {
            logger.error("doProcess.read error - " + measureReq, e);
            throw new RuntimeException("doProcess.read error", e);
        } catch (IOException e) {
            logger.error("doProcess.read error - " + measureReq, e);
            throw new RuntimeException("doProcess.read error", e);
        } finally {
            if (htmlTemplate != null) {
                try {
                    htmlTemplate.close();
                } catch (IOException e) {
                    logger.error("doProcess.close error - " + measureReq, e);
                }
            }
        }
        String resultHtml = template.toString();
        resultHtml = StringUtils.replace(resultHtml, styleKey("reliability_rating"), reliability_rating.getStyle());
        resultHtml = StringUtils.replace(resultHtml, styleKey("security_rating"), security_rating.getStyle());
        resultHtml = StringUtils.replace(resultHtml, styleKey("sqale_rating"), sqale_rating.getStyle());
        resultHtml = StringUtils.replace(resultHtml, "snapshot.date", date);
        resultHtml = StringUtils.replace(resultHtml, "snapshot.version", version);
        resultHtml = StringUtils.replace(resultHtml, "#projectKee#", project.getKee());
        resultHtml = StringUtils.replace(resultHtml, "#projectName#", project.getName());
        if (report.getResult() != null && CollectionUtils.isNotEmpty(report.getResult().getComponentResult().getList())) {
            for (MeasureVO measureVO : report.getResult().getComponentResult().getList()) {
                String metricsKey = measureVO.getMetric();
                MetricsDef def = report.getMetricsDefMap().get(metricsKey);
                String value = ComponentResultConverter.convert(measureVO, def);

                resultHtml = StringUtils.replace(resultHtml, valueKey(metricsKey), value);
            }
        }

        File newFile = new File(measureReq.getOutputPath());
        FileWriter fw = null;
        try {
            fw = new FileWriter(newFile);
            fw.write(resultHtml);
        } catch (IOException e) {
            logger.error("doProcess.write error - " + measureReq, e);
            throw new RuntimeException("doProcess.write error", e);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    logger.error("doProcess.close error - " + measureReq, e);
                    throw new RuntimeException("doProcess.close error", e);
                }
            }
        }

    }

    protected String valueKey(String metricsKey) {
        return StringUtils.join(new String[]{preFix, metricsKey, valueEnd});
    }

    protected String styleKey(String metricsKey) {
        return StringUtils.join(new String[]{preFix, metricsKey, styleEnd});
    }


    protected void init() {
        sonarProperties = new SonarProperties();
        sonarDao = new SonarDao(sonarProperties);
    }

    public static void main(String[] args) {
        args = new String[]{"gcth-project-trunk-parent", "measure-template.html", "measurement-report.html"};
        MeasureReq req = new MeasureReq();
        req.setProjectName(args[0]);
        req.setTemplateName(args[1]);
        req.setOutputPath(args[2]);

        MeasurementService service = new MeasurementService();
        service.process(req);
    }

    /***********************************************************private************************************************/
}
