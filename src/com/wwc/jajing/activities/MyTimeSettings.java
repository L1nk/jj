package com.wwc.jajing.activities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wwc.jajing.R;
import com.wwc.jajing.domain.entity.TimeSetting;
import com.wwc.jajing.receivers.TimeSettingReceiver;

public class MyTimeSettings extends Activity {

	private static final String TAG = "MyTimeSettings";

	private List<TimeSetting> aListOfTimeSettings;

	private TableLayout aTimeSettingTabeLayout;
	
	public static final String EXTRA_REFRSH_AND_CLOSE = "refresh_and_close";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_of_time_settings);

		this.aListOfTimeSettings = TimeSetting.listAll(TimeSetting.class);
		Log.d(TAG, "time settings list size:" + aListOfTimeSettings.size());
		this.aTimeSettingTabeLayout = (TableLayout) findViewById(R.id.tableTimeSettingTableLayout);

		this.loadTimeSettings(aListOfTimeSettings);

		// make sure we scroll the user down to the most recently added time
		// setting if they added one
		if (getIntent().getBooleanExtra("addedTimeSetting", false)) {
			this.scrollUserDown();

		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		
		if(this.getIntent().getBooleanExtra(EXTRA_REFRSH_AND_CLOSE, false) == true) {
			this.finish();
		}
		
	}

	private void scrollUserDown() {
		getScrollView().post(new Runnable() {

			@Override
			public void run() {
				getScrollView().fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}

	private ScrollView getScrollView() {
		return (ScrollView) findViewById(R.id.scrollViewMyTimeSettings);
	}

	private void loadTimeSettings(List<TimeSetting> timeSettings) {
		if (timeSettings.size() <= 1) {
			TextView tv = new TextView(this);
			tv.setText("You have not added any time settings at this time.");
			tv.setPadding(10, 10, 10, 10);
			tv.setTextColor(Color.WHITE);
			aTimeSettingTabeLayout.addView(tv);
		}

		for (TimeSetting aTimeSetting : this.aListOfTimeSettings) {
			if(aTimeSetting.getId() != 1) {
				this.createTimeSettingView(aTimeSetting);
			}
			Log.d(TAG, aTimeSetting.getId().toString());
		}
	}

	private void createTimeSettingView(TimeSetting aTimeSetting) {
		// we need a parent table row to encapsulate
		TableLayout aParentTableLayout = new TableLayout(this);
		TableLayout.LayoutParams myTableLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		myTableLayoutParams.setMargins(10, 10, 10, 10);
		aParentTableLayout.setLayoutParams(myTableLayoutParams);
		
		
		aParentTableLayout.setId(Integer.parseInt(aTimeSetting.getId()
				.toString()));
		aParentTableLayout.setBackgroundColor(Color.BLACK);

		// we need a table row
		TableRow aTableRow = new TableRow(this);
		aTableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		// this row has 4 children - 3 text views and a button
		// text view 1 is the start time
		TextView startTimeView = new TextView(this);
		startTimeView.setPadding(10, 10, 10, 10);
		startTimeView.setText(aTimeSetting.getStartTime());
		// TAG*****
		startTimeView.setTag("startTimeView" + aTimeSetting.getId().toString());
		if (aTimeSetting.isOn()) {
			startTimeView.setTextAppearance(this, R.style.onTimeSetting);
		} else {
			startTimeView.setTextAppearance(this, R.style.offTimeSetting);

		}
		// text view 2 is the separator
		TextView separator = new TextView(this);
		separator.setPadding(10, 10, 10, 10);
		separator.setText("-");
		// TAG*****
		separator.setTag("-" + aTimeSetting.getId().toString());
		if (aTimeSetting.isOn()) {
			separator.setTextAppearance(this, R.style.onTimeSetting);
		} else {
			separator.setTextAppearance(this, R.style.offTimeSetting);

		}

		// textviw 3 end time
		TextView endTimeView = new TextView(this);
		endTimeView.setPadding(10, 10, 10, 10);
		endTimeView.setText(aTimeSetting.getEndTime());
		// TAG*****
		endTimeView.setTag("endTimeView" + aTimeSetting.getId().toString());
		if (aTimeSetting.isOn()) {
			endTimeView.setTextAppearance(this, R.style.onTimeSetting);
		} else {
			endTimeView.setTextAppearance(this, R.style.offTimeSetting);

		}

		// empty view to act as a separator
		// add a view to the table layout
		TextView randomText = new TextView(this);
		randomText.setText("hghjgjhgjh");
		randomText.setTextColor(Color.BLACK);


		// toggle button is last
		ToggleButton tb = new ToggleButton(this);
		// TAG*****
		tb.setTag(aTimeSetting.getId());
		tb.setOnCheckedChangeListener(new mToggleButtonListener());
		if (aTimeSetting.isOn()) {
			tb.setTextColor(Color.parseColor("#ff33b5e5"));
			tb.setChecked(true);
		} else {
			tb.setChecked(false);
			tb.setTextColor(Color.BLACK);
		}

		// add these child view first table row
		aTableRow.addView(startTimeView);
		aTableRow.addView(separator);
		aTableRow.addView(endTimeView);
		aTableRow.addView(randomText);
		aTableRow.addView(tb);

		// add a view to the table layout
		View aLine = new View(this);
		aLine.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));
		aLine.setBackgroundColor(Color.parseColor("#444444"));

		// add a second view this will be the days of the week the time setting
		// is ON for
		TextView daysTimeSettingIsOn = new TextView(this);
		daysTimeSettingIsOn.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		daysTimeSettingIsOn.setText(aTimeSetting
				.getDaysOfTheWeekTimeSettingIsActiveFor(aTimeSetting));
		// TAG*****
		daysTimeSettingIsOn.setTag("daysTimeSettingIsOn"
				+ aTimeSetting.getId().toString());
		daysTimeSettingIsOn.setPadding(0, 0, 0, 20);
		if (aTimeSetting.isOn()) {
			daysTimeSettingIsOn.setTextColor(Color.parseColor("#ffffbb33"));
		} else {
			daysTimeSettingIsOn.setTextColor(Color.parseColor("#444444"));
		}
		// append all this to parent table
		aParentTableLayout.addView(aTableRow);
		aParentTableLayout.addView(aLine);
		aParentTableLayout.addView(daysTimeSettingIsOn);

		// appending
		aTimeSettingTabeLayout.addView(aParentTableLayout);
		// aTimeSettingTabeLayout.addView(aLine);
		// aTimeSettingTabeLayout.addView(daysTimeSettingIsOn);

	}

	private class mToggleButtonListener implements
			CompoundButton.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				// The toggle is enabled
				Intent i = new Intent()
						.setAction("com.exmaple.jajingprototype.system.event.TIME_SETTING_ON");
				i.putExtra(TimeSettingReceiver.EXTRA_TIME_SETTING_ID,
						Long.parseLong(buttonView.getTag().toString()));
				sendBroadcast(i);
			} else {
				// The toggle is disabled
				Intent i = new Intent()
						.setAction("com.exmaple.jajingprototype.system.event.TIME_SETTING_OFF");
				i.putExtra(TimeSettingReceiver.EXTRA_TIME_SETTING_ID,
						Long.parseLong(buttonView.getTag().toString()));
				sendBroadcast(i);

			}
			MyTimeSettings.this.toggleTimeSettingView(buttonView, isChecked);

		}

	}

	private void toggleTimeSettingView(CompoundButton buttonView,
			boolean isChecked) {

		String aTimeSettingID = buttonView.getTag().toString();
		TableLayout aTableLayoutWithTimeSettingId = (TableLayout) findViewById(Integer
				.valueOf(aTimeSettingID));
		// change the color of our views
		if (aTableLayoutWithTimeSettingId != null) {
			TextView startTimeView = (TextView) aTableLayoutWithTimeSettingId
					.findViewWithTag("startTimeView" + aTimeSettingID);
			TextView endTimeView = (TextView) aTableLayoutWithTimeSettingId
					.findViewWithTag("endTimeView" + aTimeSettingID);
			TextView separator = (TextView) aTableLayoutWithTimeSettingId
					.findViewWithTag("-" + aTimeSettingID);
			TextView daysTimeSettingIsOnView = (TextView) aTableLayoutWithTimeSettingId
					.findViewWithTag("daysTimeSettingIsOn" + aTimeSettingID);

			if (isChecked) {
				buttonView.setTextColor(Color.parseColor("#ff33b5e5"));

				startTimeView.setTextColor(Color.parseColor("#ffffbb33"));
				endTimeView.setTextColor(Color.parseColor("#ffffbb33"));
				separator.setTextColor(Color.parseColor("#ffffbb33"));
				daysTimeSettingIsOnView.setTextColor(Color.parseColor("#ffffbb33"));
			} else {
				startTimeView.setTextColor(Color.parseColor("#444444"));
				endTimeView.setTextColor(Color.parseColor("#444444"));
				separator.setTextColor(Color.parseColor("#444444"));
				daysTimeSettingIsOnView.setTextColor(Color.parseColor("#444444"));
				buttonView.setTextColor(Color.BLACK);

			}
		}

	}

}
