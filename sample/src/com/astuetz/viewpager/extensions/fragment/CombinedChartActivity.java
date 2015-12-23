package com.astuetz.viewpager.extensions.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.astuetz.viewpager.extensions.sample.R;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CombinedChartActivity extends DemoBase {

    private CombinedChart mChart;
    private final int itemcount = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_combined);

        mChart = (CombinedChart) findViewById(R.id.chart1);
        mChart.setDescription("");
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);

        // draw bars behind lines
        mChart.setDrawOrder(new DrawOrder[] {
                DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.CANDLE, DrawOrder.LINE, DrawOrder.SCATTER
        });

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTH_SIDED);

        CombinedData data = new CombinedData(getDayOrder());

        data.setData(generateLineData());
        data.setData(generateBarData());
//        data.setData(generateBubbleData());
//         data.setData(generateScatterData());
//         data.setData(generateCandleData());

        mChart.setData(data);
        mChart.invalidate();
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
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleSize(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setDrawCubic(true);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);

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

    protected ScatterData generateScatterData() {

        ScatterData d = new ScatterData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < itemcount; index++)
            entries.add(new Entry(getRandom(20, 15), index));

        ScatterDataSet set = new ScatterDataSet(entries, "Scatter DataSet");
        set.setColor(Color.GREEN);
        set.setScatterShapeSize(7.5f);
        set.setDrawValues(false);
        set.setValueTextSize(10f);
        d.addDataSet(set);

        return d;
    }

    protected CandleData generateCandleData() {

        CandleData d = new CandleData();

        ArrayList<CandleEntry> entries = new ArrayList<CandleEntry>();

        for (int index = 0; index < itemcount; index++)
            entries.add(new CandleEntry(index, 20f, 10f, 13f, 17f));

        CandleDataSet set = new CandleDataSet(entries, "Candle DataSet");
        set.setColor(Color.rgb(80, 80, 80));
        set.setBodySpace(0.3f);
        set.setValueTextSize(10f);
        set.setDrawValues(false);
        d.addDataSet(set);

        return d;
    }

    protected BubbleData generateBubbleData() {

        BubbleData bd = new BubbleData();

        ArrayList<BubbleEntry> entries = new ArrayList<BubbleEntry>();

        for (int index = 0; index < itemcount; index++) {
            float rnd = getRandom(10, 0);
            entries.add(new BubbleEntry(index, rnd, rnd));
        }

        BubbleDataSet set = new BubbleDataSet(entries, "Bubble DataSet");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.WHITE);
        set.setHighlightCircleWidth(1.5f);
        set.setDrawValues(true);
        bd.addDataSet(set);

        return bd;
    }

    private float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }



//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.actionToggleLineValues: {
//                for (IDataSet set : mChart.getData().getDataSets()) {
//                    if (set instanceof LineDataSet)
//                        set.setDrawValues(!set.isDrawValuesEnabled());
//                }
//
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleBarValues: {
//                for (IDataSet set : mChart.getData().getDataSets()) {
//                    if (set instanceof BarDataSet)
//                        set.setDrawValues(!set.isDrawValuesEnabled());
//                }
//
//                mChart.invalidate();
//                break;
//            }
//        }
//        return true;
//    }
}
