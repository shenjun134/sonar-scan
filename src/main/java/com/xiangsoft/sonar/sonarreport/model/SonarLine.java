package com.xiangsoft.sonar.sonarreport.model;

import java.util.Date;

public class SonarLine {

	public Date lastCommitDate;
	public String lastCommitEmpId;
	public int covered;
	public String snapshot;
	public String testClazzName;
	public int lineNumber;
	public String componentId;//projectId
	public int effeciveCoverageLine;
}
