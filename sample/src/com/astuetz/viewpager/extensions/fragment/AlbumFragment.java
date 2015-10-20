package com.astuetz.viewpager.extensions.fragment;

/**
 * Created by Viktor on 10/19/2015.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.viewpager.extensions.sample.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AlbumFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    @InjectView(R.id.textView)
    TextView textView;

    private int position;

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
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card,container,false);
        ButterKnife.inject(this, rootView);
        ViewCompat.setElevation(rootView,50);
        textView.setText("CARD "+position);
        return rootView;
    }
}