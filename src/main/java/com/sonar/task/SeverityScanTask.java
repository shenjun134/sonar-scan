package com.sonar.task;

import com.sonar.component.FetchComponent;
import com.sonar.constant.SeverityEnum;
import com.sonar.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.sonar.wsclient.Host;
import org.sonar.wsclient.connectors.HttpClient4Connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * with the input class list to calculate:
 * the number of change class,
 * the number of line code,
 * the number of change line code,
 * each author's related class result list
 * <p>
 * attention: http request may not stable, sometimes will 400(bad request),
 * here will retry 5 times by default, but sometimes may still bad,
 * so this class information will be lost and it will make the result not correctly.
 * what's more, we supposed that sonar result has already combine with CVS control and we can sort them by scmAuthor/author
 */
public class SeverityScanTask implements Callable<SeverityResult> {
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(SeverityScanTask.class);
    private List<ClazzDO> fileObjects = new ArrayList<>();
    private HttpClient4Connector connector;
    private String name;
    private String webHost;
    private String webUser;
    private String webPassword;
    private FetchComponent fetchComponent;

    public SeverityScanTask(List<ClazzDO> fileObjects, String webHost, String name, String webUser, String webPassword) {
        this.fileObjects = fileObjects;
        this.webHost = webHost;
        this.webUser = webUser;
        this.webPassword = webPassword;
        this.name = name;
        logger.info("name:" + name + ", size:" + (fileObjects == null ? 0 : fileObjects.size()));
    }


    /**
     * @return
     */
    private SeverityResult process() {
        SeverityResult scanResult = new SeverityResult();
        if (CollectionUtils.isEmpty(fileObjects)) {
            return scanResult;
        }
        for (ClazzDO clazzDO : fileObjects) {
            List<IssueResult> issueResults = fetchComponent.fetchClazzIssueResult(clazzDO, connector);
            if (CollectionUtils.isEmpty(issueResults)) {
                continue;
            }
            fulfill(clazzDO, issueResults, scanResult);
        }
        return scanResult;
    }


    /**
     * @param clazzDO
     * @param issueResults
     * @param scanResult
     */
    private void fulfill(ClazzDO clazzDO, List<IssueResult> issueResults, SeverityResult scanResult) {
        for (IssueResult issueResult : issueResults) {
            SeverityInfo info = create(clazzDO, issueResult);
            String author = info.getIssueResult().getAuthor();
            AuthorSeverity authorSeverity = scanResult.getAuthorSeverityMap().get(author);
            if (authorSeverity == null) {
                authorSeverity = new AuthorSeverity(author);
                scanResult.getAuthorSeverityMap().put(author, authorSeverity);
            }
            List<SeverityInfo> list = authorSeverity.getSeverityMap().get(info.getSeverity());
            if (list == null) {
                list = new ArrayList<>();
                authorSeverity.getSeverityMap().put(info.getSeverity(), list);
            }
            list.add(info);
        }
    }

    /**
     * @param clazzDO
     * @param issueResult
     * @return
     */
    private SeverityInfo create(ClazzDO clazzDO, IssueResult issueResult) {
        SeverityEnum severityEnum = SeverityEnum.codeOf(issueResult.getSeverity());
        SeverityInfo severityInfo = new SeverityInfo(severityEnum, issueResult, clazzDO);
        int issueCount = 0;
        if (CollectionUtils.isEmpty(issueResult.getFlows())) {
            issueCount++;
        } else {
            //TODO need to confirm, flows will be influenced by the original variable
//            issueCount = issueResult.getFlows().size();
            issueCount++;
        }
        severityInfo.setCount(issueCount);
        return severityInfo;
    }


    @Override
    public SeverityResult call() throws Exception {
        Long beginAt = System.currentTimeMillis();
        Thread.currentThread().setName("Thread-" + name);
        try {
            this.connector = new HttpClient4Connector(new Host(webHost, webUser, webPassword));
            return process();
        } finally {
            logger.info("SonarScanTask used:" + (System.currentTimeMillis() - beginAt));
            if (connector != null) {
                connector.close();
            }
        }
    }

    public SeverityScanTask setFetchComponent(FetchComponent fetchComponent) {
        this.fetchComponent = fetchComponent;
        return this;
    }
}
