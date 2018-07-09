package com.xiangsoft.sonar.sonarreport.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Report extends StatisticsInfo{
	
	public Date startTime = new Date();
	public Date endTime = new Date();
	public String moduleName = "";
	public String moduleId = "";
	public int totalFiles;
	public boolean skipTest;
	
	public List<TeamLevelStatisticsInfo> teamLevelStatisticsInfoList = new ArrayList<TeamLevelStatisticsInfo>();
	
	public void calculateAll(){
		if(teamLevelStatisticsInfoList.size() == 0){
			System.out.println("The Report Object 's team level list info is empty , end system run!!!");
			return;
		}
		for(TeamLevelStatisticsInfo teamStatisticsInfo : teamLevelStatisticsInfoList){
			if(teamStatisticsInfo==null){
				System.out.println("Hang here , moduleId = " + moduleId);
				return;
			}
			teamStatisticsInfo.accumulateTeamStatistics();
		}
		for(TeamLevelStatisticsInfo accumulateTeamStatistics : teamLevelStatisticsInfoList){
			this.checkedLineNumber += accumulateTeamStatistics.checkedLineNumber;
			this.checkedEffectiveCoveredLineCount += accumulateTeamStatistics.checkedEffectiveCoveredLineCount;
			this.checkedCoveredLineNumber += accumulateTeamStatistics.checkedCoveredLineNumber;
			this.checkedCreditSum += accumulateTeamStatistics.checkedCreditSum;
		}
		calculate();
	}
}
