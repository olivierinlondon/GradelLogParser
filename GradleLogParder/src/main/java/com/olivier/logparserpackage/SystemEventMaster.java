package com.olivier.logparserpackage;

public class SystemEventMaster {
	
	private SystemEvent eventStart;
	private	SystemEvent eventEnd;
	
	SystemEventMaster(SystemEvent evOne,SystemEvent evTwo)
	{
		if(evOne.getState().equals("STARTED") && evTwo.getState().equals("FINISHED"))
		{
			eventStart=	evOne;
			eventEnd = evTwo;
		}else
		{
			eventStart=evTwo;
			eventEnd=evOne;
		}
		
	}
	
	public boolean getAlert() {
		return this.getDuration()>4;
	}
	
	public double getDuration() {
		return eventEnd.getTimestamp()-eventStart.getTimestamp();
	}
	public double getHost() {
		return  eventStart.getHost();
	}
	public String getType() {
		return eventStart.getType();
	}
	public String getId() {
		return eventStart.getId();
	}


}
