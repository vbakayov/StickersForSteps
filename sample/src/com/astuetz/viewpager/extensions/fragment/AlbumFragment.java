package com.astuetz.viewpager.extensions.fragment;

/**
 * Created by Viktor on 10/19/2015.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.SlidingTabLayout;
import com.astuetz.viewpager.extensions.sample.R;

import bluetoothchat.BluetoothChatFragment;
import butterknife.ButterKnife;
import butterknife.InjectView;
import logger.Log;

public class AlbumFragment extends Fragment {

    private static final String ARG_POSITION = "position";


    TextView textView;

    private int position;
    private MyPagerAdapter adapter;

    public static AlbumFragment newInstance(int position) {

        AlbumFragment f = new AlbumFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_album, container, false);

        //TextView tv = (TextView) v.findViewById(R.id.tvFragFirst);
        //tv.setText(getArguments().getString("msg"));
        ViewPager pager = (ViewPager) v.findViewById(R.id.pager2);
        SlidingTabLayout tabs  = (SlidingTabLayout) v.findViewById(R.id.tabs2);

        adapter = new MyPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        tabs.setDistributeEvenly(true);
        tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setOffscreenPageLimit(3);
        pager.setCurrentItem(1);
        //changeColor(getResources().getColor(R.color.green));
        tabs.setBackgroundColor(getResources().getColor(R.color.green));

        return v;
    }



    // Extend from SmartFragmentStatePagerAdapter now instead for more dynamic ViewPager items
    public static class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
        private static int NUM_ITEMS = 2;
        private final String[] TITLES = {"Steps", "Album"};

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 1 - This will show Steps Fragment
                    return PageOneAlbum.newInstance(0);
                case 1: // Fragment # 2 - This will show  Album
                    return PageTwoAlbum.newInstance(1);
//                case 2: // Fragment # 3 - This will show Stickers
//                    return StickersFragment.newInstance(2);
//                case 3 : //Fragmern 4 - Swapping
//                    return BluetoothChatFragment.newInstance(3);
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

    }

}




abstract class SmartFragmentStatePagerAdapter2 extends FragmentStatePagerAdapter {
    // Sparse array to keep track of registered fragments in memory
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public SmartFragmentStatePagerAdapter2(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        Log.d("PUT", "FRAGMENT");
        registeredFragments.put(position, fragment);
        return fragment;
    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    // Returns the fragment for the position (if instantiated)
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }


}