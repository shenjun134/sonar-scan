package com.xiangsoft.sonar.sonarreport;

import com.xiangsoft.sonar.sonarreport.model.Report;
import com.xiangsoft.sonar.sonarreport.processor.HtmlWriteProcessor;
import com.xiangsoft.sonar.sonarreport.processor.LineParseProcessor;
import com.xiangsoft.sonar.sonarreport.processor.LineParseProcessorForV54;
import org.apache.commons.lang.math.NumberUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.xiangsoft.universal.AppUniversal.teamNameMap;

public class Client {
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public static Properties prop = new Properties();

    public static void process(String[] args) {
        String userDir = System.getProperty("user.dir");
        InputStream is = null;
        try {
            is = new FileInputStream(userDir + "/appconfig.properties");
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }
        if (is == null) {
            System.exit(1);
        }
        try {
            prop.load(is);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String projectName = args[0];
        String projectChoice = projectName;
        List<String> pickedTeam = new ArrayList<String>();
        if (args.length > 4) {
            String[] pickedTeamArr = args[4].replace(" ", "").split(",");
            pickedTeam.addAll(new ArrayList<String>(Arrays.asList(pickedTeamArr)));
        }
        int threadCount = 10;
        boolean skipTest = false;
        if (args.length > 5) {
            threadCount = NumberUtils.isNumber(args[5].trim()) ? NumberUtils.toInt(args[5].trim()) : 10;
        }
        if (args.length > 6) {
            skipTest = Boolean.valueOf(args[6].trim());
        }

        if (prop.containsKey(args[0])) {
            projectChoice = (String) prop.getProperty(args[0]);
            System.out.println("Project choice = " + projectChoice);
        } else {
            System.out.print("First parameter for Project id [" + args[0] + "] is not set in property file, please verify it.");
            System.exit(1);
        }


        List<String> teamEmpList = new ArrayList<String>();
        Enumeration enumKeys = prop.keys();
        int teamIdx = 1;
        String teamPrefix = "team_";
        boolean isAllTeamIn = pickedTeam.contains("ALL");
        while (enumKeys.hasMoreElements()) {
            String key = (String) enumKeys.nextElement();
            if (key.indexOf(teamPrefix) >= 0 && (isAllTeamIn || pickedTeam.contains(key))) {
                teamEmpList.add(prop.getProperty(key));
                teamNameMap.put(String.valueOf(teamIdx++), key.substring(teamPrefix.length()));
            }
        }
        String[] teamEmpArr = teamEmpList.toArray(new String[0]);

        Date startDt = null;
        Date endDt = null;
        String startDtStr = args[1];
        String endDtStr = args[2];
        try {
            System.out.println("startDateStr =" + startDtStr);
            System.out.println("endDateStr =" + endDtStr);
            startDt = df.parse(startDtStr);
            endDt = df.parse(endDtStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDt);
            calendar.add(Calendar.SECOND, 24 * 60 * 60 - 1);
            endDt = calendar.getTime();
            System.out.println("startDateStr =" + startDt);
            System.out.println("endDateStr =" + endDt);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        Long beginAt = System.currentTimeMillis();
        Report uiReport = null;
        try {
            uiReport = generateReport(args, projectChoice, teamEmpArr, startDt, endDt, threadCount, skipTest);
        } finally {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ report generate used:" + (System.currentTimeMillis() - beginAt));
        }
        beginAt = System.currentTimeMillis();
        try {
            HtmlWriteProcessor writeProcessor = new HtmlWriteProcessor(projectName, startDt, endDt, startDtStr, endDtStr, uiReport.totalFiles);
            writeProcessor.writeOntoHtmlTemplate(uiReport);
        } finally {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ report print used:" + (System.currentTimeMillis() - beginAt));
        }
    }

    private static Report generateReport(String[] args, String projectChoice, String[] teamEmpArr, Date startDt, Date endDt, int threadCount, boolean skipTest) {
        Report uiReport = null;
        if (args.length > 3) {
            if (args[3].equals("v3.1.2")) {
                LineParseProcessor processor = new LineParseProcessor();
                uiReport = processor.processByProjectRootId(projectChoice, teamEmpArr, startDt, endDt);
            } else if (args[3].equals("v5.4")) {
                LineParseProcessorForV54 processor = new LineParseProcessorForV54(threadCount, skipTest);
                uiReport = processor.processByProjectRootId(projectChoice, teamEmpArr, startDt, endDt);
            } else {
                System.out.println("the Version " + args[3] + " is not supported.");
                System.exit(1);
            }
        } else {
            LineParseProcessor processor = new LineParseProcessor();
            uiReport = processor.processByProjectRootId(projectChoice, teamEmpArr, startDt, endDt);
        }
        uiReport.skipTest = skipTest;
        return uiReport;
    }


    public static void main(String[] args) {
//        args = new String[]{"app_parent", "2018-03-28_00:00:01", "2018-04-10_23:59:01", "v5.4"};
//        args = new String[]{"app_parent", "2018-03-13_00:00:01", "2018-03-27_23:59:01", "v5.4"};
//        args = new String[]{"app_parent", "2018-02-28_00:00:01", "2018-03-13_23:59:01", "v5.4", "team_RSS", "30"};
//        args = new String[]{"app_parent", "2011-01-01", "2018-04-13", "v5.4", "ALL", "20", "false"};
        System.out.println("begin with: " + Arrays.toString(args));
        Long beginAt = System.currentTimeMillis();
        try {
            process(args);
        } finally {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ total used:" + (System.currentTimeMillis() - beginAt));
        }
    }
}
