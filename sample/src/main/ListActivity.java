package main;



import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.ArrayList;

import Achievements.Achievement;


/**
 * Created by Viktor on 1/14/2016.
 * A listActivity used for displaying the Achievements
 */
public class ListActivity extends AppCompatActivity {
    private static Achievement clickedAchievement;
    int onStartCount = 0;
    private ArrayList<Achievement> achievements;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        Log.d("LIST CREATED", "INITIALIZED");
        achievements = (ArrayList<Achievement>) getIntent().getSerializableExtra("filtered");

       //legacy code heree
        onStartCount = 1;
        if (savedInstanceState == null) // 1st time
        {
            this.overridePendingTransition(R.anim.anim_slide_in_right,
                    R.anim.anim_slide_out_left);
        } else
        {
            onStartCount = 2;
        }

        // end of legacy code here

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
        //Build Adapter
        ArrayAdapter<Achievement> adapter = new MyListAdapter();

        //Configurate the list view
        ListView list = (ListView) findViewById(R.id.listMain);
        list.setAdapter(adapter);
    }


    private class MyListAdapter extends ArrayAdapter<Achievement>{
        public 	MyListAdapter(){
            super(ListActivity.this, R.layout.list_row, achievements);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Make sure we have a view to work with(may have been given null)
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.list_row, parent,false);
            }

            //Find acheivement to work with
            Achievement currentAchievement = achievements.get(position);

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
                //which achievements we are currently
                clickedAchievement = achievements.get(position);
                Toast toast = Toast.makeText(ListActivity.this, clickedAchievement.getTitle(), Toast.LENGTH_LONG);
                LinearLayout layout = (LinearLayout) toast.getView();
                toast.show();

            }
        });

    }


}


