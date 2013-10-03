package com.wwc.jajing.util;

/**
 * 
 * @author nagendhiran
 *
 */
public class AppLogger {
	public static String TAG = "com.wwc.jajing";

	public static void info( String message ) {
		android.util.Log.i( TAG, message );
	}

	public static void debug( String message ) {
		android.util.Log.d( TAG, message );
	}
	
	public static void error( String message ) {
		android.util.Log.e( TAG, message );
	}
}
