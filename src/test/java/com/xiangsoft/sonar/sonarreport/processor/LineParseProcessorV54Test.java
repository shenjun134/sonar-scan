package com.xiangsoft.sonar.sonarreport.processor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;
import org.sonar.wsclient.Host;
import org.sonar.wsclient.connectors.HttpClient4Connector;

import com.xiangsoft.sonar.sonarreport.Client;
import com.xiangsoft.sonar.sonarreport.model.Report;
import com.xiangsoft.sonar.sonarreport.model.SonarLine;
import com.xiangsoft.util.DateUtil;

public class LineParseProcessorV54Test {
	
	@Before
	public void init(){
		String userDir = System.getProperty("user.dir");
    	InputStream is = null;
		try {
			is = new FileInputStream(userDir+ "/appconfig.properties");
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		if(is == null){
			System.exit(1);
		}
		try {
			Client.prop.load(is);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}


	@Test
	public void suppose_get_data_successfully_by_project_id_in_v54(){
		LineParseProcessorForV54 process = new LineParseProcessorForV54();
		List<SonarLine> lineList = process.getAllProjectLineInfoByProjectIdV54("26360");
		System.out.println(lineList.size());
	}

	
	@Test
	public void suppose_generate_report_successfully_by_project_id_in_v54(){
		
		LineParseProcessorForV54 lpp = new LineParseProcessorForV54();
		String empId = "dsultana";
		String arr[] ={empId}; 
		Date startDate;
		Report rp = null;
		try {
			startDate = DateUtil.df.parse("2007-12-07 10:00:01");
			Date endDate = DateUtil.df.parse("2016-12-07 11:00:01");
			rp = lpp.processByProjectRootId("26361", arr, startDate, endDate);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new HtmlWriteProcessor().writeOntoHtmlTemplate(rp);
	}
	
	@Test
	public void suppose_get_connection(){
		HttpClient4Connector connector = new HttpClient4Connector(new Host("https://analysiscenter.veracode.com", "ywang2@statestreet.com", "2wsxBGT%"));
		HttpGet request = new HttpGet("https://analysiscenter.veracode.com/j_security_check");
		request.setHeader("Accept", "text/plain");
		String response = connector.executeRequest(request);
		System.out.println(response);
	}
}
