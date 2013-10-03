package com.wwc.jajing.sms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.wwc.jajing.sms.command.JJCommandFactoryImpl.Commands;

/*
 * Represnts JJSMS message.
 * 
 * A JJSMS contains a description of the command, a Hashmap of Data sent through ?key1=val1,key2=val2 ...
 * and a reference to the Sender.
 * 
 * JJSMS is a REQUEST object by default
 * 
 */
public class JJSMS implements Serializable {

	public static final String TAG = "JJSMS";
	
	public static final String INITIAL_MESSAGE = "#detach/The person you are trying to reach is ";

	final private String jjsmsStr;
	final private JJSMSSenderPhoneNumber sendersPhoneNumber;
	private boolean isResponse;
	
	private HashMap<String, Object> extras = null;

	public JJSMS(String jjsmsStr) {
		this.jjsmsStr = jjsmsStr;
		this.sendersPhoneNumber = null;

		this.initExtras();
		Log.d(TAG, extras.toString());
		

	}
	
	public String getRawJJSMS()
	{
		return this.jjsmsStr;
	}
	
	public Boolean isResponse()
	{
		if(this.extras.containsKey("ISRESPONSE")) {
			this.isResponse = (Boolean) Boolean.parseBoolean((String) this.extras.get("ISRESPONSE"));
			Log.d(TAG, "The JJSMS extras does contain the key isResponse. The isResponse field of the JJSMS is:" + this.isResponse);
		}
		else {
			this.isResponse = false;
			Log.d(TAG, "The JJSMS extras does not contain the key isResponse. The isResponse feld is set to false.");
		}
		
		return this.isResponse;
	}
	
	public JJSMS(String jjsmsStr, JJSMSSenderPhoneNumber phoneNumber) {
		this.jjsmsStr = jjsmsStr;
		this.sendersPhoneNumber = phoneNumber;

		Log.d(TAG, "JJSMS CONSTRUCTOR");
		this.initExtras();
		Log.d(TAG, extras.toString());

	}

	public Commands getCommand() {

		for (Commands command : Commands.values()) {
			if (this.jjsmsStr.toUpperCase().contains(command.toString())) {
				return command;
			}
		}

		return Commands.UNKNOWN_COMMAND;

	}
	
	public JJSMSSenderPhoneNumber getSendersPhoneNumber()
	{
		return this.sendersPhoneNumber;
	}

	public void addToExtras(String key, Object val) {
		this.extras.put(key, val);

	}

	public Object getFromExtras(String key) {
		if (this.extras.get(key) != null)
			return (String) this.extras.get(key);
		else
			return "UNKNOWN_EXTRA";
	}

	private void initExtras() {
		// check to see if rawjjSMSstr contains extras
		if (this.hasExtraParams(this.jjsmsStr)) {
			try {
				HashMap<String, Object> extras = new HashMap<String, Object>();
				// get the "extras" poriton of the rawjjSMSstr
				String extraParams = this.getExtrasStr(this.jjsmsStr);
				Log.d(TAG, "raw jjsmsStr has extras: " + extraParams);

				String[] keyValue = extraParams.split(",");

				for (String key_value : keyValue) {
					String[] temp = key_value.split("=");

					extras.put(temp[0].toUpperCase(), temp[1].toUpperCase());
				}
				this.extras = extras;
			} catch (ArrayIndexOutOfBoundsException ex) {
				Log.d(TAG,
						"The app failed to extract the data portion of jjsms string.");

				this.extras = new HashMap<String, Object>();
			}
		} else {
			// set an empty hashmap
			this.extras = new HashMap<String, Object>();
		}

	}

	/*
	 * If jjsms string contains a '?', we assume that the app wants to pass
	 * extra data.
	 * 
	 * TODO - Improve algorithm. Very inefficient....
	 */

	private Boolean hasExtraParams(String rawjjSMSstr) {

		if (rawjjSMSstr.contains("?"))
			return true;
		else
			return false;

	}

	private String getExtrasStr(String rawjjSMSstr) {
		String extraStr = rawjjSMSstr.substring(rawjjSMSstr.indexOf('?') + 1,
				rawjjSMSstr.length());
		return extraStr;
	}

	@Override
	public String toString() {
		Log.d(TAG, extras.toString());
		Log.d(TAG, "The converted extras are:" + convertExtrasToString());
		return "#detach/" + this.getCommand().toString() + "?"
				+ convertExtrasToString();

	}

	/*
	 * This should convert our hashmap to "key1=val1,key2=val2" ...
	 */
	private String convertExtrasToString() {
		String extrasStr = "";
		for (Map.Entry<String, Object> entry : extras.entrySet()) {
			extrasStr += (entry.getKey() + "=" + entry.getValue() + ",");
		}
		return extrasStr;
	}

}
