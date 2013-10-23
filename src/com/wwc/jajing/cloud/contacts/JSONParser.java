package com.wwc.jajing.cloud.contacts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wwc.jajing.activities.ApplicationEnums.UserStatus;
import com.wwc.jajing.activities.DetachUser;
import com.wwc.jajing.util.AppLogger;

/**
 * 
 * @author nagendhiran
 * 
 */
public class JSONParser {

	static InputStream inputStream = null;
	static JSONObject jObj = null;
	static String json = "";
	static JSONParser PARSER = new JSONParser();
	
	private JSONParser() {
		
	}
	
	public static JSONParser getParser() {
		if( PARSER == null ) {
			PARSER = new JSONParser() ;
		}
		return PARSER;
	}

	// JSON Node names
	private static final String TAG_CONTACTS = "contacts";
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_EMAIL = "email";
	private static final String TAG_ADDRESS = "address";
	private static final String TAG_PHONE = "phone_number";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_STATUS = "status";
	private static final String TAG_DETACH_AVAILABLILTY = "is_available";
	private static final String TAG_STATUS_START = "status_start" ;
	private static final String TAG_STATUS_END  = "status_end" ;


	public JSONObject getJSONFromUrl(String url) {
		JSONObject returnObject = new JSONObject();
		JSONArray jsonArray = null ;
		InputStream inputStream = null;
		String jsonContent = null ;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			inputStream = httpEntity.getContent();
		} catch (UnsupportedEncodingException e) {
			AppLogger.error(String.format("JSON Parser",
					"Unsupported Encoding " + e.toString()));
		} catch (ClientProtocolException e) {
			AppLogger.error(String.format("JSON Parser",
					"Client Protocol Exception " + e.toString()));
		} catch (IOException e) {
			AppLogger.error(String.format("JSON Parser",
					"IO Exception " + e.toString()));
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			inputStream.close();
			jsonContent = sb.toString();
		} catch (Exception e) {
			AppLogger.error(String.format("Buffer Error",
					"Error converting result " + e.toString()));
		}

		// try parse the string to a JSON object
		try {
			jsonArray = new JSONArray(jsonContent);
		} catch (JSONException e) {
			AppLogger.error(String.format("JSON Parser", "Error parsing data "
					+ e.toString()));
		}

		try {
			returnObject.put("contacts", jsonArray );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// return JSON String
		return returnObject ;

	}
	
	protected List<DetachUser> parseJsonResponse(JSONObject jObject) {
		List<DetachUser> _returnUser = new LinkedList<DetachUser>();
		try {
			JSONArray jArray = jObject.getJSONArray("contacts");
			for (int i = 0; i < jArray.length(); i++) {
				try {
					JSONObject c = (JSONObject) jArray.getJSONObject(i);
					AppLogger.debug( String.format("Contact : %s " , c.toString() ) );
					String status = c.getString(TAG_STATUS);
					String phone_number = c.getString(TAG_PHONE);
					String detachAvailblilty = c
							.getString(TAG_DETACH_AVAILABLILTY);
					String lastUpdatedAt = c.getString( TAG_STATUS_START );
					String updatesLastTo = c.getString( TAG_STATUS_END );
					
					DetachUser user = new DetachUser();
					user.setPhoneno(phone_number);
					user.setStatusmessage(status);
					if( Boolean.parseBoolean(detachAvailblilty) == false )
						user.setStatusId(UserStatus.AWAY);
					else 
						user.setStatusId(UserStatus.AVAILABLE);
					user.setDetachAvailablity(Boolean
							.parseBoolean(detachAvailblilty));
					user.setLastUpdated( lastUpdatedAt );
					user.setTimeRemaining( updatesLastTo );
					
					_returnUser.add(user);
				} catch (Exception e) {
					AppLogger.error( e.getMessage() );
				}
			}
		} catch (JSONException e) {
			AppLogger.error(String.format("JSON Parser", "Error parsing data "
					+ e.toString()));
		}

		return _returnUser;
	}

	protected JSONObject buildJsonContactsList( List<DetachUser> detachUsers ) {
		JSONObject returnObject = new JSONObject();
		if( null != detachUsers && detachUsers.size() == 0 ) {
			return returnObject ;
		}
		JSONArray contactsArray = new JSONArray();
		for(DetachUser user : detachUsers ) {
			try {
				contactsArray.put(user.toJsonObject());
			} catch (JSONException e) {
				AppLogger.error(String.format("JSON Parser %s" , e.getMessage() ) );
			}
		}
		try {
			returnObject.put(TAG_CONTACTS, contactsArray );
		} catch (JSONException e) {
			AppLogger.error(String.format("JSON Parser %s" , e.getMessage() ) );
		}
		return returnObject ;
	}
	
	public JSONObject postJsonToUrl(String url, JSONObject json ) {
		JSONObject returnObject = null;
		InputStream inputStream = null;
		String jsonContent = null ;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			 // add entity parameters
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("contacts", json.toString() ));
	        //keep json as raw string
	        httpPost.setEntity(new StringEntity(json.toString()));
	        // add json content-type
	        httpPost.setHeader("content-type", "application/json");
	        // execute post request
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			inputStream = httpEntity.getContent();
		} catch (UnsupportedEncodingException e) {
			AppLogger.error(String.format("JSON Parser",
					"Unsupported Encoding " + e.toString()));
		} catch (ClientProtocolException e) {
			AppLogger.error(String.format("JSON Parser",
					"Client Protocol Exception " + e.toString()));
		} catch (IOException e) {
			AppLogger.error(String.format("JSON Parser",
					"IO Exception " + e.toString()));
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			inputStream.close();
			jsonContent = sb.toString();
		} catch (Exception e) {
			AppLogger.error(String.format("Buffer Error",
					"Error converting result " + e.toString()));
		}

		// try parse the string to a JSON object
		try {
			returnObject = new JSONObject(jsonContent);
		} catch (JSONException e) {
			AppLogger.error(String.format("JSON Parser", "Error parsing data "
					+ e.toString()));
		} catch (NullPointerException e) {
            AppLogger.error(String.format("JSON Parser", "No service, hence no JSON received."
                    + e.toString()));
        }

		// return JSON String
		return returnObject;

	}
}
