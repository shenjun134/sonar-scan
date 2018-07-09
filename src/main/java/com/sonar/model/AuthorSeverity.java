package com.sonar.model;

import com.sonar.constant.SeverityEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AuthorSeverity {
    private String author;

    /**
     *
     */
    private Map<SeverityEnum, List<SeverityInfo>> severityMap = new HashMap<>();

    private TreeMap<SeverityEnum, Integer> severityStatistic = new TreeMap<>();

    private int compliance = 0;

    /**
     * result will fulfill in severityStatistic
     */
    public void analysis() {
        severityStatistic.clear();
        compliance = 0;
        for (Map.Entry<SeverityEnum, List<SeverityInfo>> entry : severityMap.entrySet()) {
            SeverityEnum severityEnum = entry.getKey();
            List<SeverityInfo> list = entry.getValue();
            Integer sum = 0;
            int factor = 1;
            for (SeverityInfo info : list) {
                sum = sum + info.getCount();
                if (SeverityEnum.BLOCKER.getCode().equalsIgnoreCase(info.getSeverity().getCode())) {
                    factor = 100;
                }
                compliance = compliance + factor * severityEnum.getPoint() * info.getCount();
            }
            severityStatistic.put(severityEnum, sum);

        }
    }

    public AuthorSeverity(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Map<SeverityEnum, List<SeverityInfo>> getSeverityMap() {
        return severityMap;
    }

    public void setSeverityMap(Map<SeverityEnum, List<SeverityInfo>> severityMap) {
        this.severityMap = severityMap;
    }

    public TreeMap<SeverityEnum, Integer> getSeverityStatistic() {
        return severityStatistic;
    }

    public void setSeverityStatistic(TreeMap<SeverityEnum, Integer> severityStatistic) {
        this.severityStatistic = severityStatistic;
    }

    public int getCompliance() {
        return compliance;
    }

    public AuthorSeverity setCompliance(int compliance) {
        this.compliance = compliance;
        return this;
    }

    @Override
    public String toString() {
        return "AuthorSeverity{" +
                "author='" + author + '\'' +
                ", severityMap=" + severityMap +
                ", severityStatistic=" + severityStatistic +
                ", compliance=" + compliance +
                '}';
    }
}
