package com.wwc.jajing.domain.entity;

import java.util.ArrayList;

/*
 * Singleton
 * 
 */
public class MissedCallLog {
	
	private ArrayList<MissedCall> recentMissedCalls = new ArrayList<MissedCall>();
	private static final MissedCallLog instance = new MissedCallLog();
	
	private MissedCallLog(){
		
	}
	
	public static MissedCallLog getInstance()
	{
		return instance;
	}
	
	public void addRecentMissedCall(MissedCall aMissedCall)
	{
		this.recentMissedCalls.add(aMissedCall);
	}
	
	public void clearRecentMissedCalls()
	{
		this.recentMissedCalls.clear();
	}
	
	public ArrayList<MissedCall> getRecentCalls()
	{
		return this.recentMissedCalls;
	}
}
