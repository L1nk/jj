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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wwc.jajing.R;
import com.wwc.jajing.domain.entity.TimeSetting;
import com.wwc.jajing.domain.entity.TimeSetting.Days;
import com.wwc.jajing.fragment.EndTimeTimePickerFragment;
import com.wwc.jajing.fragment.StartTimeTimePickerFragment;

import org.joda.time.DateTime;

public class TimeSettings extends FragmentActivity {

	private static final String TAG = "TimeSettings";
	
	private String startTime;
	private String endTime;

	private TableLayout tableDaysOfTheWeek;
	private Button startTimeButton;
    private Button endTimeButton;
    private EditText scheduledStatus;
	
	private HashMap<Days, Boolean> daysToRepeatCollection = new HashMap<Days, Boolean>();

	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_picker);
		ActionBar actionBar = this.getActionBar();
	    actionBar.setIcon(R.drawable.logo);
	    actionBar.setHomeButtonEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(true);


        startTimeButton = (Button) findViewById(R.id.buttonStartTime);
        endTimeButton = (Button) findViewById(R.id.buttonEndTime);
        scheduledStatus = (EditText) findViewById(R.id.scheduledStatus);


        this.initDaysToRepeatCollection();
		
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

	
	private void initDaysToRepeatCollection() {
		
		this.daysToRepeatCollection.put(Days.SUNDAY, false);
		this.daysToRepeatCollection.put(Days.MONDAY, false);
		this.daysToRepeatCollection.put(Days.TUESDAY, false);
		
		this.daysToRepeatCollection.put(Days.WEDNESDAY, false);
		this.daysToRepeatCollection.put(Days.THURSDAY, false);
		this.daysToRepeatCollection.put(Days.FRIDAY, false);
		
		this.daysToRepeatCollection.put(Days.SATURDAY, false);
		
		

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
			this.toggleDay(aDay);
            this.toggleTextViewColorForDays(aView);
			break;
		case R.id.textMon:
			this.toggleDay(aDay);
            this.toggleTextViewColorForDays(aView);
			break;
		case R.id.textTu:
			this.toggleDay(aDay);
            this.toggleTextViewColorForDays(aView);
			break;
		case R.id.textWed:
			this.toggleDay(aDay);
            this.toggleTextViewColorForDays(aView);
			break;
		case R.id.textThu:
			this.toggleDay(aDay);
            this.toggleTextViewColorForDays(aView);
			break;
		case R.id.textFri:
			this.toggleDay(aDay);
            this.toggleTextViewColorForDays(aView);
			break;
		case R.id.textSat:
			this.toggleDay(aDay);
            this.toggleTextViewColorForDays(aView);
			break;
		}
		
		Log.d(TAG, "The text:" +  abbrev);

	}

	private void toggleDay(Days aDay) {
		
		if(this.daysToRepeatCollection.get(aDay)) { //if this day is set to repeat, toggle it...

            System.out.println("Day False: " + aDay);

			this.daysToRepeatCollection.put(aDay, false);
						
		} else {
			//toggle state

            System.out.println("Day True: " + aDay);
			this.daysToRepeatCollection.put(aDay, true);
			
		}
	
		
	}
	
	private void toggleTextViewColorForDays(View aView) {
		TextView tv = (TextView) aView;
		String abbrev = (String) tv.getText();
		Days aDay = Days.getEnumByAbbrev(abbrev);

		
		if(this.daysToRepeatCollection.get(aDay)) { //if this day is set to repeat, toggle it...
			//toggle color
			tv.setTextColor(Color.parseColor("#E78A62"));
			
		} else {
			//day is not set to repeat, do this...
			tv.setTextColor(Color.parseColor("#ffffff"));
			
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
    	startTimeButton.setText("Detach at: " + aFormattedTime);
    }
	
	private void displayEndTimeToUser(String aFormattedTime)
    {

    	endTimeButton.setText("end time: " + aFormattedTime);
    }

	
	public void saveTimeSetting(View view) 
	{
		//validate start and end time
		if (!this.isStartTimeSet() || !this.isEndTimeSet()) {
			Toast.makeText(this, "select both a start and end time", Toast.LENGTH_SHORT).show();
			return;
		}

        Days[] daysToRepeat = this.getDaysToRepeat();

        if(daysToRepeat.length == 0) {
            Toast.makeText(this, "Select at least one day.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            //create a new time setting
            String status = scheduledStatus.getText().toString();
            TimeSetting ts = new TimeSetting(this, startTime, endTime, daysToRepeat, status);
            ts.save();
            this.moveToMyTimeSettings(true);
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
