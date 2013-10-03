package com.wwc.jajing.sms;

public interface JJSMSResponseDispatcher {
	
	public void handleResponse(JJSMS aJJSMSResponse);
	public void registerListener(String requestId, JJSMSResponseListener aJJSMSResponseListner);
	public void unregisterListener(String requestId);
}
