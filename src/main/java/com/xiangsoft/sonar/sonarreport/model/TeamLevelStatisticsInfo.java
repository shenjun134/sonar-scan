package com.xiangsoft.sonar.sonarreport.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TeamLevelStatisticsInfo extends StatisticsInfo{
	
	public String teamId;

	public String teamMembers;

	public List<EmployeeLevelStatisticsInfo> employeeLevelStatisticsInfoList = new ArrayList<EmployeeLevelStatisticsInfo>();
	public Map<String,EmployeeLevelStatisticsInfo> teamMemberMap = new TreeMap<String,EmployeeLevelStatisticsInfo>(); 
	
	public void accumulateTeamStatistics(){
		if(employeeLevelStatisticsInfoList.size() == 0){
			return;
		}
		for(EmployeeLevelStatisticsInfo employeeStatisticsInfo : employeeLevelStatisticsInfoList){
			if(employeeStatisticsInfo == null){
				System.out.println("Hang here , teamId = " + teamId);
				return;
			}
			employeeStatisticsInfo.accumulateKeyStatistics();
		}
		for(EmployeeLevelStatisticsInfo employeeStatisticsInfo : employeeLevelStatisticsInfoList){
			this.checkedLineNumber += employeeStatisticsInfo.checkedLineNumber;
			this.checkedEffectiveCoveredLineCount += employeeStatisticsInfo.checkedEffectiveCoveredLineCount;
			this.checkedCoveredLineNumber += employeeStatisticsInfo.checkedCoveredLineNumber;
			this.checkedCreditSum += employeeStatisticsInfo.checkedCreditSum;
		}
		calculate();
	}
	
	public boolean existEmployee(String empId){
		return teamMemberMap.containsKey(empId);
	}
	
	public EmployeeLevelStatisticsInfo getEmployeeInfo(String empId){
		return teamMemberMap.get(empId);
	}

	@Override
	public String toString() {
		return "TeamLevelStatisticsInfo [teamId=" + teamId
				+ ", checkedLineNumber="
				+ checkedLineNumber
				+ ", checkedEffectiveCoveredLineCount="
				+ checkedEffectiveCoveredLineCount
				+ ", checkedCoveredLineNumber="
				+ checkedCoveredLineNumber
				+ ", checkedCreditSum="
				+ checkedCreditSum
				+  "]";
	}
	
	
}
