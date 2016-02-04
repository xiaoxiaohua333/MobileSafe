package com.hj.mobilesafe.domain;

public class SmsInfo {
	private String body;
	private String address;
	private int type;
	private String date;
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "SmsInfo [body=" + body + ", address=" + address + ", type="
				+ type + ", date=" + date + "]";
	}
	
	

}
