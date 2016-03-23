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
package util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import Database.Database;
import main.MainActivity;
import com.astuetz.viewpager.extensions.sample.R;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Locale;


public class Fragment_Settings extends PreferenceFragment implements OnPreferenceClickListener {

    public final static int DEFAULT_GOAL = 500;
    public final static float DEFAULT_Human_Height = 175f;
    public final static String DEFAULT_STEP_UNIT = Locale.getDefault() == Locale.US ? "ft" : "cm";
    public final static String DEFAULT_SEX= "male";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        final SharedPreferences prefs =
                getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);


        Preference stepsize = findPreference("stepsize");
        stepsize.setOnPreferenceClickListener(this);
        stepsize.setSummary(getString(R.string.step_size_summary,
              prefs.getFloat("height_value", DEFAULT_Human_Height),
                prefs.getString("stepsize_unit", DEFAULT_STEP_UNIT)
       ) );
        Preference send = findPreference("send data");
        send.setOnPreferenceClickListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

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
            //if the menu clicked is 'Send Data'
            case R.string.send_data:
                showAlertDialog("Sure", "Are you sure you want to send me the data ??");
                break;

            //if the menu clicked is 'Height'
            case R.string.step_size:
                builder = new AlertDialog.Builder(getActivity());
                v = getActivity().getLayoutInflater().inflate(R.layout.stepsize, null);
                final RadioGroup unit = (RadioGroup) v.findViewById(R.id.unit);
                final RadioGroup sex = (RadioGroup) v.findViewById(R.id.sex);
                final EditText value = (EditText) v.findViewById(R.id.value);
                unit.check(
                        prefs.getString("stepsize_unit", DEFAULT_STEP_UNIT).equals("cm") ? R.id.cm :
                                R.id.ft);

                sex.check(
                        prefs.getString("sex_value", DEFAULT_SEX).equals("male") ? R.id.male :
                                R.id.female);
                value.setText(String.valueOf(prefs.getFloat("height_value", DEFAULT_Human_Height)));
                builder.setView(v);
                builder.setTitle(R.string.set_step_size);
                builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            prefs.edit().putFloat("height_value",
                                    Float.valueOf(value.getText().toString()))
                                    .putString("stepsize_unit",
                                            unit.getCheckedRadioButtonId() == R.id.cm ?
                                                    "cm" : "ft").apply();
                            prefs.edit().putString("sex",
                                    (value.getText().toString()))
                                    .putString("sex_value",
                                            sex.getCheckedRadioButtonId() == R.id.male ?
                                                    "male" : "female").apply();

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
                    createCachedFile(getActivity(), "data.txt", prapareMessageString());
                    String bodyMessage = "Hi There, The information for your steps and collected stickers is in the file attached."+
                                        "If you have any additional comments you may write them here below this text. \n\n"+
                                        "Thank you for your participation!!!";
                    Intent emailIntent = getSendEmailIntent(getActivity(),"viktorbakayov@gmail.com","SFS Participant Data",bodyMessage,"data.txt");

                    try {
                       startActivityForResult(emailIntent,1);
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

    public static Intent getSendEmailIntent(Context context, String email,
                                            String subject, String body, String fileName) {

        final Intent emailIntent = new Intent(
                android.content.Intent.ACTION_SEND);

        //Explicitly only use Gmail to send
        emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");

        emailIntent.setType("plain/text");

        //Add the recipients
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[] { email });

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);

        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);

        //Add the attachment by specifying a reference to our custom ContentProvider
        //and the specific file of interest
        // this create a URI sting for the locations of the file
        emailIntent.putExtra(
                Intent.EXTRA_STREAM,
                Uri.parse("content://" + CachedFileProvider.AUTHORITY + "/"
                        + fileName));

        return emailIntent;
    }

    /**
     * generate the file to be send with the Gmail client
     * @param context
     * @param fileName
     * @param content
     * @throws IOException
     */
    public static void createCachedFile(Context context, String fileName,
                                        String content) throws IOException {

        File cacheFile = new File(context.getCacheDir() + File.separator
                + fileName);
        cacheFile.createNewFile();

        FileOutputStream fos = new FileOutputStream(cacheFile);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
        PrintWriter pw = new PrintWriter(osw);

        pw.println(content);

        pw.flush();
        pw.close();
    }

    /**
     * generate the message sttring containing the information we need
     * the sting consists for Total Steps, steps taken for the last 7 days
     * average step count, collected stickers for the last 7 days; stickers glued
     * stickers recived
     * @return
     */
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
        //number recievedStickers does not cover the case
        //when the sticker is already glued, but it is already recieved
        // as its status
        //will be 2 and the logic transfers to checking the count
        message+= db.getNumberRecievedStickers();
        db.close();

    return  message;
    }

    /**
     *  generate collected stickers for the last 7 days Boolean sticker = true
     *  generate Collected stickers for the last 7 days Boolean sticker = false
     * @param db
     * @param stickers
     * @return
     */
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
