package com.wwc.jajing.domain.services;

import com.wwc.jajing.domain.entity.MissedCallLog;
import com.wwc.jajing.domain.entity.MissedMessageLog;

public abstract class CallManagerAbstract{
	
	public abstract void sendCallToVoicemail();
	public abstract void silenceRinger(Boolean bool);
	public abstract void disconnectCall();
	
	public abstract MissedCallLog getRecentMissedCallLog();
	public abstract MissedMessageLog getRecentMissedMessageLog();
}
