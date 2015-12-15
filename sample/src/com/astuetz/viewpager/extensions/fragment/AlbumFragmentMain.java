/*
 * Copyright 2012 The Android Open Source Project
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



import android.os.Bundle;
//import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.astuetz.viewpager.extensions.sample.R;

import java.util.ArrayList;
import java.util.Arrays;

import album.PageOneAlbum;
import album.PageThreeAlbum;
import album.PageTwoAlbum;

/**
 * Demonstrates a "screen-slide" animation using a {@link ViewPager}. Because {@link ViewPager}
 * automatically plays such an animation when calling {@link ViewPager#setCurrentItem(int)}, there
 * isn't any animation-specific code in this sample.
 *
 * <p>This sample shows a "next" button that advances the user to the next step in a wizard,
 * animating the current screen out (to the left) and the next screen in (from the right). The
 * reverse animation is played when the user presses the "previous" button.</p>
 *
 * @see AlbumScreenSlide
 */
public class AlbumFragmentMain extends Fragment {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 6;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;


    public static AlbumFragmentMain newInstance(int i) {
        AlbumFragmentMain f = new AlbumFragmentMain();
        Bundle b = new Bundle();
        b.putInt("POSITION", i);
        f.setArguments(b);

        return f;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.activity_screen_slide,container,false);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager)rootView. findViewById(R.id.pager);

        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                //  invalidateOptionsMenu();
            }
        });



        ImageView returnButton = (ImageView) rootView.findViewById(R.id.imageView3);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(getItem(-1), true); //getItem(-1) for previous
            }
        });

        ImageView forwardButton = (ImageView) rootView.findViewById(R.id.imageView4);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(getItem(+1), true); //getItem(-1) for previous
            }
        });
        return rootView;
    }




    private int getItem(int i) {
        return  mPager.getCurrentItem() + i;
    }


    /**
     * A simple pager adapter that represents 5 {@link AlbumScreenSlide} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0: // Fragment # 1 - This will show Steps Fragment
                    return PageOneAlbum.newInstance(0);
                case 1: // Fragment # 2 - This will show  Album
                    return PageTwoAlbum.newInstance(1);
                case 2: // Fragment # 3 - This will show Stickers
                    return AlbumScreenSlide.create(2);
                case 3 : //Fragmern 4 - Swapping
                    return AlbumScreenSlide.create(3);
                case 4 : //Fragmern 4 - Swapping
                    return AlbumScreenSlide.create(4);
                case 5 : //Fragmern 4 - Swapping
                    return PageThreeAlbum.newInstance(
                            "moview1",
                            new ArrayList<Integer>(Arrays.asList(22,30,40,80,90,110,111,112)),
                            new ArrayList<Integer>(Arrays.asList(70,120,90,90,90,90,90,90)));
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
