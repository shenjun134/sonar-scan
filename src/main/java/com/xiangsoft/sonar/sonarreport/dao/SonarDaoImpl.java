package com.xiangsoft.sonar.sonarreport.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.xiangsoft.sonar.sonarreport.model.Component;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.driver.OracleConnection;

import com.statestr.gcth.core.dao.AbstractJdbcDao;
import com.xiangsoft.sonar.sonarreport.model.SonarIssue;
import com.xiangsoft.sonar.sonarreport.model.TotalLineInfo;
import com.xiangsoft.util.DbUtil;
import org.apache.commons.lang.StringUtils;

public class SonarDaoImpl extends AbstractJdbcDao {


    //project_name
    String projectSql = "select * from projects where \"NAME\" = 'gcth-application-parent application_1.8_branch'";
    //project_id = 33255
    String projectSqlByProjectId = "select \"NAME\" projectName from projects where \"ID\" = :projectId";

    //'gcth-application-parent application_1.8_branch' and  project_id = 33215
    //all projcet_id
    String getAllProjectSql = "select \"ID\" from projects where root_id in (" +
            "select \"ID\" from projects where root_id = (select \"ID\" from projects where name = :projectName)" +
            ") and qualifier = 'CLA' order by kee";

    String queryLineInfo = "select m.name, md.data,prst.kee from measure_data  md join " +
            " project_measures pm on md.measure_id = pm.id " +
            " join metrics m on pm.metric_id = m.id " +
            " join (select prj.kee kee, sst.id snapshot_id from projects prj, snapshots sst " +
            " where prj.id = sst.project_id and prj.id = :projectId ) prst " +
            " on prst.snapshot_id =  md.snapshot_id " +
            " where (m.name = 'coverage_line_hits_data' or m.name = 'last_commit_datetimes_by_line' or m.name = 'authors_by_line' )";

    String queryLineInfoForV54 = "select m.name, pm.measure_data data,prst.kee from " +
            " project_measures pm join metrics m " +
            " on pm.metric_id = m.id " +
            " join (select prj.id prj_id, prj.kee kee, sst.id snapshot_id from projects prj, snapshots sst " +
            " where prj.id = sst.project_id and prj.id = 50351 ) prst " +
            " on pm.snapshot_id = prst.snapshot_id " +
            " where (m.name = 'coverage_line_hits_data' or m.name = 'last_commit_datetimes_by_line' or m.name = 'authors_by_line' )";

    String queryIssueInfoByComponentId = "select s.component_id,s.line, s.severity from issues s where s.component_id = :componentId order by s.line";


    String queryAllLineInfoByModuleRootProjectId = "select prst.proj_id, m.name, md.data, prst.kee, md.snapshot_id  from measure_data  md join " +
            " project_measures pm on md.measure_id = pm.id " +
            " join metrics m on pm.metric_id = m.id " +
            " join (select prj.id proj_id, prj.kee kee, sst.id snapshot_id from projects prj, snapshots sst " +
            " where prj.id = sst.project_id " +                                                            //" and prj.id = 37033 " +
            " and prj.id  in " +

            " (select \"ID\" from projects where root_id in ( " +
            " select \"ID\" from projects where root_id =  :projectId " +
            " ) and qualifier = 'CLA' ) " +
            //" (select \"ID\" from projects where root_id in ( :projectId ) and qualifier = 'CLA' ) " +

            " ) prst " +
            " on prst.snapshot_id =  md.snapshot_id " +
            " where " +
            " (m.name = 'last_commit_datetimes_by_line' or m.name = 'authors_by_line' or m.name ='overall_coverage_line_hits_data' or m.name = 'coverage_line_hits_data' or m.name = 'it_coverage_line_hits_data' ) " +
            " order by kee,name";


    String queryAllLineInfoByModuleRootProjectIdForV54 = "select prst.proj_id, m.name, pm.measure_data data,prst.kee, pm.snapshot_id  from " +
            " project_measures pm " +
            " join metrics m on pm.metric_id = m.id " +
            " join (select prj.id proj_id, prj.kee kee, sst.id snapshot_id from projects prj, snapshots sst " +
            " where prj.id = sst.project_id " +
            " and prj.id  in " +

            " ( select \"ID\" from projects where root_id = :projectId and language = 'java' and qualifier = 'FIL' ) " +

            " ) prst " +
            " on prst.snapshot_id =  pm.snapshot_id " +
            " where " +
            " (m.name = 'last_commit_datetimes_by_line' or m.name = 'authors_by_line' or m.name ='overall_coverage_line_hits_data' or m.name = 'coverage_line_hits_data' or m.name = 'it_coverage_line_hits_data' ) " +
            " order by kee,name";

    String queryAllLineInfoByProjectId = "select prst.proj_id, m.name, md.data,prst.kee, md.snapshot_id  from measure_data  md join " +
            " project_measures pm on md.measure_id = pm.id " +
            " join metrics m on pm.metric_id = m.id " +
            " join (select prj.id proj_id, prj.kee kee, sst.id snapshot_id from projects prj, snapshots sst " +
            " where prj.id = sst.project_id and prj.id  in ( :projectId )" +
            " ) prst " +
            " on prst.snapshot_id =  md.snapshot_id " +
            " where " +
            " (m.name = 'coverage_line_hits_data' or m.name = 'last_commit_datetimes_by_line' or m.name = 'authors_by_line' ) " +
            " order by kee,name";

    String queryAllIssueInfoByComponentId = "select s.component_id,s.line, s.severity from issues s where s.component_id in (:componentId) order by s.component_id,s.line";

    String queryAllIssueInfoByRootId = "select s.component_id,s.line, s.severity from issues s where " +               //" s.component_id = 37033  and " +
            "s.component_id in " +
            " (select \"ID\" from projects where root_id in ( " +
            " select \"ID\" from projects where root_id =  :componentId " +
            " ) and qualifier = 'CLA' ) " +

//	" (select \"ID\" from projects where root_id in (  :componentId  " +
//	 " ) and qualifier = 'CLA' ) " +

            " order by s.component_id,s.line";

    private static final String QUERY_FILE_LIST = "select id, name, kee from projects where  scope = 'FIL' and root_id in (\n" +
            "select id from projects where root_id = :projectId and scope = 'PRJ' or id in (:projectId))";


    private static final String QUERY_FILE_LIST_SKIP_TEST_PART = " and kee not like '%test/java%'";

    private OracleConnection rawOracleConn = null;


    public List<String> getAllProjectId(String projectName) {
        if (rawOracleConn == null) {
            rawOracleConn = (OracleConnection) new DbUtil().getCon();
        }
        OracleStatement st = null;

        List<String> projectIdList = new ArrayList<String>();

        try {
            st = (OracleStatement) rawOracleConn.createStatement();
            String exeSql = getAllProjectSql.replace(":projectName", "'" + projectName + "'");
            ResultSet resultSet = st.executeQuery(exeSql);

            while (resultSet.next()) {
                projectIdList.add(resultSet.getString("ID"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
                if (rawOracleConn != null) {
                    rawOracleConn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return projectIdList;
    }

    //all class 's lines map, list match class , and map match
    public List<TotalLineInfo> getKeyBlobByProjectId(String projectId) {

        if (rawOracleConn == null) {
            rawOracleConn = (OracleConnection) new DbUtil().getCon();
        }

        OracleStatement st = null;

        List<TotalLineInfo> totalLineInfoList = new ArrayList<TotalLineInfo>();

        try {
            st = (OracleStatement) rawOracleConn.createStatement();
            String exeSql = queryLineInfo.replace(":projectId", "" + projectId + "");

            ResultSet resultSet = st.executeQuery(exeSql);
            TotalLineInfo tli = new TotalLineInfo();
            while (resultSet.next()) {

                tli.testClassName = resultSet.getString("kee");
                System.out.println(" kee = " + tli.testClassName);

                String name = resultSet.getString("name");
                oracle.sql.BLOB data = (oracle.sql.BLOB) resultSet.getBlob("data");
                InputStream is = data.getBinaryStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String s;
                StringBuilder sb = new StringBuilder();
                while ((s = reader.readLine()) != null) {
                    sb.append(s);
                }

                if (name.equalsIgnoreCase("coverage_line_hits_data")) {
                    tli.coverageLineHitsData = sb.toString();
                    System.out.println("name = " + name);
                    System.out.println("data = " + sb.toString());
                    continue;
                }
                if (name.equalsIgnoreCase("authors_by_line")) {
                    tli.authorsByLine = sb.toString();
                    System.out.println("name = " + name);
                    System.out.println("data = " + sb.toString());
                    continue;
                }
                if (name.equalsIgnoreCase("last_commit_datetimes_by_line")) {
                    tli.lastCommitDatetimeByLine = sb.toString();
                    System.out.println("name = " + name);
                    System.out.println("data = " + sb.toString());
                    continue;
                }

            }
            resultSet.close();
            totalLineInfoList.add(tli);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {

                st.close();
                if (rawOracleConn != null) {
                    rawOracleConn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return totalLineInfoList;
    }

    public List<SonarIssue> getIssueListByComponentId(String componentId) {

        OracleConnection rawOracleConn = (OracleConnection) new DbUtil().getCon();
        OracleStatement st;

        List<SonarIssue> totalLineInfoList = new ArrayList<SonarIssue>();
        String exeSql = queryIssueInfoByComponentId.replace(":componentId", "'" + componentId + "'");

        try {
            st = (OracleStatement) rawOracleConn.createStatement();
            ResultSet rs = st.executeQuery(exeSql);
            while (rs.next()) {

                SonarIssue si = new SonarIssue();
                int line = rs.getInt("line");
                String severity = rs.getString("severity");
                si.lineNumber = line;
                si.severity = severity;
                si.componentId = rs.getString("component_Id");
                if (line > 0) {
                    totalLineInfoList.add(si);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalLineInfoList;

    }

    public List<TotalLineInfo> getLineKeyBlobInfoByModuleSpecProjectId(String projectId) {

        String arr[] = projectId.split(",");
        String allProId = "";
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                allProId += ",";
            }
            allProId += "'" + arr[i] + "'";
        }

        String exeSql = queryAllLineInfoByProjectId.replace(":projectId", allProId);
        System.out.println("exeSql [" + exeSql + "]");
        return getLineKeyBlobInfoBySql(exeSql);
    }

    public List<TotalLineInfo> getLineKeyBlobInfoByModuleRootProjectId(String projectId) {
        String exeSql = queryAllLineInfoByModuleRootProjectId.replace(":projectId", "'" + projectId + "'");
        return getLineKeyBlobInfoBySql(exeSql);
    }

    public String getProjectNameByProjectId(String projectId) {
        OracleConnection rawOracleConn = (OracleConnection) new DbUtil().getCon();
        String exeSql = projectSqlByProjectId.replace(":projectId", "'" + projectId + "'");
        OracleStatement st = null;

        String projectName = "";
        try {
            st = (OracleStatement) rawOracleConn.createStatement();
            ResultSet rs = st.executeQuery(exeSql);
            while (rs.next()) {
                projectName = rs.getString("projectName");
            }
        } catch (Exception e) {
            e.printStackTrace();
            projectName = "SomeComponentName";

        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (rawOracleConn != null) {
                try {
                    rawOracleConn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return projectName;
    }


    public List<Component> getComponents(String projectId, boolean skipTest) {
        OracleConnection rawOracleConn = (OracleConnection) new DbUtil().getCon();
        String exeSql = StringUtils.replace(QUERY_FILE_LIST, ":projectId", projectId);
        if (skipTest) {
            exeSql = exeSql + QUERY_FILE_LIST_SKIP_TEST_PART;
        }
        OracleStatement st = null;


        List<Component> list = new ArrayList<Component>();
        try {
            st = (OracleStatement) rawOracleConn.createStatement();
            st.setFetchSize(1000);
            ResultSet rs = st.executeQuery(exeSql);
            while (rs.next()) {
                String name = rs.getString("name");
                String kee = rs.getString("kee");
                String id = rs.getString("id");
                list.add(new Component(id, kee, name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (rawOracleConn != null) {
                try {
                    rawOracleConn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    //  project_id = 33215
    public List<TotalLineInfo> getLineKeyBlobInfoBySql(String exeSql) {

        if (rawOracleConn == null) {
            rawOracleConn = (OracleConnection) new DbUtil().getCon();
        }

        OracleStatement st = null;

        List<TotalLineInfo> totalLineInfoList = new ArrayList<TotalLineInfo>();

        try {
            st = (OracleStatement) rawOracleConn.createStatement();

            ResultSet resultSet = st.executeQuery(exeSql);

            List<ClassIdMetricsNameBlobDataPair> classMetricsNameBlobDataList = new ArrayList<ClassIdMetricsNameBlobDataPair>();
            while (resultSet.next()) {

                String testClassName = resultSet.getString("kee");
                String componentId = resultSet.getString("proj_id");

                String name = resultSet.getString("name");

                oracle.sql.BLOB data = (oracle.sql.BLOB) resultSet.getBlob("data");
                InputStream is = data.getBinaryStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String s;
                StringBuilder sbBlobStr = new StringBuilder();
                while ((s = reader.readLine()) != null) {
                    sbBlobStr.append(s);
                }
                classMetricsNameBlobDataList.add(
                        ClassIdMetricsNameBlobDataPair.newInstance(componentId, testClassName, name, sbBlobStr.toString()));

            }

            totalLineInfoList = packLineInfoToCollection(classMetricsNameBlobDataList);

            resultSet.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
                rawOracleConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return totalLineInfoList;
    }

    public static class ClassIdMetricsNameBlobDataPair {
        public String classId;
        public String kee;
        public String metricsName;
        public String blobDataStr;

        public ClassIdMetricsNameBlobDataPair(String classId, String kee, String metricsName, String pBlobData) {
            this.classId = classId;
            this.kee = kee;
            this.metricsName = metricsName;
            this.blobDataStr = pBlobData;
        }

        public static ClassIdMetricsNameBlobDataPair newInstance(String classId, String kee, String metricsName, String pBlobData) {
            return new ClassIdMetricsNameBlobDataPair(classId, kee, metricsName, pBlobData);
        }
    }

    public List<TotalLineInfo> packLineInfoToCollection(List<ClassIdMetricsNameBlobDataPair> classIdMetricsBlobPairList) {

        List<TotalLineInfo> resultLineInfoList = new ArrayList<TotalLineInfo>();

        TotalLineInfo singleInfo = new TotalLineInfo();

        int lastClassIdHappenCount = 0;
        String lastClassId = "";

        for (int i = 0; classIdMetricsBlobPairList != null && i < classIdMetricsBlobPairList.size(); i++) {

            ClassIdMetricsNameBlobDataPair classMetricsDataPair = classIdMetricsBlobPairList.get(i);

            if (classMetricsDataPair.metricsName.equalsIgnoreCase("authors_by_line")) {

                if (singleInfo.authorsByLine != null && singleInfo.authorsByLine.length() > 0) {
                    if (lastClassIdHappenCount > 1) {
                        resultLineInfoList.add(singleInfo.makeNewCopy());
                        System.out.println("Put the class id = " + singleInfo.componentId + " in to List.");
                    }
                    singleInfo = new TotalLineInfo();
                    singleInfo.componentId = classMetricsDataPair.classId;
                    singleInfo.testClassName = classMetricsDataPair.kee;
                    singleInfo.authorsByLine = classMetricsDataPair.blobDataStr;
                    lastClassId = classMetricsDataPair.classId;
                    lastClassIdHappenCount = 1;
                    continue;
                }
                singleInfo.componentId = classMetricsDataPair.classId;
                singleInfo.testClassName = classMetricsDataPair.kee;
                singleInfo.authorsByLine = classMetricsDataPair.blobDataStr;
            } else if (classMetricsDataPair.metricsName.equalsIgnoreCase("last_commit_datetimes_by_line")) {
                singleInfo.lastCommitDatetimeByLine = classMetricsDataPair.blobDataStr;
            }
            // coverage priority  : overall_coverage > coverage_line_hits_data > it_coverage_line_hits_data , pick one of these to the property [singleInfo.coverageLineHitsData]
            else if (classMetricsDataPair.metricsName.equalsIgnoreCase("coverage_line_hits_data")) {
                if (singleInfo.coverageLineHitsData == null || singleInfo.coverageLineHitsData.length() == 0) {
                    singleInfo.coverageLineHitsData = classMetricsDataPair.blobDataStr;
                }
            } else if (classMetricsDataPair.metricsName.equalsIgnoreCase("it_coverage_line_hits_data")) {
                if (singleInfo.coverageLineHitsData == null || singleInfo.coverageLineHitsData.length() == 0) {
                    singleInfo.coverageLineHitsData = classMetricsDataPair.blobDataStr;
                }
            } else if (classMetricsDataPair.metricsName.equalsIgnoreCase("overall_coverage_line_hits_data")) {
                singleInfo.coverageLineHitsData = classMetricsDataPair.blobDataStr;
            }

            if (i == 0) {
                lastClassId = classMetricsDataPair.classId;
                lastClassIdHappenCount++;
            } else {
                if (lastClassId.equals(classMetricsDataPair.classId)) {
                    lastClassIdHappenCount++;
                } else {
                    lastClassId = classMetricsDataPair.classId;
                    lastClassIdHappenCount = 1;
                }
            }

            //for insert last Line info Object
            if (i == classIdMetricsBlobPairList.size() - 1 && lastClassIdHappenCount > 1) {
                resultLineInfoList.add(singleInfo.makeNewCopy());
            }

        }

        return resultLineInfoList;
    }

    private String assembleProjectCommaList(String projectId) {
        String arr[] = projectId.split(",");
        String allProId = "";
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                allProId += ",";
            }
            allProId += "'" + arr[i] + "'";
        }
        return allProId;
    }

    public List<SonarIssue> getAllIssueListByComponentId(String componentId) {
        String componentStr = assembleProjectCommaList(componentId);
        String exeSql = queryAllIssueInfoByComponentId.replace(":componentId", componentStr);

        return getIssueListBySql(exeSql);
    }

    public List<SonarIssue> getAllIssueListByRootId(String componentId) {
        String componentStr = assembleProjectCommaList(componentId);
        String exeSql = queryAllIssueInfoByRootId.replace(":componentId", componentStr);

        return getIssueListBySql(exeSql);
    }

    public List<SonarIssue> getIssueListBySql(String exeSql) {

        OracleConnection rawOracleConn = (OracleConnection) new DbUtil().getCon();
        OracleStatement st = null;

        List<SonarIssue> totalLineInfoList = new ArrayList<SonarIssue>();

        try {
            st = (OracleStatement) rawOracleConn.createStatement();
            ResultSet rs = st.executeQuery(exeSql);
            while (rs.next()) {

                SonarIssue si = new SonarIssue();
                int line = rs.getInt("line");
                String severity = rs.getString("severity");
                si.lineNumber = line;
                si.severity = severity;
                si.componentId = rs.getString("component_Id");
                if (line > 0) {
                    totalLineInfoList.add(si);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (rawOracleConn != null) {
                try {
                    rawOracleConn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return totalLineInfoList;

    }
}
