
package util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import main.SensorListener;
import Database.Database;


public class ShutdownRecevier extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        context.startService(new Intent(context, SensorListener.class));

        // if the user used a root script for shutdown, the DEVICE_SHUTDOWN
        // broadcast might not be send. Therefore, the app will check this
        // setting on the next boot and displays an error message if it's not
        // set to true
        context.getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS).edit()
                .putBoolean("correctShutdown", true).commit();

        Database db = Database.getInstance(context);
        // if it's already a new day, add the temp. steps to the last one
        if (db.getSteps(Util.getToday()) == Integer.MIN_VALUE) {
            int steps = db.getCurrentSteps();
            int pauseDifference = steps -
                    context.getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS)
                            .getInt("pauseCount", steps);
            db.insertNewDay(Util.getToday(), steps - pauseDifference);
            if (pauseDifference > 0) {
                // update pauseCount for the new day
                context.getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS).edit()
                        .putInt("pauseCount", steps).commit();
            }
        } else {
            db.updateSteps(Util.getToday(), db.getCurrentSteps());
        }
        // current steps will be reset on boot
        db.close();
    }

}
