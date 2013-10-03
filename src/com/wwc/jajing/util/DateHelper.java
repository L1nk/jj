package com.wwc.jajing.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;




public class DateHelper {

	private static final String TAG = "DateHelper";
	public static String dateToString(Date aDate) {
		if(aDate == null) {
			throw new NullPointerException("Date cannot be null.");
		}
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yy hh:mm:ss a");
		String s = formatter.format(aDate);
		return s;

	}
	
	public static Date StringToDate(String aString) {
		
		try {
			Date date = new SimpleDateFormat("MMM-dd-yy hh:mm:ss a", Locale.ENGLISH).parse(aString);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
