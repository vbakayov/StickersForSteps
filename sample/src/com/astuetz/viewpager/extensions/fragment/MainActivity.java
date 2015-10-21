/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.astuetz.viewpager.extensions.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;



import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.astuetz.viewpager.extensions.sample.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static java.security.AccessController.getContext;

public class MainActivity extends ActionBarActivity implements SensorEventListener {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    ViewPager pager;

    private SensorManager sensorManager;
    private boolean firstTime;
    private int initialStep;
    private TextView count;
    private Button refresh;
    boolean activityRunning;
    private View.OnClickListener myhandler1;
    private float steps;



    private MyPagerAdapter adapter;
    private Drawable oldBackground = null;
    private int currentColor;
    private SystemBarTintManager mTintManager;
    private SmartFragmentStatePagerAdapter adapterViewPager;
    private StepsFragment m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        count = (TextView) findViewById(R.id.count);
        refresh= (Button) findViewById(R.id.buttonrefresh);
        refresh.setOnClickListener(myhandler1);
        initialStep = 0;
        firstTime= true;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        // create our manager instance after the content view is set
        mTintManager = new SystemBarTintManager(this);
        // enable status bar tint
        mTintManager.setStatusBarTintEnabled(true);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setCurrentItem(1);
        //changeColor(getResources().getColor(R.color.green));
        tabs.setBackgroundColor(getResources().getColor(R.color.green));

        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(MainActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });
         //m = ((StepsFragment) getSupportFragmentManager().findFragmentById(R.id.steps_fragment));


  }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contact:
                QuickContactFragment.newInstance().show(getSupportFragmentManager(), "QuickContactFragment");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeColor(int newColor) {
        tabs.setBackgroundColor(newColor);
        mTintManager.setTintColor(newColor);
        // change ActionBar color just if an ActionBar is available
        Drawable colorDrawable = new ColorDrawable(newColor);
        Drawable bottomDrawable = new ColorDrawable(getResources().getColor(android.R.color.transparent));
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});
        if (oldBackground == null) {
            getSupportActionBar().setBackgroundDrawable(ld);
        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});
            getSupportActionBar().setBackgroundDrawable(td);
            td.startTransition(200);
        }

        oldBackground = ld;
        currentColor = newColor;
    }

//    public void onColorClicked(View v) {
//        int color = Color.parseColor(v.getTag().toString());
//        changeColor(color);
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentColor = savedInstanceState.getInt("currentColor");
        changeColor(currentColor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
        // if you unregister the last listener, the hardware will stop detecting step events
//        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {



        if (activityRunning) {
            StepsFragment frag = (StepsFragment) adapter.getRegisteredFragment(0);
            if (frag != null) {
                // If article frag is available, we're in two-pane layout...

                // Call a method in the ArticleFragment to update its content


            if(firstTime){
                initialStep = (int) event.values[0];
                steps= event.values[0] - initialStep;
                count.setText(String.valueOf(event.values[0] - initialStep));
                firstTime= false;

                steps= event.values[0] - initialStep;
                frag.updateCountView(steps);



                }else{
                count.setText(String.valueOf(event.values[0] - initialStep));
                steps= event.values[0] - initialStep;
//                if(articleFrag != null)
//                    articleFrag.updateArticleView(event.values[0] - initialStep);
                    steps= event.values[0] - initialStep;
                frag.updateCountView(steps);

            }

            myhandler1 = new View.OnClickListener() {
                public void onClick(View v) {
                    initialStep =  (int) event.values[0];
                    count.setText(String.valueOf(event.values[0] - initialStep));
//                    if(articleFrag != null)
//                        articleFrag.updateArticleView(event.values[0] - initialStep);
                   // frag.updateCountView(steps);
                }
            };
        }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }





    // Extend from SmartFragmentStatePagerAdapter now instead for more dynamic ViewPager items
    public static class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return StepsFragment.newInstance(0);
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return StepsFragment.newInstance(1);
                case 2: // Fragment # 1 - This will show SecondFragment
                    return StepsFragment.newInstance(2);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

}



abstract class SmartFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    // Sparse array to keep track of registered fragments in memory
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public SmartFragmentStatePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        Log.d("PUT","FRAGMENT");
        registeredFragments.put(position, fragment);
        return fragment;
    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    // Returns the fragment for the position (if instantiated)
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}