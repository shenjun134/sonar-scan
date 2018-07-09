package com.sonar.model;

import com.sonar.constant.SeverityEnum;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeverityResult {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(SeverityResult.class);

    private List<String> projects = new ArrayList<>();

    private Map<String, AuthorSeverity> authorSeverityMap = new HashMap<>();

    private Map<SeverityEnum, Integer> severityStatistic = new HashMap<>();

    public Map<String, AuthorSeverity> getAuthorSeverityMap() {
        return authorSeverityMap;
    }

    public void setAuthorSeverityMap(Map<String, AuthorSeverity> authorSeverityMap) {
        this.authorSeverityMap = authorSeverityMap;
    }

    public List<String> getProjects() {
        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public Map<SeverityEnum, Integer> getSeverityStatistic() {
        return severityStatistic;
    }

    public void setSeverityStatistic(Map<SeverityEnum, Integer> severityStatistic) {
        this.severityStatistic = severityStatistic;
    }

    /**
     * result will fulfill in severityStatistic
     */
    public void analysis() {
        severityStatistic.clear();
        for (AuthorSeverity authorSeverity : authorSeverityMap.values()) {
            authorSeverity.analysis();
            for (Map.Entry<SeverityEnum, Integer> map : authorSeverity.getSeverityStatistic().entrySet()) {
                SeverityEnum severityEnum = map.getKey();
                Integer temp = map.getValue();
                Integer sum = severityStatistic.get(severityEnum);
                if (sum == null) {
                    sum = 0;
                }
                sum = sum + temp;
                severityStatistic.put(severityEnum, sum);
            }
        }
    }

    public void print() {
        logger.info("authorSeverityMap" + authorSeverityMap);
        logger.info("severityStatistic" + severityStatistic);
    }

    @Override
    public String toString() {
        return "SeverityResult{" +
                "projects=" + projects +
                ", authorSeverityMap=" + authorSeverityMap +
                ", severityStatistic=" + severityStatistic +
                '}';
    }
}
