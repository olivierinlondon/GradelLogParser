package com.olivier.logparserpackage;

public class LogParser {
	
	public static void main(String args[])throws InterruptedException 
	{
        // Object of a class that has both produce() 
        // and consume() methods 
        final LogeParserThread lpt = new LogeParserThread(); 
  
        // Create producer thread 
        Thread t1 = new Thread(new Runnable() 
        { 
            @Override
            public void run() 
            { 
                try
                { 
                    lpt.produce(args[0]); 
                } 
                catch(InterruptedException e) 
                { 
                    e.printStackTrace(); 
                } 
            } 
        }); 
  
        // Create consumer thread 
        Thread t2 = new Thread(new Runnable() 
        { 
            @Override
            public void run() 
            { 
                try
                { 
                    lpt.consume(); 
                } 
                catch(InterruptedException e) 
                { 
                    e.printStackTrace(); 
                } 
            } 
        }); 
  
        t1.start(); 
        t2.start(); 
  
        t1.join(); 
        t2.join(); 

	}
	

}
