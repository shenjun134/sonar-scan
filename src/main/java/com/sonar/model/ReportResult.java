package com.sonar.model;

import java.util.*;

public class ReportResult {


    private Map<String, String> projectMap = new HashMap<>();

    private int totalClz;

    private int totalChangeClz;

    private int totalChangeAuthor;

    private int totalLine;

    private int totalCoveredLine;

    private int totalChangeLine;

    private Date beginDate;

    private Date endDate;

    private ScanResult scanResult = new ScanResult();

    private String version;

    private List<TeamResult> pickedTeam = new ArrayList<>();

    private int threadCount;

    private boolean skipTest;

    private String severityList;


    public Map<String, String> getProjectMap() {
        return projectMap;
    }

    public void setProjectMap(Map<String, String> projectMap) {
        this.projectMap = projectMap;
    }

    public int getTotalClz() {
        return totalClz;
    }

    public void setTotalClz(int totalClz) {
        this.totalClz = totalClz;
    }

    public void addTotalClz(int totalClz) {
        this.totalClz = this.totalClz + totalClz;
    }

    public void addTotalLine(int totalLine) {
        this.totalLine = this.totalLine + totalLine;
    }

    public void addTotalCoveredLine(int totalCoveredLine){
        this.totalCoveredLine = this.totalCoveredLine + totalCoveredLine;
    }

    public int getTotalChangeClz() {
        return totalChangeClz;
    }

    public void setTotalChangeClz(int totalChangeClz) {
        this.totalChangeClz = totalChangeClz;
    }

    public void addTotalChangeClz(int totalChangeClz) {
        this.totalChangeClz = this.totalChangeClz + totalChangeClz;
    }

    public int getTotalChangeAuthor() {
        return totalChangeAuthor;
    }

    public void setTotalChangeAuthor(int totalChangeAuthor) {
        this.totalChangeAuthor = totalChangeAuthor;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<TeamResult> getPickedTeam() {
        return pickedTeam;
    }

    public void setPickedTeam(List<TeamResult> pickedTeam) {
        this.pickedTeam = pickedTeam;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public boolean isSkipTest() {
        return skipTest;
    }

    public void setSkipTest(boolean skipTest) {
        this.skipTest = skipTest;
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

    public void addTotalChangeLine(int totalChangeLine) {
        this.totalChangeLine = this.totalChangeLine + totalChangeLine;
    }

    public String getSeverityList() {
        return severityList;
    }

    public void setSeverityList(String severityList) {
        this.severityList = severityList;
    }


    public int getTotalCoveredLine() {
        return totalCoveredLine;
    }

    public void setTotalCoveredLine(int totalCoveredLine) {
        this.totalCoveredLine = totalCoveredLine;
    }

    @Override
    public String toString() {
        return "ReportResult{" +
                "projectMap=" + projectMap +
                ", totalClz=" + totalClz +
                ", totalChangeClz=" + totalChangeClz +
                ", totalChangeAuthor=" + totalChangeAuthor +
                ", totalLine=" + totalLine +
                ", totalCoveredLine=" + totalCoveredLine +
                ", totalChangeLine=" + totalChangeLine +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", scanResult=" + scanResult +
                ", version='" + version + '\'' +
                ", pickedTeam=" + pickedTeam +
                ", threadCount=" + threadCount +
                ", skipTest=" + skipTest +
                ", severityList='" + severityList + '\'' +
                '}';
    }
}
