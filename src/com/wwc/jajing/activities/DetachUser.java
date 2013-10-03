package com.wwc.jajing.activities;

import org.json.JSONException;
import org.json.JSONObject;

import com.wwc.jajing.activities.ApplicationEnums.UserStatus;

/**
 * A simple cloud pojo for user contact lists
 * 
 * @author nagendhiran
 * 
 * FOR DESSS TECHNOLOGIES
 * 
 */
public class DetachUser {

	private String m_name;

	private String m_phoneno;

	private String m_statusmessage;

	private long m_lastUpdateTime;

	private String m_lastUpdated;

	private UserStatus m_statusId;

	private String m_contactImage;

	private long m_localContactId;

	private boolean m_isDetachAvailable;
	
	private String m_timeRemaining ;

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	public String getPhoneno() {
		return m_phoneno;
	}

	public void setPhoneno(String phoneno) {
		this.m_phoneno = phoneno;
	}

	public String getStatusmessage() {
		return m_statusmessage;
	}

	public void setStatusmessage(String statusmessage) {
		this.m_statusmessage = statusmessage;
	}

	public long getLastUpdateTime() {
		return m_lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.m_lastUpdateTime = lastUpdateTime;
	}

	public String getLastUpdated() {
		return m_lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.m_lastUpdated = lastUpdated;
	}

	public UserStatus getStatusId() {
		return m_statusId;
	}

	public void setStatusId(UserStatus statusId) {
		this.m_statusId = statusId;
	}

	public String getContactImage() {
		return m_contactImage;
	}

	public void setContactImage(String contactImage) {
		this.m_contactImage = contactImage;
	}

	public void setLocalContactId(long id) {
		this.m_localContactId = id;
	}

	public long getLocalContactId() {
		return this.m_localContactId;
	}

	@Override
	public int hashCode() {
		return Integer.parseInt( m_phoneno ) ;
	}

	public boolean isDetachAvailable() {
		return m_isDetachAvailable;
	}

	public void setDetachAvailablity(boolean available) {
		this.m_isDetachAvailable = available;
	}
	
	public JSONObject toJsonObject() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("name", this.m_name );
		json.put("phone_number", this.m_phoneno );
		json.put("status", this.m_statusmessage );
		json.put("id", this.m_localContactId );
		json.put("is_available", Boolean.toString(m_isDetachAvailable) );
		return json ;
	}
	
	public String getTimeRemainingAsString() {
		return m_timeRemaining ;
	}
	
	public void setTimeRemaining(String timeRemaining) {
		this.m_timeRemaining = timeRemaining ;
	}
}
