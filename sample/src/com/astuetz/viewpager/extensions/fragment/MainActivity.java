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

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.SparseArray;
import android.util.TypedValue;

import bluetoothchat.DeviceListActivity;
import logger.Log;


import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.astuetz.SlidingTabLayout;
import com.astuetz.viewpager.extensions.sample.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import bluetoothchat.BluetoothChatFragment;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity  {

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
    private MyReceiver myReceiver;


    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;

    private MyPagerAdapter adapter;//
    // Whether the Log Fragment is currently shown
    private boolean mLogShown;

    private Drawable oldBackground = null;
    private int currentColor;
    private SystemBarTintManager mTintManager;
    private SmartFragmentStatePagerAdapter adapterViewPager;
    private StepsFragment m;
    private int since_boot;
    private int todayOffset;

    @Override
    protected void onCreate(Bundle b) {
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
        pager.setCurrentItem(1);
        //changeColor(getResources().getColor(R.color.green));
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



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.clear();
//        getMenuInflater().inflate(R.menu.bluetooth_chat, menu);
//        menu.findItem(R.id.secure_connect_scan).setVisible(false);
//        return true;
//    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
//        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
//        logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);
//
//        return super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
     //   MenuItem pause = menu.getItem(0);
       // Drawable d;
//        if (getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS)
//                .contains("pauseCount")) { // currently paused
//            pause.setTitle(R.string.resume);
//            d = getResources().getDrawable(R.drawable.ic_resume);
//        } else {
//            pause.setTitle(R.string.pause);
//            d = getResources().getDrawable(R.drawable.ic_pause);
//        }
        //d.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
      //  pause.setIcon(d);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())  {
            case R.id.secure_connect_scan: {
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;
        }
        case R.id.insecure_connect_scan: {
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            return true;
        }
        case R.id.discoverable: {
            // Ensure this device is discoverable by others
            BluetoothChatFragment frag = (BluetoothChatFragment) adapter.getRegisteredFragment(3);
            if(frag != null)
                    frag.ensureDiscoverable();
            return true;
        }


        default:
        return optionsItemSelected(item);

    }
    }


    public boolean optionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    getFragmentManager().popBackStackImmediate();
                }
                break;
            case R.id.action_settings:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    getFragmentManager().beginTransaction()
                            .replace(android.R.id.content, new Fragment_Settings()).addToBackStack(null)
                            .commit();
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
//        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//        if (countSensor != null) {
//            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
//        } else {
//          //  Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
//        }

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


    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            StepsFragment frag = (StepsFragment) adapter.getRegisteredFragment(0);
            if(frag != null) {
                int datapassed = arg1.getIntExtra("DATAPASSED", 0);
//                Toast.makeText(MainActivity.this,
//                        "Triggered by Service!\n"
//                                + "Data passed: " + String.valueOf(datapassed),
//                        Toast.LENGTH_LONG).show();
//                Log.w("TRIGGED BY SERVICE", String.valueOf(datapassed));
                frag.updateCountView(datapassed);
            }
        }
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
                    return AlbumFragment.newInstance(1);
                case 2: // Fragment # 3 - This will show Stickers
                    return StickersFragment.newInstance(2);
                case 3 : //Fragmern 4 - Swapping
                    return BluetoothChatFragment.newInstance(3);
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