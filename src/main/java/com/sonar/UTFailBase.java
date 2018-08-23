package com.sonar;

import com.sonar.model.MeasureReq;
import com.sonar.model.UTFailReq;
import com.sonar.service.MeasurementService;
import com.sonar.service.UTFailService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Arrays;

public class UTFailBase {
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(UTFailBase.class);

    public static void main(String[] args) {
        System.out.println("begin with: " + Arrays.toString(args));
        Long beginAt = System.currentTimeMillis();
        try {
//            args = new String[]{"gcth-project-trunk-parent"};
            process(args);
        } catch (Exception e) {
            throw e;
        } finally {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ total used:" + (System.currentTimeMillis() - beginAt));
        }
    }

    public static void process(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("no enough args");
            System.exit(1);
        }
        String projectName = args[0];

        if (StringUtils.isBlank(projectName)) {
            System.out.println("projectName is blank");
            System.exit(0);
            return;
        }
        if (args.length > 2) {
            MeasureReq req = new MeasureReq();
            req.setProjectName(args[0]);
            req.setTemplateName(args[1]);
            req.setOutputPath(args[2]);
            MeasurementService service = new MeasurementService();
            service.process(req);
        }

        UTFailReq utFailReq = new UTFailReq();
        utFailReq.setProjectName(projectName);

        UTFailService utFailService = new UTFailService();

        utFailService.process(utFailReq);

    }
}
