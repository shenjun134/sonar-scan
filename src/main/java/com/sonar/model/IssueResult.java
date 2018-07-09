package com.sonar.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IssueResult {

    private String rule;

    private String severity;

    private String status;

    private String message;

    private String author;

    private String effort;

    private String debt;

    private String type;

    private Date creationDate;

    private Date updateDate;

    private int line;

    private IssueTextRange textRange;

    private List<IssueFlow> flows = new ArrayList<>();

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEffort() {
        return effort;
    }

    public void setEffort(String effort) {
        this.effort = effort;
    }

    public String getDebt() {
        return debt;
    }

    public void setDebt(String debt) {
        this.debt = debt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public IssueTextRange getTextRange() {
        return textRange;
    }

    public void setTextRange(IssueTextRange textRange) {
        this.textRange = textRange;
    }

    public List<IssueFlow> getFlows() {
        return flows;
    }

    public void setFlows(List<IssueFlow> flows) {
        this.flows = flows;
    }

    @Override
    public String toString() {
        return "IssueResult{" +
                "rule='" + rule + '\'' +
                ", severity='" + severity + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", author='" + author + '\'' +
                ", effort='" + effort + '\'' +
                ", debt='" + debt + '\'' +
                ", type='" + type + '\'' +
                ", creationDate=" + creationDate +
                ", updateDate=" + updateDate +
                ", line=" + line +
                ", textRange=" + textRange +
                ", flows=" + flows +
                '}';
    }
}
