package com.xiangsoft.sonar.sonarreport.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

public class SonarReportTest {
	
	SimpleDateFormat smf = new SimpleDateFormat("yyyyMMdd hhmmss");
	
	@Test
	public void test_calculate(){
		SonarReport report = new SonarReport();
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		hm.put("12345_"+Integer.valueOf(1), Integer.valueOf(4));
		//hm.put(Integer.valueOf(2), Integer.valueOf(1));
		
		report.creditMap = hm;
		List<SonarLine> sonarLineList = new ArrayList<SonarLine>();
		
		try {
			SonarLine sl1 = new SonarLine();
			sl1.covered = 1;
			sl1.lineNumber = 1;
			sl1.lastCommitEmpId = "a528692";
			sl1.lastCommitDate = smf.parse("20161002 000002");
			SonarLine sl2 = new SonarLine();
			sl2.covered = 0;
			sl2.lineNumber = 2;
			sl2.lastCommitEmpId = "a528692";
			sl2.lastCommitDate = smf.parse("20161002 000002");
			SonarLine sl3 = new SonarLine();
			sl3.covered = 0;
			sl3.lineNumber = 3;
			sl3.lastCommitEmpId = "a528692";
			sl3.lastCommitDate = smf.parse("20161002 000002");
			SonarLine sl4 = new SonarLine();
			sl4.covered = 0;
			sl4.lineNumber = 3;
			sl4.lastCommitEmpId = "a528692";
			sl4.lastCommitDate = smf.parse("20161002 000002");
			SonarLine sl5 = new SonarLine();
			sl5.covered = 0;
			sl5.lineNumber = 3;
			sl5.lastCommitEmpId = "a528692";
			sl5.lastCommitDate = smf.parse("20161002 000002");
			SonarLine sl6 = new SonarLine();
			sl6.covered = 0;
			sl6.lineNumber = 3;
			sl6.lastCommitEmpId = "a528692";
			sl6.lastCommitDate = smf.parse("20161002 000002");
			sonarLineList.add(sl1);
			sonarLineList.add(sl2);
			sonarLineList.add(sl3);
			sonarLineList.add(sl4);
			sonarLineList.add(sl5);
			sonarLineList.add(sl6);
			
			Date startDate = null;
			Date endDate = null;
			
			startDate = smf.parse("20161002 000001");
			endDate = smf.parse("20161002 000003");
			
			report.sonarLineList = sonarLineList;
			report.creditMap = hm;
			report.startDate = startDate;
			report.endDate = endDate;
			report.calculate();
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(report.coverageRate);
		System.out.println(report.complianceRate);
	}

}
