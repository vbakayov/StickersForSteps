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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.astuetz.viewpager.extensions.sample.R;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;



import java.util.HashMap;

import album.SampleImage;
import butterknife.InjectView;
import stickers.DistributedRandomNumberGenerator;


public class StepsFragment extends Fragment {

	private static final String ARG_POSITION = "position";


	private static int steps;


	private int todayOffset, total_start, since_boot, total_days;
	private DecoView mDecoView;
	private int mBackIndex;
	private int mSeries1Index;
	private  float goal ;
	private TextView textToGo;
	private TextView textSteps;
	private TextView textGoal;
	private boolean isChecked;
	private boolean goalAnimationPlaying;
	private DistributedRandomNumberGenerator rg;
	private TextView buttonOpenPack;
	private  OnStickerChange notifyActivityStickerStatusChange;
	static final AnimationSet as = new AnimationSet(true);
	private boolean firstTime= true;
	private TextView textNextPack;

	public interface OnStickerChange {
		void notifyChange();
	}



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
//		total_start = db.getTotalWithoutToday();
//		total_days = db.getDays();

		rg= new DistributedRandomNumberGenerator(getActivity());

		db.close();

	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			notifyActivityStickerStatusChange = (OnStickerChange) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.steps_fragment,container,false);
        ViewCompat.setElevation(rootView, 50);
		Log.w("Create VIew", "HERE");

		textToGo = (TextView) rootView.findViewById(R.id.textRemaining);
		textSteps = (TextView) rootView.findViewById(R.id.textSteps);
		textGoal = (TextView) rootView.findViewById(R.id.textCurrentGoal);
		textNextPack = (TextView) rootView.findViewById(R.id.textView3);
		isChecked=true;
		goalAnimationPlaying=false;


		SharedPreferences prefs = getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
		int availableStickerPacks = prefs.getInt("packs", 0);



		buttonOpenPack = (TextView) rootView.findViewById(R.id.packButton);
		buttonOpenPack.setText(Integer.toString(availableStickerPacks)+" New Packs");
		buttonOpenPack.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				SharedPreferences prefs = getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
				int availableStickerPacks = prefs.getInt("packs", 0);
				buttonOpenPack.setText(Integer.toString(availableStickerPacks) + " New Packs");
				if(availableStickerPacks > 0) {
					int sticker1 = rg.getDistributedRandomNumber();
					int sticker2 = rg.getDistributedRandomNumber();
					int sticker3 = rg.getDistributedRandomNumber();
					Database db = Database.getInstance(getActivity());
					final Sticker sticker_1 = db.getSticker(sticker1);
					final Sticker sticker_2 = db.getSticker(sticker2);
					final Sticker sticker_3 = db.getSticker(sticker3);
					db.close();
					showNewSticker(sticker_1, sticker_2, sticker_3);
					Log.w("pressButton", "pressed");
					updateStickerPackCountDecrease();
					updateCountForAchievements();
					updateCountAndStatusDatabase(sticker_1, sticker_2, sticker_3);
					notifyActivityStickerStatusChange.notifyChange();
				}else{
					Toast.makeText(getActivity(), "You don't have any stickers to open right now",
							Toast.LENGTH_LONG).show();
				}


			}
		});



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



		mDecoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isChecked) {
					// show steps
					isChecked = true;
					updateViews();

				} else {    //showDistance
					isChecked = false;
					updateViews();
				}
			}


		});



		ImageView img = (ImageView) rootView.findViewById(R.id.trendsImage);
		img.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.w("Test","Trends Clicked");
				Intent intent = new Intent(getActivity(), CombinedChartActivity.class);
				startActivity(intent);
			}
		});



		return rootView;
	}

	private void updateCountForAchievements() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		int count = prefs.getInt("received stickers", 0)+3;
		if (count ==6 || count ==15 || count==30){
			updateStickerPackCountIncrease();
			Toast.makeText(getActivity(), "Achieved for received stickers completed. You received one free pack",
					Toast.LENGTH_LONG).show();

		}
		prefs.edit().putInt("received stickers", count).apply();
	}

	private void updateCountAndStatusDatabase(Sticker sticker_1, Sticker sticker_2, Sticker sticker_3) {
		Database db= Database.getInstance(getActivity());
		if(sticker_1.getCount()== 0){
			db.updateStatus(sticker_1.getId(), 1);
			db.updateCount(sticker_1.getId(),"increase");
		}else{
			db.updateCount(sticker_1.getId(),"increase");
		}
		if(sticker_2.getCount()== 0){
			db.updateStatus(sticker_2.getId(),1);
			db.updateCount(sticker_2.getId(),"increase");
		}else{
			db.updateCount(sticker_2.getId(),"increase");
		}
		if(sticker_3.getCount()== 0){
			db.updateStatus(sticker_3.getId(),1);
			db.updateCount(sticker_3.getId(),"increase");
		}else{
			db.updateCount(sticker_3.getId(),"increase");
		}
		db.close();
	}

	private void showStickerMoreInfo(final Sticker clickedSticker) {
		// custom dialog


		clickedSticker.getName();
		Log.d("NAMe", clickedSticker.getName());


		final Dialog dialog = new Dialog(getActivity());

		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
			}
		});


		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		dialog.setContentView(R.layout.sticker_dialog);
		ImageView image = (ImageView) (dialog).findViewById(R.id.image);

		//get the correct image
		String file= clickedSticker.getImagesrc();
		file = file.substring(0, file.lastIndexOf(".")); //trim the extension
		Resources resources = getActivity().getResources();
		int resourceId = resources.getIdentifier(file, "drawable", getActivity().getPackageName());
		image.setImageBitmap(SampleImage.decodeSampledBitmapFromResource(getResources(), resourceId, 250, 250));

		//load the additional details and information
		TextView id = (TextView) (dialog).findViewById(R.id.sticker_id);
		id.setText("#" + Integer.toString(clickedSticker.getId()));

		TextView status = (TextView) (dialog).findViewById(R.id.sticker_status);
		//at this poinrt only glued and notSticker available glued=1 notGlued=0
		String statuss =  clickedSticker.getStatus().equals(2)? "1": "0";
		Integer count =  clickedSticker.getCount();
		status.setText("(" + statuss + " glued, " + count + " left)");





		TextView title = (TextView) (dialog).findViewById(R.id.sticker_title);
		title.setText(clickedSticker.getName());

		TextView rarity = (TextView) (dialog).findViewById(R.id.rarity);
		rarity.setText(clickedSticker.getPopularity());

		TextView movie = (TextView) (dialog).findViewById(R.id.sticker_movie);
		movie.setText(clickedSticker.getMovie());
		//set the layout to have the same widh and height as the  windows screen


		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;


		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = width;
		lp.height = height;
		dialog.getWindow().setAttributes(lp);
		RelativeLayout mainLayout = (RelativeLayout) dialog.findViewById(R.id.showStickerLayout);
		dialog.show();
		// if button is clicked, close the custom dialog
		mainLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}

		});
		//listen for the inf tab
		ImageView info = (ImageView) (dialog).findViewById(R.id.info_image);
		info.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showInfoDialog(clickedSticker);
			}

		});

	}

	private void showInfoDialog(Sticker clickedSticker) {
		AlertDialog.Builder builder =
				new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
		builder.setTitle("More Information");
		builder.setMessage(clickedSticker.getDescription());
		builder.setPositiveButton("OK", null);
		builder.show();
	}

	private void showNewSticker( final Sticker sticker_1, final Sticker sticker_2,final  Sticker sticker_3) {
		// custom dialog


		final Dialog dialog = new Dialog(getActivity());

		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
			}
		});


		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.new_stickers_dialog);
		//get the correct image -1st sticker

		ImageView image = (ImageView) (dialog).findViewById(R.id.sticker1);
		image.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {showStickerMoreInfo(sticker_1);}});
		String file= sticker_1.getImagesrc();
		file = file.substring(0, file.lastIndexOf(".")); //trim the extension
		Resources resources = getActivity().getResources();
		int resourceId = resources.getIdentifier(file, "drawable", getActivity().getPackageName());
		TextView number = (TextView) (dialog).findViewById(R.id.text_sticker1);
		number.setText("#"+ Integer.toString(sticker_1.getId()));
		RelativeLayout image_layout = (RelativeLayout) (dialog).findViewById(R.id.sticker1_layout);
		ImageView imageCategory = (ImageView) (dialog).findViewById(R.id.category_image1);
		determineCategoty(imageCategory,sticker_1);
		determinePicture(sticker_1, image, resourceId);
		animate(image_layout, 3000);

		//get the correct image -2nd sticker
		ImageView image2 = (ImageView) (dialog).findViewById(R.id.sticker2);
		image2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {showStickerMoreInfo(sticker_2);}});
		String file2= sticker_2.getImagesrc();
		file2 = file2.substring(0, file2.lastIndexOf(".")); //trim the extension
		Resources resources2 = getActivity().getResources();
		int resourceId2 = resources2.getIdentifier(file2, "drawable", getActivity().getPackageName());
		TextView number2 = (TextView) (dialog).findViewById(R.id.text_sticker2);
		number2.setText("#" + Integer.toString(sticker_2.getId()));
		RelativeLayout image_layout2 = (RelativeLayout) (dialog).findViewById(R.id.sticker2_layout);
		ImageView imageCategory2 = (ImageView) (dialog).findViewById(R.id.category_image2);
		determineCategoty(imageCategory2, sticker_2);
		determinePicture(sticker_2,image2,resourceId2);
		animate(image_layout2, 3000);


		//get the correct image -3rd sticker
		ImageView image3 = (ImageView) (dialog).findViewById(R.id.sticker3);
		image3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {showStickerMoreInfo(sticker_3);}});
		String file3= sticker_3.getImagesrc();
		file3 = file3.substring(0, file3.lastIndexOf(".")); //trim the extension
		Resources resources3 = getActivity().getResources();
		int resourceId3 = resources3.getIdentifier(file3, "drawable", getActivity().getPackageName());

		TextView number3 = (TextView) (dialog).findViewById(R.id.text_sticker3);
		number3.setText("#" + Integer.toString(sticker_3.getId()));
		RelativeLayout image_layout3 = (RelativeLayout) (dialog).findViewById(R.id.sticker3_layout);
		ImageView imageCategory3 = (ImageView) (dialog).findViewById(R.id.category_image3);
		determineCategoty(imageCategory3, sticker_3);
		determinePicture(sticker_3,image3,resourceId3);
		animate(image_layout3, 3000);




		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;


		//set the layout to have the same widh and height as the  windows screen
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = width;
		lp.height = height;
		dialog.getWindow().setAttributes(lp);
		RelativeLayout mainLayout = (RelativeLayout) dialog.findViewById(R.id.showStickerLayout);
		dialog.show();
		// if button is clicked, close the custom dialog
		Button doneButton = (Button) (dialog).findViewById(R.id.doneButton);
		doneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}

		});
	}

	private void determinePicture(Sticker sticker, ImageView image, int resourceID) {
		if(sticker.getPopularity().equals("rare") || sticker.getPopularity().equals( "super rare")){
			setBackgroundGlow(image, resourceID, 200, 200, 200);}
		else{
			image.setImageBitmap(SampleImage.decodeSampledBitmapFromResource(getResources(), resourceID, 250, 250));
		}
	}

	private void determineCategoty(ImageView imageCategory, Sticker sticker) {
		//new Sticker
		if(sticker.getCount() == 0){
			Resources resources = getActivity().getResources();
			int resourceId = resources.getIdentifier("neww", "drawable", getActivity().getPackageName());
			imageCategory.setImageBitmap(SampleImage.decodeSampledBitmapFromResource(getResources(), resourceId, 250, 250));
			Animation pulse = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse);
			pulse.setRepeatCount(Animation.INFINITE);
			imageCategory.startAnimation(pulse);
		}

	}


	@Override
	public void onResume(){
		super.onResume();

		Log.d("RESUMMEE","resume");
		Database db = Database.getInstance(getActivity());

		SharedPreferences prefs = getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);

		goal = prefs.getInt("goal", Fragment_Settings.DEFAULT_GOAL);
//		total_start = db.getTotalWithoutToday();
//		total_days = db.getDays();
		db.close();
	}

	public void updateCountView(float steps_today) {
		this.steps=Math.round(steps_today);
		if(steps>= (int)goal){
			Log.w("GOOOAL", "ACHIEVED");
			updateGoal();
			updateStickerPackCountIncrease();
			playGoalAnimation();
		}else {
			updatePie();
			updateViews();
		}
	}

	private void updateStickerPackCountIncrease() {

		SharedPreferences prefs = getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
		 int availableStickerPacks = prefs.getInt("packs", 0);
		availableStickerPacks++;
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("packs", availableStickerPacks);
		editor.commit();
		buttonOpenPack.setText(Integer.toString(availableStickerPacks) + " New Packs");
	}

	private void updateStickerPackCountDecrease() {
		SharedPreferences prefs = getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
		int availableStickerPacks = prefs.getInt("packs", 0);
		availableStickerPacks--;
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("packs", availableStickerPacks);
		editor.commit();
		buttonOpenPack.setText(Integer.toString(availableStickerPacks) + " New Packs");
	}




	private void updateGoal() {
		SharedPreferences prefs = getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
		goal = prefs.getInt("goal", Fragment_Settings.DEFAULT_GOAL);
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
						goalAnimationPlaying = false;
						updatePie();

					}
				}).build());
	}


	private void updateViews() {
		Log.w("updateView", "updateViews");
		//Log.w("update  VIew", Integer.toString(total_start + steps));
		if(!goalAnimationPlaying) {
			if (isChecked) {
				textSteps.setText(Integer.toString(steps) + " steps today ");
			} else {
				SharedPreferences prefs = getActivity().getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
				float stepsize = prefs.getFloat("stepsize_value", Fragment_Settings.DEFAULT_STEP_SIZE);
				float distance_today = steps * stepsize;
				if (prefs.getString("stepsize_unit", Fragment_Settings.DEFAULT_STEP_UNIT)
						.equals("cm")) {
					distance_today /= 100000;
				} else {
					distance_today /= 5280;
				}
				textSteps.setText(String.format("%.3f km.", distance_today));
			}
		}
	}




	private void createBackSeries() {
		SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
				.setRange(0, goal, 0)
				.setInitialVisibility(true)
				.build();

		mBackIndex = mDecoView.addSeries(seriesItem);
	}

	private void createDataSeries1() {
		Log.w("creting seriess- goal", Float.toString(goal));
		Log.w("creting seriess- steps", Float.toString(steps));
		final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF8800"))
				.setRange(0, goal, steps)
				.setInitialVisibility(false)
				.build();


		seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
			@Override
			public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
				float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));

				textSteps.setText(Integer.toString(steps) + " steps");
				textNextPack.setText("Next Pack:");
				textToGo.setText( Integer.toString(Math.round(seriesItem.getMaxValue() - currentPosition))+" steps");
				textSteps.setText(String.format("%.0f steps today", currentPosition));
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




	private void animate(final RelativeLayout sticker,long durationMillis) {

		//final AnimationSet as = new AnimationSet(true);
		as.setFillEnabled(true);
		as.setFillAfter(true);

		final RotateAnimation rotateLeft = new RotateAnimation((float) 320, (float) 375,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotateLeft.setDuration(durationMillis);
		rotateLeft.setFillEnabled(true);


		if(firstTime) as.addAnimation(rotateLeft);

		Animation rotateRight = new RotateAnimation((float) 375, (float) 320,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotateRight.setStartOffset(durationMillis);
		rotateRight.setDuration(durationMillis);
		rotateRight.setFillEnabled(true);
		rotateRight.setFillAfter(true);


		if(firstTime) as.addAnimation(rotateRight);
		//sticker.clearAnimation();
		sticker.startAnimation(as);
		firstTime= false;
	}

	private void resetText() {

		textToGo.setText("");
		textSteps.setText("");
		textGoal.setText("");
		textNextPack.setText("");
	}

	private void setBackgroundGlow(ImageView imgview, int imageicon,int r,int g,int b)
	{
// An added margin to the initial image
		int margin = 50;
		int halfMargin = margin / 2;
		// the glow radius
		int glowRadius = 90;

		// the glow color
		int glowColor = Color.rgb(r, g, b);

		// The original image to use reduced(re-sampled)
		Bitmap src = SampleImage.decodeSampledBitmapFromResource(getResources(), imageicon, 250, 250);

		// extract the alpha from the source image
		Bitmap alpha = src.extractAlpha();

		// The output bitmap (with the icon + glow)
		Bitmap bmp =  Bitmap.createBitmap(src.getWidth() + margin, src.getHeight() + margin, Bitmap.Config.ARGB_8888);

		// The canvas to paint on the image
		Canvas canvas = new Canvas(bmp);

		Paint paint = new Paint();
		paint.setColor(glowColor);

		// outer glow
		paint.setMaskFilter(new BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.OUTER));//For Inner glow set Blur.INNER
		canvas.drawBitmap(alpha, halfMargin, halfMargin, paint);

		// original icon
		canvas.drawBitmap(src, halfMargin, halfMargin, null);

		imgview.setImageBitmap(bmp);


	}

	//This basically states that the onAnimationEnd method doesn't really work well when an AnimationListener is attached to an Animation
	// link http://stackoverflow.com/questions/2650351/android-translateanimation-resets-after-animation
	public  static class CustomLayout  extends RelativeLayout {
		public CustomLayout(Context context) {
			super(context);
		}


		public CustomLayout(Context context, AttributeSet attrs)
		{
			super(context, attrs);
			// TODO Auto-generated constructor stub
		}

		public CustomLayout(Context context, AttributeSet attrs, int defStyle)
		{
			super(context, attrs, defStyle);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onAnimationEnd() {
			super.onAnimationEnd();
			//Functionality here
			startAnimation(as);
		}
	}
}