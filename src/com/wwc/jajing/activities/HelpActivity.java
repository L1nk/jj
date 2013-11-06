package com.wwc.jajing.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.wwc.jajing.helpers.ViewPageAdapter;
import com.wwc.jajing.R;

public class HelpActivity extends Activity {


    private int imageArray[] = { R.drawable.help_one, R.drawable.help_two,
            R.drawable.help_three, R.drawable.help_four};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ViewPageAdapter adapter = new ViewPageAdapter(this, imageArray);
        ViewPager myPager = (ViewPager) findViewById(R.id.myfivepanelpager);
        myPager.setAdapter(adapter);
        myPager.setCurrentItem(0);
    }

}