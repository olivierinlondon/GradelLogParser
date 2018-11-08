package com.olivier.logparserpackage;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class LogeParserThread {

	LinkedList<SystemEventMaster> listMast = new LinkedList<SystemEventMaster>();
	
    public void produce(String path) throws InterruptedException 
    { 
    	Gson gson = new Gson();
    	SystemEvent eventCurrent;
    	SystemEvent eventSec;
    	Map<String, SystemEvent> eventDic = new HashMap<String, SystemEvent>();

    	SystemEventMaster eventMastCurr;
    	String filePath = path;
    	
    	 try {
             
             FileInputStream inputStream = new FileInputStream(filePath);
             Scanner sc = new Scanner(inputStream,"UTF-8");
             String line="";
             while (sc.hasNextLine()) {
            	 line = sc.nextLine();
                 System.out.println(line);
            	try {
            		eventCurrent = gson.fromJson(line,SystemEvent.class);
            		
            		if(!eventDic.containsKey(eventCurrent.getId()))
            		{
            			eventDic.put(eventCurrent.getId(),eventCurrent);        			
            		}
            		else
            		{
            			eventSec = eventDic.remove(eventCurrent.getId());
            			eventMastCurr = new SystemEventMaster(eventCurrent,eventSec);
            			synchronized (this) 
            			{
            				listMast.add(eventMastCurr);
                            notify(); 
            			}
            		}
            	
            	} catch (JsonSyntaxException | JsonIOException e1) {
            		e1.printStackTrace();
            	}
             }
             
             if(sc.ioException() !=null)
            	 throw sc.ioException();         
             if(inputStream != null)
            	 inputStream.close();
             if(sc !=null)
            	 sc.close();

         } catch (IOException e) {
             e.printStackTrace();
         }
    	
    } 
    
    public void consume() throws InterruptedException 
    { 
    	Connection con = null;
    	Statement stmt = null;
    	ResultSet result = null;
    	int countTable = 0;
    	PreparedStatement insertStmnt;
    	int resultCreateTable;

          
          try {
    	         //Registering the HSQLDB JDBC driver
    	         Class.forName("org.hsqldb.jdbc.JDBCDriver");
    	         //Creating the connection with HSQLDB
    	         con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/eventlogdb", "SA", "");
    	         if (con!= null){
    	            System.out.println("Connection created successfully");
    	            
    	         }else{
    	            System.out.println("Problem with creating connection");
    	         }
    	         
    	         stmt = con.createStatement();
    	         result = stmt.executeQuery("Select COUNT(*) as \"Count\" From INFORMATION_SCHEMA.SYSTEM_TABLES Where TABLE_NAME = 'EVENTLOG'");
    	         if(result.next())
    	         {
    	        	 countTable = result.getInt("Count");
    	        	 
    	         }
    	         
    	         if(countTable==0)
    	         {
    	        	 resultCreateTable= stmt.executeUpdate("CREATE TABLE EventLog ("
    	                 +"id VARCHAR(50) NOT NULL,"
    	                 +"duration DECIMAL NOT NULL,"
    	                 +"type VARCHAR(50)  NULL,"
    	                 +"host INT NULL,"
    	                 +"alert BIT NOT NULL"
    	                 +");");
    	         }
    	         insertStmnt = con.prepareStatement("insert into EVENTLOG values(?,?,?,?,?)");
    	         SystemEventMaster m;
    	         
    	         while (true) 
    	         { 
                     // consumer thread waits while list 
                     // is empty 
          			synchronized (this) 
          			{
          				while (listMast.size()==0) 
          					wait(); 
                    
	                     m = listMast.removeFirst();
	                     notify();
         			}
    	        	 insertStmnt.setString(1,m.getId());
    	        	 insertStmnt.setDouble(2,m.getDuration());
    	        	 insertStmnt.setString(3,m.getType());
    	        	 insertStmnt.setDouble(4,m.getHost());
    	        	 insertStmnt.setBoolean(5,m.getAlert());
    	        	 insertStmnt.executeUpdate();
        	         con.commit();
    	        	 
    	         }

    		         
    		  }  catch (Exception e) {
    		         e.printStackTrace(System.out);
    		  }
    		   
    	}
	
}
