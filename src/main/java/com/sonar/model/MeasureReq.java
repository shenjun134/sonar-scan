package com.sonar.model;

public class MeasureReq {

    private String projectName;

    private String templateName;

    private String outputPath;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public String toString() {
        return "MeasureReq{" +
                "projectName='" + projectName + '\'' +
                ", templateName='" + templateName + '\'' +
                ", outputPath='" + outputPath + '\'' +
                '}';
    }
}
