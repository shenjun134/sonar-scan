package com.sonar.dao;

import com.sonar.model.ClazzDO;
import com.sonar.model.SonarProperties;
import com.sonar.util.DbUtil;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.driver.OracleConnection;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SonarDao {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(SonarDao.class);

    private static final String QUERY_FILE_LIST = "select id, name, kee from projects where  scope = 'FIL' and root_id in (\n" +
            "select id from projects where root_id = :projectId and scope = 'PRJ' or id in (:projectId))";


    private static final String QUERY_FILE_LIST_SKIP_TEST_PART = " and kee not like '%test/java%'";

    private static final String QUERY_PROJECTS = "SELECT REGEXP_REPLACE(name,' ' ,'_') name, id  FROM  projects where scope = 'PRJ' and path is null";


    private SonarProperties sonarProperties;

    public SonarDao(SonarProperties sonarProperties) {
        this.sonarProperties = sonarProperties;
    }

    /**
     * @param projectId
     * @param skipTest
     * @return
     */
    public List<ClazzDO> getClazzList(String projectId, boolean skipTest) {
        OracleConnection rawOracleConn = (OracleConnection) new DbUtil(sonarProperties).getCon();
        String exeSql = StringUtils.replace(QUERY_FILE_LIST, ":projectId", projectId);
        if (skipTest) {
            exeSql = exeSql + QUERY_FILE_LIST_SKIP_TEST_PART;
        }
        OracleStatement st = null;


        List<ClazzDO> list = new ArrayList<ClazzDO>();
        try {
            st = (OracleStatement) rawOracleConn.createStatement();
            st.setFetchSize(1000);
            ResultSet rs = st.executeQuery(exeSql);
            while (rs.next()) {
                String name = rs.getString("name");
                String kee = rs.getString("kee");
                String id = rs.getString("id");
                list.add(new ClazzDO(id, kee, name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    logger.error("close st error", e);
                }
            }
            if (rawOracleConn != null) {
                try {
                    rawOracleConn.close();
                } catch (SQLException e) {
                    logger.error("close rawOracleConn error", e);
                }
            }
        }
        return list;
    }


    public Map<String, Long> getAllProject() {
        OracleConnection rawOracleConn = (OracleConnection) new DbUtil(sonarProperties).getCon();
        String exeSql = QUERY_PROJECTS;
        OracleStatement st = null;
        Map<String, Long> projects = new HashMap<String, Long>();

        try {
            st = (OracleStatement) rawOracleConn.createStatement();
            st.setFetchSize(1000);
            ResultSet rs = st.executeQuery(exeSql);
            while (rs.next()) {
                String name = rs.getString("name");
                Long id = rs.getLong("id");
                projects.put(name, id);
            }
        } catch (Exception e) {
            logger.error(" getAllProject error", e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    logger.error(" close st error", e);
                }
            }
            if (rawOracleConn != null) {
                try {
                    rawOracleConn.close();
                } catch (SQLException e) {
                    logger.error(" close rawOracleConn error", e);
                }
            }
        }
        return projects;
    }
}
