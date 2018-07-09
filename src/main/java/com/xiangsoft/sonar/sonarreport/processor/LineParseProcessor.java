package com.xiangsoft.sonar.sonarreport.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.statestr.gcth.core.util.StopTimer;
import com.xiangsoft.sonar.sonarreport.dao.SonarDaoImpl;
import com.xiangsoft.sonar.sonarreport.model.EmployeeLevelStatisticsInfo;
import com.xiangsoft.sonar.sonarreport.model.Report;
import com.xiangsoft.sonar.sonarreport.model.SonarIssue;
import com.xiangsoft.sonar.sonarreport.model.SonarLine;
import com.xiangsoft.sonar.sonarreport.model.SonarReport;
import com.xiangsoft.sonar.sonarreport.model.TeamLevelStatisticsInfo;
import com.xiangsoft.sonar.sonarreport.model.TotalLineInfo;
import com.xiangsoft.universal.AppUniversal;
import com.xiangsoft.util.DateUtil;

public class LineParseProcessor {
	
	public SonarDaoImpl dao = new SonarDaoImpl(); 
	
	public Report processByProjectRootId(String projectId , String allTeamEmpIdArr[], Date startDate, Date endDate){
		
		if(allTeamEmpIdArr == null || allTeamEmpIdArr.length == 0 ){
			System.out.println("Please check the property that contain team_ property!!!");
			System.exit(1);
		}
		
		String projectName = dao.getProjectNameByProjectId(projectId);
		
		System.out.println("projectId : " + projectId);
		System.out.println("empId : " + allTeamEmpIdArr);
		System.out.println("startDate : " + startDate);
		System.out.println("endDate : " + endDate);
		
		Report uiReportData = new Report();
		uiReportData.moduleName = projectName;
		uiReportData.moduleId = projectId;
		uiReportData.startTime = startDate;
		uiReportData.endTime = endDate;
		
		List<String> allEmpIdList = new ArrayList<String>();
		Map<String,String> allEmpMap = new HashMap<String,String>();
		
		for(int i = 0 ; i < allTeamEmpIdArr.length ; i++){
			String teamEmpIdArr = allTeamEmpIdArr[i];
			TeamLevelStatisticsInfo teamInfo = new TeamLevelStatisticsInfo();
			if(AppUniversal.teamNameMap.containsKey(String.valueOf(i+1))){
				teamInfo.teamId = "team_"+ AppUniversal.teamNameMap.get(String.valueOf(i+1));
			}else{
				teamInfo.teamId = "team_"+ (i+1);
			}
			
			String[] empIdArr =  teamEmpIdArr.split(",");
			
			for(String empId : empIdArr){
				
				EmployeeLevelStatisticsInfo empInfo = new EmployeeLevelStatisticsInfo();
				empInfo.employeeId = empId;
				allEmpIdList.add(empId);
				allEmpMap.put(empId, empId);
				teamInfo.employeeLevelStatisticsInfoList.add(empInfo);
			}
			uiReportData.teamLevelStatisticsInfoList.add(teamInfo);
		}
		
		StopTimer st1 = new StopTimer();
		//List<TotalLineInfo> tliList = dao.getLineKeyBlobInfoByModuleSpecProjectId(projectId);
		List<TotalLineInfo> tliList = dao.getLineKeyBlobInfoByModuleRootProjectId(projectId);
		System.out.println(" dao.getLineKeyBlobInfoByModuleRootProjectId(projectId) takes " + st1.check()/1000 + " seconds."); 
		
		st1.reset();
		List<SonarLine> sonarLineList = parseToLineInfo(tliList);
		System.out.println("sonarLineList size = " + sonarLineList.size());
		System.out.println(" parseToLineInfo(tliList) takes " + st1.check()/1000 + " seconds.");
		
		st1.reset();
		
		List<SonarLine> filterSonarLineList = new ArrayList<SonarLine>();
		TreeSet<String> empTs = new TreeSet<String>();
		st1.reset();
		for(SonarLine line : sonarLineList){
			//clear the odd empID
			empTs.add(line.lastCommitEmpId);
			if(line == null){
				System.out.println(" line is null !!! " + " line number : " + line.lineNumber + " , class : " + line.testClazzName);
			}
			if(line.lastCommitDate.after(startDate) && line.lastCommitDate.before(endDate)){
				filterSonarLineList.add(line);
			}
		}
		
		TeamLevelStatisticsInfo teamNotInListInfo = new TeamLevelStatisticsInfo();
		teamNotInListInfo.teamId="Team:not_in_list";
		for(String dbEmpId : empTs){
			if(!allEmpMap.containsKey(dbEmpId)){
				EmployeeLevelStatisticsInfo empInfo = new EmployeeLevelStatisticsInfo();
				empInfo.employeeId = dbEmpId;
				teamNotInListInfo.employeeLevelStatisticsInfoList.add(empInfo);
			}
		}
		uiReportData.teamLevelStatisticsInfoList.add(teamNotInListInfo);
		
		System.out.println(" filter sonarLineList size is  " + filterSonarLineList.size() + " and put to set take " + st1.check()/1000 + " seconds.");
		
		
		st1.reset();
		List<SonarIssue> issueList = dao.getAllIssueListByRootId(projectId);
		//List<SonarIssue> issueList = dao.getAllIssueListByComponentId(projectId);
		
		System.out.println(" dao.getAllIssueListByRootId(projectId) takes " + st1.check()/1000 + " seconds.");
		
		Map<String, Integer> issueCreditMap = convertToIssueCredit(issueList);
//		Set<Entry<String, Integer>> entrySet = issueCreditMap.entrySet();
//		Iterator<Entry<String,Integer>> it = entrySet.iterator();
//		while(it.hasNext()){
//			Entry<String, Integer> entry = it.next();
//			System.out.println("key = " + entry.getKey()  + ", " + "value = " + entry.getValue());
//		}
		
		System.out.println(" dao.getAllIssueListByRootId(projectId) takes " + st1.check()/1000 + " seconds.");
		
		System.out.println(" uiReportData.teamLevelStatisticsInfoList " + uiReportData.teamLevelStatisticsInfoList.size());
		
		for(int i = 0 ; i < uiReportData.teamLevelStatisticsInfoList.size(); i++){
			TeamLevelStatisticsInfo teamLevelInfo = uiReportData.teamLevelStatisticsInfoList.get(i);
			
			System.out.println(" employeeLevelStatisticsInfoList size: " + teamLevelInfo.employeeLevelStatisticsInfoList.size());
			for(int j=0; j<teamLevelInfo.employeeLevelStatisticsInfoList.size(); j++){
				EmployeeLevelStatisticsInfo employeeInfo = teamLevelInfo.employeeLevelStatisticsInfoList.get(j);
				
				SonarReport report = new SonarReport();
				report.creditMap = issueCreditMap;
				report.sonarLineList = filterSonarLineList;
				report.empId = employeeInfo.employeeId;
				report.startDate = startDate;
				report.endDate = endDate;
				report.calculate();
				employeeInfo.classLevelStatistiscInfoList = report.employeeStaticInfo.classLevelStatistiscInfoList;

			}
		}
		
		uiReportData.calculateAll();
		
		return uiReportData;
		
	}
	
	public void processBySpecProjectId(String projectId , String empId, Date startDate, Date endDate){
		SonarDaoImpl dao = new SonarDaoImpl();
		
		List<TotalLineInfo> tliList = dao.getLineKeyBlobInfoByModuleSpecProjectId(projectId);
		List<SonarLine> sonarLineList = parseToLineInfo(tliList);
		
		List<SonarIssue> issueList = dao.getAllIssueListByComponentId(projectId);
		
		Map<String, Integer> issueCreditMap = convertToIssueCredit(issueList);
		Set<Entry<String, Integer>> entrySet = issueCreditMap.entrySet();
		Iterator<Entry<String,Integer>> it = entrySet.iterator();
		while(it.hasNext()){
			Entry<String, Integer> entry = it.next();
			System.out.println("key = " + entry.getKey()  + ", " + "value = " + entry.getValue());
		}
		
		SonarReport report = new SonarReport();
		report.creditMap = issueCreditMap;
		report.sonarLineList = sonarLineList;
		
		for(SonarLine sl : report.sonarLineList){
			System.out.println(sl.componentId +"_"+ sl.lineNumber + ":" + sl.covered + ":" + sl.lastCommitEmpId +":" + DateUtil.formatDate(sl.lastCommitDate));
		}
		
		report.empId = empId;
		report.startDate = startDate;
		report.endDate = endDate;
		
		report.calculate();
		
		System.out.println(report.coverageRate);
		System.out.println(report.complianceRate);
		
	}
	
	public List<SonarLine> process(String empId, Date startDate, Date endDate){
		
		SonarDaoImpl dao = new SonarDaoImpl();
		String projectId = "33255";
		List<TotalLineInfo> tliList = dao.getKeyBlobByProjectId(projectId);
		List<SonarIssue> issueList = dao.getIssueListByComponentId(projectId);
		
		Map<String, Integer> issueCreditMap = convertToIssueCredit(issueList);
		
		List<SonarLine> sonarLineList = parseToLineInfo(tliList);
		if(sonarLineList == null){
			System.out.println("Did not contain key property!");
			return null;
		}
		SonarReport report = new SonarReport();
		report.creditMap = issueCreditMap;
		report.sonarLineList = sonarLineList;
		report.empId = empId;
		report.startDate = startDate;
		report.endDate = endDate;
		report.calculate();
		System.out.println(report.coverageRate);
		System.out.println(report.complianceRate);
		
		return sonarLineList;
		
	}

	
	private List<SonarLine> parseToLineInfo(List<TotalLineInfo> lineInfoList){
		
		List<SonarLine> sonarLineList = new ArrayList<SonarLine>();
		if(lineInfoList != null && lineInfoList.size() >0){ 
			//3 info , author , commit time , coverage hit
			for(TotalLineInfo tli : lineInfoList){
				List<SonarLine> oneClassLines = convertToSonarLineModelList(tli);
				if(oneClassLines == null){
					continue;
				}
				sonarLineList.addAll(oneClassLines);
			}
		}
		
		return sonarLineList; 
	}

	//convert single total Project snapshot info model to a set of SonarlineModel 
	private List<SonarLine> convertToSonarLineModelList(TotalLineInfo tli) {
		List<SonarLine> sonarLineList;
		String authorInfos = tli.authorsByLine;
		if(authorInfos == null || authorInfos.length() == 0){
			System.out.println("Test class: " + tli.testClassName + " , author info is null");
			return null;
		}
		String[] authorArr = authorInfos.split(";");
		sonarLineList = parseForAuthor(authorArr);
		
		String lastCommitDatetimeInfos = tli.lastCommitDatetimeByLine;
		if(lastCommitDatetimeInfos == null || lastCommitDatetimeInfos.length() == 0){
			System.out.println("Test class : " +tli.testClassName + " , lastCommitDatetime info is null");
			return null;
		}
		String[] lastCommitDatetimeArr = lastCommitDatetimeInfos.split(";");
		
		String coverageHitsInfos = tli.coverageLineHitsData;
		if(coverageHitsInfos == null ||  coverageHitsInfos.length() == 0 ){
			System.out.println("Test class : " +tli.testClassName + " , has EMPTY coverageHits info .");
			parseForCommitDatetimeCoverage(sonarLineList, lastCommitDatetimeArr, null, tli.testClassName, tli.componentId);
		}else{
			String[] coverageHitsArr = coverageHitsInfos.split(";");
			parseForCommitDatetimeCoverage(sonarLineList, lastCommitDatetimeArr, coverageHitsArr, tli.testClassName, tli.componentId);
		}
		return sonarLineList;
	}

	private Map<String, Integer> convertToIssueCredit(List<SonarIssue> sonarLineIssueList){
		Map<String, Integer> lineIssueCreditSumMap = new TreeMap<String, Integer>();
		for(int i=0; sonarLineIssueList != null && i<sonarLineIssueList.size(); i++){
			SonarIssue si = sonarLineIssueList.get(i);
			
		//	System.out.println("issue: [" + si.componentId + "],[" + si.lineNumber + "], [" + si.severity +"]");
			if(lineIssueCreditSumMap.containsKey(si.componentId + "_" + si.lineNumber)){
				Integer sumInteger = sumInteger(lineIssueCreditSumMap.get(si.componentId + "_" + si.lineNumber) ,
						getShareByIssueSeverity(si.severity));
				lineIssueCreditSumMap.put(si.componentId + "_" + si.lineNumber, sumInteger);
			}else{
				lineIssueCreditSumMap.put(si.componentId + "_" + si.lineNumber, getShareByIssueSeverity(si.severity));
			}
		}
		return lineIssueCreditSumMap;
	}
	
	private Integer sumInteger(Integer a, Integer b){
		return Integer.valueOf(a.intValue() + b.intValue());
	}
	
	private Integer getShareByIssueSeverity(String severtity){
		if(severtity.equalsIgnoreCase("MINOR")){
			return Integer.valueOf(1);
		}else if(severtity.equalsIgnoreCase("MAJOR")){
			return Integer.valueOf(4);
		}else if(severtity.equalsIgnoreCase("CRITICAL")){
			return Integer.valueOf(10);
		}else{
			return Integer.valueOf(0);
		}
	}
	
	private List<SonarLine> parseForAuthor(String[] authorArr) {
		int idx = 0;
		
		ArrayList<SonarLine> allLine = new ArrayList<SonarLine>();
		
		for(String keyVal : authorArr){
			SonarLine sl = new SonarLine();
			String[] kvPair = keyVal.split("=");
			sl.lineNumber = ++idx;
			sl.lastCommitEmpId = kvPair[1];
			allLine.add(sl);
		}
		return allLine;
	}
	
	private List<SonarLine> parseForCommitDatetimeCoverage(List<SonarLine> lineList , String[] commitDatetimeArr, String[] coverageHitsArr, String testClassName, String componentId) {
		
		//ArrayList<SonarLine> allLine = new ArrayList<SonarLine>();
		TreeMap<String,SonarLine> lineTreeMap = new TreeMap<String,SonarLine>();
		
		for(int i=0 ; lineList != null && i < lineList.size() ; i++){
			lineList.get(i).testClazzName = testClassName;
			lineList.get(i).componentId = componentId;
			lineTreeMap.put(componentId + "_" + lineList.get(i).lineNumber, lineList.get(i));
		}
		
		for(int j=0; j<commitDatetimeArr.length; j++){
			
			String[] keyCommitDatePair = commitDatetimeArr[j].split("=");
		
			if(lineTreeMap.containsKey(componentId + "_" +keyCommitDatePair[0])){
				SonarLine line = lineTreeMap.get(componentId + "_" + keyCommitDatePair[0]);
				line.lastCommitDate = parseComplexDate(keyCommitDatePair[1]);
			}
		}
		if(coverageHitsArr != null && coverageHitsArr.length > 0){
						
			for(int j=0; j<coverageHitsArr.length; j++){
				
				String[] coverageHitsPair = coverageHitsArr[j].split("=");
				if(lineTreeMap.containsKey(componentId + "_" +coverageHitsPair[0])){
					SonarLine line = lineTreeMap.get(componentId + "_" +coverageHitsPair[0]);
					line.effeciveCoverageLine = 1;// this line is effective line for coverage to calculate, always set 1
					line.covered = Integer.parseInt(coverageHitsPair[1]);
				}
			}
			
		}else{
			for(int i = 0; lineList != null && i < lineList.size() ; i++){
				lineList.get(i).covered = 0;
			}
		}
		
		return lineList;
	}

	public Date parseComplexDate(String datetimeStr) {
		String dateStr = datetimeStr.substring(0,10);
		String timeStr = datetimeStr.substring(11,19);
		return DateUtil.parse(dateStr + " " + timeStr);
	}
	
}
