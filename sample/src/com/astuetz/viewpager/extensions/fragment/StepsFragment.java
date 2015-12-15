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

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.astuetz.viewpager.extensions.sample.R;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;



import java.util.HashMap;

import butterknife.InjectView;
import stickers.DistributedRandomNumberGenerator;


public class StepsFragment extends Fragment {

	private static final String ARG_POSITION = "position";

    @InjectView(R.id.textView2)
    TextView textView;

	private static int steps;

	private TextView  totalView, averageView;
	private int todayOffset, total_start, since_boot, total_days;
	private DecoView mDecoView;
	private int mBackIndex;
	private int mSeries1Index;
	private  float goal ;
	private float mSeriesCurrent;
	private TextView textPercentage;
	private TextView textToGo;
	private TextView textSteps;
	private TextView textGoal;
	private boolean isChecked;
	private boolean goalAnimationPlaying;
	private DistributedRandomNumberGenerator rg;
	private NumberProgressBar bnp;


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
		SharedPreferences prefs = getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
		goal = prefs.getInt("goal", Fragment_Settings.DEFAULT_GOAL);
		//fix on create activity cout not to be 0 (non initalized)
		Database db = Database.getInstance(getActivity());
		todayOffset = db.getSteps(Util.getToday());
		since_boot = db.getCurrentSteps();
		steps = Math.max(todayOffset + since_boot, 0);
		total_start = db.getTotalWithoutToday();
		total_days = db.getDays();

		rg= new DistributedRandomNumberGenerator(getActivity());

		db.close();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.steps_fragment,container,false);
        ViewCompat.setElevation(rootView, 50);
		Log.w("Create VIew", "HERE");

		totalView = (TextView) rootView.findViewById(R.id.total);
		averageView = (TextView) rootView.findViewById(R.id.average);
		textPercentage = (TextView) rootView.findViewById(R.id.textPercentage);
		textToGo = (TextView) rootView.findViewById(R.id.textRemaining);
		textSteps = (TextView) rootView.findViewById(R.id.textSteps);
		textGoal = (TextView) rootView.findViewById(R.id.textCurrentGoal);
		isChecked=true;
		goalAnimationPlaying=false;


		bnp = (NumberProgressBar)rootView.findViewById(R.id.number_progress_bar);
		bnp.setMax(144);
		bnp.setProgress(20);



		mDecoView = (DecoView) rootView.findViewById(R.id.dynamicArcView);
		createBackSeries();
		createDataSeries1();



		mDecoView.executeReset();

		mDecoView.addEvent(new DecoEvent.Builder(goal)
				.setIndex(mBackIndex)
				.setDuration(3000)
				.setDelay(100)
				.build());

		mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT_FILL)
				.setIndex(mSeries1Index)
				.setDuration(3000)
				.setDelay(1250)
				.build());


		mDecoView.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isChecked) {
				// show steps
					isChecked=true;
					updateViews();

				}else {	//showDistance
					isChecked=false;
					updateViews();
					}
			}


		});

		return rootView;
	}
	private void saveNextGoal(int nextGoal) {
		SharedPreferences prefs = getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("goal", nextGoal);
		editor.commit();
	}

	@Override
	public void onResume(){
		super.onResume();

		Database db = Database.getInstance(getActivity());

		SharedPreferences prefs = getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
		goal = prefs.getInt("goal", Fragment_Settings.DEFAULT_GOAL);
		total_start = db.getTotalWithoutToday();
		total_days = db.getDays();
		db.close();
	}

	public void updateCountView(float steps_today) {
		this.steps=Math.round(steps_today);
		if(steps>= (int)goal){
			Log.w("GOOOAL", "ACHIEVED");
			playGoalAnimation();
		}else {
			updatePie();
			updateViews();
		}
	}

	private void updatePie() {
		if(!goalAnimationPlaying)
			mDecoView.addEvent(new DecoEvent.Builder(steps).setIndex(mSeries1Index).build());
	}

	private void playGoalAnimation() {
		goalAnimationPlaying=true;
		resetText();
		mDecoView.addEvent(new DecoEvent.Builder(0)
				.setIndex(mSeries1Index)
				.setDuration(1000)
				.setInterpolator(new AnticipateInterpolator())
				.setListener(new DecoEvent.ExecuteEventListener() {
					@Override
					public void onEventStart(DecoEvent decoEvent) {

					}

					@Override
					public void onEventEnd(DecoEvent decoEvent) {
						resetText();
						playGoalAnimation2();




					}
				}).build());



		resetText();
	}

	private void playGoalAnimation2() {

		mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
				.setIndex(mSeries1Index)
				.setDuration(3000)
				.setDisplayText("GOAL!")
				.setListener(new DecoEvent.ExecuteEventListener() {
					@Override
					public void onEventStart(DecoEvent decoEvent) {

					}

					@Override
					public void onEventEnd(DecoEvent decoEvent) {
						resetText();
						createDataSeries1();
						goalAnimationPlaying=false;
						updatePie();

			}
		}).build());
	}


	private void updateViews() {
		Log.w("updateView", "updateViews");
		//Log.w("update  VIew", Integer.toString(total_start + steps));
		if(!goalAnimationPlaying) {
			if (isChecked) {
				totalView.setText(Integer.toString(total_start + steps));
				textSteps.setText(Integer.toString(steps) + " steps");
				averageView.setText(Integer.toString((total_start + steps / total_days)));
			} else {
				SharedPreferences prefs = getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
				float stepsize = prefs.getFloat("stepsize_value", Fragment_Settings.DEFAULT_STEP_SIZE);
				float distance_today = steps * stepsize;
				float distance_total = (total_start + steps) * stepsize;
				if (prefs.getString("stepsize_unit", Fragment_Settings.DEFAULT_STEP_UNIT)
						.equals("cm")) {
					distance_today /= 100000;
					distance_total /= 100000;
				} else {
					distance_today /= 5280;
					distance_total /= 5280;
				}
				textSteps.setText(String.format("%.3f km.", distance_today));
				totalView.setText(String.format("%.3f", distance_total));
				averageView.setText(String.format("%.3f", distance_total / total_days));
			}
		}
	}

	/**
	 * Updates the pie graph to show todays steps/distance as well as the
	 * yesterday and total values. Should be called when switching from step
	 * count to distance.
	 */
//	private void updatePie(int steps_today) {
//		// todayOffset might still be Integer.MIN_VALUE on first start
//
//		sliceCurrent.setValue(steps_today);
//		Log.w("GOAL", Integer.toString(goal));
//		if (goal - steps_today > 0) {
//			// goal not reached yet
//            if (pg.getData().size() == 1) {
//                // can happen if the goal value was changed: old goal value was
//                // reached but now there are some steps missing for the new goal
//                pg.addPieSlice(sliceGoal);
//            }
//
//            sliceGoal.setValue(goal - steps_today);
//        } else {
//            // goal reached
//            pg.clearChart();
//            pg.addPieSlice(sliceCurrent);
//        }


	private void createBackSeries() {
		SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
				.setRange(0, goal, 0)
				.setInitialVisibility(true)
				.build();

		mBackIndex = mDecoView.addSeries(seriesItem);
	}

	private void createDataSeries1() {

		final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF8800"))
				.setRange(0, goal, steps)
				.setInitialVisibility(false)
				.build();


		seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
			@Override
			public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
				float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
				textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
				totalView.setText(Integer.toString(total_start + steps));
				textSteps.setText(Integer.toString(steps) + " steps");
				averageView.setText(Integer.toString((total_start + steps / total_days)));
				textToGo.setText(String.format("%.1f steps to goal", seriesItem.getMaxValue() - currentPosition));
				textSteps.setText(String.format("%.0f steps", currentPosition));
				textGoal.setText(String.format("Goal: %.0f steps", seriesItem.getMaxValue()));
			}

			@Override
			public void onSeriesItemDisplayProgress(float percentComplete) {

			}
		});
		
		mSeries1Index = mDecoView.addSeries(seriesItem);
	}


	public void animateTextView(int initialValue, int finalValue, final TextView  textview) {

		ValueAnimator valueAnimator = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			valueAnimator = ValueAnimator.ofInt((int) initialValue, (int) finalValue);

			valueAnimator.setDuration(1500);

			valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@TargetApi(Build.VERSION_CODES.HONEYCOMB)
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {

					textview.setText(valueAnimator.getAnimatedValue().toString());

				}
			});
			valueAnimator.start();

		}}



	private void createEvents() {


		//InitialState
		mDecoView.addEvent(new DecoEvent.Builder(mSeriesCurrent) //here set the value
				.setIndex(mSeries1Index)
				.setDelay(3250)
				.build());

		mDecoView.addEvent(new DecoEvent.Builder(40f).setIndex(mSeries1Index).build());

		mDecoView.addEvent(new DecoEvent.Builder(0)
				.setIndex(mSeries1Index)
				.setDuration(1000)
				.setInterpolator(new AnticipateInterpolator())
				.setListener(new DecoEvent.ExecuteEventListener() {
					@Override
					public void onEventStart(DecoEvent decoEvent) {

					}

					@Override
					public void onEventEnd(DecoEvent decoEvent) {
						resetText();
					}
				})
				.build());

		mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
				.setIndex(mSeries1Index)
				.setDuration(3000)
				.setDisplayText("GOAL!")
				.setListener(new DecoEvent.ExecuteEventListener() {
					@Override
					public void onEventStart(DecoEvent decoEvent) {

					}

					@Override
					public void onEventEnd(DecoEvent decoEvent) {
						createEvents();
					}
				})
				.build());

		resetText();
	}

	private void resetText() {

		textPercentage.setText("");
		textToGo.setText("");
		textSteps.setText("");
		textGoal.setText("");
	}
	}