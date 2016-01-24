package com.astuetz.viewpager.extensions.fragment;

/**
 * Created by Viktor on 1/14/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.viewpager.extensions.sample.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;





/**
 * Created by Viktor on 11/18/2015.
 */
public class ListActivity extends AppCompatActivity {
    private static Achievement clickedTrip;
    int onStartCount = 0;
    private ArrayList<Achievement> trips;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        Log.d("LIST CREATED", "INITIALIZED");
        trips = (ArrayList<Achievement>) getIntent().getSerializableExtra("filtered");

        //for animation the activity
        onStartCount = 1;
        if (savedInstanceState == null) // 1st time
        {
            this.overridePendingTransition(R.anim.anim_slide_in_right,
                    R.anim.anim_slide_out_left);
        } else // already created so reverse animation
        {
            onStartCount = 2;
        }
//
//        final PullRefreshLayout layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
//        layout.setRefreshStyle(PullRefreshLayout.STYLE_CIRCLES);
//
//        // listen refresh event
//        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                layout.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        layout.setRefreshing(false);
//                    }
//                }, 3000);
//
//            }
//        });
//
//        // refresh complete
//        layout.setRefreshing(false);

        populateListView();
        registerClickCallback();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (onStartCount > 1) {
            this.overridePendingTransition(R.anim.anim_slide_in_right,
                    R.anim.anim_slide_out_right);

        } else if (onStartCount == 1) {
            onStartCount++;
        }

    }


    private void populateListView() {
        //Build Adapter //Context for the activity, layout to use ,Items to be displayed
        ArrayAdapter<Achievement> adapter = new MyListAdapter();

        //Configurate the list view
        ListView list = (ListView) findViewById(R.id.listMain);
//        list.setBackgroundResource(R.drawable.gradient_bg);;
        list.setAdapter(adapter);
    }


    private class MyListAdapter extends ArrayAdapter<Achievement>{
        public 	MyListAdapter(){
            super(ListActivity.this, R.layout.list_row, trips);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Make sure we have a view to work with(may have been given null)
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.list_row, parent,false);
            }

            //Find trip to work with
            Achievement currentAchievement = trips.get(position);

            RelativeLayout mRowLayout = (RelativeLayout) itemView.findViewById(R.id.mRowLayout);
            if(currentAchievement.isAchieved()) {
//                Log.i("LIST ACTIVITY", "Contacts");
                mRowLayout.setBackgroundResource(R.drawable.list_selector_green);

            }
            else{
                mRowLayout.setBackgroundResource(R.drawable.list_selector);
            }




            //Fill the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.list_image);
            imageView.setImageResource(getResources().getIdentifier(currentAchievement.getPictureSrc(), "drawable", getPackageName()));


            TextView ownerName = (TextView) itemView.findViewById(R.id.title);
            ownerName.setText(currentAchievement.getTitle());

            TextView toTown = (TextView) itemView.findViewById(R.id.desciption);
            toTown.setText(currentAchievement.getDescription());




            return itemView;
        }
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.listMain);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long ID) {
                //which trips we are currently
                clickedTrip = trips.get(position);
//                Intent myIntent = new Intent(ListActivity.this, TripActivity.class);
//                myIntent.putExtra("trip", clickedTrip); //Optional parameters
//                ListActivity.this.startActivity(myIntent);
                //build and center the toast
                Toast toast = Toast.makeText(ListActivity.this, clickedTrip.getTitle(), Toast.LENGTH_LONG);
                LinearLayout layout = (LinearLayout) toast.getView();
//                if (layout.getChildCount() > 0) {
//                    TextView tv = (TextView) layout.getChildAt(0);
//                    tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
//                }
                toast.show();

            }
        });

    }


}


