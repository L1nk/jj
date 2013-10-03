package com.wwc.jajing.activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wwc.jajing.R;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.domain.services.CallManager;
import com.wwc.jajing.system.JJSystem;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    public static final String DASHBOARD_INTENT = "com.exmaple.jajingprototype.intent.DASHBOARD_NOTIFICATION_AVAILABILITY_STATUS";

    /* For Navigation Drawer */
    private String[] navigation = new String[] { "History", "Time Settings", "My Status" };
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle = "Welcome";

    private Button buttonStatus;
    private TextView textHeading;
    private TextView textCallersCanForceDisturb;

    private User user;
    private CallManager cm;
    private String status;

    private IntentFilter intentFilter = new IntentFilter(
            MainActivity.DASHBOARD_INTENT);

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_main);
        ActionBar actionBar = getActionBar();
        actionBar.setTitle("   Welcome   ");
        this.buttonStatus = (Button) findViewById(R.id.buttonStatus);
        this.textHeading = (TextView) findViewById(R.id.textHeading);
        this.textCallersCanForceDisturb = (TextView) findViewById(R.id.textCallersCanForceDisturb);

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
                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
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

        setTitle("Welcome");

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
                case 1:
                    // send the user to set his time settings
                    Intent i1 = new Intent(MainActivity.this, TimeSettings.class);
                    i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i1);
                    break;
                case 2:
                    if (!MainActivity.this.user.getUserStatus().getavailabilityTime()
                            .equalsIgnoreCase("UNKNOWN")) {
                        // send the user to set his status page

                        Intent i2 = new Intent(MainActivity.this, AwayActivity.class);
                        i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i2);
                    } else {
                        Toast.makeText(MainActivity.this, "set availability status",
                                Toast.LENGTH_SHORT).show();
                        MainActivity.this.setTitle("Welcome");
                    }
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

    public void awayOptionsMenu(View view) {
        Intent intent = new Intent(this, AwayOptions.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    public void goAvailable(View view) {
        this.user.goAvailable();

        Intent intent = new Intent(this, MissedLog.class);
        intent.putExtra("recentFlag", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void updateAvailabilityStatus(String status) {

        if (status.equalsIgnoreCase("AVAILABLE")) {
            this.setHeading("Want to get away?");
            this.showCallersCanForceDisturb(true);
            this.changeButtonStatusText("set your status");

        } else {
            this.setHeading("Still Not Available?");
            this.showCallersCanForceDisturb(false);
            this.changeButtonStatusText("update status");
            this.buttonStatus.setHeight(100);
        }
        Log.d(TAG, "Availability status has been updated!" + status);
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

    private BroadcastReceiver dashboardReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MainActivity.DASHBOARD_INTENT)) {
                Log.d(TAG,
                        "dashboard intent receiveed. status field has been set.");
                // Do something with the string
                MainActivity.this.status = intent.getStringExtra("status");
                MainActivity.this.updateAvailabilityStatus(status);
            }
        }

    };

}
