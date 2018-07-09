package com.xiangsoft.sonar.sonarreport;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.OracleStatement;

import org.junit.Test;

import com.statestr.gcth.core.util.StopTimer;
import com.xiangsoft.util.DbUtil;

public class ClientTest {
	
	@Test
	public void test_main(){
		Client c = new Client();
		
		StopTimer st = new StopTimer();
		
		String args[] = new String[3];
		args[0]="app_branch";
		args[2]="2016-09-01_00:00:01";
		args[3]="2016-11-21_00:00:01";
		c.main(args);
		
		System.out.println(st.check());
		
	} 
	
	@Test
	public void test_degeDB(){
		Connection conn = new DbUtil("jdbc:oracle:thin:@dccdw3211.ad.imsi.com:1521/xe", "oracle.jdbc.OracleDriver", "sonariss", "sonariss").getCon();
		String queryLineInfo = "select measure_data from project_measures where id = 16780090  ";
		OracleStatement st = null;
		
		
		try {
			st = (OracleStatement) conn.createStatement();
			ResultSet resultSet = st.executeQuery(queryLineInfo);
			
			while(resultSet.next()){
				if(resultSet.getBlob("measure_data") == null){
					System.out.println("measure_data is null");
					break;
				}
				oracle.sql.BLOB data = (oracle.sql.BLOB)resultSet.getBlob("measure_data");
				InputStream is = data.getBinaryStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String s;
				StringBuilder sb = new StringBuilder();
				while( (s = reader.readLine()) != null) {
					sb.append(s);
				}
				System.out.println(sb);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				st.close();
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
