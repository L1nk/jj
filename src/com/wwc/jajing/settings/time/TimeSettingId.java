package com.wwc.jajing.settings.time;

import java.io.Serializable;

public class TimeSettingId implements Serializable{

	private Long Id;
	
	public TimeSettingId(Long aTimeSettingId)
	{
		this.Id = aTimeSettingId;
	}
	
	public Long getId()
	{
		return this.Id;
	}
}
