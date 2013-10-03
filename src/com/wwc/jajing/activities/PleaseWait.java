package com.wwc.jajing.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PleaseWait extends Activity{

	ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i = getIntent();
		String title  = i.getStringExtra("title");
		String description  = i.getStringExtra("description");
		
		pd = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK).show(this, title, description);
		pd.show();
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		pd.dismiss();
		this.finish();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		pd.dismiss();
		this.finish();
	}
	
	public static void showPleaseWaitDialog(Context aContext, String aTitle, String aDescription)
	{
		Intent i = new Intent(aContext, PleaseWait.class);
		i.putExtra("title", aTitle);
		i.putExtra("description", aDescription);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		aContext.startActivity(i);
	}
}
