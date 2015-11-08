package com.astuetz.viewpager.extensions.fragment;

/**
 * Created by Viktor on 10/21/2015.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.viewpager.extensions.sample.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;



/**
 * Created by Viktor on 10/19/2015.
 */

public class StickersFragment extends Fragment {

    GridView gridView;
    ArrayList<Item> gridArray = new ArrayList<Item>();
    CustomGridViewAdapter customGridAdapter;

    private static final String ARG_POSITION = "position";
    private int position;

    public static StickersFragment newInstance(int position) {

        StickersFragment f = new StickersFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        //set grid view item
        Bitmap smek = BitmapFactory.decodeResource(this.getResources(), R.drawable.smek);
        Bitmap skiper = BitmapFactory.decodeResource(this.getResources(), R.drawable.skiper);

        gridArray.add(new Item(smek,"Smek"));
        gridArray.add(new Item(skiper,"Skipper"));
        gridArray.add(new Item(smek,"Smek"));
        gridArray.add(new Item(skiper,"Skipper"));
        gridArray.add(new Item(smek,"Smek"));
        gridArray.add(new Item(skiper,"Skipper"));
        gridArray.add(new Item(smek,"Smek"));
        gridArray.add(new Item(skiper,"Skipper"));
        gridArray.add(new Item(smek,"Smek"));
        gridArray.add(new Item(skiper,"Skipper"));
        gridArray.add(new Item(smek,"Smek"));
        gridArray.add(new Item(skiper, "Skipper"));



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stickers,container,false);
        ButterKnife.inject(this, rootView);
        ViewCompat.setElevation(rootView, 50);
        gridView = (GridView) rootView.findViewById(R.id.gridView1);

        customGridAdapter = new CustomGridViewAdapter(getActivity(), R.layout.row_grid, gridArray);
        gridView.setAdapter(customGridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }
}