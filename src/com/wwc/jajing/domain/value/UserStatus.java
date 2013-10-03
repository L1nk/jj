package com.wwc.jajing.domain.value;



public class UserStatus{

	private String availabilityStatus;
	private String availabilityTime;

	
	public UserStatus(String availabilityStatus, String availabilityTime) {
		this.availabilityStatus = availabilityStatus;
		this.availabilityTime = availabilityTime;
	}
	
	public String getAvailabilityStatus() {
		return this.availabilityStatus;
	}

	public String getavailabilityTime() {
		return this.availabilityTime;
	}
}
