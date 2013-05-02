package com.pratik.hciproject.db;

import java.util.Date;

public class CallRecord {
	private  String id ;
	private  String contact_name;
	private  String phone_number;
	private  Date datetime;
	private  String call_duration;
	private  String latitude ;
	private  String longitude;
	private  String type;
	private  int time_diff;
	private  long location_diff;
	private String contact_id;
	
	
	
	public int getTime_diff() {
		return time_diff;
	}
	public void setTime_diff(int time_diff) {
		this.time_diff = time_diff;
	}
	public long getLocation_diff() {
		return location_diff;
	}
	public void setLocation_diff(float result) {
		this.location_diff = (long)result;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContact_name() {
		return contact_name;
	}
	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}
	public String getPhone_number() {
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date callDayTime) {
		this.datetime = callDayTime;
	}
	public String getCall_duration() {
		return call_duration;
	}
	public void setCall_duration(String call_duration) {
		this.call_duration = call_duration;
	}
	
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContact_id() {
		return contact_id;
	}
	public void setContact_id(String contact_id) {
		this.contact_id = contact_id;
	}
	
	
}
