package com.xiangsoft.sonar.sonarreport.model;

import java.math.BigDecimal;

public class ClassLevelStatisticsInfo extends StatisticsInfo{

	public String className;
	public String classId;
	
	public ClassLevelStatisticsInfo() {

	}
	
	public ClassLevelStatisticsInfo(String classId , BigDecimal sumLineNumber, BigDecimal sumEffecitveCoveredLineNumber, BigDecimal sumCoveredLine, BigDecimal sumCredit) {
		this.classId = classId;
		checkedLineNumber = sumLineNumber.longValue();
		checkedEffectiveCoveredLineCount = sumEffecitveCoveredLineNumber.longValue();
		checkedCoveredLineNumber = sumCoveredLine.longValue();
		checkedCreditSum = sumCredit.longValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classId == null) ? 0 : classId.hashCode());
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		
		ClassLevelStatisticsInfo other = (ClassLevelStatisticsInfo) obj;
		if (classId == null) {
			if (other.classId != null){
				return false;
			}
		} 
		if (!classId.equals(other.classId)){
			return false;
		}
		if (className == null) {
			if (other.className != null){
				return false;
			}
		} 
		if (!className.equals(other.className)){
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ClassId = [" + classId + "], " + " number : [" + checkedLineNumber + "] ,checkedEffectiveCoveredLineCount: [ " + checkedEffectiveCoveredLineCount + "], cover:[" + checkedCoveredLineNumber + "], credit : [" + checkedCreditSum + "], coverateRate : ["  + coverageRate + "] , complianceRate :[" + complianceRate +"]";
	}
}

