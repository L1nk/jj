package com.wwc.jajing.activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.wwc.jajing.R;

public class MissedLog extends TabActivity {

	
	TabHost mTabHost;
	TextView tvCallLogTitle;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitiy_missed_log);
		ActionBar actionBar = this.getActionBar();
	    actionBar.setIcon(R.drawable.logo);
	    actionBar.setHomeButtonEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(true);

		mTabHost = this.getTabHost();
		tvCallLogTitle = (TextView) findViewById(R.id.textLogTitle);


		// Tab for Missed Calls
		TabSpec missedCallsSpec = mTabHost.newTabSpec("Missed Calls");
		Intent missedCallsIntent = new Intent().setClass(this,
				MissedCalls.class);
		missedCallsSpec.setContent(missedCallsIntent);
		missedCallsSpec.setIndicator(this.getTabView("Missed Calls", "#ffffbb33"));
		
		if(this.receivedListOfRecentCallsFlag()) {
			this.setTitleOfActivity("Activity While Unavailable");
			missedCallsIntent.putExtra("recentFlag",true);
		}
		
		// Tab for Missed Messages
		TabSpec missedMessagesSpec = mTabHost.newTabSpec("Missed Messages");
		Intent missedMessagesIntent = new Intent().setClass(this,MissedMessages.class);
		missedMessagesSpec.setContent(missedMessagesIntent);
		missedMessagesSpec.setIndicator(this.getTabView("Missed Messages", null));
		
		if(this.receivedListOfRecentCallsFlag()) {
			this.setTitleOfActivity("Activity While Unavailable");
			missedMessagesIntent.putExtra("recentFlag",true);
		}

		mTabHost.addTab(missedCallsSpec);
		mTabHost.addTab(missedMessagesSpec);
		mTabHost.setCurrentTab(0);
		
		mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
			  @Override
			  public void onTabChanged(String tabId) {
			    int tab = mTabHost.getCurrentTab();
			    mTabHost.getTabWidget().getChildAt(tab).setBackgroundResource(R.xml.tabbg);
			    mTabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.xml.tabbg);

			  }
			});

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		switch(id)
		{
		case android.R.id.home:
			Intent i = new Intent(this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;
	    default: return super.onOptionsItemSelected(item);  

			
		}
	}
	
	private Boolean receivedListOfRecentCallsFlag()
	{
		if(getIntent().getBooleanExtra("recentFlag", false) == true)
		{
			return true;
		} else {
			return false;
		}
	}
	
	private Boolean receivedListOfRecentMessagesFlag()
	{
		if(getIntent().getBooleanExtra("recentFlag", false) == true)
		{
			return true;
		} else {
			return false;
		}
	}
	
	private void setTitleOfActivity(String attile)
	{
		//set the title to recent history
		tvCallLogTitle.setText(attile);
	}
	
	private TextView getTabView(String aLabel, String aColorHex)
	{
		TextView tv = new TextView(this);
		tv.setText(aLabel);
		tv.setTextColor(Color.WHITE);
		tv.setPadding(10, 10, 10, 10);
		tv.setGravity(Gravity.CENTER);
		if(aColorHex != null) {
			tv.setBackgroundColor(Color.parseColor(aColorHex));
		} else {
			tv.setBackgroundColor(Color.parseColor("#ff33b5e5"));
		}
		return tv;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		this.finish();
	}

}
