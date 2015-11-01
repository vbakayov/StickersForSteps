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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.astuetz.viewpager.extensions.sample.R;


import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class StepsFragment extends Fragment {

	private static final String ARG_POSITION = "position";

    @InjectView(R.id.textView2)
    TextView textView;

	private static int steps;
	private PieChart pg;
	private PieModel sliceGoal, sliceCurrent;
	private TextView stepsView, totalView, averageView;
	private int todayOffset, total_start, goal, since_boot, total_days;
	private boolean showSteps = true;

	public static StepsFragment newInstance(int position) {
		StepsFragment f = new StepsFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.steps_fragment,container,false);
//        ButterKnife.inject(this, rootView);
        ViewCompat.setElevation(rootView, 50);
		Log.w("Create VIew", "HERE");
//        textView.setText("Steps "+steps);
		stepsView = (TextView) rootView.findViewById(R.id.steps);
		totalView = (TextView) rootView.findViewById(R.id.total);
		averageView = (TextView) rootView.findViewById(R.id.average);


		pg = (PieChart) rootView.findViewById(R.id.graph);

        // slice for the steps taken today
        sliceCurrent = new PieModel("", 0, Color.parseColor("#99CC00"));
        pg.addPieSlice(sliceCurrent);

        // slice for the "missing" steps until reaching the goal
        sliceGoal = new PieModel("", Fragment_Settings.DEFAULT_GOAL, Color.parseColor("#CC0000"));
        pg.addPieSlice(sliceGoal);

//        pg.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(final View view) {
//				showSteps = !showSteps;
//				stepsDistanceChanged();
//			}
//		});

		ToggleButton toggle = (ToggleButton) rootView.findViewById(R.id.toggleButton);
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					showSteps=true;
				} else {
					showSteps=false;
				}
				stepsDistanceChanged();
			}
		});

        pg.setDrawValueInPie(false);
        pg.setUsePieRotation(true);
        pg.startAnimation();
		return rootView;
	}

	@Override
	public void onResume(){
		super.onResume();

		Database db = Database.getInstance(getActivity());

		SharedPreferences prefs =
				getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
		goal = prefs.getInt("goal", Fragment_Settings.DEFAULT_GOAL);

		total_start = db.getTotalWithoutToday();
		total_days = db.getDays();

		db.close();
		stepsDistanceChanged();
	}
	public void updateCountView(float steps) {
		Log.w("Count", Float.toString(steps));
		this.steps=Math.round(steps);
	//	textView.setText("Steps "+this.steps);
		updatePie(Math.round(steps));

	}

	/**
	 * Updates the pie graph to show todays steps/distance as well as the
	 * yesterday and total values. Should be called when switching from step
	 * count to distance.
	 */
	private void updatePie(int steps_today) {
		// todayOffset might still be Integer.MIN_VALUE on first start

		sliceCurrent.setValue(steps_today);
		Log.w("GOAL", Integer.toString(goal));
		if (goal - steps_today > 0) {
			// goal not reached yet
            if (pg.getData().size() == 1) {
                // can happen if the goal value was changed: old goal value was
                // reached but now there are some steps missing for the new goal
                pg.addPieSlice(sliceGoal);
            }

            sliceGoal.setValue(goal - steps_today);
        } else {
            // goal reached
            pg.clearChart();
            pg.addPieSlice(sliceCurrent);
        }
        pg.update();
        if (showSteps) { //showSteps
            stepsView.setText(Integer.toString(steps_today));
            totalView.setText(Integer.toString(total_start));
			Log.w("TOTAL STEPS", Integer.toString(total_start));
			Log.w("TOTAL DAYS", Integer.toString(total_days));
            averageView.setText(Integer.toString((total_start + steps_today) / total_days));
        } else {
            // update only every 10 steps when displaying distance
            SharedPreferences prefs =
                    getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
            float stepsize = prefs.getFloat("stepsize_value", Fragment_Settings.DEFAULT_STEP_SIZE);
            float distance_today = steps_today * stepsize;
            float distance_total = (total_start + steps_today) * stepsize;
            if (prefs.getString("stepsize_unit", Fragment_Settings.DEFAULT_STEP_UNIT)
                    .equals("cm")) {
                distance_today /= 100000;
                distance_total /= 100000;
            } else {
                distance_today /= 5280;
                distance_total /= 5280;
            }
//			DecimalFormat df = new DecimalFormat();
//			df.setMaximumFractionDigits(3);
//			df.format(distance_today);
//			df.format(distance_total);
            stepsView.setText( String.format("%.3f", distance_today));
            totalView.setText( String.format("%.3f", distance_total));
            averageView.setText( String.format("%.3f", distance_total / total_days));
		}
	}


	/**
	 * Call this method if the Fragment should update the "steps"/"km" text in
	 * the pie graph as well as the pie and the bars graphs.
	 */
	private void stepsDistanceChanged() {
		if (showSteps) {
			((TextView) getView().findViewById(R.id.unit)).setText(getString(R.string.steps));
		} else {
			String unit =
					getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS)
							.getString("stepsize_unit", Fragment_Settings.DEFAULT_STEP_UNIT);
			if (unit.equals("cm")) {
				unit = "km";
			} else {
				unit = "mi";
			}
			((TextView) getView().findViewById(R.id.unit)).setText(unit);
		}

		  updatePie(steps);
		//updateBars();
	}



}