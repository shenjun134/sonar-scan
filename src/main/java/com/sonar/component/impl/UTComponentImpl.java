package com.sonar.component.impl;

import com.sonar.component.UTComponent;
import com.sonar.constant.WebApi;
import com.sonar.convert.UTConverter;
import com.sonar.convert.UTResultConverter;
import com.sonar.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sonar.wsclient.Host;
import org.sonar.wsclient.JdkUtils;
import org.sonar.wsclient.connectors.HttpClient4Connector;

import java.util.ArrayList;
import java.util.List;

public class UTComponentImpl extends AbstractComponent implements UTComponent {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(UTComponentImpl.class);

    private interface Constant {
        int pageSize = 500;
        int shortPageSize = 50;
    }

    public UTComponentImpl() {
    }

    public UTComponentImpl(String webUser, String webHost, String webPassword) {
        this.webUser = webUser;
        this.webHost = webHost;
        this.webPassword = webPassword;
        util = new JdkUtils();
    }

    @Override
    public ProjectUTDO queryUTList(ProjectDO projectDO) {
        try {
            this.check(projectDO);
            return getUTList(projectDO);
        } catch (Exception e) {
            logger.error("queryUTList error - " + projectDO, e);
            throw new RuntimeException("queryUTList error", e);
        }
    }

    @Override
    public ProjectUTDO queryUTFail(ProjectDO projectDO) {
        try {
            this.check(projectDO);
            return getFailList(projectDO);
        } catch (Exception e) {
            logger.error("queryUTFail error - " + projectDO, e);
            throw new RuntimeException("queryUTFail error", e);
        }
    }


    /**
     * @param projectDO
     * @return
     */
    private ProjectUTDO getUTList(ProjectDO projectDO) {
        HttpClient4Connector connector = new HttpClient4Connector(new Host(webHost, webUser, webPassword));
        try {
            int i = 1;

            String baseUrl = this.webHost + StringUtils.replace(WebApi.UT_PAGE_SEARCH, "#pageSize#", "" + Constant.pageSize);
            baseUrl = StringUtils.replace(baseUrl, "#baseComponentKey#", projectDO.getKee());

            String url = StringUtils.replace(baseUrl, "#pageIndex#", "" + i);
            String jsonResp = this.httpGet(url, connector);

            logger.info("UT getUTList response: " + jsonResp);

            ProjectUTDO projectUTDO = UTConverter.firstConvert(jsonResp);
            projectUTDO.setId(projectDO.getId());
            projectUTDO.setKee(projectDO.getKee());
            projectUTDO.setName(projectDO.getName());

            if (projectUTDO.getPageDO() == null) {
                logger.warn("no page obj here for " + projectDO);
                return projectUTDO;
            }
            int rest = projectUTDO.getPageDO().getTotal() % Constant.pageSize;
            int pageCount = projectUTDO.getPageDO().getTotal() / Constant.pageSize;
            if (rest > 0) {
                pageCount = pageCount + 1;
            }

            for (i = 2; i <= pageCount; i++) {
                String tempUrl = StringUtils.replace(baseUrl, "#pageIndex#", "" + i);
                String tempJsonResp = this.httpGet(tempUrl, connector);
                logger.info("UT getUTList tempJsonResp: " + tempJsonResp);
                UTConverter.convert(projectUTDO, tempJsonResp);
            }
            return projectUTDO;
        } finally {
            connector.close();
        }


    }


    /**
     * @param projectDO
     * @return
     */
    private ProjectUTDO getFailList(ProjectDO projectDO) {

        HttpClient4Connector connector = new HttpClient4Connector(new Host(webHost, webUser, webPassword));
        try {
            int i = 1;

            String baseUrl = this.webHost + StringUtils.replace(WebApi.UT_SUCC_RATE_BY_KEE, "#pageSize#", "" + Constant.shortPageSize);
            baseUrl = StringUtils.replace(baseUrl, "#baseComponentKey#", projectDO.getKee());

            String url = StringUtils.replace(baseUrl, "#pageIndex#", "" + i);
            String jsonResp = this.httpGet(url, connector);

            logger.info("UT getUTList response: " + jsonResp);

            ProjectUTDO projectUTDO = UTConverter.firstConvert(jsonResp);
            projectUTDO.setId(projectDO.getId());
            projectUTDO.setKee(projectDO.getKee());
            projectUTDO.setName(projectDO.getName());

            fillInFailUT(projectUTDO);

            return projectUTDO;
        } finally {
            connector.close();
        }
    }


    private void fillInFailUT(ProjectUTDO projectUTDO) {
        List<UTDO> failList = new ArrayList<>();
        for (UTDO utdo : projectUTDO.getComponentMap().values()) {
            if (utdo.getError() > 0 || utdo.getFailure() > 0) {
                failList.add(utdo);
                continue;
            }
        }
        projectUTDO.setFailList(getResult(failList));
    }


    private List<UTResult> getResult(List<UTDO> failList) {
        List<UTResult> list = new ArrayList<>();
        HttpClient4Connector connector = null;
        try {
            if (CollectionUtils.isEmpty(failList)) {
                return list;
            }
            connector = new HttpClient4Connector(new Host(webHost, webUser, webPassword));
            for (UTDO utdo : failList) {
                String baseUrl = this.webHost + StringUtils.replace(WebApi.UT_TRACE_BY_KEE, "#testFileKey#", utdo.getKee());
                String jsonResp = this.httpGet(baseUrl, connector);

                logger.info("UT getUTList response: " + jsonResp);

                UTResult utResult = UTResultConverter.convert2Result(jsonResp);
                if (utResult != null) {
                    list.add(utResult);
                }
            }


        } finally {
            if (connector != null) {
                connector.close();
            }
        }
        return list;
    }


    public static void main(String[] args) {
        test_getFailList();

    }


    private static void test_getUTList() {
        String webUser = "admin";

        String webHost = "http://jabdl3504.it:9113";

        String webPassword = "admin";
        UTComponentImpl utComponent = new UTComponentImpl(webUser, webHost, webPassword);
        String name = "gcth-project-trunk-parent";
        Long id = 40344L;
        String kee = "com.xxxxxxxxx.gcth:gcth-project-trunk-parent";
        ProjectDO projectDO = new ProjectDO(name, kee, id);
        ProjectUTDO projectUTDO = utComponent.getUTList(projectDO);

        System.out.println(projectUTDO.getBaseComponent());

        System.out.println("--------------------------");

        System.out.println(projectUTDO.getComponentMap().size());

        System.out.println(projectUTDO.getComponentMap());
    }

    private static void test_getFailList() {
        String webUser = "admin";

        String webHost = "http://jabdl3504.it:9113";

        String webPassword = "admin";
        UTComponentImpl utComponent = new UTComponentImpl(webUser, webHost, webPassword);
        String name = "gcth-project-trunk-parent";
        Long id = 40344L;
        String kee = "com.xxxxxxxxx.gcth:gcth-project-trunk-parent";
        ProjectDO projectDO = new ProjectDO(name, kee, id);
        ProjectUTDO projectUTDO = utComponent.getFailList(projectDO);

        System.out.println(projectUTDO.getFailList());

        System.out.println("--------------------------");

        System.out.println(projectUTDO.getComponentMap().size());

        System.out.println(projectUTDO.getComponentMap());
    }

}
