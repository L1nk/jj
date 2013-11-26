package com.wwc.jajing.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.wwc.jajing.helpers.ViewPageAdapter;
import com.wwc.jajing.R;

public class HelpActivity extends Activity {

    private int imageArray[] = { R.drawable.help_one, R.drawable.help_two,
            R.drawable.help_three, R.drawable.help_four, R.drawable.help_five};

    private ViewPageAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        adapter = new ViewPageAdapter(this, imageArray);
        ViewPager myPager = (ViewPager) findViewById(R.id.myfivepanelpager);
        myPager.setAdapter(adapter);
        myPager.setCurrentItem(0);
    }

}