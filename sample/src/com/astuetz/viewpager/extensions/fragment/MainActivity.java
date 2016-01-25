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

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;



import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.astuetz.SlidingTabLayout;
import com.astuetz.viewpager.extensions.sample.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import bluetoothchat.BluetoothChatFragment;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements StepsFragment.OnStickerChange  {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tabs)
    SlidingTabLayout tabs;
    @InjectView(R.id.pager)
    ViewPager pager;

    private SensorManager sensorManager;
    private boolean firstTime;
    private int initialStep;
    private Button refresh;
    boolean activityRunning;
    private View.OnClickListener myhandler1;
    private int steps;
    private Database db;
//    private SensorManager mSensorManager;
//    private StepDetector mStepDetector;
//    private Sensor mSensor;

    private MyReceiver myReceiver;



    private MyPagerAdapter adapter;//

    private Drawable oldBackground = null;
    private int currentColor;
    private SystemBarTintManager mTintManager;
    private SmartFragmentStatePagerAdapter adapterViewPager;
    private StepsFragment m;
    private int since_boot;
    private int todayOffset;
    private int total_start;
    private static StickersFragment fragmenttStickers;
    private static BluetoothChatFragment fragmentBlt;

    @Override
    protected void onCreate(Bundle b) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(b);

        setContentView(R.layout.activity_main);
        startService(new Intent(this, SensorListener.class));

//        if (b == null) {
//            // Create new fragment and transaction
//            android.app.Fragment newFragment = new Fragment_Overview();
//            FragmentTransaction transaction = null;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
//                transaction = getFragmentManager().beginTransaction();
//
//
//            // Replace whatever is in the fragment_container view with this
//            // fragment,
//            // and add the transaction to the back stack
//            transaction.replace(android.R.id.content, newFragment);
//
//            // Commit the transaction
//            transaction.commit();
//            }
//        }

//        if (savedInstanceState == null) {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            BluetoothChatFragment fragment = new BluetoothChatFragment();
//             transaction.replace(R.id.sample_content_fragment, fragment);
//            transaction.commit();
//        }

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
        tabs.setDistributeEvenly(true);
        tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setOffscreenPageLimit(3);
        pager.setCurrentItem(0);
        tabs.setBackgroundColor(getResources().getColor(R.color.green));

//        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
//            @Override
//            public void onTabReselected(int position) {
//                Toast.makeText(MainActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
//            }
//        });

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
//        SpannableString s = new SpannableString(" Stickers For Steps");
//        s.setSpan(new TypefaceSpan("IDroid Bold.otf"), 0, s.length(),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        //getSupportActionBar().setTitle(s);
        getSupportActionBar().setIcon(R.drawable.icon);


        Database db = Database.getInstance(this);
       db.getSticker(3);

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub

        //Register BroadcastReceiver
        //to receive event from our service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SensorListener.ACTION_STEPS);
        registerReceiver(myReceiver, intentFilter);
        super.onStart();
    }

    @Override
    protected  void onStop(){
        unregisterReceiver(myReceiver);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        Database db = Database.getInstance(this);
        // read todays offset
        todayOffset = db.getSteps(Util.getToday());

        SharedPreferences prefs =
                MainActivity.this.getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);

        since_boot = db.getCurrentSteps(); // do not use the value from the sharedPreferences
        int pauseDifference = since_boot - prefs.getInt("pauseCount", since_boot);

        // register a sensorlistener to live update the UI if a step is taken
        // Start detecting
//        mStepDetector = new StepDetector();
//        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        registerDetector();
//        mStepDetector.setSensitivity(10);

        since_boot -= pauseDifference;
        total_start = db.getTotalWithoutToday();

        db.close();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bluetooth_chat, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //nasty hack to fix the chat pass the menu item to the BluetoothChat, if the menu
        //is of type connect to device or make discoverable do the operations if not
        // the menu will be passed back to the next method (optionsItemSelected)
        BluetoothChatFragment frag = (BluetoothChatFragment) adapter.getRegisteredFragment(3);
        if(frag != null)
            frag. onOptionsItemSelectedPrivate(item);

    return true;
    }

    @Override
    public void onBackPressed() {
        if (fragmentBlt.backPressed()) { // and then you define a method allowBackPressed with the logic to allow back pressed or not
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStackImmediate();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Really Exit?")
                        .setMessage("Are you sure you want to exit?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                MainActivity.super.onBackPressed();
                            }
                        }).create().show();
            }
        }

//        else{        //so that pressing back arrou on settings fragmen does not go out of the activity
//        if (getFragmentManager().getBackStackEntryCount() > 0) {
//            getFragmentManager().popBackStackImmediate();
//        } else {
//            finish();
//        }
    }



    public boolean optionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    getFragmentManager().popBackStackImmediate();
                }
                break;
            case R.id.action_settings:
                    getFragmentManager().beginTransaction()
                            .replace(android.R.id.content, new Fragment_Settings()).addToBackStack("SettingsFragment")
                            .commit();
                break;
            case R.id.action_achievements:

                populateAchievementView();
                ArrayList<Achievement> list=   AchievementStorage.getAchievements();
                if(list != null) {
                    Intent myIntent = new Intent(this, ListActivity.class);
                    myIntent.putExtra("filtered", list); //pass the filted array with the trips
                    this.startActivity(myIntent);
                }
                break;
        }
        return true;
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

    private void populateAchievementView() {
        AchievementStorage.clear();

        Database db = Database.getInstance(this);
        db.removeInvalidEntries();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int count = prefs.getInt("swap count", 0);
        AchievementStorage.addAchievement(new Achievement("swap", count>= 1, "swap1", "swap a sticker with the bluetooth"));
        AchievementStorage.addAchievement(new Achievement("swap 2", count >= 3, "swap1", "swap 3 stickers with the bluetooth"));
        AchievementStorage.addAchievement(new Achievement("swap 3", count >= 5, "swap1", "swap 5 stickers with the bluetooth"));

        int numberRecieved = prefs.getInt("received stickers", 0);

        AchievementStorage.addAchievement(new Achievement("Pack 1", numberRecieved>= 6, "swap1", "receive 6 stickers"));
        AchievementStorage.addAchievement(new Achievement("Pack 2", numberRecieved >= 15, "swap1", "receive 15 stickers"));
        AchievementStorage.addAchievement(new Achievement("Pack 3", numberRecieved >= 30, "swap1", "receive 30 stickers"));

        int numberGlued=     prefs.getInt("glued stickers", 0);

        AchievementStorage.addAchievement(new Achievement("Collection", numberGlued>= 5, "swap1", "Stick 5 stickers into the album"));
        AchievementStorage.addAchievement(new Achievement("Collection", numberGlued >= 10, "swap1", "Stick 10 stickers into the album"));
        AchievementStorage.addAchievement(new Achievement("Collection", numberGlued >= 15, "swap1", "Stick 15 stickers into the album"));


        //add the achievements to the storage
        AchievementStorage.addAchievement(new Achievement("boots distance", prefs.getBoolean("boots distance", false), "boots1", "walk 7500 steps in one day"));


        if (!prefs.getBoolean("boots distance 2", false)) {
            Cursor c =
                    db.query(new String[]{"steps"}, "steps >= 10000 AND date > 0", null, null,
                            null, null, "1");
            if (c.getCount() >= 1) {
                prefs.edit().putBoolean("boots distance 2", true).apply();
            }
            c.close();
        }
        AchievementStorage.addAchievement(new Achievement("boots distance 2", prefs.getBoolean("boots distance 2", false), "boots2", "walk 10000 steps in one day"));


        if (!prefs.getBoolean("boots distance 3", false)) {
            Cursor c =
                    db.query(new String[]{"steps"}, "steps >= 20000 AND date > 0", null, null,
                            null, null, "1");
            if (c.getCount() >= 1) {
                prefs.edit().putBoolean("boots distance 3", true).apply();
            }
            c.close();
        }
        AchievementStorage.addAchievement(new Achievement("boots distance 3", prefs.getBoolean("boots distance 3", false), "boots3", "walk 20000 steps in one day"));

        if (!prefs.getBoolean("boots distance 4", false)) {
            Cursor c =
                    db.query(new String[]{"steps"}, "steps >= 25000 AND date > 0", null, null,
                            null, null, "1");
            if (c.getCount() >= 1) {
                prefs.edit().putBoolean("boots distance 4", true).apply();
            }
            c.close();
        }
        AchievementStorage.addAchievement(new Achievement("boots distance 4", prefs.getBoolean("boots distance 4", false), "boots4", "walk 25000 steps in one day"));



            Cursor c = db.query(new String[]{"COUNT(*)"}, "steps >= 10000 AND date > 0", null, null,
                    null, null, null);
            c.moveToFirst();
            int daysForStamina = c.getInt(0);
            c.close();
        if (!prefs.getBoolean("stamina", false)) {
            if (daysForStamina >= 5)  {
                prefs.edit().putBoolean("stamina", true).apply();
            }
        }
        AchievementStorage.addAchievement(new Achievement("stamina", prefs.getBoolean("stamina", false), "stamina1", "walk more than 10 000 steps per day on 5 different days"));

        if (!prefs.getBoolean("stamina 2", false)) {
            if (daysForStamina >= 10)  {
                prefs.edit().putBoolean("stamina 2", true).apply();
            }
        }
        AchievementStorage.addAchievement(new Achievement("stamina 2", prefs.getBoolean("stamina 2", false), "stamina2", "walk more than 10 000 steps per day on 10 different days"));

        if (!prefs.getBoolean("stamina 3", false)) {
            if (daysForStamina >= 20)  {
                prefs.edit().putBoolean("stamina 3", true).apply();
            }
        }
        AchievementStorage.addAchievement(new Achievement("stamina 3", prefs.getBoolean("stamina 3", false), "stamina3", "walk more than 10 000 steps per day on 20 different days"));

        int totalSteps = db.getTotalWithoutToday();

        if (!prefs.getBoolean("maraton", false)) {
            if (totalSteps > 10000)  {
                prefs.edit().putBoolean("maraton", true).apply();
            }
        }
        AchievementStorage.addAchievement(new Achievement("marathon", prefs.getBoolean("maraton", false), "maraton1", "walk total of 10 000 steps"));

        if (!prefs.getBoolean("maraton 2", false)) {
            if (totalSteps > 25000)  {
                prefs.edit().putBoolean("maraton 2", true).apply();
            }
        }
        AchievementStorage.addAchievement(new Achievement("marathon 2", prefs.getBoolean("maraton 2", false), "maraton2", "walk total of 25 000 steps"));

        if (!prefs.getBoolean("maraton 3", false)) {
            if (totalSteps > 50000)  {
                prefs.edit().putBoolean("maraton 3", true).apply();
            }
        }
        AchievementStorage.addAchievement(new Achievement("marathon 3", prefs.getBoolean("maraton 3", false), "maraton3", "walk total of 50 000 steps"));

    }

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
    protected void onPause() {
        super.onPause();
        activityRunning = false;
        // if you unregister the last listener, the hardware will stop detecting step events
//        sensorManager.unregisterListener(this);
    }


       @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void notifyChange() {
        fragmenttStickers.updateList();
        Log.w("Notifed","Notified");
    }


    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            StepsFragment frag = (StepsFragment) adapter.getRegisteredFragment(0);
            if(frag != null) {
                int datapassed = arg1.getIntExtra("stepsToday", 0);
                todayOffset= arg1.getIntExtra("todayOffset",0);
                since_boot= arg1.getIntExtra("since_boot",0);
                frag.updateCountView(datapassed);
            }
        }
    }


    public ViewPager getViewPager() {
        return AlbumFragmentMain.getViewPagerAlbum()    ;
    }
    public ViewPager getMainPager() {
        return pager    ;
    }


    // Extend from SmartFragmentStatePagerAdapter now instead for more dynamic ViewPager items
    public static class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
        private static int NUM_ITEMS = 4;
        private final String[] TITLES = {"Steps", "Album","Stickers","Swap"};

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
                case 0: // Fragment # 1 - This will show Steps Fragment
                    return StepsFragment.newInstance(0);
                case 1: // Fragment # 2 - This will show  Album
                    return AlbumFragmentMain.newInstance(3);
                case 2: // Fragment # 3 - This will show Stickers
                        fragmenttStickers= StickersFragment.newInstance(2); return fragmenttStickers;
                case 3 : //Fragmern 4 - Swapping
                       fragmentBlt =BluetoothChatFragment.newInstance(3);return fragmentBlt;


                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
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
        Log.d("PUT", "FRAGMENT");
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