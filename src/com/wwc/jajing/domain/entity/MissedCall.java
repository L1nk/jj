package com.wwc.jajing.domain.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.wwc.jajing.domain.services.CallManager;
import com.wwc.jajing.domain.value.PhoneNumber;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;
import com.wwc.jajing.util.DateHelper;

/*
 * Represents a missed phone call.
 */
public class MissedCall extends SugarRecord implements Call{

	@Ignore
	private static final String TAG = "MissedCall";
	@Ignore
	private static CallManager callManager;
	/*
	 * Represent the possible actions caller can take when he decided not to
	 * disturb.
	 */
	@Ignore
	public enum Actions{
		WILL_CALL_BACK("WILL_CALL_BACK"), HUNG_UP("HUNG_UP"), LEFT_VOICEMAIL(
				"LEFT_VOICEMAIL"), MISSED_CALL("MISSED_CALL"), NO_APP("NO_APP");

		private String name;

		private Actions(String aName) {
			this.name = aName;
		}

		public String getName() {
			String newName  = this.name.replace("_", " ");
			Log.d(TAG, newName);
			return newName;

		}

	};

	private Long Id;
	private String phoneNumber;
	private String time;
	private String action;
	
	@Ignore
	private PhoneNumber mPhoneNumber;

	public MissedCall(Context context) {
		super(context);
	}

	public MissedCall(Context context, PhoneNumber aPhoneNumber, Date aDate,
			Actions action) {
		super(context);

		
		this.phoneNumber = aPhoneNumber.toString();
		this.time = DateHelper.dateToString(aDate);
		this.action = action.toString();
	}
	
	public Long getId() {
		return this.Id;
	}

	public PhoneNumber phoneNumber() {
		if(this.phoneNumber == null) {
			throw new NullPointerException("Phone number cannnot be null");
		}
		return new PhoneNumber(this.phoneNumber);
	}

	public Date occuredOn() {

		return DateHelper.StringToDate(this.time);
	}

	public Actions getActionTakenByCaller() {
		return Actions.valueOf(this.action);
	}
	
	public static void delteNoAppLogsByPhoneNumber(PhoneNumber aPhoneNumber)
	{
		List<MissedCall> missedCallList = MissedCall.find(MissedCall.class, "phone_number=? and action=?", new String[] {aPhoneNumber.toString(), MissedCall.Actions.NO_APP.toString()});
		for(MissedCall mc : missedCallList) {
			mc.delete();
		}
		deleteNoAppLogsInRecentCallsByPhoneNumber(aPhoneNumber);
		Log.d(TAG, "Deleted 'NO_APP' logs for phone number:" + aPhoneNumber.toString());
	}
	
	private static void deleteNoAppLogsInRecentCallsByPhoneNumber(PhoneNumber aPhoneNumber)
	{
	
		callManager = (CallManager) JJSystemImpl.getInstance().getSystemService(Services.CALL_MANAGER);
		if (callManager == null ) {
			return;
		}
		//TODO - BUG HERE CRASHED AT THIS LINE concurrent modification exception. BEWARE!!!
		ArrayList<MissedCall> missedCalls = callManager.getRecentMissedCallLog().getRecentCalls();
		for(MissedCall mc : missedCalls) {
			if(mc.phoneNumber().toString().equalsIgnoreCase(aPhoneNumber.toString())){
				callManager.getRecentMissedCallLog().getRecentCalls().remove(mc);
			}
		}
	}

	@Override
	public void save() {
		super.save();
		
		if(this.callManager == null) {
			this.callManager = (CallManager) JJSystemImpl.getInstance().getSystemService(Services.CALL_MANAGER);
		}
		//Add it to the recent missed calls list
		this.callManager.getRecentMissedCallLog().addRecentMissedCall(this);
		Log.d(TAG, "Missed Call Logged.");

	}


}
