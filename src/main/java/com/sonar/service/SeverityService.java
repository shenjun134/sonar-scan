package com.sonar.service;

import com.sonar.component.FetchComponent;
import com.sonar.component.impl.FetchComponentImpl;
import com.sonar.constant.SeverityEnum;
import com.sonar.dao.SonarDao;
import com.sonar.model.*;
import com.sonar.task.SeverityScanTask;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class SeverityService {
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(SeverityService.class);

    private FetchComponent fetchComponent;

    private SonarDao sonarDao;

    private SonarProperties sonarProperties;
    private TeamProperties teamProperties;

    private List<String> projectList = new ArrayList<>();

    private SeverityResult severityResult;

    private String severityList;

    interface Constant {
        int threadCount = 10;
    }

    public SeverityService(List<String> projectList, List<SeverityEnum> severityEnumList) {
        if (CollectionUtils.isEmpty(projectList)) {
            throw new RuntimeException("no project here ... ");
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (CollectionUtils.isEmpty(severityEnumList)) {
            stringBuilder.append(SeverityEnum.BLOCKER.getCode()).append(",");
            stringBuilder.append(SeverityEnum.CRITICAL.getCode()).append("");
        } else {
            for (SeverityEnum severityEnum : severityEnumList) {
                if (SeverityEnum.DEF.getCode().equalsIgnoreCase(severityEnum.getCode())) {
                    continue;
                }
                stringBuilder.append(severityEnum.getCode()).append(",");
            }
        }
        this.projectList.addAll(projectList);
        this.severityList = stringBuilder.toString();
    }


    public void process() {
        init();
        before();
        doProcess();
        after();
    }


    private void init() {
        severityResult = new SeverityResult();
        sonarProperties = new SonarProperties();
        teamProperties = new TeamProperties();
        fetchComponent = new FetchComponentImpl(sonarProperties.getWebUser(), sonarProperties.getWebHost(), sonarProperties.getWebPassword());
        sonarDao = new SonarDao(sonarProperties);
        severityResult.setProjects(projectList);
    }

    private void before() {
        ((FetchComponentImpl) fetchComponent).setSeverityList(severityList);
        ((FetchComponentImpl) fetchComponent).setTeamProperties(teamProperties);
    }

    private void after() {
        SeverityHtmlService htmlService = new SeverityHtmlService(sonarProperties, teamProperties, severityResult);
        try {
            htmlService.generateReport();
        } catch (IOException e) {
            logger.error("generateReport error", e);
            System.exit(0);
        }
    }

    /**
     *
     */
    private void doProcess() {
        Map<String, ProjectDO> allProject = sonarDao.getAllProject();
        if (allProject == null || allProject.size() == 0) {
            return;
        }
        for (String pro : projectList) {
            String temp = StringUtils.trim(pro);
            ProjectDO project = allProject.get(temp);
            if (project == null) {
                logger.warn("no project find for - " + temp);
                continue;
            }
            fetchProjectIssue("" + project.getId());
        }
        severityResult.analysis();
        severityResult.print();
    }

    /**
     * @param projectId
     */
    private void fetchProjectIssue(String projectId) {
        List<ClazzDO> clazzDOList = sonarDao.getClazzList(projectId, true);
        if (CollectionUtils.isEmpty(clazzDOList)) {
            return;
        }
        int totalClz = clazzDOList.size();
        int mod = calcMod(totalClz, Constant.threadCount);
        logger.info("file size:" + totalClz + ", threadCount:" + Constant.threadCount + ", mod:" + mod);
        List<Future<SeverityResult>> futures = new ArrayList<Future<SeverityResult>>();
        ExecutorService executorService = Executors.newFixedThreadPool(Constant.threadCount);
        for (int i = 0; i < Constant.threadCount; i++) {
            int fromIndex = i * mod;
            int endIndex = (i + 1) * mod;
            fromIndex = fromIndex > totalClz ? totalClz : fromIndex;
            endIndex = endIndex > totalClz ? totalClz : endIndex;

            List<ClazzDO> temp = null;
            if (fromIndex < endIndex) {
                temp = clazzDOList.subList(fromIndex, endIndex);
            }
            String name = fromIndex + "~" + endIndex;
            Callable<SeverityResult> callable = new SeverityScanTask(temp, sonarProperties.getWebHost(), name, sonarProperties.getWebUser(), sonarProperties.getWebPassword()).setFetchComponent(fetchComponent);
            Future<SeverityResult> future = executorService.submit(callable);
            futures.add(future);
        }

        for (Future<SeverityResult> future : futures) {
            try {
                SeverityResult result = future.get();
                if (result == null || result.getAuthorSeverityMap().size() == 0) {
                    continue;
                }
                logger.info("future size:" + result.getAuthorSeverityMap().size());
                fulfill(result);
            } catch (InterruptedException e) {
                logger.error("severity task interrupted", e);
            } catch (ExecutionException e) {
                logger.error("severity execution error", e);
            }
        }
        executorService.shutdown();
    }

    private void fulfill(SeverityResult result) {
        for (Map.Entry<String, AuthorSeverity> entry : result.getAuthorSeverityMap().entrySet()) {
            String author = entry.getKey();
            AuthorSeverity current = entry.getValue();
            AuthorSeverity authorSeverity = severityResult.getAuthorSeverityMap().get(author);
            if (authorSeverity == null) {
                authorSeverity = current;
                severityResult.getAuthorSeverityMap().put(author, authorSeverity);
                continue;
            }
            for (Map.Entry<SeverityEnum, List<SeverityInfo>> enumListEntry : current.getSeverityMap().entrySet()) {
                SeverityEnum severityEnum = enumListEntry.getKey();
                List<SeverityInfo> tempList = enumListEntry.getValue();
                List<SeverityInfo> list = authorSeverity.getSeverityMap().get(severityEnum);
                if (list == null) {
                    list = new ArrayList<>();
                    authorSeverity.getSeverityMap().put(severityEnum, list);
                }
                list.addAll(tempList);
            }

        }
    }

    private int calcMod(int size, int threadCount) {
        int rest = size % threadCount;
        if (rest == 0) {
            return size / threadCount;
        }
        return size / threadCount + 1;
    }


}
