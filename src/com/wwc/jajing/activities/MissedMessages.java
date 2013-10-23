package com.wwc.jajing.activities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.wwc.jajing.R;
import com.wwc.jajing.domain.entity.MissedMessage;
import com.wwc.jajing.domain.services.CallManager;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;
import com.wwc.jajing.util.DateHelper;

public class MissedMessages extends Activity {

	private static final String TAG = "MissedMessages";
	private CallManager callManager = (CallManager) JJSystemImpl.getInstance()
			.getSystemService(Services.CALL_MANAGER);
	private TableLayout tl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_missed_messages);

		tl = (TableLayout) findViewById(R.id.tableMissedMessages);
		tl.setShrinkAllColumns(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent intentThatCausedStart = this.getIntent();
		if (this.receivedListOfRecentMessagesFlag()) {
			List<MissedMessage> mmList = this.callManager
					.getRecentMissedMessageLog().getRecentMessages();
			Log.d(TAG, "Populating list with:" + mmList.size()
					+ " number of recent messages");
			this.populateTable(mmList);
			// now clear the recent message list
			this.callManager.getRecentMissedMessageLog()
					.clearRecentMissedMessages();
			Log.d(TAG, "Recent calls cleared from missed call log.");

		} else {
			// populate our table with missed messages
			List<MissedMessage> missedMessages = MissedMessage.find(
					MissedMessage.class, "action=? ",
					new String[] { MissedMessage.Actions.MISSED_MESSAGE
							.toString() }, "", "time", "");
			this.populateTable(missedMessages);
			Log.d(TAG, "Finished populating table");
		}

	}

	private void populateTable(List<MissedMessage> mmList) {
		if (mmList == null) {
			throw new NullPointerException("Missed Message List is NULL");
		}
		if (mmList.size() == 0) {
			TextView tv = new TextView(this);
			tv.setText("You do not have any missed messages at this time.");
			tv.setPadding(10, 10, 10, 10);
			tv.setTextColor(Color.WHITE);
			setContentView(tv);
		}

		for (MissedMessage missedMessage : mmList) {

			// create a table row
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
			TableRow messageRow = new TableRow(this);
			messageRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		

			// create text view for number
			TextView tvNumber = new TextView(this);
			tvNumber.setText(missedMessage.getContactName(this) + " | " + DateHelper.dateToString(missedMessage.occuredOn()));
			tvNumber.setTextColor(Color.WHITE);
			tvNumber.setPadding(10, 10, 10, 10);
			tvNumber.setTextColor(Color.parseColor("#ffffbb33"));
			Log.d(TAG, missedMessage.phoneNumber().toString());

			// create text view for time
			TextView tvTime = new TextView(this);
			tvTime.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			Log.d(TAG, DateHelper.dateToString(missedMessage.occuredOn()));
			tvTime.setText(DateHelper.dateToString(missedMessage.occuredOn()));
			tvTime.setTextColor(Color.WHITE);
			tvTime.setPadding(10, 10, 10, 10);

			// create text view for the message
			TextView tvMessage = new TextView(this);
			Log.d(TAG, missedMessage.getMessage());
			tvMessage.setText(missedMessage.getMessage());
			tvMessage.setTextColor(Color.WHITE);
			tvMessage.setPadding(10, 10, 10, 10);

			// add the view to tthe table row
			tr.addView(tvNumber);
			tr.addView(tvTime);
			// add the view to the table row containg the message
			messageRow.addView(tvMessage);

			// add table row to table layout
			tl.addView(tr);
			tl.addView(messageRow);
		}

	}

	private Boolean receivedListOfRecentMessagesFlag() {
		if (getIntent().getBooleanExtra("recentFlag", false) == true) {
			return true;
		} else {
			return false;
		}
	}

}
