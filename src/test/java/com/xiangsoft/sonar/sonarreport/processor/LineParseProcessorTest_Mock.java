package com.xiangsoft.sonar.sonarreport.processor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.xiangsoft.sonar.sonarreport.dao.SonarDaoImpl;
import com.xiangsoft.sonar.sonarreport.model.Report;
import com.xiangsoft.sonar.sonarreport.model.SonarIssue;
import com.xiangsoft.sonar.sonarreport.model.TeamLevelStatisticsInfo;
import com.xiangsoft.sonar.sonarreport.model.TotalLineInfo;
import com.xiangsoft.util.DateUtil;



public class LineParseProcessorTest_Mock {
	
	@Test
	public void suppose_coverage_3333_total_20(){
		
		SonarDaoImpl mockDao = Mockito.mock(SonarDaoImpl.class);
		
		LineParseProcessor lpp = new LineParseProcessor();
		lpp.dao = mockDao;
		
		List<TotalLineInfo>  mockTotalLineInfoList = new ArrayList<TotalLineInfo>();
		TotalLineInfo class_1_info = new TotalLineInfo();
		class_1_info.authorsByLine = "1=e001;2=e002;3=e001;4=e001;5=e002";
		class_1_info.componentId = "c001";
		class_1_info.coverageLineHitsData = "1=1;2=0;3=0;4=0";
		class_1_info.lastCommitDatetimeByLine = "1=2016-09-15T12:00:00;2=2016-09-15T12:00:00;3=2016-09-15T12:00:00;4=2016-09-15T12:00:00;5=2016-09-15T12:00:00";
		class_1_info.testClassName = "class1";
		mockTotalLineInfoList.add(class_1_info);
		
		Mockito.when(mockDao.getLineKeyBlobInfoByModuleRootProjectId(Mockito.anyString())).thenReturn(mockTotalLineInfoList);
		
		Mockito.when(mockDao.getProjectNameByProjectId(Mockito.anyString())).thenReturn("TestProject");
		
		List<SonarIssue> sonarIssueList = new ArrayList<SonarIssue>();
		SonarIssue sonarIssue1 = new SonarIssue();
		sonarIssue1.componentId="c001";
		sonarIssue1.lineNumber=1;
		sonarIssue1.severity="MAJOR";
		sonarIssueList.add(sonarIssue1);
		
		Mockito.when(mockDao.getAllIssueListByRootId(Mockito.anyString())).thenReturn(sonarIssueList);
		
		String allTeamEmpIdArr[] = new String[]{"e001"};
		
		Report report = lpp.processByProjectRootId("33215", allTeamEmpIdArr, DateUtil.parse("2016-09-01 00:00:01"), DateUtil.parse("2016-11-01 00:00:01"));
		
		Assert.assertEquals(1, report.checkedCoveredLineNumber);
		Assert.assertEquals(4, report.checkedCreditSum);
		Assert.assertEquals(5, report.checkedLineNumber);
		Assert.assertTrue( BigDecimal.valueOf(25.00).compareTo(report.coverageRate)==0);
		for(int i=0; i<report.teamLevelStatisticsInfoList.size(); i++){
			TeamLevelStatisticsInfo teamInfo = report.teamLevelStatisticsInfoList.get(i);
			if(i==0){
				Assert.assertTrue(BigDecimal.valueOf(33.3).compareTo(teamInfo.coverageRate)==0);
			}
			if(i==1){
				Assert.assertTrue(BigDecimal.ZERO.compareTo(teamInfo.coverageRate)==0);
			}
		}
	}
	
	@Test
	public void suppose_two_class_by_one_person_calculate_rate(){
		SonarDaoImpl mockDao = Mockito.mock(SonarDaoImpl.class);
		
		LineParseProcessor lpp = new LineParseProcessor();
		lpp.dao = mockDao;
		
		List<TotalLineInfo>  mockTotalLineInfoList = new ArrayList<TotalLineInfo>();
		TotalLineInfo class_1_info = new TotalLineInfo();
		class_1_info.authorsByLine = "1=e001;2=e002;3=e001;4=e001;5=e002";
		class_1_info.componentId = "c001";
		class_1_info.coverageLineHitsData = "1=1;2=1;3=0;4=1";
		class_1_info.lastCommitDatetimeByLine = "1=2016-09-15T12:00:00;2=2016-09-15T12:00:00;3=2016-09-15T12:00:00;4=2016-09-15T12:00:00;5=2016-09-15T12:00:00";
		class_1_info.testClassName = "class1";
		mockTotalLineInfoList.add(class_1_info);
		
		TotalLineInfo class_2_info = new TotalLineInfo();
		class_2_info.authorsByLine = "1=e002;2=e003;3=e001;4=e222;5=e002";
		class_2_info.componentId = "c002";
		class_2_info.coverageLineHitsData = "1=0;2=1;3=1;4=0";
		class_2_info.lastCommitDatetimeByLine = "1=2016-09-15T12:00:00;2=2016-09-15T12:00:00;3=2016-09-15T12:00:00;4=2016-09-15T12:00:00;5=2016-09-15T12:00:00";
		class_2_info.testClassName = "class2";
		mockTotalLineInfoList.add(class_2_info);
		
		
		Mockito.when(mockDao.getLineKeyBlobInfoByModuleRootProjectId(Mockito.anyString())).thenReturn(mockTotalLineInfoList);
		
		Mockito.when(mockDao.getProjectNameByProjectId(Mockito.anyString())).thenReturn("TestProject");
		
		List<SonarIssue> sonarIssueList = new ArrayList<SonarIssue>();
		SonarIssue sonarIssue1 = new SonarIssue();
		sonarIssue1.componentId="c001";
		sonarIssue1.lineNumber=1;
		sonarIssue1.severity="MAJOR";
		sonarIssueList.add(sonarIssue1);
		
		Mockito.when(mockDao.getAllIssueListByRootId(Mockito.anyString())).thenReturn(sonarIssueList);
		
		String  allTeamEmpIdArr[] = new String[]{"e001","e002"};
		
		Report report = lpp.processByProjectRootId("33215", allTeamEmpIdArr, DateUtil.parse("2016-09-01 00:00:01"), DateUtil.parse("2016-11-01 00:00:01"));
		
		Assert.assertEquals(3, report.teamLevelStatisticsInfoList.size());
		Assert.assertEquals(4, report.checkedCreditSum);
		Assert.assertEquals(5, report.checkedCoveredLineNumber);
		Assert.assertEquals(0, BigDecimal.valueOf(62.5).compareTo(report.coverageRate));
		Assert.assertEquals(0, BigDecimal.valueOf(60).compareTo(report.complianceRate));
		Assert.assertEquals(0, BigDecimal.valueOf(75).compareTo(report.teamLevelStatisticsInfoList.get(0).coverageRate));
		Assert.assertEquals(0, BigDecimal.valueOf(50).compareTo(report.teamLevelStatisticsInfoList.get(1).coverageRate));
		Assert.assertEquals(0, BigDecimal.valueOf(50).compareTo(report.teamLevelStatisticsInfoList.get(2).coverageRate));
	}
	

}
