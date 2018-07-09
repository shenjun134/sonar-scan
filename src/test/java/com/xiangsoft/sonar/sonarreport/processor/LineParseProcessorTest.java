package com.xiangsoft.sonar.sonarreport.processor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.statestr.gcth.core.util.StopTimer;
import com.xiangsoft.sonar.sonarreport.model.SonarLine;
import com.xiangsoft.util.DateUtil;

public class LineParseProcessorTest {
	
	
	@Test
	public void test_suppose_parseComplexDate_equals_specify_date_format(){
		LineParseProcessor lpp = new LineParseProcessor();
		String val  = "2012-10-30 07:32:37-0400";
		Assert.assertEquals("2012-10-30 07:32:37", DateUtil.df.format(lpp.parseComplexDate(val)));
	}
	
	//need to remove
	@Test
	public void test_process(){
		LineParseProcessor lpp = new LineParseProcessor();
		
		String empId = "e589918";
		Date startDate;
		try {
			startDate = DateUtil.df.parse("2016-03-27 00:00:01");
			Date endDate = DateUtil.df.parse("2016-03-29 00:00:01");
			List<SonarLine> sll = lpp.process(empId, startDate, endDate);
			
			for(SonarLine sl : sll){
				if(sl.lastCommitDate.after(startDate) 
						&& sl.lastCommitDate.before(endDate) 
						&& sl.lastCommitEmpId.equals("e589918")){
					System.out.println(sl.lineNumber + " " + sl.lastCommitEmpId + " " + sl.covered + " " + sl.lastCommitDate);
				}
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	//2016-11-15
	//
	@Test
	public void test_process_allBySpecProjectId(){
		LineParseProcessor lpp = new LineParseProcessor();
		
		String empId = "e540766";
		Date startDate;
		try {
			startDate = DateUtil.df.parse("2016-11-10 00:00:01");
			Date endDate = DateUtil.df.parse("2016-11-20 00:00:01");
			lpp.processBySpecProjectId("33720", empId, startDate, endDate);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void test_process_allByRootProjectId(){
		
		StopTimer st = new StopTimer();
		LineParseProcessor lpp = new LineParseProcessor();
		String empId = "e521770";
		//String empId = "e521770,e589918";
		 String arr[] ={empId}; 
		Date startDate;
		try {
			startDate = DateUtil.df.parse("2012-10-01 00:00:01");
			Date endDate = DateUtil.df.parse("2016-03-29 00:00:01");
			lpp.processByProjectRootId("33215", arr, startDate, endDate);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(st.check());
	}
	
	@Test
	public void test_loadproperty(){
	
		InputStream is = null;
		try {
			is = new FileInputStream("C:/a528692/private_project/sonarreport/appconfig.properties");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Properties p = new Properties();
		try {
			p.load(is);
			Assert.assertEquals(p.getProperty("usecase_parent"),"10792");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void test_process_Trank_UseCase_ByRootProjectId(){
		
		StopTimer st = new StopTimer();
		LineParseProcessor lpp = new LineParseProcessor();
		
		 String empId = "e540766,e530765,e523080,e536260,a520538,e521770,e521899,e458506,a528903,e458502,e521904,a521333,e536690,a543177,a547144,e524807,a525423,e467885,a538296,a527005,a545373,e519504,e551224,e550299,a549238,a534453,e594657,e596066,a587205,e471719,e465907,e580718,e465907";
		 String arr[] ={empId}; 
		//String empId = "e540766";
		Date startDate, endDate;
		try {
			startDate = DateUtil.df.parse("2016-09-01");
			endDate = DateUtil.df.parse("2016-11-21");
			lpp.processByProjectRootId("17618", arr, startDate, endDate);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(st.check());
	}
	
	//33216
	/*
	String empId = "e521770";
	Date startDate, endDate;
	try {
		startDate = sdf.parse("2013-09-01_00:00:01");
		endDate = sdf.parse("2016-11-21_00:00:01");
		lpp.processByProjectRootId("33216", empId, startDate, endDate);
	*/
	
	@Test
	public void test_client_one_module(){
		
		StopTimer st = new StopTimer();
		LineParseProcessor lpp = new LineParseProcessor();
		
		 //String empId = "e501878,e540766,e530765,e523080,e536260,a520538,e521770,e521899,e458506,a528903,e458502,e521904,a521333,e536690,a543177,a547144,e524807,a525423,e467885,a538296,a527005,a545373,e519504,e551224,e550299,a549238,a534453,e594657,e596066,a587205,e471719,e465907,e580718,e465907";
		String empId = "e501878,e540766,e530765,e523080,e536260,a52053";
		//String empId = "e521770";
		
		String arr[] ={empId}; 
		
		Date startDate, endDate;
		try {
			startDate = DateUtil.df.parse("2013-09-01 00:00:01");
			endDate = DateUtil.df.parse("2016-11-21 00:00:01");
			lpp.processByProjectRootId("17707", arr, startDate, endDate);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(st.check());
	}
	
	//17618  33215
	@Test
	public void test_client(){
		
		StopTimer st = new StopTimer();
		LineParseProcessor lpp = new LineParseProcessor();
		
		 //String empId = "e501878,e540766,e530765,e523080,e536260,a520538,e521770,e521899,e458506,a528903,e458502,e521904,a521333,e536690,a543177,a547144,e524807,a525423,e467885,a538296,a527005,a545373,e519504,e551224,e550299,a549238,a534453,e594657,e596066,a587205,e471719,e465907,e580718,e465907";
		//String empId = "e540766";
		String empId = "e501878,e540766,e530765,e523080,e536260";
		String arr[] ={empId}; 
		Date startDate, endDate;
		try {
			startDate = DateUtil.df.parse("2016-09-01 00:00:01");
			endDate = DateUtil.df.parse("2016-11-2100:00:01");
			lpp.processByProjectRootId("33215", arr, startDate, endDate);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(st.check());
	}
	
	//project id = {'33720','33432' ,'33721','33431'}
	@Test
	public void test_client_projectId(){
		
		StopTimer st = new StopTimer();
		LineParseProcessor lpp = new LineParseProcessor();
		
		 String empId = "e501878,e540766,e530765,e523080,e536260,a520538,e521770,e521899,e458506,a528903,e458502,e521904,a521333,e536690,a543177,a547144,e524807,a525423,e467885,a538296,a527005,a545373,e519504,e551224,e550299,a549238,a534453,e594657,e596066,a587205,e471719,e465907,e580718,e465907,e589918";
		//String empId = "e540766";
		 String arr[] ={empId}; 
		Date startDate, endDate;
		try {
			startDate = DateUtil.df.parse("2016-09-01 00:00:01");
			endDate = DateUtil.df.parse("2016-11-21 00:00:01");
			lpp.processByProjectRootId("app_branch", arr, startDate, endDate);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(st.check());
	}
	
	//e589918,e501878,e540766,e530765
	
}
