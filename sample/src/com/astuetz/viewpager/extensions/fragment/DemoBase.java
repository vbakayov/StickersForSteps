package com.astuetz.viewpager.extensions.fragment;

/**
 * Created by Viktor on 12/22/2015.
 */


import android.support.v4.app.FragmentActivity;

import com.astuetz.viewpager.extensions.sample.R;

/**
 * Baseclass of all Activities of the Demo Application.
 *
 * @author Philipp Jahoda
 */
public abstract class DemoBase extends FragmentActivity {



//    protected String[] mParties = new String[] {
//            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
//            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
//            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
//            "Party Y", "Party Z"
//    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
      //  overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }
}
