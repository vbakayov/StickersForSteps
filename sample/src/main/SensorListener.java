/*
 * Copyright 2013 Thomas Hoffmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package main;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import Database.Database;
import Steps.StepDetector;
import Steps.StepListener;
import util.Fragment_Settings;

import com.astuetz.viewpager.extensions.sample.R;

import java.text.NumberFormat;
import java.util.Locale;

import util.Util;


/**
 * Background service which keeps the step-sensor listener alive to always get
 * the number of steps
 *
 *
 *
 */
public class SensorListener extends Service implements SensorEventListener, StepListener {
    private final static int NOTIFICATION_ID = 1;
    private final static int BaseGOAL = 500;
    public final static String ACTION_PAUSE = "pause";
    public final static String ACTION_STEPS = "ACTION_STEPS";
    private  float goal =  Fragment_Settings.DEFAULT_GOAL;
    private static boolean WAIT_FOR_VALID_STEPS = true;
    private static int steps;
    private int since_boot;
    private int todayOffset;


    private StepDetector mStepDetector;
    private Sensor mSensor;


    private final static int MICROSECONDS_IN_ONE_MINUTE = 60000000;



    @Override
    public void onAccuracyChanged(final Sensor sensor, int accuracy) {
        // nobody knows what happens here: step value might magically decrease
        // when this method is called...
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        Log.w("CHAAANGE", "Chhange");
        steps=  (int) event.values[0];
        saveSteps(true,steps);

    }


    @Override
    public void onStep() {
        steps++;
        saveSteps(false,steps);
    }





    @Override
    public IBinder onBind(final Intent intent) {
        return null;

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        //pausing counting was not succesfully implemented
        if (intent != null && ACTION_PAUSE.equals(intent.getStringExtra("action"))) {
            if (steps == 0) {
                Database db = Database.getInstance(this);
                steps = db.getCurrentSteps();
                db.close();
            }
            SharedPreferences prefs = getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
            if (prefs.contains("pauseCount")) { // resume counting
                int difference = steps -
                        prefs.getInt("pauseCount", steps); // number of steps taken during the pause
                Database db = Database.getInstance(this);
                db.updateSteps(Util.getToday(), -difference);
                db.close();
                prefs.edit().remove("pauseCount").commit();
                updateNotificationState();
            } else { // pause counting
                // cancel restart
                ((AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE))
                        .cancel(PendingIntent.getService(getApplicationContext(), 2,
                                new Intent(this, SensorListener.class),
                                PendingIntent.FLAG_UPDATE_CURRENT));
                prefs.edit().putInt("pauseCount", steps).commit();
                updateNotificationState();
                stopSelf();
                return START_NOT_STICKY;
            }
        }

        // restart service every hour to get the current step count
        ((AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC, System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR,
                        PendingIntent.getService(getApplicationContext(), 2,
                                new Intent(this, SensorListener.class),
                                PendingIntent.FLAG_UPDATE_CURRENT));

        WAIT_FOR_VALID_STEPS = true;

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
        goal = prefs.getInt("goal", Fragment_Settings.DEFAULT_GOAL);
        reRegisterSensor();
        updateNotificationState();
    }

    @Override
    public void onTaskRemoved(final Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        // Restart service in 500 ms
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC, System.currentTimeMillis() + 500, PendingIntent
                        .getService(this, 3, new Intent(this, SensorListener.class), 0));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      //  if (BuildConfig.DEBUG) Logger.log("SensorListener onDestroy");
        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            sm.unregisterListener(this);
        } catch (Exception e) {
            //if (BuildConfig.DEBUG) Logger.log(e);
            e.printStackTrace();
        }
    }


    /**
     * Notification for achieved goal
     * This functionality was removed for the final iteration, but it may
     * be added for future iterations
     * */
    private void updateNotificationState() {
        Log.w("SensorListener", " updateNotificationState");
        SharedPreferences prefs = getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
        NotificationManager nm =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (prefs.getBoolean("notification", true)) {
            int goal = prefs.getInt("goal", 10000);
            Database db = Database.getInstance(this);
            int today_offset = db.getSteps(Util.getToday());
            if (steps == 0)
                steps = db.getCurrentSteps(); // use saved value if we haven't anything better
            db.close();
            Notification.Builder notificationBuilder = new Notification.Builder(this);
            if (steps > 0) {
                if (today_offset == Integer.MIN_VALUE) today_offset = -steps;
                notificationBuilder.setProgress(goal, today_offset + steps, false).setContentText(
                        today_offset + steps >= goal ? getString(R.string.goal_reached_notification,
                                NumberFormat.getInstance(Locale.getDefault())
                                        .format((today_offset + steps))) :
                                getString(R.string.notification_text,
                                        NumberFormat.getInstance(Locale.getDefault())
                                                .format((goal - today_offset - steps))));
            } else { // still no step value?
                notificationBuilder
                        .setContentText(getString(R.string.your_progress_will_be_shown_here_soon));
            }
            boolean isPaused = prefs.contains("pauseCount");
//            notificationBuilder.setPriority(Notification.PRIORITY_MIN).setShowWhen(false)
//                    .setContentTitle(isPaused ? getString(R.string.ispaused) :
//                            getString(R.string.notification_title)).setContentIntent(PendingIntent
//                    .getActivity(this, 0, new Intent(this, MainActivity.class),
//                            PendingIntent.FLAG_UPDATE_CURRENT));
//            nm.notify(NOTIFICATION_ID, notificationBuilder.build());
        } else {
            nm.cancel(NOTIFICATION_ID);
        }
    }

//    private void reRegisterSensor() {
//        //Sensor Manager
//        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
//        sm.unregisterListener(this);
//
//        if(sm.getSensorList(Sensor.TYPE_STEP_COUNTER).size() != 0) {
//            sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_UI);
//        }else{
//            Toast.makeText(this, "Step detector not available! Don't worry Accelerometer is available", Toast.LENGTH_LONG).show();
//            StepDetector  mStepDetector = new StepDetector(this);
//            sm.registerListener(mStepDetector, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
//        }
//
//    }
private void reRegisterSensor() {
    SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
    try {
        sm.unregisterListener(this);
    } catch (Exception e) {
        e.printStackTrace();
    }
    if(sm.getSensorList(Sensor.TYPE_STEP_COUNTER).size() != 0) {
        // enable batching
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_UI);
    }else{
        Toast.makeText(this, "Step detector not available! Don't worry Accelerometer is available", Toast.LENGTH_LONG).show();
        StepDetector  mStepDetector = new StepDetector(this);
        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mStepDetector, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

}


    private void saveSteps(boolean isHardwareCount, int steps) {
         Database db = Database.getInstance(this);
        android.util.Log.w("CountActivity", Integer.toString(steps));
        if (db.getSteps(Util.getToday()) == Integer.MIN_VALUE) {
            //start of new day
            db.insertNewDay(Util.getToday(), steps);
            //set goal to be the goal for today
            goal= BaseGOAL;
            saveNextGoal((int)goal);
        }

        Log.d("SaveCurrentSteps", Integer.toString(steps));
        db.saveCurrentSteps(steps);
        todayOffset = db.getSteps(Util.getToday());
        Log.w("TodayOfset", Integer.toString(todayOffset));
        since_boot = db.getCurrentSteps();
        Log.w("StepsSinceReboot", Integer.toString(since_boot));
        // todayOffset might still be Integer.MIN_VALUE on first start
        int steps_today = Math.max(todayOffset + since_boot, 0);
        Log.w("StepsToday", Integer.toString(steps_today));
        db.close();

        if(steps_today>= (int)goal){
            Log.w("GOOOAL", "ACHIEVED");
            goal= goal+goal;
            saveNextGoal((int)goal);
            updateStickerPackCountIncrease();
            updateWonStickerCount();
        }

        //send broadcast so that Steps can be updated dynamically
        Intent intent = new Intent();
        intent.setAction(ACTION_STEPS);
        intent.putExtra("stepsToday", steps_today);
        sendBroadcast(intent);

    }

    private void updateWonStickerCount() {
        Database db = Database.getInstance(this);
        if (db.getStickerCount(Util.getToday()) == Integer.MIN_VALUE) {
            db.insertNewDayStickers(Util.getToday(), 3);
            Log.w("not existing", "create today");
        }
        else{
            db.updateStickerCount(Util.getToday(), 3);
            Log.w("existing", Integer.toString(db.getStickerCount(Util.getToday())));
        }
        db.close();
    }

    private void saveNextGoal(int nextGoal) {
        SharedPreferences prefs =getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("goal", nextGoal);
        editor.commit();
    }

    private void updateStickerPackCountIncrease() {
        SharedPreferences prefs = getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
        int availableStickerPacks = prefs.getInt("packs", 0);
        availableStickerPacks++;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("packs", availableStickerPacks);
        editor.commit();
    }

}
