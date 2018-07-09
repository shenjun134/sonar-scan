package com.xiangsoft.sonar.sonarreport.processor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.xiangsoft.sonar.sonarreport.dao.SonarDaoImpl;
import com.xiangsoft.sonar.sonarreport.model.Report;
import com.xiangsoft.sonar.sonarreport.model.SonarIssue;
import com.xiangsoft.sonar.sonarreport.model.TotalLineInfo;
import com.xiangsoft.universal.AppUniversal;
import com.xiangsoft.util.DateUtil;

public class HtmlWriterProcessorTest {

	@Test
	public void testWriteToFile() {
		
		AppUniversal.teamNameMap.put("1", "OneWorld");
		
		SonarDaoImpl mockDao = Mockito.mock(SonarDaoImpl.class);

		LineParseProcessor lpp = new LineParseProcessor();
		lpp.dao = mockDao;

		List<TotalLineInfo> mockTotalLineInfoList = new ArrayList<TotalLineInfo>();
		TotalLineInfo class_1_info = new TotalLineInfo();
		class_1_info.authorsByLine = "1=e001;2=e002;3=e001;4=e001;5=e002;6=e003";
		class_1_info.componentId = "c001";
		class_1_info.coverageLineHitsData = "1=1;2=0;3=0;4=0;5=0;6=1";
		class_1_info.lastCommitDatetimeByLine = "1=2016-09-15T12:00:00;2=2016-09-15T12:00:00;3=2016-09-15T12:00:00;4=2016-09-15T12:00:00;5=2016-09-15T12:00:00;6=2016-09-15T12:00:00;";
		class_1_info.testClassName = "class1";
		mockTotalLineInfoList.add(class_1_info);

		Mockito.when(
				mockDao.getLineKeyBlobInfoByModuleRootProjectId(Mockito
						.anyString())).thenReturn(mockTotalLineInfoList);

		Mockito.when(mockDao.getProjectNameByProjectId(Mockito.anyString()))
				.thenReturn("TestProject");

		List<SonarIssue> sonarIssueList = new ArrayList<SonarIssue>();
		SonarIssue sonarIssue1 = new SonarIssue();
		sonarIssue1.componentId = "c001";
		sonarIssue1.lineNumber = 1;
		sonarIssue1.severity = "MAJOR";
		sonarIssueList.add(sonarIssue1);

		Mockito.when(mockDao.getAllIssueListByRootId(Mockito.anyString()))
				.thenReturn(sonarIssueList);

		String allTeamEmpIdArr[] = new String[] { "e001,e002" };

		Report report = lpp.processByProjectRootId("33215", allTeamEmpIdArr,
				DateUtil.parse("2016-09-01 00:00:01"),
				DateUtil.parse("2016-11-01 00:00:01"));

		new HtmlWriteProcessor().writeOntoHtmlTemplate(report);
	}
}
