package com.wwc.jajing.cloud.contacts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.wwc.jajing.activities.DetachUser;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.util.AppLogger;

/**
 * CloudBackendAsync API class that provides asynchronous APIs calls to Cloud. 
 * The added methods work asynchronously with {@link CloudCallbackHandler}, 
 * so they can be called from UI thread of {@link Activity}s or {@link Fragment}s. 
 * 
 */
 
public class CloudBackendAsync {

	protected static Application application;

	protected static final Map<String, DetachUser> detachContacts = new ConcurrentHashMap<String, DetachUser>();

	private static final String DETACH_API_URI = "http://api.detach.io";
	private static final String DETACH_CREATE_API_URI = "/create";
	private static final String DETACH_CONTACTS_API_URI = "/contacts";
	private static final String DETACH_MOBILE_BACKEND_URI = "/?client=mobile-android&client_secret=detach1101";
	private static final String DETACH_USER_PRERFERNCE_URI = "/user/%s";
	
	private static final String PHONE_NUMBER_TAG = "phone_number" ;
	private static final String CONTACTS_TAG = "contacts" ;

	public CloudBackendAsync(Context context) {
		// set Application
		application = (Application) (context != null ? context
				.getApplicationContext() : null);
	}

	public void getDetachContacts(long phoneNumber,
			CloudCallbackHandler<List<DetachUser>> handler,
			Handler uiThreadHandler) {
		this.getDetachContacts(phoneNumber, handler);
	}

	private void getDetachContacts(long phoneNumber,
			CloudCallbackHandler<List<DetachUser>> handler) {
		(new BackendCaller<Long, List<DetachUser>>(phoneNumber, handler) {
			@Override
			protected List<DetachUser> callBackend(Long phoneNumber)
					throws IOException {
				JSONParser jParser = JSONParser.getParser();
				String userPreference = String.format( DETACH_USER_PRERFERNCE_URI , phoneNumber ); 
		        String getUrl = String.format("%s%s%s%s",DETACH_API_URI , userPreference , DETACH_CONTACTS_API_URI, DETACH_MOBILE_BACKEND_URI );
		        JSONObject json = jParser.getJSONFromUrl(getUrl);
		        return jParser.parseJsonResponse(json);
			}
		}).start();
	}
	
	public void pushContactsToCloud(long phoneNumber, CloudCallbackHandler<JSONObject> handler) throws JSONException {
		this.createContacts(phoneNumber, handler);
	}
	
	private void createContacts(long phoneNumber, CloudCallbackHandler<JSONObject> handler) throws JSONException {
		(new BackendCaller<Long, JSONObject>(phoneNumber, handler) {
			@Override
			protected JSONObject callBackend(Long phoneNumber)
					throws IOException {
				JSONObject users = null;
				try {
					//Retrieve local contacts
					JSONArray contacts = getPhoneLocalContactsList();
					users = new JSONObject();
					if ( contacts.length() > 0 ) {
						users.put( CONTACTS_TAG, contacts );
					}
					//Push to cloud
					JSONParser jParser = JSONParser.getParser();
					String userPreference = String.format( DETACH_USER_PRERFERNCE_URI , phoneNumber ); 
			        String postUrl = String.format("%s%s%s%s",DETACH_API_URI , userPreference, DETACH_CREATE_API_URI, DETACH_MOBILE_BACKEND_URI );
			        JSONObject returnObject = jParser.postJsonToUrl(postUrl, users );
			        AppLogger.debug(String.format("Phone contacts pushed to cloud : %s" , users.toString() )) ;
			        AppLogger.debug( String.format("Cloud return json : %s " , returnObject.toString() ) );
					return  returnObject ;
				} catch ( JSONException e ) {
					Toast.makeText( application , e.toString(), Toast.LENGTH_LONG ).show();
				}
				return users ;
			}
		}).start();
	}
	
	
	public void pushStatusToCloud(long phoneNumber, User user, CloudCallbackHandler<JSONObject> handler) {
		this.pushMyStatus(phoneNumber, user, handler);
	}
	
	private void pushMyStatus(final long phoneNumber, User user, CloudCallbackHandler<JSONObject> handler) {
		(new BackendCaller<User, JSONObject>(user , handler) {
			@Override
			protected JSONObject callBackend(User user)
					throws IOException {
				JSONParser jParser = JSONParser.getParser();
				String userPreference = String.format( DETACH_USER_PRERFERNCE_URI , phoneNumber ); 
		        String postUrl = String.format("%s%s%s",DETACH_API_URI , userPreference, DETACH_MOBILE_BACKEND_URI );
		        JSONObject json = new JSONObject();
		        try {
					json.put("status", user.getUserStatus().getAvailabilityStatus() );
					json.put( "status_start", user.getFullStartDateTime() );
					json.put( "status_end", user.getFullEndDateTime() );
					AppLogger.debug(String.format("Status update json : %s" , json.toString() )) ;
				} catch (JSONException e) {
					AppLogger.error( String.format(e.getMessage()) );
				}
				return  jParser.postJsonToUrl(postUrl, json);
			}
		}).start();
	}

	/**
	 * a Thread class that will call backend API asynchronously
	 * and call back the handler on UI thread
	 * 
	 * @param <PARAM> - Generic representation of any object types
	 * @param <RESULT> - Generic return types of any object types
	 */
	private abstract class BackendCaller<PARAM, RESULT> extends Thread {

		final Handler uiThreadHandler;

		final CloudCallbackHandler<RESULT> handler;

		final PARAM param;

		private BackendCaller(PARAM param, CloudCallbackHandler<RESULT> crh,
				Handler uiThreadHandler) {
			this.handler = crh;
			this.param = param;
			this.uiThreadHandler = uiThreadHandler;
		}

		private BackendCaller(PARAM param, CloudCallbackHandler<RESULT> crh) {
			this.handler = crh;
			this.param = param;
			this.uiThreadHandler = new Handler();
		}

		@Override
		public void run() {

			// execute call
			RESULT r = null;
			IOException ie = null;
			try {
				r = callBackend(param);
			} catch (IOException e) {
				AppLogger.info(String.format(AppLogger.TAG, "error: ", e));
				ie = e;
			}
			final RESULT results = r;
			final IOException exception = ie;

			// if no handler specified, no need to callback
			if (handler == null) {
				return;
			}

			// pass the result to the handler on UI thread
			uiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					if (exception == null) {
						handler.onComplete(results);
					} else {
						handler.onError(exception);
					}
				}
			});
		}

		abstract protected RESULT callBackend(PARAM param) throws IOException;
	};

	public List<DetachUser> list(long phoneNumber) throws IOException {
		return new ArrayList<DetachUser>();
	}
	
	public static void handleEndpointException( IOException e ) {
		Toast.makeText( application  , e.toString(), Toast.LENGTH_LONG ).show();
		Log.e( "Error", e.toString() );
	}
	
	private static JSONArray getPhoneLocalContactsList() throws JSONException {
		JSONArray returnList = new JSONArray() ;
	    Cursor cursor = application.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI , new String[] {
	    	ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER }, null, null, null);
	    String contactNumber = "";
	    while (cursor.moveToNext()) {
	    	contactNumber = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            
            if( contactNumber != null && contactNumber.length() > 0 ) {
            	contactNumber = contactNumber.trim();
            	if( contactNumber.contains( " " )) {
            		contactNumber = contactNumber.replace( " ", "" );
            	}
            	if( contactNumber.contains( "-" )) {
            		contactNumber = contactNumber.replace( "-", "" );
            	}
            	if( contactNumber.contains( "+" )) {
            		contactNumber = contactNumber.replace( "+", "" );
            	}
            	if( contactNumber.contains( "(" ) || contactNumber.contains( ")" ) ) {
            		contactNumber = contactNumber.replace( "(", "" );
            		contactNumber = contactNumber.replace( ")", "" );
            	}
            	if( contactNumber.length() > 11 && contactNumber.startsWith("+1")) {
    				contactNumber = contactNumber.replace("+1", "");
    				AppLogger.debug( String.format("Truncated +1 from my mobile number %s " , contactNumber ) );
    			} 
            	if ( contactNumber.length() > 10 && contactNumber.startsWith("1")) {
    				contactNumber = contactNumber.replaceFirst( "1", "");
    				AppLogger.debug( String.format("Truncated starting '1' from my mobile number %s " , contactNumber ) );
    			} 
            	if ( contactNumber.contains("+")) {
    				contactNumber = contactNumber.replace("+", "");
    				AppLogger.debug( String.format("Truncated '+' from my mobile number %s " , contactNumber ) );
    			}
            	JSONObject contact = new JSONObject();
                contact.put( PHONE_NUMBER_TAG , contactNumber );
                returnList.put( contact );
            }
            
        }
	    cursor.close();
	    cursor = null;
		return returnList ;
	}
	
}
