package com.sonar;

import com.sonar.constant.SeverityEnum;
import com.sonar.service.SeverityService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SeverityBase {
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(SeverityBase.class);

    public static void main(String[] args) {
        System.out.println("begin with: " + Arrays.toString(args));
        Long beginAt = System.currentTimeMillis();
        try {
//            args = new String[]{"gcth-project-trunk-parent"};
            process(args);
        } catch (Exception e) {
            logger.error("severity report process error", e);
        } finally {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ total used:" + (System.currentTimeMillis() - beginAt));
        }
    }

    public static void process(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("no enough args");
            System.exit(1);
        }
        String projectNames = args[0];

        if (StringUtils.isBlank(projectNames)) {
            System.out.println("projectNames is blank");
            System.exit(0);
            return;
        }
        List<String> projects = Arrays.asList(projectNames);
        List<SeverityEnum> severityEnums = new ArrayList<SeverityEnum>() {
            {
                add(SeverityEnum.BLOCKER);
                add(SeverityEnum.CRITICAL);
            }
        };
        SeverityService severityService = new SeverityService(projects, severityEnums);
        severityService.process();
    }
}
