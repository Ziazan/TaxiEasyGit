package com.example.bean;

public class Taxi {
	private Driver driver=null;//司机
	private Double Latitude=null;//经度
	private Double Longitude=null;//纬度
	private int distance=0;//与我的距离
	private int time=0;//时刻
	
	public Double getLatitude() {
		return Latitude;
	}
	public void setLatitude(Double latitude) {
		this.Latitude = latitude;
	}
	public Double getLongitude() {
		return Longitude;
	}
	public void setLongitude(Double Longitude) {
		this.Longitude = Longitude;
	}
	
	public Driver getDriver() {
		return driver;
	}
	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
}
