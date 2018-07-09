package com.sonar;

import com.sonar.model.ReportReq;
import com.sonar.service.ReportService;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

public class Base {
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(Base.class);
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        System.out.println("begin with: " + Arrays.toString(args));
        Long beginAt = System.currentTimeMillis();
        try {
//            args = new String[]{"zeusui_trunk_quality", "2018-01-01", "2018-05-13", "v5.4", "BLANK", "30"};
//            args = new String[]{"GTM_Job", "2018-01-01", "2018-03-01", "v5.4", "RSS", "30"};
//            args = new String[]{"gcth-project-trunk-parent", "2018-01-01", "2018-05-01", "v5.4", "BLANK", "30"};
//            args = new String[]{"gcth-application-parent", "2017-01-01", "2018-03-01", "v5.4", "BLANK", "20"};
            process(args);
        } catch (Exception e) {
            logger.error("sonar report process error", e);
        } finally {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ total used:" + (System.currentTimeMillis() - beginAt));
        }
    }

    public static void process(String[] args) {
        if (args == null || args.length < 3) {
            System.out.println("no enough args");
            System.exit(1);
        }
        String projectNames = args[0];
        List<String> pickedTeam = new ArrayList<String>();
        Date startDt = null;
        Date endDt = null;
        String startDtStr = args[1];
        String endDtStr = args[2];
        try {
            startDt = df.parse(startDtStr);
            endDt = df.parse(endDtStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDt);
            calendar.add(Calendar.SECOND, 24 * 60 * 60 - 1);
            endDt = calendar.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        String version = null;
        if (args.length > 3) {
            version = args[3];
        }
        if (args.length > 4) {
            String[] pickedTeamArr = args[4].replace(" ", "").split(",");
            pickedTeam.addAll(new ArrayList<String>(Arrays.asList(pickedTeamArr)));
        }
        int threadCount = 10;
        boolean skipTest = true;
        if (args.length > 5) {
            threadCount = NumberUtils.isNumber(args[5].trim()) ? NumberUtils.toInt(args[5].trim()) : 10;
        }
        if (args.length > 6) {
            skipTest = Boolean.valueOf(args[6].trim());
        }
        String severityList = "";
        if (args.length > 7) {
            severityList = args[7].trim();
        }

        ReportReq reportReq = new ReportReq(projectNames, startDt, endDt);
        reportReq.setPickedTeam(pickedTeam);
        reportReq.setSkipTest(skipTest);
        reportReq.setThreadCount(threadCount);
        reportReq.setVersion(version);
        reportReq.setSeverityList(severityList);
        ReportService reportService = new ReportService();
        reportService.process(reportReq);
    }
}
