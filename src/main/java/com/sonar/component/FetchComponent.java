package com.sonar.component;

import com.sonar.model.ClazzDO;
import com.sonar.model.ClazzResult;
import com.sonar.model.IssueResult;
import com.sonar.model.LineResult;
import org.sonar.wsclient.connectors.HttpClient4Connector;

import java.util.Date;
import java.util.List;

public interface FetchComponent {


    /**
     * calculate
     * 1. total line count of class
     * 2. total change line count of class
     * 3. each author commit line count, coverage, compliance, etc...
     *
     * @param clazz
     * @param connector
     * @return
     */
    ClazzResult fetchClazzLevel(ClazzDO clazz, HttpClient4Connector connector);


    /**
     * @param clazz
     * @param connector
     * @return
     */
    List<LineResult> fetchClazzLineResult(ClazzDO clazz, HttpClient4Connector connector);


    /**
     * @param clazz
     * @param connector
     * @return
     */
    List<IssueResult> fetchClazzIssueResult(ClazzDO clazz, HttpClient4Connector connector);
}
