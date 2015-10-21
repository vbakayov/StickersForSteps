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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.viewpager.extensions.sample.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class StepsFragment extends Fragment {

	private static final String ARG_POSITION = "position";

    @InjectView(R.id.textView2)
    TextView textView;

	private int steps;

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
		//steps = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.steps_fragment,container,false);
        ButterKnife.inject(this, rootView);
        ViewCompat.setElevation(rootView, 50);
        textView.setText("Steps "+steps);
		return rootView;
	}

	public void updateCountView(float steps) {
		Log.w("Count", Float.toString(steps));
		this.steps=Math.round(steps);
		textView.setText("Steps "+this.steps);

	}



}