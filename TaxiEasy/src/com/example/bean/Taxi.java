package com.example.bean;

public class Taxi {
	private Driver driver=null;//˾��
	private Double Latitude=null;//����
	private Double Longitude=null;//γ��
	private int distance=0;//���ҵľ���
	private int time=0;//ʱ��
	
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
