package com.astuetz.viewpager.extensions.fragment;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.astuetz.viewpager.extensions.sample.R;

import static com.astuetz.PagerSlidingTabStrip.CustomTabProvider;

public class QuickContactFragment extends DialogFragment {

    public static QuickContactFragment newInstance() {
        return new QuickContactFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getDialog() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        View root = inflater.inflate(R.layout.fragment_quick_contact, container, false);
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) root.findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) root.findViewById(R.id.pager);
        ContactPagerAdapter adapter = new ContactPagerAdapter(getActivity());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        return root;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart() {
        super.onStart();

        // change dialog width
        if (getDialog() != null) {
            int fullWidth;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                fullWidth = size.x;
            } else {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                fullWidth = display.getWidth();
            }

            final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                    .getDisplayMetrics());
            int w = fullWidth - padding;
            int h = getDialog().getWindow().getAttributes().height;
            getDialog().getWindow().setLayout(w, h);
        }
    }

    public static class ContactPagerAdapter extends PagerAdapter implements CustomTabProvider {

        private final int[] ICONS = {
                R.drawable.ic_launcher_gplus,
                R.drawable.ic_launcher_gmail,
                R.drawable.ic_launcher_gmaps,
                R.drawable.ic_launcher_chrome
        };
        private final String[] TITLES = {
                "GPlus",
                "GMail",
                "GMaps",
                "GChrome"
        };

        private final Context mContext;

        public ContactPagerAdapter(Context context) {
            super();
            mContext = context;
        }

        @Override
        public int getCount() {
            return ICONS.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView textview = (TextView) LayoutInflater.from(mContext).inflate(R.layout.fragment_quickcontact, container, false);
            textview.setText("PAGE " + position);
            container.addView(textview);
            return textview;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object view) {
            container.removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View v, Object o) {
            return v == o;
        }

        @Override
        public View getCustomTabView(ViewGroup parent, int position) {
            View tab = LayoutInflater.from(mContext).inflate(R.layout.custom_tab, parent, false);
            ((ImageView) tab.findViewById(R.id.image)).setImageResource(ICONS[position]);
            return tab;
        }

        @Override
        public void tabSelected(View tab) {
            //Callback with the tab on his selected state. It is up to the developer to change anything on it.
        }

        @Override
        public void tabUnselected(View tab) {
            //Callback with the tab on his unselected state. It is up to the developer to change anything on it.
        }
    }
}
