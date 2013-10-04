package com.wwc.jajing.system;

import android.content.Context;
import android.util.Log;

import com.orm.SugarApp;

public class JJApplication extends SugarApp {

	public JJApplication() {
		super();

	}

	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize the singletons so their instances
		// are bound to the application process.
		initSystem(getApplicationContext());
	}

	private void initSystem(Context context) {
		if (!JJSystemImpl.hasBeenIntialized()) {
			JJSystemImpl.getInstance().initSystem(
					context.getApplicationContext());
			Log.d("Application", "App was initialized!");
			
		} else {
			Log.d("Application", "App was not initialized!");

		}
	}
}
