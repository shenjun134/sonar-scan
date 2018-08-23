package com.sonar.model;

import com.sonar.constant.CoverageOptionEnum;
import com.sonar.util.CoverageUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SonarProperties {
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(SonarProperties.class);
    private String webUser;

    private String webHost;

    private String webPassword;

    private String dbDriver;

    private String dbUrl;

    private String dbUsername;

    private String dbPassword;

    private String templateName;

    private String severityTemplate;

    private Properties properties = new Properties();

    private boolean enableUTSucc;

    private CoverageOptionEnum coverageOptionEnum = CoverageOptionEnum.REAL;


    public SonarProperties() {
        String userDir = System.getProperty("user.dir");
        InputStream is = null;
        try {
            is = new FileInputStream(userDir + "/sonar-config.properties");
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }
        if (is == null) {
            logger.error("no sonar-config.properties found...");
            System.exit(1);
        }
        try {
            Properties prop = new Properties();
            prop.load(is);
            if (prop == null) {
                logger.error("load sonar-config properties fail");
                System.exit(1);
            }
            dbUrl = prop.getProperty("db.url");
            if (StringUtils.isBlank(dbUrl)) {
                logger.error("load sonar-config properties fail without - db.url");
                System.exit(1);
            }

            dbDriver = prop.getProperty("db.driver");
            if (StringUtils.isBlank(dbUrl)) {
                logger.error("load sonar-config properties fail without - db.driver");
                System.exit(1);
            }

            dbUsername = prop.getProperty("db.user");
            if (StringUtils.isBlank(dbUrl)) {
                logger.error("load sonar-config properties fail without - db.user");
                System.exit(1);
            }

            dbPassword = prop.getProperty("db.password");
            if (StringUtils.isBlank(dbUrl)) {
                logger.error("load sonar-config properties fail without - db.password");
                System.exit(1);
            }

            webHost = prop.getProperty("web.host");
            if (StringUtils.isBlank(dbUrl)) {
                logger.error("load sonar-config properties fail without - web.host");
                System.exit(1);
            }

            webUser = prop.getProperty("web.user");
            if (StringUtils.isBlank(dbUrl)) {
                logger.error("load sonar-config properties fail without - web.user");
                System.exit(1);
            }

            webPassword = prop.getProperty("web.password");
            if (StringUtils.isBlank(dbUrl)) {
                logger.error("load sonar-config properties fail without - web.password");
                System.exit(1);
            }

            templateName = prop.getProperty("html.template");
            if (StringUtils.isBlank(templateName)) {
                logger.error("load sonar-config properties fail without - html.template");
            }

            severityTemplate = prop.getProperty("html.severity.template");
            if (StringUtils.isBlank(severityTemplate)) {
                logger.error("load sonar-config properties fail without - html.severity.template");
            }

            enableUTSucc = Boolean.valueOf(prop.getProperty("ut.success.rate.enable"));

            this.setCoverageOptionEnum(prop);

            CoverageUtil.init(coverageOptionEnum);


            logger.info("load sonar-config properties finished - " + this);
        } catch (IOException e) {
            logger.error("load sonar-config properties error", e);
            System.exit(1);
        }
    }


    private void setCoverageOptionEnum(Properties prop) {
        String coverageStr = prop.getProperty("coverage.option");
        if (StringUtils.isBlank(coverageStr)) {
            return;
        }
        coverageStr = coverageStr.trim();
        for (CoverageOptionEnum temp : CoverageOptionEnum.values()) {
            if (StringUtils.equalsIgnoreCase(coverageStr, temp.name())) {
                this.coverageOptionEnum = temp;
                break;
            }
        }
    }

    public String getWebUser() {
        return webUser;
    }

    public void setWebUser(String webUser) {
        this.webUser = webUser;
    }

    public String getWebHost() {
        return webHost;
    }

    public void setWebHost(String webHost) {
        this.webHost = webHost;
    }

    public String getWebPassword() {
        return webPassword;
    }

    public void setWebPassword(String webPassword) {
        this.webPassword = webPassword;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getSeverityTemplate() {
        return severityTemplate;
    }

    public void setSeverityTemplate(String severityTemplate) {
        this.severityTemplate = severityTemplate;
    }

    public boolean isEnableUTSucc() {
        return enableUTSucc;
    }

    public void setEnableUTSucc(boolean enableUTSucc) {
        this.enableUTSucc = enableUTSucc;
    }

    @Override
    public String toString() {
        return "SonarProperties{" +
                "webUser='" + webUser + '\'' +
                ", webHost='" + webHost + '\'' +
                ", webPassword='" + webPassword + '\'' +
                ", dbDriver='" + dbDriver + '\'' +
                ", dbUrl='" + dbUrl + '\'' +
                ", dbUsername='" + dbUsername + '\'' +
                ", dbPassword='" + dbPassword + '\'' +
                ", templateName='" + templateName + '\'' +
                ", severityTemplate='" + severityTemplate + '\'' +
                ", properties=" + properties +
                ", enableUTSucc=" + enableUTSucc +
                '}';
    }
}
