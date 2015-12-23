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
    private static final int NUM_PAGES = 23;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private static ViewPager mPager;

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
        mPager.setOffscreenPageLimit(0);



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




    public static ViewPager getViewPagerAlbum(){
        return mPager;
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
                case 0: // Fragment # 1 - 
                    return PageTwoAlbum.newInstance(1);
                case 1: // Fragment # 2 - This will show  Album
                    return PageOneAlbum.newInstance(0);
                case 2: // Fragment # 3 - This will show Stickers
                    return PageThreeAlbum.newInstance(
                            "Cars",
                            new ArrayList<>(Arrays.asList(16, 17, 18, 19, 20, 21, 22)),
                            new ArrayList<>(Arrays.asList(90, 120, 90, 90, 90, 90, 90)));
                case 3 : //Fragmern 4 - Swapping
                    return PageThreeAlbum.newInstance(
                            "Croods",
                            new ArrayList<>(Arrays.asList(23, 24, 25, 26, 27, 28, 29)),
                            new ArrayList<>(Arrays.asList(90, 120, 90, 90, 90, 90, 90)));
                case 4 : //Fragmern 4 - Swapping
                    return PageThreeAlbum.newInstance(
                            "Frozen", ///add here one more
                            new ArrayList<>(Arrays.asList(30,31,32,33,34,35,36,37)),
                            new ArrayList<>(Arrays.asList(90, 120, 90, 90, 90, 90, 90,90)));
                case 5 : //Fragmern 4 - Swapping
                    return PageThreeAlbum.newInstance(
                            "Home", ///problme loading 44 -Tip-last one
                            new ArrayList<>(Arrays.asList(-1,39,-1,40,42,41,43)),
                            new ArrayList<>(Arrays.asList(90, 120, 90, 90, 90, 90, 90)));

                case 6 : //Fragmern 4 - Swapping
                    return PageThreeAlbum.newInstance(
                            "How to Train Your Dragon",
                            new ArrayList<>(Arrays.asList(45,46,47,48,49,50,51,52)),
                            new ArrayList<>(Arrays.asList(90, 120, 90, 90, 90, 90, 90,90)));
                case 7 : //Fragmern 4 - Swapping
                    return PageThreeAlbum.newInstance(
                            "Ice Age",
                            new ArrayList<>(Arrays.asList(-1,53,54,55,56,57)),
                            new ArrayList<>(Arrays.asList(90, 120, 90, 90, 90, 90)));
                case 8 :
                    return PageThreeAlbum.newInstance(
                            "Incredible",
                            new ArrayList<>(Arrays.asList(58,59,-1,61,62,63,64,60)),
                            new ArrayList<>(Arrays.asList(100, 120, 100, 100, 100, 100, 100,100)));
                case 9 :
                    return PageThreeAlbum.newInstance(
                            "Inside Out",
                            new ArrayList<>(Arrays.asList(65,66,67,68,69,-1,70,71)),
                            new ArrayList<>(Arrays.asList(100, 120, 100, 100, 100, 100, 100,100)));

                case 10 :
                    return PageThreeAlbum.newInstance(
                            "Kung Fu Panda",
                            new ArrayList<>(Arrays.asList(72,73,74,75,76,77,78,79)),
                            new ArrayList<>(Arrays.asList(100, 120, 100, 100, 100, 100, 100,100)));

                case 11 :
                    return PageThreeAlbum.newInstance(
                            "Madagascar",
                            new ArrayList<>(Arrays.asList(80,81,82,83,84,85)),
                            new ArrayList<>(Arrays.asList(100, 120, 100, 100, 100, 100, 100,100)));

                case 12 :
                    return PageThreeAlbum.newInstance(
                            "Madagascar2",
                            new ArrayList<>(Arrays.asList(86,87,88,89,-1,-1)),
                            new ArrayList<>(Arrays.asList(100, 120, 100, 100,100,100)));

                case 13 :
                    return PageThreeAlbum.newInstance(
                            "Megamind",
                            new ArrayList<>(Arrays.asList(-1,90,-1,91,-1,92,93,94)),
                            new ArrayList<>(Arrays.asList(100, 120, 100, 100, 100, 100, 100,100)));

                case 14 :
                    return PageThreeAlbum.newInstance(
                            "Monsters",
                            new ArrayList<>(Arrays.asList(95,96,97,98,99,-1,100,-1)),
                            new ArrayList<>(Arrays.asList(100, 120, 100, 100, 100, 100, 100,100)));
                case 15 :
                    return PageThreeAlbum.newInstance(
                            "Nemo",
                            new ArrayList<>(Arrays.asList(100,101,102,103,104,105)),
                            new ArrayList<>(Arrays.asList(100, 120, 100, 100, 100, 100, 100,100)));

                case 16 :
                    return PageThreeAlbum.newInstance(
                            "Nemo2",
                            new ArrayList<>(Arrays.asList(106,107,108,109,110,111)),
                            new ArrayList<>(Arrays.asList(100, 120, 100, 100, 100, 100, 100,100)));
                case 17 :
                    return PageThreeAlbum.newInstance(
                            "Ratatouille",
                            new ArrayList<>(Arrays.asList(112,113,-1,114,115,116,117,118)),
                            new ArrayList<>(Arrays.asList(100, 120, 100, 100, 100, 100, 100,100)));

                case 18 :
                    return PageThreeAlbum.newInstance(
                            "Shrek",
                            new ArrayList<>(Arrays.asList(119,120,-1,122,121)),
                            new ArrayList<>(Arrays.asList(120, 120, 100, 120, 120, 100, 100,100)));
                case 19 :
                    return PageThreeAlbum.newInstance(
                            "Tangled",
                            new ArrayList<>(Arrays.asList(123,124,125,-1,126,127,128)),
                            new ArrayList<>(Arrays.asList(100, 120, 100, 100, 100, 100, 100,100)));
                case 20 :
                    return PageThreeAlbum.newInstance(
                            "Toystory",
                            new ArrayList<>(Arrays.asList(129,130,-1,-1,-1,131,132,133)),
                            new ArrayList<>(Arrays.asList(100, 120, 100, 100, 100, 100, 100,100)));
                case 21 :
                    return PageThreeAlbum.newInstance(
                            "Up",
                            new ArrayList<>(Arrays.asList(134,135,136,137,138,139,-1,-1)),
                            new ArrayList<>(Arrays.asList(100, 120, 100, 100, 100, 100, 100,100)));
                case 22 :
                    return PageThreeAlbum.newInstance(
                            "Walle",
                            new ArrayList<>(Arrays.asList(145,140,142,143,144,141,-1,-1)),
                            new ArrayList<>(Arrays.asList(100, 120, 100, 100, 100, 100, 100,100)));







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
