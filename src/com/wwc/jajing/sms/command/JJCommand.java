package com.wwc.jajing.sms.command;





/*
 * Every action that can be triggered by an JJSMS must implement this interface.
 * It encapsulates the behavior(and data) that must be invoked when we recieve a JJSMS message.
 * 
 */
public interface JJCommand {

		public void execute();
		


}
