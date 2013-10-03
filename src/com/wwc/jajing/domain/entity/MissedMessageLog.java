package com.wwc.jajing.domain.entity;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

public class MissedMessageLog {
	
	private static final String TAG ="MissedMessageLog";
	private final ArrayList<MissedMessage> recentMissedMessages = new ArrayList<MissedMessage>();
	private static final MissedMessageLog instance = new MissedMessageLog();
	private Context context = (Context) JJSystemImpl.getInstance().getSystemService(Services.CONTEXT);
	
	private MissedMessageLog(){
		
	}
	
	public static MissedMessageLog getInstance()
	{
		return instance;
	}
	
	public void addRecentMissedMessage(MissedMessage aMissedMessage)
	{
		this.recentMissedMessages.add(aMissedMessage);
		Log.d(TAG, "Added a missed message to  recent missed messages.");
	}
	
	public void clearRecentMissedMessages()
	{
		this.recentMissedMessages.clear();
	}
	
	/*
	 * We deliver these messages when the user becomes available
	 * 
	 */
	public void deliverRecentMissedMessagesToInbox() {
		
		for(MissedMessage mm : this.recentMissedMessages) {
			ContentValues values = new ContentValues();
			values.put("address", mm.phoneNumber().toString());
			values.put("body", mm.getMessage());
			context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
		}
		
	}
	
	public ArrayList<MissedMessage> getRecentMessages()
	{
		return this.recentMissedMessages;
	}
}
