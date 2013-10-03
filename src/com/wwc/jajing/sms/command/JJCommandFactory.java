package com.wwc.jajing.sms.command;

import android.content.Context;

import com.wwc.jajing.sms.JJSMS;
import com.wwc.jajing.sms.command.JJCommandFactoryImpl.Commands;


public interface JJCommandFactory {
	
	public JJCommand createCommand(Context context,JJSMS aJJSMS);
}
