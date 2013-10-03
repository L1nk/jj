package com.wwc.jajing.domain.entity;

import java.util.Date;

import com.wwc.jajing.domain.value.PhoneNumber;


public interface Call extends Entity {

	public Date occuredOn();
	public PhoneNumber phoneNumber();
}
