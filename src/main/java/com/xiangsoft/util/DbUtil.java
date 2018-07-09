package com.xiangsoft.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.xiangsoft.sonar.sonarreport.Client;


public class DbUtil {
	
	private String url = "jdbc:oracle:thin:@10.1.114.205:1753:O05GCE0";
	private String userName = "sonar";
	private String userPassword = "sonar";
	private String jdbcName = "oracle.jdbc.OracleDriver";
	  
//	  private String url = "jdbc:mysql://127.0.0.1:3306/ccgl";
//	  private String userName = "root";
//	  private String userPassword = "admin";
//	  private String jdbcName = "com.mysql.jdbc.Driver";
	
	public DbUtil() {
		if(Client.prop != null){
			url = (String)Client.prop.get("db_url");
			jdbcName = (String)Client.prop.get("db_driver");
			userName = (String)Client.prop.get("db_user");
			userPassword = (String)Client.prop.get("db_password");
		}
	}
	
	public DbUtil(String url, String jdbcName, String userName, String userPassword) {
		this.url = url;
		this.jdbcName = jdbcName;
		this.userName = userName;
		this.userPassword = userPassword;
	}
	
	  public Connection getCon(){
		  Connection con = null;
		  try {
			Class.forName(jdbcName);
			con = DriverManager.getConnection(url,userName,userPassword);
		} catch (Exception e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		}
		 
		  return con;
	  }
	  public void closeCon(Connection con) throws Exception{
		  if(con!=null){
			  con.close();
		  }
	  }
	  
	 
	  public static void main(String[] args) {
		  
		  
		  List<Connection> connArray = new ArrayList<Connection>();
		 try {
			 
			
			 Connection con = null;
			 int i=0;
			 while(i<200){
				 
				  con = new DbUtil().getCon();
				  connArray.add(con);
				  Statement st = con.createStatement();
				  st.executeQuery("select count(1) from opgcep2.gcal_use_case");
				  
				  st.close();
				  System.out.print("conn number: " + (++i));
				  System.out.println(con);
				   
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				for(int sizeI = 0; sizeI < connArray.size() ; sizeI++){
						connArray.get(sizeI).close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	  }
	}

