package com.xiangsoft.sonar.sonarreport.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.junit.Test;

import com.xiangsoft.sonar.sonarreport.dao.SonarDaoImpl.ClassIdMetricsNameBlobDataPair;
import com.xiangsoft.sonar.sonarreport.model.SonarIssue;
import com.xiangsoft.sonar.sonarreport.model.TotalLineInfo;

public class SonarDaoImplTest {
	
	@Test
	public void testGetKeyBlob(){
		SonarDaoImpl impl =	new SonarDaoImpl();
		String projectId = "33255";
		List<TotalLineInfo> list = impl.getKeyBlobByProjectId(projectId);
		
	}

	@Test
	public void test_getIssueListByComponentId(){
		SonarDaoImpl impl =	new SonarDaoImpl();
		String projectId = "33255";
		List<SonarIssue> list = impl.getIssueListByComponentId(projectId);
		System.out.println(list.size());
	}
	
	@Test
	public void test_getLineKeyBlobInfoByModuleSpecProjectId(){
		SonarDaoImpl impl =	new SonarDaoImpl();
		String projectId = "33255,33256,33248";
		List<TotalLineInfo> list = impl.getLineKeyBlobInfoByModuleSpecProjectId(projectId);
		System.out.println(list.size());
	}
	
	@Test
	public void test_packLineInfoToCollection(){
		
		List<TotalLineInfo> totalLineInfoList = new ArrayList<TotalLineInfo>();
		SonarDaoImpl dao = new SonarDaoImpl();
		
		SonarDaoImpl.ClassIdMetricsNameBlobDataPair  pair1 = ClassIdMetricsNameBlobDataPair.newInstance("300123","com.pck.ClassA","authors_by_line", "1");
		SonarDaoImpl.ClassIdMetricsNameBlobDataPair  pair11 = ClassIdMetricsNameBlobDataPair.newInstance("300123","com.pck.ClassA","last_commit_datetimes_by_line", "11");
		SonarDaoImpl.ClassIdMetricsNameBlobDataPair  pair111 = ClassIdMetricsNameBlobDataPair.newInstance("300123","com.pck.ClassA","coverage_line_hits_data", "111");

		SonarDaoImpl.ClassIdMetricsNameBlobDataPair  pair2 = ClassIdMetricsNameBlobDataPair.newInstance("300123","com.pck.ClassA","authors_by_line", "2");
		SonarDaoImpl.ClassIdMetricsNameBlobDataPair  pair22 = ClassIdMetricsNameBlobDataPair.newInstance("300123","com.pck.ClassA","last_commit_datetimes_by_line", "22");
		SonarDaoImpl.ClassIdMetricsNameBlobDataPair  pair222 = ClassIdMetricsNameBlobDataPair.newInstance("300123","com.pck.ClassA","coverage_line_hits_data", "222");
		
		SonarDaoImpl.ClassIdMetricsNameBlobDataPair  pair3 = ClassIdMetricsNameBlobDataPair.newInstance("300123","com.pck.ClassA","authors_by_line", "3");
		SonarDaoImpl.ClassIdMetricsNameBlobDataPair  pair33 = ClassIdMetricsNameBlobDataPair.newInstance("300123","com.pck.ClassA","last_commit_datetimes_by_line", "33");

		SonarDaoImpl.ClassIdMetricsNameBlobDataPair  pair4 = ClassIdMetricsNameBlobDataPair.newInstance("300123","com.pck.ClassA","authors_by_line", "4");
		SonarDaoImpl.ClassIdMetricsNameBlobDataPair  pair44 = ClassIdMetricsNameBlobDataPair.newInstance("300123","com.pck.ClassA","last_commit_datetimes_by_line", "44");

		SonarDaoImpl.ClassIdMetricsNameBlobDataPair  pair5 = ClassIdMetricsNameBlobDataPair.newInstance("300123","com.pck.ClassA","authors_by_line", "5");
		SonarDaoImpl.ClassIdMetricsNameBlobDataPair  pair55 = ClassIdMetricsNameBlobDataPair.newInstance("300123","com.pck.ClassA","last_commit_datetimes_by_line", "555");
		SonarDaoImpl.ClassIdMetricsNameBlobDataPair  pair555 = ClassIdMetricsNameBlobDataPair.newInstance("300123","com.pck.ClassA","coverage_line_hits_data", "55");
    
		List<ClassIdMetricsNameBlobDataPair> classMetricsNameBlobDataList = new ArrayList<ClassIdMetricsNameBlobDataPair>();
		
		classMetricsNameBlobDataList.add(pair1);
		classMetricsNameBlobDataList.add(pair11);
		classMetricsNameBlobDataList.add(pair111);
		classMetricsNameBlobDataList.add(pair2);
		classMetricsNameBlobDataList.add(pair22);
		classMetricsNameBlobDataList.add(pair222);
		classMetricsNameBlobDataList.add(pair3);
		classMetricsNameBlobDataList.add(pair33);
		classMetricsNameBlobDataList.add(pair4);
		classMetricsNameBlobDataList.add(pair44);
		classMetricsNameBlobDataList.add(pair5);
		classMetricsNameBlobDataList.add(pair55);
		classMetricsNameBlobDataList.add(pair555);
		
		totalLineInfoList = dao.packLineInfoToCollection(classMetricsNameBlobDataList);
		StringBuilder sb = new StringBuilder();
		for(TotalLineInfo info :  totalLineInfoList){
			System.out.println(info.toString());
			sb.append(info.toString());
		}
		
		Assert.assertEquals(sb.toString(), "TotalLineInfo [coverageLineHitsData=111, authorsByLine=1, lastCommitDatetimeByLine=11, testClassName=com.pck.ClassA, componentId=300123]"+
			               "TotalLineInfo [coverageLineHitsData=222, authorsByLine=2, lastCommitDatetimeByLine=22, testClassName=com.pck.ClassA, componentId=300123]"+
			               "TotalLineInfo [coverageLineHitsData=, authorsByLine=3, lastCommitDatetimeByLine=33, testClassName=com.pck.ClassA, componentId=300123]"+
			               "TotalLineInfo [coverageLineHitsData=, authorsByLine=4, lastCommitDatetimeByLine=44, testClassName=com.pck.ClassA, componentId=300123]"+
			                "TotalLineInfo [coverageLineHitsData=55, authorsByLine=5, lastCommitDatetimeByLine=555, testClassName=com.pck.ClassA, componentId=300123]");
	} 

}
