package com.xiangsoft.sonar.sonarreport.model;

import java.math.BigDecimal;

public class StatisticsInfo {
	
	public BigDecimal coverageRate = BigDecimal.ZERO;
	public BigDecimal complianceRate = BigDecimal.ZERO; 
	
	public long checkedLineNumber = 0;
	public long checkedCoveredLineNumber = 0;
	public long checkedEffectiveCoveredLineCount = 0;
	public long checkedCreditSum = 0;
	
	public void calculate(){
		if(checkedLineNumber == 0){
			coverageRate = BigDecimal.ZERO;
			complianceRate = BigDecimal.ZERO;
			return;
		}
		if(checkedEffectiveCoveredLineCount == 0){
			coverageRate = BigDecimal.ZERO;
		}else{
			coverageRate = (BigDecimal.valueOf(checkedCoveredLineNumber).divide(BigDecimal.valueOf(checkedEffectiveCoveredLineCount), 4, BigDecimal.ROUND_HALF_EVEN)).multiply(BigDecimal.valueOf(100)).setScale(1, BigDecimal.ROUND_HALF_EVEN);
		}
		BigDecimal totalCredit = (BigDecimal.valueOf(checkedCreditSum).divide(BigDecimal.valueOf(checkedLineNumber), 4, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100))).setScale(1, BigDecimal.ROUND_HALF_EVEN);
		complianceRate = BigDecimal.valueOf(100).subtract(totalCredit).compareTo(BigDecimal.ZERO) == 1 ? BigDecimal.valueOf(100).subtract(totalCredit) : BigDecimal.ZERO;
		complianceRate = complianceRate.setScale(1, BigDecimal.ROUND_HALF_EVEN);
	} 
	
}
