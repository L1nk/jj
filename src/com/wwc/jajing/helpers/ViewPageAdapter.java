package com.wwc.jajing.helpers;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.wwc.jajing.activities.MainActivity;

public class ViewPageAdapter extends PagerAdapter {

    final Activity activity;
    int imageArray[];
    public int position;

    public ViewPageAdapter(Activity act, int[] imgArray) {
        this.imageArray = imgArray;
        activity = act;
    }

    public int getCount() {
        return imageArray.length;
    }

    public Object instantiateItem(View collection, final int position) {
        ImageView view = new ImageView(activity);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        view.setScaleType(ScaleType.CENTER);
        view.setBackgroundResource(imageArray[position]);
        view.setOnClickListener( new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                if(position == 4) {
                    Intent i = new Intent(activity, MainActivity.class);
                    activity.startActivity(i);
                }
            }
        });
        this.position = position;
        ((ViewPager) collection).addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}