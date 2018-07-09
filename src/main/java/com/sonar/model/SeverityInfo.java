package com.sonar.model;

import com.sonar.constant.SeverityEnum;

public class SeverityInfo {
    private SeverityEnum severity;

    private int count;

    private IssueResult issueResult;

    private ClazzDO clazz;

    public SeverityInfo(SeverityEnum severity, IssueResult issueResult, ClazzDO clazz) {
        this.severity = severity;
        this.issueResult = issueResult;
        this.clazz = clazz;
    }

    public SeverityEnum getSeverity() {
        return severity;
    }

    public void setSeverity(SeverityEnum severity) {
        this.severity = severity;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public IssueResult getIssueResult() {
        return issueResult;
    }

    public void setIssueResult(IssueResult issueResult) {
        this.issueResult = issueResult;
    }

    public ClazzDO getClazz() {
        return clazz;
    }

    public void setClazz(ClazzDO clazz) {
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return "SeverityInfo{" +
                "severity=" + severity +
                ", count=" + count +
                ", issueResult=" + issueResult +
                ", clazz=" + clazz +
                '}';
    }
}
