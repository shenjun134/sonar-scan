package com.sonar.task;

import com.sonar.component.FetchComponent;
import com.sonar.model.AuthorResult;
import com.sonar.model.ClazzDO;
import com.sonar.model.ClazzResult;
import com.sonar.model.ScanResult;
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
public class SonarScanTask implements Callable<ScanResult> {
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(SonarScanTask.class);
    private List<ClazzDO> fileObjects = new ArrayList<>();
    private HttpClient4Connector connector;
    private String name;
    private String webHost;
    private String webUser;
    private String webPassword;
    private FetchComponent fetchComponent;

    public SonarScanTask(List<ClazzDO> fileObjects, String webHost, String name, String webUser, String webPassword) {
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
    private ScanResult process() {
        ScanResult scanResult = new ScanResult();
        if (CollectionUtils.isEmpty(fileObjects)) {
            return scanResult;
        }
        int totalChangeClz = 0;
        for (ClazzDO clazzDO : fileObjects) {
            ClazzResult clazzResult = fetchComponent.fetchClazzLevel(clazzDO, connector);
            if (clazzResult == null || clazzResult.getAuthorMap().size() == 0) {
                continue;
            }
            totalChangeClz = totalChangeClz + 1;
            for (Map.Entry<String, AuthorResult> entry : clazzResult.getAuthorMap().entrySet()) {
                List<AuthorResult> list = scanResult.getAuthorMapList().get(entry.getKey());
                if (list == null) {
                    list = new ArrayList<>();
                    scanResult.getAuthorMapList().put(entry.getKey(), list);
                }
                list.add(entry.getValue());
            }
            scanResult.getClazzResultList().add(clazzResult);
        }
        int totalLine = 0;
        int totalChangeLine = 0;
        int totalCoveredLine = 0;
        for (ClazzResult c : scanResult.getClazzResultList()) {
            totalLine = totalLine + c.getTotalLine();
            totalCoveredLine = totalCoveredLine + c.getTotalCoveredLine();
            totalChangeLine = totalChangeLine + c.getTotalChangeLine();
        }
        scanResult.setTotalLine(totalLine);
        scanResult.setTotalCoveredLine(totalCoveredLine);
        scanResult.setTotalChangeLine(totalChangeLine);
        scanResult.setTotalChangeClz(totalChangeClz);
        return scanResult;
    }


    @Override
    public ScanResult call() throws Exception {
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

    public SonarScanTask setFetchComponent(FetchComponent fetchComponent) {
        this.fetchComponent = fetchComponent;
        return this;
    }
}
