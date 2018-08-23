package com.sonar.service;

import com.sonar.component.FetchComponent;
import com.sonar.component.UTComponent;
import com.sonar.component.impl.FetchComponentImpl;
import com.sonar.component.impl.UTComponentImpl;
import com.sonar.constant.SeverityEnum;
import com.sonar.dao.SonarDao;
import com.sonar.model.*;
import com.sonar.task.SonarScanTask;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ReportService {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(ReportService.class);


    private ReportResult reportResult;

    private FetchComponent fetchComponent;


    private UTComponent utComponent;

    private SonarDao sonarDao;

    private SonarProperties sonarProperties;
    private TeamProperties teamProperties;

    public ReportService() {
    }

    interface Constant {
        int threadCount = 10;
    }

    /**
     * @param reportReq
     */
    public void process(ReportReq reportReq) {
        init();
        before(reportReq);
        doProcess(reportReq);
        after(reportReq);
    }


    protected void before(ReportReq reportReq) {
        if (StringUtils.isBlank(reportReq.getProjectNames())) {
            throw new IllegalArgumentException("no project found");
        }
        if (reportReq.getBeginDt() == null || reportReq.getEndDt() == null) {
            throw new IllegalArgumentException("date range is not set");
        }
        if (reportReq.getBeginDt().after(reportReq.getEndDt())) {
            throw new IllegalArgumentException("begin date is after then end date");
        }

        reportResult.setEndDate(reportReq.getEndDt());
        reportResult.setBeginDate(reportReq.getBeginDt());
        if (reportReq.getThreadCount() < 1) {
            reportResult.setThreadCount(Constant.threadCount);
        } else {
            reportResult.setThreadCount(reportReq.getThreadCount());
        }
        if (CollectionUtils.isEmpty(reportReq.getPickedTeam())) {
            throw new IllegalArgumentException("no team selected...");
        }
        setTeam(reportReq);
        reportResult.setSkipTest(reportReq.isSkipTest());
        reportResult.setVersion(reportReq.getVersion());


        String severityList = reportReq.getSeverityList();
        StringBuilder tempSeverity = new StringBuilder();
        if (StringUtils.isNotBlank(severityList)) {
            String[] severityArr = severityList.split(",");
            if (severityArr != null) {
                for (String temp : severityArr) {
                    SeverityEnum severityEnum = SeverityEnum.codeOf(StringUtils.trim(temp));
                    if (severityEnum.getCode().equalsIgnoreCase(SeverityEnum.DEF.getCode())) {
                        continue;
                    }
                    tempSeverity.append(severityEnum.getCode()).append(",");
                }
            }
        }
        reportResult.setSeverityList(tempSeverity.toString());
        ((FetchComponentImpl) fetchComponent).setBeginDate(reportReq.getBeginDt());
        ((FetchComponentImpl) fetchComponent).setEndDate(reportReq.getEndDt());
        ((FetchComponentImpl) fetchComponent).setSeverityList(tempSeverity.toString());
        ((FetchComponentImpl) fetchComponent).setTeamProperties(teamProperties);


        if (sonarProperties.isEnableUTSucc()) {
            utComponent = new UTComponentImpl(sonarProperties.getWebUser(), sonarProperties.getWebHost(), sonarProperties.getWebPassword());
        }

    }


    /**
     * generate report
     *
     * @param reportReq
     */
    protected void after(ReportReq reportReq) {
        HtmlService htmlService = new HtmlService(sonarProperties, teamProperties, reportResult);
        try {
            htmlService.generateReport();
        } catch (IOException e) {
            logger.error("generateReport error", e);
        }
    }


    /**
     * @param reportReq
     */
    protected void doProcess(ReportReq reportReq) {
        String[] proArr = StringUtils.split(reportReq.getProjectNames(), ",");
        if (proArr == null || proArr.length == 0) {
            return;
        }

        Map<String, ProjectDO> allProject = sonarDao.getAllProject();
        if (allProject == null || allProject.size() == 0) {
            return;
        }
        for (String pro : proArr) {
            String temp = StringUtils.trim(pro);
            ProjectDO project = allProject.get(temp);
            if (project == null) {
                logger.warn("no project find for - " + temp);
                continue;
            }
            reportResult.getProjectMap().put(temp, "" + project);
            fetchClassList("" + project.getId());
        }
        reportResult.setTotalChangeClz(reportResult.getScanResult().getClazzResultList().size());
        reportResult.setTotalChangeAuthor(reportResult.getScanResult().getAuthorMapList().size());
        reportResult.getScanResult().calcTotal(reportResult.getPickedTeam(), teamProperties);
        reportResult.getScanResult().printTotal();


        if (sonarProperties.isEnableUTSucc()) {
            for (String pro : proArr) {
                String temp = StringUtils.trim(pro);
                ProjectDO project = allProject.get(temp);
                if (project == null) {
                    logger.warn("no project find for - " + temp);
                    continue;
                }
                ProjectUTDO projectUTDO = utComponent.queryUTList(project);
                reportResult.getProjectUTMap().put(projectUTDO.getId(), projectUTDO);
            }


        }

    }


    protected void init() {
        reportResult = new ReportResult();
        sonarProperties = new SonarProperties();
        teamProperties = new TeamProperties();
        fetchComponent = new FetchComponentImpl(sonarProperties.getWebUser(), sonarProperties.getWebHost(), sonarProperties.getWebPassword());
        sonarDao = new SonarDao(sonarProperties);
    }

    /***********************************************************private************************************************/

    /**
     * @param reportReq
     */
    private void setTeam(ReportReq reportReq) {
        for (String team : reportReq.getPickedTeam()) {
            if (StringUtils.equalsIgnoreCase(team, "ALL")) {
                for (Map.Entry<String, List<String>> entry : teamProperties.getTeamMap().entrySet()) {
                    TeamResult teamResult = new TeamResult(entry.getKey(), entry.getValue());
                    reportResult.getPickedTeam().add(teamResult);
                }
                break;
            }
            List<String> userList = teamProperties.getTeamMap().get(team);
            if (CollectionUtils.isEmpty(userList)) {
                continue;
            }
            TeamResult teamResult = new TeamResult(team, userList);
            reportResult.getPickedTeam().add(teamResult);
        }
    }


    private void fetchClassList(String projectId) {
        List<ClazzDO> clazzDOList = sonarDao.getClazzList(projectId, reportResult.isSkipTest());
        if (CollectionUtils.isEmpty(clazzDOList)) {
            return;
        }
        reportResult.addTotalClz(clazzDOList.size());
        fetchClazzLevel(clazzDOList);
    }


    private void fetchClazzLevel(List<ClazzDO> clazzDOList) {
        int totalClz = clazzDOList.size();
        int mod = calcMod(totalClz, reportResult.getThreadCount());
        logger.info("file size:" + totalClz + ", threadCount:" + reportResult.getThreadCount() + ", mod:" + mod);
        List<Future<ScanResult>> futures = new ArrayList<Future<ScanResult>>();
        ExecutorService executorService = Executors.newFixedThreadPool(reportResult.getThreadCount());
        for (int i = 0; i < reportResult.getThreadCount(); i++) {
            int fromIndex = i * mod;
            int endIndex = (i + 1) * mod;
            fromIndex = fromIndex > totalClz ? totalClz : fromIndex;
            endIndex = endIndex > totalClz ? totalClz : endIndex;

            List<ClazzDO> temp = null;
            if (fromIndex < endIndex) {
                temp = clazzDOList.subList(fromIndex, endIndex);
            }
            String name = fromIndex + "~" + endIndex;
            Callable<ScanResult> callable = new SonarScanTask(temp, sonarProperties.getWebHost(), name, sonarProperties.getWebUser(), sonarProperties.getWebPassword()).setFetchComponent(fetchComponent);
            Future<ScanResult> future = executorService.submit(callable);
            futures.add(future);
        }

        for (Future<ScanResult> future : futures) {
            try {
                ScanResult result = future.get();
                if (result == null || CollectionUtils.isEmpty(result.getClazzResultList())) {
                    continue;
                }
                logger.info("future size:" + result.getClazzResultList().size());
                reportResult.getScanResult().getClazzResultList().addAll(result.getClazzResultList());
                reportResult.addTotalLine(result.getTotalLine());
                reportResult.addTotalCoveredLine(result.getTotalCoveredLine());
                reportResult.addTotalChangeLine(result.getTotalChangeLine());
                reportResult.addTotalChangeClz(result.getTotalChangeClz());
                for (Map.Entry<String, List<AuthorResult>> entry : result.getAuthorMapList().entrySet()) {
                    List<AuthorResult> list = reportResult.getScanResult().getAuthorMapList().get(entry.getKey());
                    if (list == null) {
                        list = new ArrayList<>();
                        reportResult.getScanResult().getAuthorMapList().put(entry.getKey(), list);
                    }
                    list.addAll(entry.getValue());
                }
            } catch (InterruptedException e) {
                logger.error("report task interrupted", e);
            } catch (ExecutionException e) {
                logger.error("report execution error", e);
            }
        }
        executorService.shutdown();
    }

    private int calcMod(int size, int threadCount) {
        int rest = size % threadCount;
        if (rest == 0) {
            return size / threadCount;
        }
        return size / threadCount + 1;
    }

}
