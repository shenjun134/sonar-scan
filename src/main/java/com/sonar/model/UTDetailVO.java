package com.sonar.model;

public class UTDetailVO {

    private String id;

    private String name;

    private String fileId;

    private String fileKey;

    private String fileName;

    private String status;

    private int durationInMs;

    private int coveredLines;

    private String stacktrace;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDurationInMs() {
        return durationInMs;
    }

    public void setDurationInMs(int durationInMs) {
        this.durationInMs = durationInMs;
    }

    public int getCoveredLines() {
        return coveredLines;
    }

    public void setCoveredLines(int coveredLines) {
        this.coveredLines = coveredLines;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    @Override
    public String toString() {
        return "UTDetailVO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", fileId='" + fileId + '\'' +
                ", fileKey='" + fileKey + '\'' +
                ", fileName='" + fileName + '\'' +
                ", status='" + status + '\'' +
                ", durationInMs=" + durationInMs +
                ", coveredLines=" + coveredLines +
                ", stacktrace='" + stacktrace + '\'' +
                '}';
    }
}
