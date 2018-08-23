package com.sonar.convert;

import com.sonar.model.ClazzResult;
import com.sonar.model.LineResult;
import com.sonar.model.TeamProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sonar.wsclient.JdkUtils;

import java.util.Date;

public class LineResultConverter {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(LineResultConverter.class);

    private static JdkUtils util = new JdkUtils();

    public static ClazzResult convertLine2Clz(String json) {
        //TODO
        return null;
    }


    /**
     * @param ar
     * @param teamProperties
     * @return
     */
    public static LineResult conver2Line(Object ar, TeamProperties teamProperties) {
        String scmAuthor = util.getString(ar, "scmAuthor");
        if (StringUtils.isBlank(scmAuthor)) {
            logger.warn("parseLineArray no scmAuthor");
            return null;
        }
        Integer line = util.getInteger(ar, "line");
        if (line == null) {
            logger.warn("parseLineArray no line");
            return null;
        }
        scmAuthor = convertAuthor(scmAuthor, teamProperties);

        Date scmDate = util.getDateTime(ar, "scmDate");
        String scmRevision = util.getString(ar, "scmRevision");
        boolean duplicated = util.getBoolean(ar, "duplicated");
        String code = util.getString(ar, "code");
        Integer utLineHits = util.getInteger(ar, "utLineHits");
        Integer utConditions = util.getInteger(ar, "utConditions");
        Integer utCoveredConditions = util.getInteger(ar, "utCoveredConditions");

        LineResult lineResult = new LineResult();
        lineResult.setCode(code);
        lineResult.setDuplicated(duplicated);
        lineResult.setLine(line);
        lineResult.setScmAuthor(scmAuthor);
        lineResult.setScmDate(scmDate);
        lineResult.setScmRevision(scmRevision);
        lineResult.setUtConditions(utConditions);
        lineResult.setUtCoveredConditions(utCoveredConditions);
        lineResult.setUtLineHits(utLineHits);

        return lineResult;
    }

    /**
     * @param author
     * @param teamProperties
     * @return
     */
    private static String convertAuthor(String author, TeamProperties teamProperties) {
        String tempAuthor = StringUtils.lowerCase(author);
        String employeeId = teamProperties.getMemberProperties().getTagMap().get(tempAuthor);
        if (StringUtils.isBlank(employeeId)) {
            return tempAuthor;
        }
        return employeeId;
    }
}
