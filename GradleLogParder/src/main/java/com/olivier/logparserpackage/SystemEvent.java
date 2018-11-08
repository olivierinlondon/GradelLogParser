package com.olivier.logparserpackage;

public class SystemEvent {

	private String id;
	private String state;
	private	String type;
	private double host;
	private double timestamp;
	public double getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(double timestamp) {
		this.timestamp = timestamp;
	}
	public double getHost() {
		return host;
	}
	public void setHost(double host) {
		this.host = host;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
