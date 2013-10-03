package com.wwc.jajing.settings.time;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.wwc.jajing.domain.entity.TimeSetting;

public class TimeSettingValidator {
	
	private static final String TAG = "TimeSettingValidator";
	
	public static ArrayList<TimeSetting> getTimeSettingsThisEndTimeInterferesWith(String anEndTime)
	{
		ArrayList<TimeSetting> listOfTimeSettingsThatInterfere = new ArrayList<TimeSetting>();
		//get a list of our current time settings that are on
		ArrayList<TimeSetting> aListOfTimeSettingsThatAreOn = getListOfTimeSettingsThatAreOn();
		for(TimeSetting aTimeSetting : aListOfTimeSettingsThatAreOn)
		{
			Long someEndTime = TimeSetting.getDateOfTimeString(anEndTime).getTime();
			Long startTime = aTimeSetting.getStartTimeAsDate().getTime();
			Long endTime = aTimeSetting.getEndTimeAsDate().getTime();
			
			if( ((startTime < someEndTime) && (someEndTime < endTime))  || (Math.abs(startTime - someEndTime) < 3000) ||( Math.abs(endTime - someEndTime) < 30000)) {
				listOfTimeSettingsThatInterfere.add(aTimeSetting);
				Log.d(TAG, "" + Math.abs(startTime - someEndTime));
				Log.d(TAG, "" + Math.abs(endTime - someEndTime));

				
			}
			
		}
		return listOfTimeSettingsThatInterfere;

	}
	
	

	private static List<TimeSetting> getListOfTimeSettingsThatApplyToday()
	{
		List<TimeSetting> allTimeSetttings = TimeSetting.listAll(TimeSetting.class);
		ArrayList<TimeSetting> aListOfTimeSettingsThatApplyToday = new ArrayList<TimeSetting>();
		for (TimeSetting aTimeSetting : allTimeSetttings) {
			if(aTimeSetting.hasTimeSettingForToday())
			{
				aListOfTimeSettingsThatApplyToday.add(aTimeSetting);
			}
		}
		return aListOfTimeSettingsThatApplyToday;
	}
	
	private static ArrayList<TimeSetting> getListOfTimeSettingsThatAreOn()
	{
		ArrayList<TimeSetting> listOfTimeSettingsThatAreOn = new ArrayList<TimeSetting>();
		
		for (TimeSetting aTimeSetting : getListOfTimeSettingsThatApplyToday())
		{
			if (aTimeSetting.isOn()) {
				listOfTimeSettingsThatAreOn.add(aTimeSetting);
			}
		}
		return listOfTimeSettingsThatAreOn;
	}
}
