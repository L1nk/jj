package com.wwc.jajing.system;

import android.content.Context;

import com.wwc.jajing.system.JJSystemImpl.Services;

/*
 * This interface defines the contract for classes that initialize and provide GLOBAL services within our application.
 */
public interface JJSystem {
	
	
	public Object getSystemService(Services service);
	public void initSystem(Context context);

}
