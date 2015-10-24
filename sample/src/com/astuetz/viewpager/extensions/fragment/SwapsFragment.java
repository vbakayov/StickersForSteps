package com.astuetz.viewpager.extensions.fragment;

/**
 * Created by Viktor on 10/21/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.viewpager.extensions.sample.R;

import bluetoothchat.BluetoothChatFragment;
import butterknife.ButterKnife;
import butterknife.InjectView;



/**
 * Created by Viktor on 10/19/2015.
 */

public class SwapsFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    @InjectView(R.id.textView)
    TextView textView;

    private int position;

    public static SwapsFragment newInstance(int position) {

        SwapsFragment f = new SwapsFragment();
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
        View rootView = inflater.inflate(R.layout.swaps_fragment,container,false);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        BluetoothChatFragment fragment = new BluetoothChatFragment();
        transaction.replace(R.id.sample_content_fragment, fragment);
        transaction.commit();
        //ButterKnife.inject(this, rootView);
        //ViewCompat.setElevation(rootView, 50);
       // textView.setText("CARD "+position);
        return rootView;
    }
}