package com.wwc.jajing.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.RawContacts;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.wwc.jajing.R;
import com.wwc.jajing.cloud.contacts.CloudBackendAsync;
import com.wwc.jajing.cloud.contacts.CloudCallbackHandler;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.listadapter.FastIndexEntityAdapter;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

/**
 * This activity initialized through contacts icon of home screen
 * which loads the detach contacts of the prescribed user.
 * 
 * The contacts will be populated through cloudbackendasync 
 * APIs and loads the contacts asynchronously on contact lists
 * 
 * @author nagendhiran
 *
 */
public class DetachActivity extends FragmentActivity {

	FastIndexEntityAdapter m_entityListAdapter = null;
	FastSearchListView m_entityListView = null;
	List<DetachUser> m_detachUsers = new ArrayList<DetachUser>();
	CloudBackendAsync m_cloudAsync ;
	User user ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer_detacher);
		
		m_cloudAsync = new CloudBackendAsync(getApplicationContext());
		
		m_entityListView = (FastSearchListView) findViewById(R.id.left_drawer);
		
		user = (User) JJSystemImpl.getInstance().getSystemService(Services.USER);
		
		OnItemLongClickListener listItemClicked = new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int listPosition, long arg3) {
					if( m_detachUsers.get(listPosition).getLocalContactId() == -1 ) {
						Toast.makeText(getApplicationContext(), "No valid contacts found in phone", Toast.LENGTH_SHORT ).show();
						return true ;
					}
					Intent contactIntent = new Intent(Intent.ACTION_VIEW);
					Uri rawContactUri = ContentUris.withAppendedId(
							RawContacts.CONTENT_URI, m_detachUsers.get(listPosition)
									.getLocalContactId());
					contactIntent.setData(rawContactUri);
					startActivityForResult( contactIntent , 100);
					return true ;
			}
		};

		startDetachContactsLoading();
		
		m_entityListView.setOnItemLongClickListener( listItemClicked );

	}

	/**
	 * Asynchronously loads detached user contacts from cloud
	 * 
	 */
	private void startDetachContactsLoading() {

		CloudCallbackHandler<List<DetachUser>> handler = new CloudCallbackHandler<List<DetachUser>>() {
			@Override
			public void onComplete( List<DetachUser> results ) {
				if ( results.size() > 0 ) {
					m_entityListAdapter = new FastIndexEntityAdapter( results, getApplicationContext() );
					m_entityListAdapter.notifyDataSetChanged();
					m_entityListView.setAdapter( m_entityListAdapter );
				} 
				//Atomic update
				m_detachUsers = results;
			}

			@Override
			public void onError( IOException exception ) {
				handleEndpointException( exception );
			}
		};
		//Change userId to actual user phone number
		long userId = 1231231 ;
		m_cloudAsync.getDetachContacts(  userId , handler, new Handler());
	}
	
	private void handleEndpointException( IOException e ) {
		Toast.makeText( this, e.toString(), Toast.LENGTH_LONG ).show();
		Log.e( "Error", e.toString() );
	}
}
