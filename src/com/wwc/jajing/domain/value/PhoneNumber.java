package com.wwc.jajing.domain.value;

import java.io.Serializable;

public class PhoneNumber implements Serializable{

	protected String phoneNumber;

	public PhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return this.phoneNumber.replace("+", "");
	}
}
