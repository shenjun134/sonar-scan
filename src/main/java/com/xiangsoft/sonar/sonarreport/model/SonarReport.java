package com.xiangsoft.sonar.sonarreport.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SonarReport {
	
	public Date startDate;
	public Date endDate;
	public String empId;
	
	public List<SonarLine> sonarLineList;
	public Map<String, Integer> creditMap = new HashMap<String, Integer>();
	
	
	public BigDecimal coverageRate;
	public BigDecimal complianceRate; 
	
	public long checkedLineNumber = 0;
	public long checkedCoveredLineNumber = 0;
	public long checkedCreditSum = 0;
	
	public TreeMap<String, ArrayList<SonarReport>> empReportMap = new TreeMap<String, ArrayList<SonarReport>>(); 
	public EmployeeLevelStatisticsInfo employeeStaticInfo = new EmployeeLevelStatisticsInfo(); 
	
	public void calculate(){
		
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal coveredSum = BigDecimal.ZERO;
		BigDecimal effectiveCoveredSum = BigDecimal.ZERO;
		BigDecimal creditSum = BigDecimal.ZERO;
		
		EmployeeLevelStatisticsInfo empLevelStaticInfo = new EmployeeLevelStatisticsInfo();
		empLevelStaticInfo.employeeId = empId;
		ClassLevelStatisticsInfo classLevelinfo = new ClassLevelStatisticsInfo();
		String lastClassId = null;
		String lastTestClazzName = null;
		
		BigDecimal classLevelSum = BigDecimal.ZERO;
		BigDecimal classLevelCoveredSum = BigDecimal.ZERO;
		BigDecimal classLevelEffectiveCoveredSum = BigDecimal.ZERO;
		BigDecimal classLevelCreditSum = BigDecimal.ZERO;
		
		if(sonarLineList != null && sonarLineList.size() > 0){
			
			for(int i = 0 ; i < sonarLineList.size() ; i++){
				
				SonarLine singleSonarLine = sonarLineList.get(i);
				
				if(singleSonarLine.lastCommitEmpId.equals(empId) && singleSonarLine.lastCommitDate.after(startDate) && singleSonarLine.lastCommitDate.before(endDate)){
					
					// 1.not the first time  and 2. a new class id appear , then pack the old class info to ClassLevelStaticInfo 
					if( classLevelSum.longValue() > 0  && !singleSonarLine.componentId.equalsIgnoreCase(lastClassId)){

						classLevelinfo = buildClassLevelStatisticsInfo(lastClassId, classLevelSum, classLevelEffectiveCoveredSum, classLevelCoveredSum, classLevelCreditSum);
						classLevelinfo.calculate();
						classLevelinfo.className = lastTestClazzName;
						empLevelStaticInfo.classLevelStatistiscInfoList.add(classLevelinfo);
						
						lastClassId = singleSonarLine.componentId;
						
						classLevelSum = BigDecimal.ZERO;
						classLevelCoveredSum = BigDecimal.ZERO;
						classLevelCreditSum = BigDecimal.ZERO;
						classLevelEffectiveCoveredSum = BigDecimal.ZERO;
					}
					
					lastClassId = singleSonarLine.componentId;
					lastTestClazzName = singleSonarLine.testClazzName;
					
					
					if(singleSonarLine.effeciveCoverageLine == 1){
						effectiveCoveredSum = effectiveCoveredSum.add(BigDecimal.ONE);
						classLevelEffectiveCoveredSum = classLevelEffectiveCoveredSum.add(BigDecimal.ONE);
					}
					if(singleSonarLine.covered == 1){
						coveredSum = coveredSum.add(BigDecimal.ONE);
						classLevelCoveredSum = classLevelCoveredSum.add(BigDecimal.ONE);
					}
					sum = sum.add(BigDecimal.ONE);
					//System.out.println("Commit line number [" + (i+1) +"]");
					
					classLevelSum = classLevelSum.add(BigDecimal.ONE);

					if(creditMap.containsKey(singleSonarLine.componentId+"_"+singleSonarLine.lineNumber)){
						int tmpCredit = creditMap.get(singleSonarLine.componentId + "_" + singleSonarLine.lineNumber).intValue();
						creditSum = creditSum.add(BigDecimal.valueOf(tmpCredit));
						classLevelCreditSum = classLevelCreditSum.add(BigDecimal.valueOf(tmpCredit));
					}

				}
				
				//System.out.println(" i number is = ["+ i +"]");
				if( i == sonarLineList.size() - 1 && classLevelSum.longValue() > 0){

					classLevelinfo = buildClassLevelStatisticsInfo(lastClassId, classLevelSum, classLevelEffectiveCoveredSum, classLevelCoveredSum, classLevelCreditSum);
					classLevelinfo.calculate();
					classLevelinfo.className = lastTestClazzName;
					empLevelStaticInfo.classLevelStatistiscInfoList.add(classLevelinfo);
					
					classLevelSum = BigDecimal.ZERO;
					classLevelEffectiveCoveredSum = BigDecimal.ZERO;
					classLevelCoveredSum = BigDecimal.ZERO;
					classLevelCreditSum = BigDecimal.ZERO;
				}
			}
			System.out.println("empId " + empId + " empLevelStaticInfo.classLevelStatistiscInfoList size = [ " + empLevelStaticInfo.classLevelStatistiscInfoList.size()+"]");
			if(sum == BigDecimal.ZERO){
				checkedLineNumber = 0;
				checkedCoveredLineNumber = 0;
				checkedCreditSum = 0;
				coverageRate = BigDecimal.ZERO;
				complianceRate = BigDecimal.ZERO;
			}else{
				checkedLineNumber = sum.longValue();
				checkedCoveredLineNumber = coveredSum.longValue();
				checkedCreditSum = creditSum.longValue();
				coverageRate = (coveredSum.divide(sum, 4, BigDecimal.ROUND_HALF_EVEN)).multiply(BigDecimal.TEN).multiply(BigDecimal.TEN);
				complianceRate =  new BigDecimal(100).subtract((creditSum.divide(sum, 4, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.TEN).multiply(BigDecimal.TEN)));
			}
		}else{
			checkedLineNumber = 0;
			checkedCoveredLineNumber = 0;
			checkedCreditSum = 0;
			coverageRate = BigDecimal.ZERO;
			complianceRate = BigDecimal.ZERO;
		}
		
		this.employeeStaticInfo = empLevelStaticInfo;
	}
	
	private ClassLevelStatisticsInfo buildClassLevelStatisticsInfo(String classId, BigDecimal sumLineNumber, BigDecimal sumEffecitveCoveredLineNumber,BigDecimal sumCoveredLineNumber, BigDecimal sumCredit){
		return new ClassLevelStatisticsInfo(classId, sumLineNumber, sumEffecitveCoveredLineNumber, sumCoveredLineNumber,  sumCredit);
	}

}
