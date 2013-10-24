package com.wwc.jajing.domain.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class TimeSetting extends SugarRecord implements Entity{

	/*
	 * sun, mon, tue, wed, thur, fri, sat
	 * 1, 2,3,4,5, 6, 7 // calendar ints -- 
	 * 0, 1, 2, 3 , 4, 5, 6 // ordinal ints --  
	 */
	@Ignore
	public enum Days {
		  SUNDAY("SUN"), MONDAY("MON"), TUESDAY("TUE"), WEDNESDAY("WED"), THURSDAY("THU"), FRIDAY("FRI"),SATURDAY("SAT");
		
		private String abbrev;
		
		Days(String abbrev) {
			this.abbrev = abbrev;
		}
		private String getAbbrev() {
			return this.abbrev;
		}
		
		public static Days getEnumByAbbrev(String abbrev) {
			
			for(Days aDay : Days.values()) {
				if (abbrev.equalsIgnoreCase(aDay.getAbbrev())) {
					return aDay;
				}
			}
			return null;
		
		}
	}
	
	
	@Ignore
	private static final String TAG = "TimeSetting";
	@Ignore
	private Days[] days;
	
	@Ignore 
	private Calendar today = Calendar.getInstance();
	
	private Long id;
	private int isOn; 
	private String startTime;
	private String endTime;
	private String daysStr = "";
	
	@Ignore
	public static SimpleDateFormat dateFormatterTIME = new SimpleDateFormat("hh:mm a");
	@Ignore
	public static SimpleDateFormat dateFormatterTIME_SECONDS = new SimpleDateFormat("hh:mm:ss a");
	
	
	public Long getId() {
		return this.id;
	}
	
	public TimeSetting(Context context) {
		super(context);
		
	}
	
	public TimeSetting(Context context, String startTime, String endTime, Days[] days) {
		super(context);
		
		this.startTime = startTime;
		this.endTime = endTime;
		this.days = days;
	
	}
	
	public String getStartTime()
	{
		return this.startTime;
	}
	
	public String getEndTime()
	{
		return this.endTime;

	}
	
	public void setStartTime(String aStartTime)
	{
		this.startTime = aStartTime;
	}
	
	public void setEndTime(String anEndTime)
	{
		this.endTime = anEndTime;
	}
	
	
	private void loadDays() {
		String[] ordinals = this.daysStr.split(",");
		ArrayList<Days> daysList = new ArrayList<Days>();
		for (String ordinal: ordinals) {
			if (ordinal != "" && ordinal != null) {
				Days aDay = Days.values()[Integer.parseInt(ordinal)];
				daysList.add(aDay);
			}
		}
		
		this.days = daysList.toArray(new Days[daysList.size()]);
	}
	
	
	
	public boolean hasTimeSettingForToday() {
		if (this.days == null) 	this.loadDays();
		
		return Arrays.asList(days).contains(Days.values()[this.getTodaysOrdinal()]);
	}
	
	
	private int getTodaysOrdinal() {
		
		Log.d(TAG, "Toadays ordinal:" + this.convertCalendarToOrdinal(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
		return (this.convertCalendarToOrdinal(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
		
	}
	
	
	private int convertOrdinalToCalendar(int anOrdinal)
	{
		return anOrdinal + 1;
	}
	
	private int convertCalendarToOrdinal(int aCalendarDay) {
		return aCalendarDay - 1;

	}

	
	//toggles active state to on
	public void on(){
		if (this.days == null) 	this.loadDays();

		this.isOn = 1;
	}
	
	//toggles active state to off
	public void off() {
		if (this.days == null) 	this.loadDays();

		this.isOn = 0;
	}
	

	@Override
	public void save() {
		if (this.days == null) 	this.loadDays();
		//before calling save make sure we set the daysStr
		this.daysStr = ""; //clear the current daysStr
		for (Days aDay : this.days) {
			if (aDay != null) {
				this.daysStr += aDay.ordinal() + ",";
			}
		}
		
		super.save();
		
		
	}
	
	
	public boolean isOn() {
		if (this.days == null) 	this.loadDays();

		return (this.isOn == 1)? true:false;
		
	}
	
	
	
	/*
	 * This formats the days of the week the time setting is active for
	 * in a human readable format
	 */
	public String getDaysOfTheWeekTimeSettingIsActiveFor(TimeSetting aTimeSetting) {
		if (this.days == null) 	this.loadDays();

		String result = "";
		for (Days aDay : this.days ) {
			String temp = (aDay.getAbbrev() + " | ");
			result += temp;
		}
		return result;
	}
	
	public static boolean isValidTimeInterval(String aStartTime, String anEndTime)
	{
		if(aStartTime == null || anEndTime == null ) {
			return false;
		}
		//end time
		try {

            System.out.println(aStartTime);
            System.out.println(anEndTime);

			Date endTime = dateFormatterTIME.parse(anEndTime);
			Date startTime = dateFormatterTIME.parse(aStartTime);
			
			if (endTime.after(startTime)) {
				return true;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean hasEndTimePassed()
	{
		return hasEndTimePassed(this.endTime);
		
	}
	
	public static boolean hasEndTimePassed(String anEndTime)
	{
		try {
			Date now = dateFormatterTIME.parse(dateFormatterTIME.format((new Date() ))) ;
			Date endTime = dateFormatterTIME.parse(anEndTime);
			if (now.after(endTime)) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean hasStartTimePassed()
	{
		try {
			Date now = dateFormatterTIME.parse(dateFormatterTIME.format((new Date() ))) ;
			Date startTime = dateFormatterTIME.parse(this.startTime);
			if (now.after(startTime)){
				return true;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return false;
	}

	public Date getStartTimeAsDate()
	{
		try {
			Date startTime = dateFormatterTIME.parse(this.startTime);
			return startTime;

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Date getEndTimeAsDate()
	{
		try {
			Date endTime = dateFormatterTIME.parse(this.endTime);
			return endTime;

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date getTimeNow()
	{
		try {
			return dateFormatterTIME_SECONDS.parse((dateFormatterTIME_SECONDS.format(new Date())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date getDateOfTimeString(String aTimeString)
	{
		try {
			Date someTime = dateFormatterTIME.parse(aTimeString);
			return someTime;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public static boolean isEndTimeInFuture(String endTime)
	{
		Long endTimeMilli = TimeSetting.getDateOfTimeString(endTime).getTime();
		Long nowMilli = TimeSetting.getTimeNow().getTime();
		if(endTimeMilli > nowMilli) {
			return true;
		} else {
			return true;
		}
	}

	

}
