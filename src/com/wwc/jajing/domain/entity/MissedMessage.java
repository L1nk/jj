package com.wwc.jajing.domain.entity;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.wwc.jajing.domain.entity.MissedCall.Actions;
import com.wwc.jajing.domain.services.CallManager;
import com.wwc.jajing.domain.value.PhoneNumber;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;
import com.wwc.jajing.util.DateHelper;

/*
 * Represents a missed text message.
 * 
 * TODO- REFACTOR LATER. MISSED CALL AND MISSED MESSAGE HAVE ALMOST IDENTICAL IMPLEMENTATIONS
 */
public class MissedMessage extends SugarRecord{

	@Ignore
	private static final String TAG = "MissedMessage";
	
	@Ignore
	private static CallManager callManager;
	/*
	 * Represent the possible actions caller can take when he decided not to
	 * disturb.
	 */
	@Ignore
	public enum Actions{ MISSED_MESSAGE("MISSED_MESSAGE"), NO_APP("NO_APP");

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
	private String message;
	
	@Ignore
	private PhoneNumber mPhoneNumber;

	public MissedMessage(Context context) {
		super(context);
	}

	public MissedMessage(Context context, PhoneNumber aPhoneNumber, Date aDate,
			Actions action, String aMessage) {
		super(context);

		
		this.phoneNumber = aPhoneNumber.toString();
		this.time = DateHelper.dateToString(aDate);
		this.action = action.toString();
		this.message = aMessage;
	}
	
	public String getMessage() {
		return this.message;
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
		List<MissedMessage> missedMessageList = MissedMessage.find(MissedMessage.class, "phone_number=? and action=?", new String[] {aPhoneNumber.toString(), MissedMessage.Actions.NO_APP.toString()});
		for(MissedMessage mm : missedMessageList) {
			mm.delete();
		}
		deleteNoAppLogsInRecentMessageByPhoneNumber(aPhoneNumber);
		Log.d(TAG, "Deleted 'NO_APP' logs for phone number:" + aPhoneNumber.toString());
	}
	
	private static void deleteNoAppLogsInRecentMessageByPhoneNumber(PhoneNumber aPhoneNumber)
	{
		for(MissedMessage mm : callManager.getRecentMissedMessageLog().getRecentMessages()) {
			if(mm.phoneNumber().toString().equalsIgnoreCase(aPhoneNumber.toString())){
				callManager.getRecentMissedMessageLog().getRecentMessages().remove(mm);
			}
		}
	}

	@Override
	public void save() {
		super.save();
			if(callManager == null){
			this.callManager = (CallManager) JJSystemImpl.getInstance().getSystemService(Services.CALL_MANAGER);
		} 
		//Add it to the recent missed calls list
		callManager.getRecentMissedMessageLog().addRecentMissedMessage(this);
	}


}
