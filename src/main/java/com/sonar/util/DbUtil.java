package com.sonar.util;

import com.sonar.model.SonarProperties;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;


public class DbUtil {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(DbUtil.class);
    private SonarProperties sonarProperties;


    public DbUtil(SonarProperties sonarProperties) {
        this.sonarProperties = sonarProperties;
    }


    public Connection getCon() {
        Connection con;
        try {
            Class.forName(sonarProperties.getDbDriver());
            con = DriverManager.getConnection(sonarProperties.getDbUrl(), sonarProperties.getDbUsername(), sonarProperties.getDbPassword());
        } catch (Exception e) {
            logger.error("loadd db driver error" + sonarProperties, e);
            throw new RuntimeException("get connection error", e);
        }
        return con;
    }

    public void closeCon(Connection con) throws Exception {
        if (con != null) {
            con.close();
        }
    }


}

