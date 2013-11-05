package com.wwc.jajing.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wwc.jajing.R;
import com.wwc.jajing.domain.entity.TimeSetting;
import com.wwc.jajing.domain.entity.TimeSetting.Days;
import com.wwc.jajing.fragment.EndTimeTimePickerFragment;
import com.wwc.jajing.fragment.StartTimeTimePickerFragment;

public class TimeSettings extends FragmentActivity {

	private static final String TAG = "TimeSettings";
	
	private String startTime;
	private String endTime;
	
	private TextView startTimeDisplay;
	private TextView endTimeDisplay;
	

	
	private CheckBox checkboxRepeat;
	private CheckBox checkboxEveryDay;

	private TableLayout tableDaysOfTheWeek;
	
	
	private HashMap<Days, Boolean> daysToRepeatCollection = new HashMap<Days, Boolean>();
	
	private int idOfCheckboxChecked;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_picker);
		ActionBar actionBar = this.getActionBar();
	    actionBar.setIcon(R.drawable.logo);
	    actionBar.setHomeButtonEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(true);
		
		this.startTimeDisplay = (TextView) findViewById(R.id.textStartDisplay);
		this.endTimeDisplay = (TextView) findViewById(R.id.textEndDisplay);
		this.checkboxRepeat = (CheckBox) findViewById(R.id.checkboxRepeat);
		this.checkboxEveryDay = (CheckBox) findViewById(R.id.checkboxEveryDay);


		
		this.tableDaysOfTheWeek = (TableLayout) findViewById(R.id.tableDaysOfTheWeek);


	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timesettings, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.myTimeSettings:
			this.moveToMyTimeSettings(false);
			return true;	
			
		case android.R.id.home:
			Intent i = new Intent(this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;
	    default: return super.onOptionsItemSelected(item);  

		}

	}
	
	
	public void showTimePickerDialog(View v) {
		
		int id = v.getId();
		switch(id)
		{
			case R.id.buttonStartTime:
			    DialogFragment startfragment = new StartTimeTimePickerFragment();
			    startfragment.show(getSupportFragmentManager(), "timePicker");

				break;
				
			case R.id.buttonEndTime:
				DialogFragment endfragment = new EndTimeTimePickerFragment();
				endfragment.show(getSupportFragmentManager(), "timePicker");
				break;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	

	public void checkboxHandler(View view)
	{
		CheckBox cb = (CheckBox) view;
		int viewId = view.getId();
		switch(viewId) 
		{
			case R.id.checkboxRepeat:
				if(cb.isChecked()) {
					this.tableDaysOfTheWeek.setVisibility(View.VISIBLE);
					//init the days to repeat collection
					this.initDaysToRepeatCollection();

				} else {
					this.tableDaysOfTheWeek.setVisibility(View.GONE);

				}
				this.uncheckExcept(cb);

				
				break;
				
			case R.id.checkboxEveryDay:
				if(cb.isChecked()) {
					this.tableDaysOfTheWeek.setVisibility(View.GONE);

				}
				this.uncheckExcept(cb);
				
				break;
		}
		
		
	}
	
	private void initDaysToRepeatCollection() {
		
		this.daysToRepeatCollection.put(Days.SUNDAY, true);
		this.daysToRepeatCollection.put(Days.MONDAY, true);
		this.daysToRepeatCollection.put(Days.TUESDAY, true);
		
		this.daysToRepeatCollection.put(Days.WEDNESDAY, true);
		this.daysToRepeatCollection.put(Days.THURSDAY, true);
		this.daysToRepeatCollection.put(Days.FRIDAY, true);
		
		this.daysToRepeatCollection.put(Days.SATURDAY, true);
		
		

	}
	
	private void uncheckExcept(View aCheckedCheckboxToKeepChecked)
	{
		CheckBox[] checkBoxes = new CheckBox[] {
				(CheckBox) findViewById(R.id.checkboxRepeat),
				(CheckBox) findViewById(R.id.checkboxEveryDay)};
		
		for(CheckBox aCheckbox : checkBoxes) {
			
			if(aCheckbox.getId() == aCheckedCheckboxToKeepChecked.getId()) {
				if (aCheckbox.isChecked()) {
					Log.d(TAG, "checbox is checked...");
					this.idOfCheckboxChecked = aCheckedCheckboxToKeepChecked.getId();

				} else {
					//we set the id to 99 to singal the user they need to select a frequency
					this.idOfCheckboxChecked = 99;
					Log.d(TAG, "setting id of checkbox to 99");
				}
				continue;
			} else {
				aCheckbox.setChecked(false);
			}
		}
	}
	
	
	public void toggleDayToRepeat(View aView)
	{

		TextView tv = (TextView) aView;
		String abbrev = (String) tv.getText();
		Days aDay = Days.getEnumByAbbrev(abbrev);
		switch(tv.getId()) 
		{
		case R.id.textSun:
			//togle color before toggleing day, because if ui color update
			this.toggleTextViewColorForDays(aView);
			this.toggleDay(aDay);
			break;
		case R.id.textMon:
			this.toggleTextViewColorForDays(aView);
			this.toggleDay(aDay);
			break;
		case R.id.textTu:
			this.toggleTextViewColorForDays(aView);
			this.toggleDay(aDay);
			break;
		case R.id.textWed:
			this.toggleTextViewColorForDays(aView);
			this.toggleDay(aDay);
			break;
		case R.id.textThu:
			this.toggleTextViewColorForDays(aView);
			this.toggleDay(aDay);
			break;
		case R.id.textFri:
			this.toggleTextViewColorForDays(aView);
			this.toggleDay(aDay);
			break;
		case R.id.textSat:
			this.toggleTextViewColorForDays(aView);
			this.toggleDay(aDay);
			break;
		}
		
		Log.d(TAG, "The text:" +  abbrev);

	}

	private void toggleDay(Days aDay) {
		
		if(this.daysToRepeatCollection.get(aDay)) { //if this day is set to repeat, toggle it...
			//toggle state
			this.daysToRepeatCollection.put(aDay, false);
						
		} else {
			//toggle state
			this.daysToRepeatCollection.put(aDay, true);
			
		}
	
		
	}
	
	private void toggleTextViewColorForDays(View aView) {
		TextView tv = (TextView) aView;
		String abbrev = (String) tv.getText();
		Days aDay = Days.getEnumByAbbrev(abbrev);

		
		if(this.daysToRepeatCollection.get(aDay)) { //if this day is set to repeat, toggle it...
			//toggle color
			tv.setTextColor(Color.DKGRAY);
			
		} else {
			//day is not set to repeat, do this...
			tv.setTextColor(Color.parseColor("#ff33b5e5"));
			
		}
		
	}
	
	/*
	 * When we set a start time, we need to make sure its a valid time, and its before the end time
	 */
	public void setStartTime(String aStartTime) {
		if(aStartTime.equalsIgnoreCase(this.endTime)) {
			Toast.makeText(this, "choose a different start time", Toast.LENGTH_SHORT).show();
			return;
		}
		if (this.isEndTimeSet() && !TimeSetting.isValidTimeInterval(aStartTime, this.endTime)) {
			
			Toast.makeText(this, "must be a valid time interval", Toast.LENGTH_SHORT).show();
			return;

		}
	
		this.startTime = aStartTime;
		this.displayStartTimeToUser(this.startTime);
		
	}
	
	private boolean isStartTimeSet() 
	{
		if(this.startTime != null) return true;
		else 
			return false;
	}
	private boolean isEndTimeSet()
	{
		if(this.endTime != null) return true;
		else 
			return false;
	}

	
	public void setEndTime(String anEndTime) {
		if (anEndTime.equalsIgnoreCase(this.startTime)) {
			Toast.makeText(this, "choose a different end time", Toast.LENGTH_SHORT).show();
			return;
		}
		if (this.isStartTimeSet() && !TimeSetting.isValidTimeInterval(this.startTime, anEndTime)) {
			
			Toast.makeText(this, "must be a valid time interval", Toast.LENGTH_SHORT).show();
			return;

		}
		Log.d(TAG, this.endTime + ":" +this.startTime);
		this.endTime = anEndTime;
		this.displayEndTimeToUser(this.endTime);
	}
	
	private void displayStartTimeToUser(String aFormattedTime)
    {
    	startTimeDisplay.setText("start time: " + aFormattedTime);
    }
	
	private void displayEndTimeToUser(String aFormattedTime)
    {

    	endTimeDisplay.setText("end time: " + aFormattedTime);
    }

	
	public void saveTimeSetting(View view) 
	{
		//validate start and end time
		if (!this.isStartTimeSet() || !this.isEndTimeSet()) {
			Toast.makeText(this, "select both a start and end time", Toast.LENGTH_SHORT).show();
			return;
		}
		if (this.idOfCheckboxChecked == R.id.checkboxEveryDay) {
			
			Days[] allDaysOfTheWeek = new Days[] {
					Days.MONDAY, Days.TUESDAY,Days.WEDNESDAY,Days.THURSDAY,Days.FRIDAY,Days.SATURDAY,Days.SUNDAY};
			//create a new time setting
			TimeSetting ts = new TimeSetting(this, startTime, endTime, allDaysOfTheWeek);
			ts.save();
			this.moveToMyTimeSettings(true);
		} else if(this.idOfCheckboxChecked == R.id.checkboxRepeat) {
			//on repeat, do this...
			
			Days[] daysToRepeat = this.getDaysToRepeat();
			//create a new time setting
			TimeSetting ts = new TimeSetting(this, startTime, endTime, daysToRepeat);
			ts.save();
			this.moveToMyTimeSettings(true);
			
		} else {
			Toast.makeText(this, "select a frequency", Toast.LENGTH_SHORT).show();
		}
	}
	
	private Days[] getDaysToRepeat()
	{
		ArrayList<Days> daysList = new ArrayList<Days>();
		for (Map.Entry<Days, Boolean> entry : this.daysToRepeatCollection.entrySet()) {
		    Days aDay = entry.getKey();
		    Boolean shouldRepeat = entry.getValue();
		   
		    if(shouldRepeat) {
		    	daysList.add(aDay);
		    }
		    
		}
		
		return daysList.toArray(new Days[daysList.size()]);
		
	}
	
	/*
	 * To link user to his time settings page we have to know if he added a new time setting, or not.
	 * So we can correctly scroll him to the the time setting he just recently added
	 */
	private void moveToMyTimeSettings(boolean addedTimeSetting)
	{
		Intent i = new Intent(this, MyTimeSettings.class);

		if (addedTimeSetting) {
			i.putExtra("addedTimeSetting", true);
		}
		this.startActivity(i);
	}
	
	
	
	
	
	
	
	
	

	
	

}
