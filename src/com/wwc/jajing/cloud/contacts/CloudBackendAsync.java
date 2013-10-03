package com.wwc.jajing.cloud.contacts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.os.Handler;

import com.wwc.jajing.activities.DetachUser;
import com.wwc.jajing.util.AppLogger;

/**
 * CloudBackendAsync API class that provides asynchronous APIs calls to Cloud. 
 * The added methods work asynchronously with {@link CloudCallbackHandler}, 
 * so they can be called from UI thread of {@link Activity}s or {@link Fragment}s. 
 * 
 */
 
public class CloudBackendAsync {

	protected final Application application;

	protected static final Map<String, DetachUser> detachContacts = new ConcurrentHashMap<String, DetachUser>();

	private static final String DETACH_API_URI = "http://api.detach.io";
	private static final String DETACH_CREATE_API_URI = "/create";
	private static final String DETACH_CONTACTS_API_URI = "/contacts";
	private static final String DETACH_MOBILE_BACKEND_URI = "/?client=mobile-android&client_secret=detach1101";
	private static final String DETACH_USER_PRERFERNCE_URI = "/user/%s";

	public CloudBackendAsync(Context context) {
		// set Application
		this.application = (Application) (context != null ? context
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
	
	public void pushContactsToCloud(long phoneNumber, List<DetachUser> users, CloudCallbackHandler<JSONObject> handler) {
		this.createContacts(phoneNumber, users, handler);
	}
	
	private void createContacts(long phoneNumber, final List<DetachUser> users, CloudCallbackHandler<JSONObject> handler) {
		(new BackendCaller<Long, JSONObject>(phoneNumber, handler) {
			@Override
			protected JSONObject callBackend(Long phoneNumber)
					throws IOException {
				JSONParser jParser = JSONParser.getParser();
				String userPreference = String.format( DETACH_USER_PRERFERNCE_URI , phoneNumber ); 
		        String postUrl = String.format("%s%s%s%s",DETACH_API_URI , userPreference, DETACH_CREATE_API_URI, DETACH_MOBILE_BACKEND_URI );
		        JSONObject newContactsJson = jParser.buildJsonContactsList(users);
		        jParser.postJsonToUrl(postUrl, newContactsJson );
				return  new JSONObject();
			}
		}).start();
	}
	
	
	public void pushStatusToCloud(long phoneNumber, String status, CloudCallbackHandler<JSONObject> handler) {
		this.pushMyStatus(phoneNumber, status, handler);
	}
	
	private void pushMyStatus(long phoneNumber, String status, CloudCallbackHandler<JSONObject> handler) {
		(new BackendCaller<String, JSONObject>(status , handler) {
			@Override
			protected JSONObject callBackend(String status)
					throws IOException {
				JSONParser jParser = JSONParser.getParser();
		        String postUrl = String.format("%s%s%s",DETACH_API_URI , DETACH_USER_PRERFERNCE_URI, DETACH_MOBILE_BACKEND_URI );
		        JSONObject json = new JSONObject();
		        try {
					json.put("status", status);
				} catch (JSONException e) {
					AppLogger.error( String.format(e.getMessage()) );
				}
		        jParser.postJsonToUrl(postUrl, json);
				return  new JSONObject();
			}
		}).start();
	}

	// a Thread class that will call backend API asynchronously
	// and call back the handler on UI thread
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
}
