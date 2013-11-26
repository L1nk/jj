package com.wwc.jajing.domain.entity;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Created by Nicolas on 11/25/13.
 */



public class Status extends SugarRecord implements Entity {

    public String status;
    public Long id;

    public Status(Context context) {
        super(context);
    }

    public Status(Context context, String status) {
        super(context);

        this.status = status;

    }
    public String getStatus()
    {
        return this.status;
    }
    public void setStatus(String aStatus)
    {
        this.status = aStatus;
    }
}