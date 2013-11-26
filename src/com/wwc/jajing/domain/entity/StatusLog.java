package com.wwc.jajing.domain.entity;

import android.content.Context;
import android.util.Log;

import com.wwc.jajing.system.JJSystemImpl;

import java.util.ArrayList;

/**
 * Created by Nicolas on 11/25/13.
 */
public class StatusLog {

    private static final String TAG ="StatusLog";
    private final ArrayList<Status> recentStatusLog = new ArrayList<Status>();
    private static final ArrayList<String> stringStatusLog = new ArrayList<String>();
    private static final StatusLog instance = new StatusLog();
    //private Context context = (Context) JJSystemImpl.getInstance().getSystemService(JJSystemImpl.Services.CONTEXT);

    private StatusLog(){
    }

    public static StatusLog getInstance()
    {
        return instance;
    }

    public void addStatus(Status aNewStatus)
    {
        this.recentStatusLog.add(aNewStatus);
        Log.d(TAG, "Added a new status to the status log.");
    }
    public void addStatus(Context context, String stringStatus)
    {
        Status aNewStatus = new Status(context, stringStatus);
        this.recentStatusLog.add(aNewStatus);
        this.stringStatusLog.add(stringStatus);
        Log.d(TAG, "Added a new status to the status log.");
    }
    public ArrayList<Status> getStatusLog(){
        return recentStatusLog;
    }
    public static ArrayList<String> getStringLog(){
        return stringStatusLog;
    }

}
