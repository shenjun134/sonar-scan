package com.sonar.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ClazzResult {

    /**
     * class name
     */
    private String clazz;

    private String componentId;

    private String componentKey;

    private List<LineResult> lineResultList = new ArrayList<>();

    private List<IssueResult> issueResultList = new ArrayList<>();

    private int totalLine;

    private int totalChangeLine;

    private int totalCoveredLine;


    private TreeMap<String, AuthorResult> authorMap = new TreeMap<>();


    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getComponentKey() {
        return componentKey;
    }

    public void setComponentKey(String componentKey) {
        this.componentKey = componentKey;
    }

    public List<LineResult> getLineResultList() {
        return lineResultList;
    }

    public void setLineResultList(List<LineResult> lineResultList) {
        this.lineResultList = lineResultList;
    }

    public List<IssueResult> getIssueResultList() {
        return issueResultList;
    }

    public void setIssueResultList(List<IssueResult> issueResultList) {
        this.issueResultList = issueResultList;
    }

    public TreeMap<String, AuthorResult> getAuthorMap() {
        return authorMap;
    }

    public void setAuthorMap(TreeMap<String, AuthorResult> authorMap) {
        this.authorMap = authorMap;
    }

    public int getTotalLine() {
        return totalLine;
    }

    public void setTotalLine(int totalLine) {
        this.totalLine = totalLine;
    }

    public int getTotalChangeLine() {
        return totalChangeLine;
    }

    public void setTotalChangeLine(int totalChangeLine) {
        this.totalChangeLine = totalChangeLine;
    }

    public int getTotalCoveredLine() {
        return totalCoveredLine;
    }

    public void setTotalCoveredLine(int totalCoveredLine) {
        this.totalCoveredLine = totalCoveredLine;
    }

    @Override
    public String toString() {
        return "ClazzResult{" +
                "clazz='" + clazz + '\'' +
                ", componentId='" + componentId + '\'' +
                ", componentKey='" + componentKey + '\'' +
                ", lineResultList=" + lineResultList +
                ", issueResultList=" + issueResultList +
                ", totalLine=" + totalLine +
                ", totalChangeLine=" + totalChangeLine +
                ", totalCoveredLine=" + totalCoveredLine +
                ", authorMap=" + authorMap +
                '}';
    }
}
