package com.wwc.jajing.activities;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.wwc.jajing.R;
import com.wwc.jajing.activities.callbacks.onUserActivitySelect;
import com.wwc.jajing.cloud.contacts.CloudBackendAsync;
import com.wwc.jajing.cloud.contacts.CloudCallbackHandler;
import com.wwc.jajing.domain.entity.TimeSetting;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.domain.services.CallManager;
import com.wwc.jajing.domain.value.AvailabilityTime;
import com.wwc.jajing.fragment.mTimePicker;
import com.wwc.jajing.settings.time.TimeSettingId;
import com.wwc.jajing.settings.time.TimeSettingValidator;
import com.wwc.jajing.system.JJSystem;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;
import com.wwc.jajing.util.AppLogger;
import android.app.DialogFragment;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity implements ActionBar.TabListener, ActionBar.OnMenuVisibilityListener {

	private static final String TAG = "MainActivity";
	public static final String DASHBOARD_INTENT = "com.exmaple.jajingprototype.intent.DASHBOARD_NOTIFICATION_AVAILABILITY_STATUS";
	
	/* For Navigation Drawer */
    private String[] navigation = new String[] { "What did I miss?", "I'm Back", "Add Contact" };
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle = "Welcome";

	private Button buttonStatus;
	private TextView textHeading;
	private TextView textCallersCanForceDisturb;
	private Button buttonAvailable;
    private EditText customStatus;
    private DialogFragment timeFrag;

	private User user;
	private CallManager cm;
	private String status;
	private Context mContext ;
	TextView mDisplay;
	CloudBackendAsync m_cloudAsync ;
    private String unavailabilityReason;

    private String endTime;
    private String startTime;

    private String pendingEndTime = "";
    private String pendingStartTime = "";


	
	private static final String AUTOSYNC = "AutoSync" ;
	private static final String USER_ID = "id" ;
	private static final String NUMBER_OF_CONTACTS = "number_of_contacts_added" ;
	
	private IntentFilter intentFilter = new IntentFilter(
			MainActivity.DASHBOARD_INTENT);

    private static MainActivity instance;

    public static MainActivity getInstance()
    {
        return instance;
    }

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        instance = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer_main);
		ActionBar actionBar = getActionBar();

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy);

		mContext = getApplicationContext();
		m_cloudAsync = new CloudBackendAsync(mContext);

		if (isNetworkAvailable(getApplicationContext()) == Boolean.FALSE) {
			Toast.makeText(mContext,
					"Network not available to push status updates",
					Toast.LENGTH_SHORT).show();
		}

		this.buttonStatus = (Button) findViewById(R.id.buttonStatus);
		this.buttonAvailable = (Button) findViewById(R.id.buttonAvailable);
		this.textHeading = (TextView) findViewById(R.id.textHeading);
		this.textCallersCanForceDisturb = (TextView) findViewById(R.id.textCallersCanForceDisturb);
        this.customStatus = (EditText) findViewById(R.id.customStatus);

		this.registerReceiver(this.dashboardReceiver, this.intentFilter);
		// CACHE JJSYSTEM
		JJSystem jjSystem = JJSystemImpl.getInstance();
		// set our user
		this.user = (User) jjSystem.getSystemService(Services.USER);
		// set our call manager
		this.cm = (CallManager) jjSystem
				.getSystemService(Services.CALL_MANAGER);

		this.status = this.user.getUserStatus().getAvailabilityStatus();

		if (!this.user.isAvailable()) {
			this.user.goUnavailable(this.status,
					this.user.getAvailabilityTime());
		} else {
			this.user.goAvailable();
		}

		String userId = getContactId();
		if (userId.equals("")) {
			Toast.makeText(mContext, "Not able to find Phone number.",
					Toast.LENGTH_LONG).show();
			updatePhoneNumber();
		} else {
			updateUserContact(getContactId());
			if (isContactsPushedToCloud() == Boolean.FALSE) {
				pushLocalPhoneContactsToCloud();
			}
		}

		initNavigationDrawer();

	}
	
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initNavigationDrawer()
	{
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.navigation_drawer_list_item, navigation));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.open_drawer,  /* "open drawer" description */
                R.string.close_drawer  /* "close drawer" description */
                ) {

            /** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
                getActionBar().setTitle("");
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("");
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "Main Activity was destroyed!");
		this.unregisterReceiver(this.dashboardReceiver);

	}

	@Override
	protected void onStart() {
		super.onStart();
		this.status = this.user.getUserStatus().getAvailabilityStatus();
		this.updateAvailabilityStatus((this.user.getUserStatus()
				.getAvailabilityStatus() != null) ? this.user.getUserStatus()
				.getAvailabilityStatus() : "Not Set!");
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch ( item.getItemId() ) {
		case R.id.action_detach:
			Toast.makeText( this, "Detach Contacts loading started", Toast.LENGTH_SHORT ).show();
			Intent detacher = new Intent(MainActivity.this, DetachActivity.class);  
		    startActivityForResult(detacher, 100 );  
			break;
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			selectItem(position);
			Log.d(TAG, position + "");
			switch (position) {
			case 0:
				// send the user to his missed call/message log
				Intent intent = new Intent(MainActivity.this, MissedLog.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			case 3:
				// send the user to set his time settings
				Intent i1 = new Intent(MainActivity.this, TimeSettings.class);
				i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i1);
				break;
			case 4:
				if (!MainActivity.this.user.getUserStatus().getavailabilityTime()
						.equalsIgnoreCase("UNKNOWN")) {
					// send the user to set his status page

					Intent i2 = new Intent(MainActivity.this, AwayActivity.class);
					i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i2);
				} else {
					Toast.makeText(MainActivity.this, "set availability status",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 2:
				updatePhoneNumber();
				break ;
            case 1:
                goAvailable();
                break;
			}
		}

	}
	
	/** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(navigation[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

    // Methods mapping to when buttons are clicked on the main screen of the app
	public void awayOptionsMenu(View view) {
		Intent intent = new Intent(this, AwayOptions.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}

    public void detachCustom(View view) {
        System.out.println(this.customStatus.getText());

        this.unavailabilityReason = this.customStatus.getText().toString();

        promptUserForTime(true);

        removeFragment();

    }

    public void detachDriving(View view) {

        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        String startTime = sdf.format(c.getTime());

        c.add(Calendar.HOUR, 1);

        String endTime = sdf.format(c.getTime());

        this.user.goUnavailable("Driving", startTime, new AvailabilityTime(endTime));
        Intent awayActivity = new Intent(this, AwayActivity.class);
        startActivity(awayActivity);
    }

    public void detachBusy(View view) {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        String startTime = sdf.format(c.getTime());

        c.add(Calendar.HOUR, 1);

        String endTime = sdf.format(c.getTime());

        System.out.println("Busy");
        this.user.goUnavailable("Busy", startTime, new AvailabilityTime(endTime));
        Intent awayActivity = new Intent(this, AwayActivity.class);
        startActivity(awayActivity);
    }

    public void detachNapping(View view) {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        String startTime = sdf.format(c.getTime());

        c.add(Calendar.MINUTE, 30);

        String endTime = sdf.format(c.getTime());

        System.out.println("Napping");
        this.user.goUnavailable("Napping", startTime, new AvailabilityTime(endTime));
        Intent awayActivity = new Intent(this, AwayActivity.class);
        startActivity(awayActivity);
    }

	public void goAvailable() {
		this.user.goAvailable();

        CloudCallbackHandler<JSONObject> handler = new CloudCallbackHandler<JSONObject>() {
            @Override
            public void onComplete( JSONObject results ) {
                Toast.makeText( mContext , "Status pushed to Cloud successfully", Toast.LENGTH_LONG ).show();
            }
            @Override
            public void onError( IOException exception ) {
                CloudBackendAsync.handleEndpointException( exception );
            }
        };
        //Change userId to actual user phone number
        long userId = 1l ;
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
        userId = shared.getLong( "id" , 1l ) ;
        if( userId == 1l ) {
            Toast.makeText( this, "Not valid phone number for API calls", Toast.LENGTH_LONG ).show();
        }
        m_cloudAsync.pushStatusToCloud( userId , user , handler );

		Intent intent = new Intent(this, MissedLog.class);
		intent.putExtra("recentFlag", true);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);

	}

	private void updateAvailabilityStatus(String status) {

//		if (status.equalsIgnoreCase("AVAILABLE")) {
//			this.setHeading("What's Your Status ?");
//			this.showCallersCanForceDisturb(true);
//			this.changeButtonStatusText("Not Available");
//			this.changeButtonAvailableText("Available");
//
//		} else {
//			this.setHeading("Change Status");
//			this.showCallersCanForceDisturb(false);
//			this.changeButtonStatusText("Still Not Available");
//			this.changeButtonAvailableText("I am now Available");
//		}
		Log.d(TAG, "Availability Status has been updated!" + status);
	}

	private void setHeading(String aHeading) {
		this.textHeading.setText(aHeading);
	}

	private void showCallersCanForceDisturb(Boolean toShow) {
		if (toShow) {
			this.textCallersCanForceDisturb.setVisibility(View.VISIBLE);
		} else {
			this.textCallersCanForceDisturb.setVisibility(View.GONE);

		}
	}

	private void changeButtonStatusText(String textForButton) {
		this.buttonStatus.setText(textForButton);
	}

	private void changeButtonAvailableText(String textForButton) {
		//this.buttonAvailable.setText(textForButton);
	}

	private BroadcastReceiver dashboardReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(MainActivity.DASHBOARD_INTENT)) {
				Log.d(TAG,
						"dashboard intent received. status field has been set.");
				// Do something with the string
				MainActivity.this.status = intent.getStringExtra("status");
				MainActivity.this.updateAvailabilityStatus(status);
			}
		}

	};

	@Override
	public void onMenuVisibilityChanged(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate( R.menu.main_activity_menu, menu );
		return true;
	}
	
	private boolean isContactsPushedToCloud() {
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
		boolean returnBool = shared.getBoolean( AUTOSYNC , false ) ;
		return returnBool ;
	}
	
	private void updateContactsPreferences( JSONObject statusObject ) throws JSONException {
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
		SharedPreferences.Editor _editor = shared.edit();
		_editor.putBoolean( AUTOSYNC , statusObject.getBoolean( AUTOSYNC ) );
		_editor.putInt( USER_ID , statusObject.getInt( USER_ID ));
		_editor.putInt( NUMBER_OF_CONTACTS , statusObject.getInt( NUMBER_OF_CONTACTS ) );
		_editor.commit();
	}
	
	private void updateUserContact( String contactId ) {
		if( contactId.length() > 0 ) {
			SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
			SharedPreferences.Editor _editor = shared.edit();
			_editor.putLong( USER_ID , Long.parseLong( contactId ) );
			_editor.commit();
		} else {
			Toast.makeText( mContext, "Not valid phone number found", Toast.LENGTH_LONG ).show();
		}
	}

	/**
	 * Asynchronously pushes phone local contacts to detach.io
	 * 
	 * This will happen only once and application preferences
	 * will be updated upon successful updates to cloud
	 * 
	 */
	private void pushLocalPhoneContactsToCloud() {

		CloudCallbackHandler<JSONObject> handler = new CloudCallbackHandler<JSONObject>() {
			@Override
			public void onComplete( JSONObject results ) {
				try {
					updateContactsPreferences( results );
				} catch ( JSONException e ) {
					//Toast.makeText( mContext , e.toString(), Toast.LENGTH_LONG ).show();
					AppLogger.error( String.format( TAG , e.toString() + ", Unable to push local contacts to Cloud" ) );
				}
			}
			@Override
			public void onError( IOException exception ) {
				CloudBackendAsync.handleEndpointException( exception );
			}
		};
		try {
			//Change userId to actual user phone number
			long userId = 1l ;
			SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
			userId = shared.getLong( USER_ID , 1l ) ;
			if( userId == 1l ) {
				Toast.makeText( this, String.format("Not valid phone number (%f) for API calls. Contacts not pushed to cloud" , userId ), Toast.LENGTH_LONG ).show();
				return ;
			}
			m_cloudAsync.pushContactsToCloud( userId , handler );
			Toast.makeText(mContext, "Phone contacts already pushed",Toast.LENGTH_SHORT).show();
		} catch ( JSONException e ) {
			//Toast.makeText( mContext , e.toString(), Toast.LENGTH_LONG ).show();
			AppLogger.error( String.format( TAG ,e.toString() + ", Unable to push local contacts to Cloud" ) );
		}
	}
	
	public static boolean isNetworkAvailable( Context con ) {
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) con.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if ( null != networkInfo ) {
				if ( networkInfo.isAvailable() && networkInfo.isConnected() ) {
					return true;
				}
			}
		} catch ( Exception e ) {
			AppLogger.error( String.format( TAG , String.format( "CheckConnectivity Exception: %s " + e.getMessage() ) ) );
		}
		return false;
	}
	
	/**
	 * Checks for valid phone contact number for cloud registration
	 * 
	 * @return valid 10 digit contact number for preferences settings.
	 */
	private String getContactId() {
		TelephonyManager phoneManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		int simState = phoneManager.getSimState() ;
		if( simState != TelephonyManager.SIM_STATE_READY ) {
			Toast.makeText( mContext , "No valid sim found in this phone", Toast.LENGTH_LONG ).show();
		}
		String contactNumber = ( phoneManager.getLine1Number() == null ? "" : phoneManager.getLine1Number() );
		AppLogger.debug( String.format("Phone owner number %s : " , contactNumber ) );
		if( contactNumber != null && contactNumber.length() >= 10 ) {
			//Check for actual length
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
		}
		AppLogger.debug( String.format("valid contact number %s : " , contactNumber ) );
		return contactNumber ;
	}
	
	private String parseContactId( String contactId ) {
		String contactNumber = contactId ;
		if( contactNumber != null && contactNumber.length() >= 10 ) {
			//Check for actual length
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
		}
		AppLogger.debug( String.format("Parsed contact number %s : " , contactNumber ) );
		return contactNumber ;
	}

	private void updatePhoneNumber() {
		final Dialog d = new Dialog( this );
		d.setContentView( R.layout.activity_add_phonenumber );
		d.setTitle( "Update Phone Number" );
		d.setCancelable( true );
		final EditText edit1 = (EditText) d.findViewById( R.id.phonenumber );
		Button updateButton = (Button) d.findViewById( R.id.updateNumber );
		Button cancellButton = (Button) d.findViewById( R.id.updateCancel );
		updateButton.setOnClickListener( new View.OnClickListener() {
			public void onClick( View v ) {
				String contactId = parseContactId( edit1.getText().toString()  ) ;
				updateUserContact( contactId );
				if (isContactsPushedToCloud() == Boolean.FALSE) {
					pushLocalPhoneContactsToCloud();
				}
				d.dismiss();
			}
		} );
		cancellButton.setOnClickListener( new View.OnClickListener() {
			public void onClick( View v ) {
				d.dismiss();
			}
		} );

		d.show();
	}


    public void setStartTime(String aStartTime) {
        if (!this.pendingStartTime.equalsIgnoreCase(aStartTime)) {
            this.pendingStartTime = aStartTime;
            this.startTime = this.pendingStartTime;
        }
        //Toast.makeText(this, "start time set to : " + aStartTime, Toast.LENGTH_SHORT).show();
        removeFragment();
    }

    public void setEndTime(String anEndTime) {
        //BUG THAT SUBMITS THE CURRENT TIME AS END TIME
        if (!this.pendingEndTime.equalsIgnoreCase(anEndTime) && !anEndTime.equalsIgnoreCase("")) {
            this.pendingEndTime = anEndTime;
            this.endTime = pendingEndTime;
            //Toast.makeText(this, "end time set to : " + pendingEndTime, Toast.LENGTH_SHORT).show();

        } else {
            // stop code execution, there is a BUG with android calling
            // onSetTimeTwice, this is a temporary fix.
            Toast.makeText(this, "could not set end time: " + anEndTime, Toast.LENGTH_SHORT).show();
            this.pendingEndTime = "";
            return;
        }

        if(!TimeSetting.isEndTimeInFuture(anEndTime)) {
            Toast.makeText(this, "end time must be in future", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!TimeSetting.isValidTimeInterval(this.startTime, pendingEndTime)) {
            Toast.makeText(this, "invalid interval", Toast.LENGTH_SHORT).show();
            return;
        }




        ArrayList<TimeSetting> interferingTimeSettings = TimeSettingValidator
                .getTimeSettingsThisEndTimeInterferesWith(anEndTime);
        // POSSIBLE BUG HERE... should not compare size, was used as a temp fix
        if (interferingTimeSettings.size() < 1) {// check to make sure its set
            // for the future
            boolean success = goUnavailable();
            if (success) {
                navigateToAway(1L);
            } else {

            }
        } else {
            PlainAlertDialog
                    .alertUser(
                            this,
                            "Sorry",
                            "This time interferes with your time setting(s), turn them off?",
                            new onUserActivitySelect(
                                    getInterferingTimeSettingIds(interferingTimeSettings),
                                    this.endTime, this.unavailabilityReason),
                            true);
        }

    }

    private void navigateToAway(Long timeSettingId) {
        Log.d(TAG,
                "availability time is now:"
                        + new AvailabilityTime(this.endTime)
                        .getAvailabilityTimeString());
        // Now we can route the user to the "Away" activity.
        Intent i = new Intent(this, AwayActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(AwayActivity.EXTRA_KEY_TIME_SETTING_ID, timeSettingId);
        startActivity(i);
    }

    private ArrayList<TimeSettingId> getInterferingTimeSettingIds(
            ArrayList<TimeSetting> interferingTimeSettings) {

        ArrayList<TimeSettingId> listOfTimeSettingIds = new ArrayList<TimeSettingId>();
        for (TimeSetting aTimeSetting : interferingTimeSettings) {
            listOfTimeSettingIds.add(new TimeSettingId(aTimeSetting.getId()));
        }
        return listOfTimeSettingIds;
    }

    private void removeFragment()
    {
        FragmentManager fragManager = this.getFragmentManager();
        FragmentTransaction ft = fragManager.beginTransaction();
        ft.remove(this.timeFrag);
        ft.commit();
        promptUserForTime(false);// prompt for end time
    }

    private void promptUserForTime(boolean isStartTime) {

        timeFrag = new mTimePicker(isStartTime);

        timeFrag.setRetainInstance(true);

        timeFrag.show(getFragmentManager(), "timePicker");
    }

    private boolean goUnavailable() {
        return this.user.goUnavailable(this.unavailabilityReason,
                this.startTime, new AvailabilityTime(this.endTime));

    }

}
