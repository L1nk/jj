package com.wwc.jajing.sms;

import java.util.HashMap;

/*
 * Dispatches the JJSMS response object to get handled by the associated JJSMSListener
 */
public class JJSMSResponseDispatcherImpl implements JJSMSResponseDispatcher{

	//keep a Map of all the registered listeners
	private HashMap<String, JJSMSResponseListener> listeners;
	
	public JJSMSResponseDispatcherImpl()
	{
		this.listeners = new HashMap<String, JJSMSResponseListener>();
	}
	
	
	/*
	 * Delegates handling to the listener that was registered to handle response
	 * 
	 */
	@Override
	public void handleResponse(JJSMS aJJSMSResponse) {
		if(!aJJSMSResponse.isResponse()) throw new IllegalArgumentException("JJSMS must be a response object.");
		//response contains the request id so find the listener that was registered with that id
		
		//get the requestId from the response
		String jjsmsResponseRequestId = (String) aJJSMSResponse.getFromExtras("REQUESTID");
		//find the associated listener
		JJSMSResponseListener listener = this.listeners.get(jjsmsResponseRequestId);
		listener.handleJJSSMSResponse(aJJSMSResponse);
	
	}

	@Override
	public void registerListener(String requestId,
			JJSMSResponseListener aJJSMSResponseListner) {
		
		this.listeners.put(requestId, aJJSMSResponseListner);
		
	}
	
	public void unregisterListener(String requestId)
	{
		this.listeners.remove(requestId);
	}
	
	

}
