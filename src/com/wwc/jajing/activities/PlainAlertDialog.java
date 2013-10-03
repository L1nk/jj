package com.wwc.jajing.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.wwc.jajing.R;
import com.wwc.jajing.util.Alert;

public class PlainAlertDialog extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		String title = i.getStringExtra("title");
		String description = i.getStringExtra("description");
		boolean hasCancel = i.getBooleanExtra("hasCancel", false);
		final Alert aCallback = (Alert) i.getSerializableExtra("callback");

				
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(description);
		builder.setTitle(title);
		
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked OK button
	        	   aCallback.onAlertPositive();
	        	   PlainAlertDialog.this.finish();
	       			
	           }
	    });
		if(hasCancel) {
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User clicked OK button
		        	   aCallback.onAlertNegative();
		        	   PlainAlertDialog.this.finish();
		       			
		           }
		    });
		}
		
	
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}



	@Override
	protected void onStop() {
		super.onStop();
		this.finish();
	}
	
	public static void alertUser(Context context, String aTitle, String aDescription, Alert aCallback, boolean hasCancel)
	{
		Intent i = new Intent(context, PlainAlertDialog.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("title", aTitle);
		i.putExtra("description", aDescription);
		i.putExtra("callback", aCallback);
		i.putExtra("hasCancel", hasCancel);

		context.startActivity(i);
	}
	
	

}
