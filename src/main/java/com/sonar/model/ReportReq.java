package com.sonar.model;

import java.util.Date;
import java.util.List;

public class ReportReq {

    private String projectNames;

    private Date beginDt;

    private Date endDt;

    private String version;

    private List<String> pickedTeam;

    private int threadCount;

    private boolean skipTest;
    //BLOCKER,CRITICAL
    private String severityList;


    public ReportReq(String projectNames, Date beginDt, Date endDt) {
        this.projectNames = projectNames;
        this.beginDt = beginDt;
        this.endDt = endDt;
    }

    public ReportReq(Date beginDt, Date endDt) {
        this.beginDt = beginDt;
        this.endDt = endDt;
    }

    public String getProjectNames() {
        return projectNames;
    }

    public void setProjectNames(String projectNames) {
        this.projectNames = projectNames;
    }

    public Date getBeginDt() {
        return beginDt;
    }

    public void setBeginDt(Date beginDt) {
        this.beginDt = beginDt;
    }

    public Date getEndDt() {
        return endDt;
    }

    public void setEndDt(Date endDt) {
        this.endDt = endDt;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getPickedTeam() {
        return pickedTeam;
    }

    public void setPickedTeam(List<String> pickedTeam) {
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

    public String getSeverityList() {
        return severityList;
    }

    public void setSeverityList(String severityList) {
        this.severityList = severityList;
    }

    @Override
    public String toString() {
        return "ReportReq{" +
                "projectNames='" + projectNames + '\'' +
                ", beginDt=" + beginDt +
                ", endDt=" + endDt +
                ", version='" + version + '\'' +
                ", pickedTeam=" + pickedTeam +
                ", threadCount=" + threadCount +
                ", skipTest=" + skipTest +
                ", severityList='" + severityList + '\'' +
                '}';
    }
}


