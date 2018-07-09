package com.xiangsoft.sonar.sonarreport.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EmployeeLevelStatisticsInfo extends StatisticsInfo{
	
	public String employeeId;
	public List<ClassLevelStatisticsInfo> classLevelStatistiscInfoList = new ArrayList<ClassLevelStatisticsInfo>();
	
	public void accumulateKeyStatistics(){
		if(classLevelStatistiscInfoList.size() == 0){
			this.checkedLineNumber = 0;
			this.checkedEffectiveCoveredLineCount = 0;
			this.checkedCoveredLineNumber = 0;
			this.checkedCreditSum = 0;
			coverageRate = BigDecimal.ZERO;
			complianceRate = BigDecimal.ZERO;
			return;
		}
		for(ClassLevelStatisticsInfo classInfo : classLevelStatistiscInfoList){
			if(classInfo == null){
				System.out.println("ClassInfo hang here");
			}
			
			this.checkedLineNumber += classInfo.checkedLineNumber;
			this.checkedEffectiveCoveredLineCount += classInfo.checkedEffectiveCoveredLineCount;
			this.checkedCoveredLineNumber += classInfo.checkedCoveredLineNumber;
			this.checkedCreditSum += classInfo.checkedCreditSum;
		}
		calculate();
	}

	@Override
	public String toString() {
		return "EmployeeLevelStatisticsInfo , employeeId= [" + employeeId + "],[ coverage ["+ this.coverageRate + "], compliance [" + this.complianceRate + "],"
				+ ", checkedLineNumber=" + checkedLineNumber
				+ ", checkedEffectiveCoveredLineCount=" + checkedEffectiveCoveredLineCount
				+ ", checkedCoveredLineNumber=" + checkedCoveredLineNumber
				+ ", checkedCreditSum=" + checkedCreditSum + "]";
		
	}
	

	
}
