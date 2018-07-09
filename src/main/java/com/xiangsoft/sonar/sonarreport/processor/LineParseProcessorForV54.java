package com.xiangsoft.sonar.sonarreport.processor;

import com.statestr.gcth.core.util.StopTimer;
import com.statestr.gcth.core.util.ThreadUtil;
import com.xiangsoft.sonar.sonarreport.Client;
import com.xiangsoft.sonar.sonarreport.dao.SonarDaoImpl;
import com.xiangsoft.sonar.sonarreport.model.*;
import com.xiangsoft.universal.AppUniversal;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.sonar.wsclient.Host;
import org.sonar.wsclient.JdkUtils;
import org.sonar.wsclient.connectors.HttpClient4Connector;

import java.util.*;
import java.util.concurrent.*;

public class LineParseProcessorForV54 {

    private static final Logger logger = Logger.getLogger(LineParseProcessorForV54.class);

    private int threadCount = 10;

    private boolean skipTest = false;

    private SonarDaoImpl dao = new SonarDaoImpl();

    public LineParseProcessorForV54(int threadCount, boolean skipTest) {
        this.threadCount = threadCount;
        this.skipTest = skipTest;
    }

    public LineParseProcessorForV54() {
    }


    public Report processByProjectRootId(String projectId, String allTeamEmpIdArr[], Date startDate, Date endDate) {
        if (allTeamEmpIdArr == null || allTeamEmpIdArr.length == 0) {
            System.out.println("Please check the property that contain team_ property!!!");
            System.exit(1);
        }

        String projectName = dao.getProjectNameByProjectId(projectId);
        if (logger.isInfoEnabled()) {
            logger.info("projectId : " + projectId);
            logger.info("empId : " + allTeamEmpIdArr);
            logger.info("startDate : " + startDate);
            logger.info("endDate : " + endDate);
        }

        Report uiReportData = new Report();
        uiReportData.moduleName = projectName;
        uiReportData.moduleId = projectId;
        uiReportData.startTime = startDate;
        uiReportData.endTime = endDate;

        List<String> allEmpIdList = new ArrayList<String>();
        Map<String, String> allEmpMap = new HashMap<String, String>();

        for (int i = 0; i < allTeamEmpIdArr.length; i++) {
            String teamEmpIdArr = allTeamEmpIdArr[i];
            TeamLevelStatisticsInfo teamInfo = new TeamLevelStatisticsInfo();
            if (AppUniversal.teamNameMap.containsKey(String.valueOf(i + 1))) {
                teamInfo.teamId = "team_" + AppUniversal.teamNameMap.get(String.valueOf(i + 1));
                teamInfo.teamMembers = teamEmpIdArr;
            } else {
                teamInfo.teamId = "team_" + (i + 1);
            }

            String[] empIdArr = teamEmpIdArr.split(",");

            for (String empId : empIdArr) {

                EmployeeLevelStatisticsInfo empInfo = new EmployeeLevelStatisticsInfo();
                empInfo.employeeId = empId;
                allEmpIdList.add(empId);
                allEmpMap.put(empId, empId);
                teamInfo.employeeLevelStatisticsInfoList.add(empInfo);
            }
            uiReportData.teamLevelStatisticsInfoList.add(teamInfo);
        }

        List<SonarLine> sonarLineList = getAllProjectLineInfoByProjectIdV54___(projectId, uiReportData);

        List<SonarLine> filterSonarLineList = new ArrayList<SonarLine>();
        TreeSet<String> empTs = new TreeSet<String>();

        for (SonarLine line : sonarLineList) {
            //clear the odd empID
            empTs.add(line.lastCommitEmpId);
            if (line == null) {
                System.out.println(" line is null !!! " + " line number : " + line.lineNumber + " , class : " + line.testClazzName);
//				return null;
                continue;
            }
            if (line.lastCommitDate.after(startDate) && line.lastCommitDate.before(endDate)) {
                filterSonarLineList.add(line);
            }
        }

        TeamLevelStatisticsInfo teamNotInListInfo = new TeamLevelStatisticsInfo();
        teamNotInListInfo.teamId = "Team:not_in_list";
        for (String dbEmpId : empTs) {
            if (!allEmpMap.containsKey(dbEmpId)) {
                EmployeeLevelStatisticsInfo empInfo = new EmployeeLevelStatisticsInfo();
                empInfo.employeeId = dbEmpId;
                teamNotInListInfo.employeeLevelStatisticsInfoList.add(empInfo);
            }
        }
        uiReportData.teamLevelStatisticsInfoList.add(teamNotInListInfo);


//		List<SonarIssue> issueList = dao.getAllIssueListByRootId(projectId);
//		
//		System.out.println(" dao.getAllIssueListByRootId(projectId) takes " + st1.check()/1000 + " seconds.");
//		
//		Map<String, Integer> issueCreditMap = convertToIssueCredit(issueList);


        System.out.println(" uiReportData.teamLevelStatisticsInfoList " + uiReportData.teamLevelStatisticsInfoList.size());

        for (int i = 0; i < uiReportData.teamLevelStatisticsInfoList.size(); i++) {
            TeamLevelStatisticsInfo teamLevelInfo = uiReportData.teamLevelStatisticsInfoList.get(i);

            System.out.println(" employeeLevelStatisticsInfoList size: " + teamLevelInfo.employeeLevelStatisticsInfoList.size());
            for (int j = 0; j < teamLevelInfo.employeeLevelStatisticsInfoList.size(); j++) {
                EmployeeLevelStatisticsInfo employeeInfo = teamLevelInfo.employeeLevelStatisticsInfoList.get(j);

                SonarReport report = new SonarReport();
                //report.creditMap = issueCreditMap;
                report.sonarLineList = filterSonarLineList;
                report.empId = employeeInfo.employeeId;
                report.startDate = startDate;
                report.endDate = endDate;
                report.calculate();
                employeeInfo.classLevelStatistiscInfoList = report.employeeStaticInfo.classLevelStatistiscInfoList;

            }
        }

        uiReportData.calculateAll();

        return uiReportData;
    }

    public List<SonarLine> getAllProjectLineInfoByProjectIdV54(String projectId) {

        logger.info("call getAllProjectLineInfoByProjectIdV54" + ":" + "project Id = " + projectId);

        String webHost = Client.prop.getProperty("web_host");
        String webUser = Client.prop.getProperty("web_user");
        String webPassword = Client.prop.getProperty("web_password");

        HttpClient4Connector connector = new HttpClient4Connector(new Host(webHost, webUser, webPassword));
        HttpGet request = new HttpGet(webHost + "/api/resources?resource=" + projectId + "&depth=-1&scopes=FIL&format=json");
        request.setHeader("Accept", "application/json");
        String json = connector.executeRequest(request);
        logger.info("response = [" + json + "]");
        logger.debug("complete get response.");
        if (json == null) {
            logger.warn("can not get data by url [" + webHost + "/api/resources?resource=" + projectId + "&depth=-1&scopes=FIL&format=json]");
            System.exit(1);
        }

        JdkUtils util = new JdkUtils();

        JSONArray array = (JSONArray) util.parse(json);


        List<SonarLine> sonarLineArr = new ArrayList<SonarLine>();
        for (Object obj : array) {
            String clazzKey = (String) util.getField(obj, "key");
            String clazzName = (String) util.getField(obj, "name");
            Long componentId = (Long) util.getField(obj, "id");
            StopTimer timer = new StopTimer();
            String url = webHost + "/api/sources/lines?key=" + clazzKey;

            request = new HttpGet(url);

            request.setHeader("Accept", "application/json");
            json = connector.executeRequest(request);

            logger.info("execute http request = [" + url + "]");

            JSONObject content = (JSONObject) JSONValue.parse(json);

            for (Object oo : content.values()) {//total only one element

                JSONArray array1 = (JSONArray) JSONValue.parse(oo.toString());
                for (Object ar : array1) {
                    if (util.getString(ar, "scmAuthor") != null && util.getString(ar, "scmAuthor").length() > 0) {
                        //System.out.println(util.getString(ar, "line")+" hit "+util.getInteger(ar, "utLineHits"));
                        Integer utLineHit = util.getInteger(ar, "utLineHits");
                        utLineHit = utLineHit == null ? 0 : utLineHit > 0 ? 1 : 0;
                        if (utLineHit != null) {
                            SonarLine sl = new SonarLine();
                            sl.lastCommitEmpId = (util.getString(ar, "scmAuthor"));
                            sl.lastCommitDate = (util.getDateTime(ar, "scmDate"));
                            sl.lineNumber = (util.getInteger(ar, "line"));
                            sl.testClazzName = clazzName;
                            sl.componentId = String.valueOf(componentId);
                            sl.covered = utLineHit;
                            sl.effeciveCoverageLine = 1;
                            sonarLineArr.add(sl);
                        }

                    }
                }

            }
            logger.info("it takes " + timer.check() / 1000 + " seconds.");
        }
        return sonarLineArr;
    }


    private List<Component> getComponents(String projectId) {
        return dao.getComponents(projectId, skipTest);
    }


    public List<SonarLine> getAllProjectLineInfoByProjectIdV54___(String projectId, Report uiReportData) {

        logger.info("call getAllProjectLineInfoByProjectIdV54" + ":" + "project Id = " + projectId);

        String webHost = Client.prop.getProperty("web_host");
        String webUser = Client.prop.getProperty("web_user");
        String webPassword = Client.prop.getProperty("web_password");

//        HttpClient4Connector connector = new HttpClient4Connector(new Host(webHost, webUser, webPassword));
//        String fileListUrl = webHost + "/api/resources?resource=" + projectId + "&depth=-1&scopes=FIL&format=json";
//        HttpGet request = new HttpGet(fileListUrl);
//        request.setHeader("Accept", "application/json");
//        String json = connector.executeRequest(request);
//        connector.close();
//        logger.info("response = [" + json + "]");
//        logger.info("complete get response.---" + fileListUrl);
//        if (json == null) {
//            logger.warn("can not get data by url [" + fileListUrl + "]");
//            System.exit(1);
//        }

//        JdkUtils util = new JdkUtils();
//
//        JSONArray array = (JSONArray) util.parse(json);
        List<Component> array = getComponents(projectId);
        uiReportData.totalFiles = array.size();
        List<SonarLine> sonarLineArr = new ArrayList<SonarLine>();

        int mod = calcMod(array.size(), threadCount);

        logger.info("file size:" + array.size() + ", theadCount:" + threadCount + ", mod:" + mod);

        List<Future<List<SonarLine>>> futures = new ArrayList<Future<List<SonarLine>>>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int fromIndex = i * mod;
            int endIndex = (i + 1) * mod;
            fromIndex = fromIndex > array.size() ? array.size() : fromIndex;
            endIndex = endIndex > array.size() ? array.size() : endIndex;
//            List<Object> temp = null;

            List<Component> temp = null;
            if (fromIndex < endIndex) {
                temp = array.subList(fromIndex, endIndex);
            }
            String name = fromIndex + "~" + endIndex;
            Callable<List<SonarLine>> callable = new SonarLineTask(temp, webHost, name, webUser, webPassword).setSkipTest(skipTest);
            Future<List<SonarLine>> future = executorService.submit(callable);
            futures.add(future);
        }

        for (Future<List<SonarLine>> future : futures) {
            try {
                List<SonarLine> result = future.get();
                if (CollectionUtils.isEmpty(result)) {
                    continue;
                }
                logger.info("future size:" + result.size());
                sonarLineArr.addAll(result);
            } catch (InterruptedException e) {
                logger.error("report task interrupted", e);
            } catch (ExecutionException e) {
                logger.error("report execution error", e);
            }
        }
        executorService.shutdown();
        return sonarLineArr;
    }


    private int calcMod(int size, int threadCount) {
        int rest = size % threadCount;
        if (rest == 0) {
            return size / threadCount;
        }
        return size / threadCount + 1;
    }

    class SonarLineTask implements Callable<List<SonarLine>> {

        private List<Component> fileObjects = new ArrayList<>();
        private HttpClient4Connector connector;
        private String webHost;
        private String name;
        private String webUser;
        private String webPassword;
        private boolean skipTest;

        public SonarLineTask(List<Component> fileObjects, String webHost, String name, String webUser, String webPassword) {
            this.fileObjects = fileObjects;
            this.webHost = webHost;
            this.webUser = webUser;
            this.webPassword = webPassword;
            this.name = name;
            logger.info("name:" + name + ", size:" + (fileObjects == null ? 0 : fileObjects.size()));
        }


        private List<SonarLine> process() {
            JdkUtils util = new JdkUtils();
            List<SonarLine> sonarLineArr = new ArrayList<SonarLine>();
            if (CollectionUtils.isEmpty(fileObjects)) {
                return sonarLineArr;
            }
            for (Component obj : fileObjects) {
//                String clazzKey = (String) util.getField(obj, "key");
//                String clazzName = (String) util.getField(obj, "name");
//                Long componentId = (Long) util.getField(obj, "id");


                String clazzKey = obj.getKey();
                if (StringUtils.contains(clazzKey, "test/java")) {
                    continue;
                }

                String clazzName = obj.getName();
                Long componentId = Long.valueOf(obj.getId());
                String url = webHost + "/api/sources/lines?key=" + clazzKey;
                HttpGet request = new HttpGet(url);

                request.setHeader("Accept", "application/json");
                String json = connector.executeRequest(request);

                logger.info("execute http request = [" + url + "]");
                JSONObject content = null;
                try {
                    content = (JSONObject) JSONValue.parse(json);
                } catch (Exception e) {
                    continue;
                }
                if (content == null) {
                    continue;
                }
                forech(content, util, clazzName, componentId, sonarLineArr);
            }
            return sonarLineArr;
        }

        private void forech(JSONObject content, JdkUtils util, String clazzName, Long componentId, List<SonarLine> sonarLineArr) {
            for (Object oo : content.values()) {//total only one element
                JSONArray array = null;
                try {
                    array = (JSONArray) JSONValue.parse(oo.toString());
                } catch (Exception e) {
                    continue;
                }
                if (array == null) {
                    continue;
                }
                parseArray(array, util, clazzName, componentId, sonarLineArr);
            }
        }

        private void parseArray(JSONArray array, JdkUtils util, String clazzName, Long componentId, List<SonarLine> sonarLineArr) {
            for (Object ar : array) {

                if (util.getString(ar, "scmAuthor") != null && util.getString(ar, "scmAuthor").length() > 0) {

                    Integer utLineHit = util.getInteger(ar, "utLineHits");
                    utLineHit = utLineHit == null ? 0 : utLineHit > 0 ? 1 : 0;
                    if (utLineHit == null) {
                        continue;
                    }

                    SonarLine sl = new SonarLine();
                    sl.lastCommitEmpId = (util.getString(ar, "scmAuthor"));
                    sl.lastCommitDate = (util.getDateTime(ar, "scmDate"));
                    sl.lineNumber = (util.getInteger(ar, "line"));
                    sl.testClazzName = clazzName;
                    sl.componentId = String.valueOf(componentId);
                    sl.covered = utLineHit;
                    sl.effeciveCoverageLine = 1;
                    sonarLineArr.add(sl);

                }
            }
        }

        @Override
        public List<SonarLine> call() throws Exception {
            Long beginAt = System.currentTimeMillis();
            Thread.currentThread().setName("Thread-" + name);
            try {
                this.connector = new HttpClient4Connector(new Host(webHost, webUser, webPassword));
                return process();
            } finally {
                System.out.println("SonarLineTask used:" + (System.currentTimeMillis() - beginAt));
                if (connector != null) {
                    connector.close();
                }
            }

        }

        public SonarLineTask setSkipTest(boolean skipTest) {
            this.skipTest = skipTest;
            return this;
        }

    }
}
