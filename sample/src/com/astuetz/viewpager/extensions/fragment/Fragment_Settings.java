/*
 * Copyright 2014 Thomas Hoffmann
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
package com.astuetz.viewpager.extensions.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.astuetz.viewpager.extensions.sample.R;


import java.util.Calendar;
import java.util.Locale;



public class Fragment_Settings extends PreferenceFragment implements OnPreferenceClickListener {

    public final static int DEFAULT_GOAL = 250;
    final static float DEFAULT_STEP_SIZE = Locale.getDefault() == Locale.US ? 2.5f : 75f;
    public final static String DEFAULT_STEP_UNIT = Locale.getDefault() == Locale.US ? "ft" : "cm";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        final SharedPreferences prefs =
                getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
//
//        Preference goal = findPreference("goal");
//        goal.setOnPreferenceClickListener(this);
//        goal.setSummary(getString(R.string.goal_summary, prefs.getInt("goal", DEFAULT_GOAL)));


        Preference stepsize = findPreference("stepsize");
        stepsize.setOnPreferenceClickListener(this);
        stepsize.setSummary(getString(R.string.step_size_summary,
                prefs.getFloat("stepsize_value", DEFAULT_STEP_SIZE),
                prefs.getString("stepsize_unit", DEFAULT_STEP_UNIT)));
        Preference send = findPreference("send data");
        send.setOnPreferenceClickListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setBackgroundColor(Color.WHITE);
        getView().setClickable(true);
    }


    @Override
    public boolean onPreferenceClick(final Preference preference) {
        AlertDialog.Builder builder;
        View v;
        final SharedPreferences prefs =
                getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
        switch (preference.getTitleRes()) {
            case R.string.send_data:
                showAlertDialog("Sure", "Are you sure you want to send me the data ??");
                break;
            case R.string.step_size:
                builder = new AlertDialog.Builder(getActivity());
                v = getActivity().getLayoutInflater().inflate(R.layout.stepsize, null);
                final RadioGroup unit = (RadioGroup) v.findViewById(R.id.unit);
                final EditText value = (EditText) v.findViewById(R.id.value);
                unit.check(
                        prefs.getString("stepsize_unit", DEFAULT_STEP_UNIT).equals("cm") ? R.id.cm :
                                R.id.ft);
                value.setText(String.valueOf(prefs.getFloat("stepsize_value", DEFAULT_STEP_SIZE)));
                builder.setView(v);
                builder.setTitle(R.string.set_step_size);
                builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            prefs.edit().putFloat("stepsize_value",
                                    Float.valueOf(value.getText().toString()))
                                    .putString("stepsize_unit",
                                            unit.getCheckedRadioButtonId() == R.id.cm ?
                                                    "cm" : "ft").apply();
                            preference.setSummary(getString(R.string.step_size_summary,
                                    Float.valueOf(value.getText().toString()),
                                    unit.getCheckedRadioButtonId() == R.id.cm ? "cm" :
                                            "ft"));
                        } catch (NumberFormatException nfe) {
                            nfe.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;

        }
        return false;
    }

    public void showAlertDialog( String title, String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                                int which) {
                Log.d("YEEES","YEESS");
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"viktorbakayov@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                    i.putExtra(Intent.EXTRA_TEXT, prapareMessageString());
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        //Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("YEEES2", "YEESS2");
                } catch (Exception e) {
                    Log.d("SendMail", e.getMessage(), e);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                                int which) {
                dialog.dismiss();
            }
        });
        // Setting Dialog Message
        builder.setMessage(message);
        // Setting alert dialog icon
      //  builder.setIcon((status) ? R.drawable.success : R.drawable.fail);

        builder.create().show();
    }

    private String prapareMessageString() {
        String message= "";
        Database db = Database.getInstance(getActivity());
        String total = Integer.toString(db.getTotalWithoutToday());
        int total_start = db.getTotalWithoutToday();
        int  total_days = db.getDays();
        int  todayOffset = db.getSteps(Util.getToday());
        int since_boot = db.getCurrentSteps();
        int  steps = Math.max(todayOffset + since_boot, 0);


         message += "Total Steps:"+ total+ "\r\n";
        message+=" Taken steps for the last 7 days:"+"\r\n";
        message+= generateData(db, false);
        message+="\r\n";
        message+="Average Step Count:"+"\r\n";
        message+=  Integer.toString((total_start + steps / total_days));
        message+="\r\n";
        message+=" Collected stickers for the last 7 days:"+"\r\n";
        message+= generateData(db, true);
        message+="\r\n";
        message+=" Stickers glued:"+"\r\n";
        message+= db.getNumberGluedStickers();
        message+="\r\n";
        message+=" Stickers recieved:"+"\r\n";
        message+= db.getNumberRecievedStickers();


        db.close();
        return  message;
    }

    private String generateData(Database db, Boolean stickers) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTimeInMillis(Util.getToday());
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        yesterday.add(Calendar.DAY_OF_YEAR, -6);
       String message="( ";
        for (int i = 0; i < 7; i++) {
            int values;
            if (stickers){
                values = db.getStickerCount(yesterday.getTimeInMillis());}
            else
            {
                values =  db.getSteps(yesterday.getTimeInMillis());
            }
            Log.d("stickers", Integer.toString(values));
            if(values !=Integer.MIN_VALUE) {
                message+=values+",";
            }else{
                message+=0+",";
            }
            yesterday.add(Calendar.DAY_OF_YEAR, 1);
        }

        message+=") ";
        return message;
    }


}
