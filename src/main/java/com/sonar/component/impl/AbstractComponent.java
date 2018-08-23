package com.sonar.component.impl;

import com.sonar.model.ProjectDO;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.sonar.wsclient.JdkUtils;
import org.sonar.wsclient.connectors.HttpClient4Connector;

abstract public class AbstractComponent {

    protected String webUser;

    protected String webHost;

    protected String webPassword;

    protected JdkUtils util;

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(AbstractComponent.class);

    interface Constant {
        int retryTimes = 5;
    }


    /**
     * @param url
     * @param connector
     * @return
     */
    protected String httpGet(String url, HttpClient4Connector connector) {
        logger.info("begin to get - " + url);
        Exception error;
        int retryCount = 0;
        do {
            try {
                HttpGet request = new HttpGet(url);
                request.setHeader("Accept", "application/json");
                return connector.executeRequest(request);
            } catch (Exception e) {
                error = e;
                retryCount++;
            }
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                logger.error("thread InterruptedException error", e);
            }
        } while (retryCount < FetchComponentImpl.Constant.retryTimes);
        logger.warn("http get fail - " + url, error);
        return null;
    }

    /**
     * @param projectDO
     */
    protected void check(ProjectDO projectDO) {
        if (projectDO == null) {
            throw new IllegalArgumentException("projectDO is null");
        }
        if (StringUtils.isBlank(projectDO.getKee())) {
            throw new IllegalArgumentException("projectDO.kee is blank");

        }
    }

    public void setWebUser(String webUser) {
        this.webUser = webUser;
    }

    public void setWebHost(String webHost) {
        this.webHost = webHost;
    }

    public void setWebPassword(String webPassword) {
        this.webPassword = webPassword;
    }

}
