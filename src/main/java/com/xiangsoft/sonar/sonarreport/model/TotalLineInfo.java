package com.xiangsoft.sonar.sonarreport.model;

public class TotalLineInfo {
	public String coverageLineHitsData = "";
	public String authorsByLine = "";
	public String lastCommitDatetimeByLine = "";
	public String testClassName = "";
	public String componentId = ""; //projectId
	
	public TotalLineInfo makeNewCopy(){
		TotalLineInfo copy = new TotalLineInfo();
		copy.coverageLineHitsData = this.coverageLineHitsData;
		copy.authorsByLine = this.authorsByLine;
		copy.lastCommitDatetimeByLine = this.lastCommitDatetimeByLine;
		copy.testClassName = this.testClassName;
		copy.componentId = this.componentId;
		return copy;
	}
	
	@Override
	public String toString() {
		return "TotalLineInfo [coverageLineHitsData=" + coverageLineHitsData
				+ ", authorsByLine=" + authorsByLine
				+ ", lastCommitDatetimeByLine=" + lastCommitDatetimeByLine
				+ ", testClassName=" + testClassName + ", componentId="
				+ componentId + "]";
	}
	
	
	
}
