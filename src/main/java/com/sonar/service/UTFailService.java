package com.sonar.service;

import com.sonar.component.UTComponent;
import com.sonar.component.impl.UTComponentImpl;
import com.sonar.dao.SonarDao;
import com.sonar.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Map;

public class UTFailService {
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(ReportService.class);


    private UTComponent utComponent;

    private SonarDao sonarDao;

    private SonarProperties sonarProperties;

    private boolean pass = false;


    /**
     * @param failReq
     */
    public void process(UTFailReq failReq) {
        init();
        before(failReq);
        doProcess(failReq);
        after(failReq);
    }


    protected void before(UTFailReq failReq) {
        if (StringUtils.isBlank(failReq.getProjectName())) {
            throw new IllegalArgumentException("no project found");
        }
        utComponent = new UTComponentImpl(sonarProperties.getWebUser(), sonarProperties.getWebHost(), sonarProperties.getWebPassword());
    }


    /**
     * generate report
     *
     * @param failReq
     */
    protected void after(UTFailReq failReq) {
    }


    /**
     * @param failReq
     */
    protected void doProcess(UTFailReq failReq) {
        Map<String, ProjectDO> allProject = sonarDao.getAllProject();
        if (allProject == null || allProject.size() == 0) {
            return;
        }
        ProjectDO project = allProject.get(failReq.getProjectName());
        if (project == null) {
            System.out.println("###################################################################################");
            System.out.println("########################### No project found in sonar Database!");
            System.out.println("###################################################################################");
            throw new IllegalArgumentException("No project found in sonar Database!");
        }
        ProjectUTDO projectUTDO = utComponent.queryUTFail(project);
        if (projectUTDO == null) {
            System.out.println("###################################################################################");
            System.out.println("########################### No Unit test list found in sonar server!");
            System.out.println("###################################################################################");
            throw new IllegalArgumentException("No Unit test list found in sonar server!");
        }
        if (CollectionUtils.isEmpty(projectUTDO.getFailList())) {
            System.out.println("###################################################################################");
            System.out.println("########################### Congratulations, there is no failed unit test!");
            System.out.println("###################################################################################");
            pass = true;
            return;
        }

        for (UTResult utResult : projectUTDO.getFailList()) {
            utResult.printNoOK();
        }
        throw new IllegalArgumentException("Oops, unit test failed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }


    protected void init() {
        sonarProperties = new SonarProperties();
        sonarDao = new SonarDao(sonarProperties);
    }

    /***********************************************************private************************************************/

}
