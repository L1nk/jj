package com.wwc.jajing.util;

import java.io.Serializable;


/*
 * Call back interface for alert dialogs
 */
public interface Alert extends Serializable{
	
	//do this when user clicks ok
	public void onAlertPositive();
	//do this when the user clicks cancel
	public void onAlertNegative();

}
