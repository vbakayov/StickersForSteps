package com.astuetz.viewpager.extensions.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import Database.Database;
import util.Fragment_Settings;
import com.astuetz.viewpager.extensions.sample.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import util.Util;


public class CombinedChartActivity extends FragmentActivity {
    private TextView totalView;
    private TextView averageView;
    private TextView averageDistanceView;
    private TextView totalDistanceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_combined);

        ListView lv = (ListView) findViewById(R.id.chart1);

        ArrayList<BarData> list = new ArrayList<>();
        //false for steps true for distance
        list.add(generateStepsOrDistanceData(false));
        list.add(generateStickerData());
        list.add(generateStepsOrDistanceData(true));


        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);


        Database db = Database.getInstance(this);
        int total_start = db.getTotalWithoutToday();
        int  total_days = db.getDays();
        int  todayOffset = db.getSteps(Util.getToday());
        int since_boot = db.getCurrentSteps();
        int  steps = Math.max(todayOffset + since_boot, 0);

        totalView = (TextView) findViewById(R.id.total);
        averageView = (TextView)findViewById(R.id.average);


        //code for the thousands numbeer separation
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);

        totalView.setText(formatter.format(total_start + steps));
        averageView.setText(formatter.format((total_start + steps) / total_days));

        SharedPreferences prefs = this.getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
        float height_value = prefs.getFloat("height_value", Fragment_Settings.DEFAULT_Human_Height);
      //  float sex = prefs.getFloat("sex", Fragment_Settings.DEFAULT_SEX);
        Log.d("height",String.valueOf(height_value));
      float stride_lenght;
        if (prefs.getString("sex", Fragment_Settings.DEFAULT_SEX).equals("male")){
            stride_lenght = (float) (height_value*0.415);
        }else {
            stride_lenght = (float) (height_value* 0.413);
        }
        float distance_total = (total_start + steps) * stride_lenght;
        if (prefs.getString("stepsize_unit", Fragment_Settings.DEFAULT_SEX).equals("cm")) {
            distance_total /= 100000;
        } else {
            distance_total /= 5280;
        }

        averageDistanceView = (TextView)findViewById(R.id.averageDistance);
        totalDistanceView = (TextView)findViewById(R.id.totalDistance);
        totalDistanceView.setText(String.format("%.1f km", distance_total));
        averageDistanceView.setText(String.format("%.1f km", distance_total / total_days));


    }

    private String[] getDayOrder() {
       ArrayList<String> mylist = new ArrayList<String>();
        Database db = Database.getInstance(this);
        Calendar yesterday = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("E", Locale.getDefault());
        yesterday.setTimeInMillis(Util.getToday());
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        yesterday.add(Calendar.DAY_OF_YEAR, -6);
        for (int i = 0; i < 7; i++) {
            int steps = db.getSteps(yesterday.getTimeInMillis());
            String date =  df.format(new Date(yesterday.getTimeInMillis()));
            Log.d("date", date);
            Log.d("space", "===========");
            mylist.add(date);
            yesterday.add(Calendar.DAY_OF_YEAR, 1);
        }

        String[] list = new String[mylist.size()];
        list = mylist.toArray(list);
        return list;
    }




    private LineData generateLineData() {
        //getStickerCount
        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<Entry>();

        Database db = Database.getInstance(this);
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTimeInMillis(Util.getToday());
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        yesterday.add(Calendar.DAY_OF_YEAR, -6);
        for (int i = 0; i < 7; i++) {
            int stickers = db.getStickerCount(yesterday.getTimeInMillis());
            Log.d("stickers", Integer.toString(stickers));
            if(stickers !=Integer.MIN_VALUE) {
                entries.add(new BarEntry(stickers, i));
            }
            yesterday.add(Calendar.DAY_OF_YEAR, 1);
        }
        LineDataSet set = new LineDataSet(entries, "Stickers");
        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData() {
        BarData d = new BarData();
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        Database db = Database.getInstance(this);
        SimpleDateFormat df = new SimpleDateFormat("E", Locale.getDefault());
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTimeInMillis(Util.getToday());
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        yesterday.add(Calendar.DAY_OF_YEAR, -6);
        for (int i = 0; i < 7; i++) {
            int steps = db.getSteps(yesterday.getTimeInMillis());

            String date =  df.format(new Date(yesterday.getTimeInMillis()));
            Log.d("steps", Integer.toString(steps));
            Log.d("date", date);
            Log.d("space", "===========");
            if (steps > 0) {
                entries.add(new BarEntry(steps, i));
            }else if(steps == Integer.MIN_VALUE){ //no dataa in the database
                entries.add(new BarEntry(0, i));
            }else if(steps < 0){
                entries.add(new BarEntry(Math.abs(steps), i));
            }
            yesterday.add(Calendar.DAY_OF_YEAR, 1);
        }
        BarDataSet set = new BarDataSet(entries, "Steps");
        set.setColor(Color.rgb(60, 220, 78));
        set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextSize(10f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        db.close();
        return d;
    }

    private class ChartDataAdapter extends ArrayAdapter<BarData> {

        private Typeface mTf;

        public ChartDataAdapter(Context context, List<BarData> objects) {
            super(context, 0, objects);

            mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            BarData data = getItem(position);

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();

                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item_barchart, null);
                holder.chart = (BarChart) convertView.findViewById(R.id.chart);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // apply styling
            data.setValueTypeface(mTf);
            data.setValueTextColor(Color.BLACK);
            holder.chart.setDescription("");
            holder.chart.setDrawGridBackground(false);

            XAxis xAxis = holder.chart.getXAxis();
            xAxis.setPosition(XAxisPosition.BOTTOM);
            xAxis.setTypeface(mTf);
            xAxis.setDrawGridLines(false);

            YAxis leftAxis = holder.chart.getAxisLeft();
            leftAxis.setTypeface(mTf);
            leftAxis.setLabelCount(5, false);
            leftAxis.setSpaceTop(15f);

            YAxis rightAxis = holder.chart.getAxisRight();
            rightAxis.setTypeface(mTf);
            rightAxis.setLabelCount(5, false);
            rightAxis.setSpaceTop(15f);

            // set data
            holder.chart.setData(data);

            // do not forget to refresh the chart
//            holder.chart.invalidate();
            holder.chart.animateY(700, Easing.EasingOption.EaseInCubic);

            return convertView;
        }

        private class ViewHolder {

            BarChart chart;
        }
    }
    private BarData generateStickerData() {
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        Database db = Database.getInstance(this);
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTimeInMillis(Util.getToday());
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        yesterday.add(Calendar.DAY_OF_YEAR, -6);
        for (int i = 0; i < 7; i++) {
            int stickers = db.getStickerCount(yesterday.getTimeInMillis());
            Log.d("stickers", Integer.toString(stickers));
            if(stickers !=Integer.MIN_VALUE) {
                entries.add(new BarEntry(stickers, i));
            }
            yesterday.add(Calendar.DAY_OF_YEAR, 1);
        }

        BarDataSet d = new BarDataSet(entries, " Collected stickers");
        d.setBarSpacePercent(20f);
        d.setColor(Color.rgb(255, 255, 0));
        d.setValueTextColor(Color.rgb(60, 220, 78));
        d.setValueTextSize(20f);
        d.setBarShadowColor(Color.rgb(203, 203, 203));

        ArrayList<BarDataSet> sets = new ArrayList<BarDataSet>();
        sets.add(d);

        BarData cd = new BarData(getDayOrder(), sets);
        return cd;
    }

    private BarData generateStepsOrDistanceData(boolean isDistance) {
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        Database db = Database.getInstance(this);
        SimpleDateFormat df = new SimpleDateFormat("E", Locale.getDefault());
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTimeInMillis(Util.getToday());
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        yesterday.add(Calendar.DAY_OF_YEAR, -6);
        for (int i = 0; i < 7; i++) {
            int steps = db.getSteps(yesterday.getTimeInMillis());
            Log.d("steps", Integer.toString(steps));
            Log.d("space", "===========");
            if (steps > 0 ||steps < 0) {
                if(isDistance && steps > 0){
                    //add distance
                    SharedPreferences prefs = this.getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
                    float stepsize = prefs.getFloat("stepsize_value", Fragment_Settings.DEFAULT_Human_Height);
                    float distance_today = steps * stepsize;
                    distance_today=distance_today/100000;
                    entries.add(new BarEntry(distance_today, i));
                }else if(isDistance && steps < 0 && steps != Integer.MIN_VALUE){ //quick fix for negative step need to find a fix
                    //add distance
                    SharedPreferences prefs = this.getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
                    float stepsize = prefs.getFloat("stepsize_value", Fragment_Settings.DEFAULT_Human_Height);
                    float distance_today = Math.abs(steps) * stepsize;
                    distance_today=distance_today/100000;
                    Log.d("DistanceToday", Float.toString(distance_today));
                    entries.add(new BarEntry(distance_today, i));
                }
                else{
                    //otherwise add steps
                    if (steps > 0) {
                        entries.add(new BarEntry(steps, i));
                    }else if(steps == Integer.MIN_VALUE){ //no dataa in the database
                      //  entries.add(new BarEntry(0, i));
                    }else if(steps < 0){
                        entries.add(new BarEntry(Math.abs(steps), i));
                    }
                }
            }
            yesterday.add(Calendar.DAY_OF_YEAR, 1);
        }
        BarDataSet d;
        if(!isDistance){
            d = new BarDataSet(entries, "Step count ");
        }
        else{
            d = new BarDataSet(entries, "Distance (km) ");
        }
        d.setBarSpacePercent(20f);
        d.setColor(Color.rgb(60, 220, 78));
        d.setValueTextColor(Color.rgb(60, 220, 78));
        d.setValueTextSize(20f);
        d.setBarShadowColor(Color.rgb(203, 203, 203));

        ArrayList<BarDataSet> sets = new ArrayList<BarDataSet>();
        sets.add(d);

        BarData cd = new BarData(getDayOrder(), sets);
        return cd;
    }




}
