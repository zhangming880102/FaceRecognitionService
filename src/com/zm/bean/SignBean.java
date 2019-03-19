package com.zm.bean;

import java.util.Date;

public class SignBean {
	int id;
	int user_id;
	Date create_time;
	String address;
	double latitude;
	double longtitude;
	int img_sign;
	int address_sign;
	int sign_status;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	
	public double getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}
	public int getImg_sign() {
		return img_sign;
	}
	public void setImg_sign(int img_sign) {
		this.img_sign = img_sign;
	}
	public int getAddress_sign() {
		return address_sign;
	}
	public void setAddress_sign(int address_sign) {
		this.address_sign = address_sign;
	}
	public int getSign_status() {
		return sign_status;
	}
	public void setSign_status(int sign_status) {
		this.sign_status = sign_status;
	}
	
	
}
