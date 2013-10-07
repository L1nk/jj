package com.wwc.jajing.domain.value;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class AvailabilityTime implements Serializable{

	private static final String TAG = "AvailabilityTime";
	private transient SimpleDateFormat dateFormatterTIME = new SimpleDateFormat("hh:mm a");

	private String availabilityTimeString;
	private transient Calendar cal = Calendar.getInstance();
	
	public AvailabilityTime(String aTime)
	{
		this.availabilityTimeString = aTime;
		
	}
	
	public Long getTime()
	{
		try {
			return this.dateFormatterTIME.parse(this.availabilityTimeString).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getAvailabilityTimeString()
	{
		return this.availabilityTimeString;
	}
	
	public String getAvailabilityTimeStringWithTimeZone()
	{
		String result = this.availabilityTimeString + " " + this.getTimeZoneString();
		return result.substring(0, Math.min(result.length(), 12));
	}
	
	public String getTimeZoneString()
	{
		return this.getTimeZone().getDisplayName(false, TimeZone.SHORT).toString();
	}
	
	public TimeZone getTimeZone()
	{
		
		return TimeZone.getDefault();
		
	}
	
	public boolean isPM()
	{
		try {
			cal.setTime(this.dateFormatterTIME.parse(this.availabilityTimeString));
			if (cal.get(Calendar.AM_PM) == Calendar.PM){
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
