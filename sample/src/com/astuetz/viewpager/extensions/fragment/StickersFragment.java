package com.astuetz.viewpager.extensions.fragment;

/**
 * Created by Viktor on 10/21/2015.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.viewpager.extensions.sample.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import album.SampleImage;
import butterknife.ButterKnife;


/**
 * Created by Viktor on 10/19/2015.
 */

public class StickersFragment extends Fragment {

    GridView gridView;
    ArrayList<Item> gridArray = new ArrayList<Item>();
    CustomGridViewAdapter customGridAdapter;

    private static final String ARG_POSITION = "position";
    private int position;
    private       int width, height;
    private      int originalPos[] = new int[2];

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



        WindowManager manager = (WindowManager) getActivity().getSystemService(Activity.WINDOW_SERVICE);

            Point point = new Point();
            manager.getDefaultDisplay().getSize(point);
            width = point.x;
            height = point.y;

        Database db = Database.getInstance(getActivity());
        List<Sticker> stickers = db.getStickersWithStatus(2);
        Log.d("File id ", Integer.toString(stickers.size()));
        for (ListIterator<Sticker > iter = stickers.listIterator(); iter.hasNext(); ) {
            Sticker element = iter.next();
            String file= element.getImagesrc();
            file = file.substring(0, file.lastIndexOf(".")); //trim the extension
            Log.d("File ", file);

            Resources resources = getActivity().getResources();
            int resourceId = resources.getIdentifier(file, "drawable",
                    getActivity().getPackageName());

            //set the sampled sized of the image with the given dimensions
            Bitmap image= SampleImage.decodeSampledBitmapFromResource(getResources(), resourceId, 70, 70);
            //set grid view item
            gridArray.add(new Item(image,element.getName()));


        }

        db.close();

    }





    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stickers,container,false);
        ButterKnife.inject(this, rootView);
        ViewCompat.setElevation(rootView, 50);
        gridView = (GridView) rootView.findViewById(R.id.gridView1);

        customGridAdapter = new CustomGridViewAdapter(getActivity(), R.layout.row_grid, gridArray);
        gridView.setAdapter(customGridAdapter);



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                String stickerName = ((TextView) v.findViewById(R.id.item_text)).getText().toString();
                View picture = v.findViewById(R.id.test);
                int imagePosition[] = new int[2];
                picture.getLocationOnScreen(imagePosition);

                DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
                int width = metrics.widthPixels / 2;
                int height = metrics.heightPixels / 2;
                moveViewToScreenCenter(picture, stickerName);

            }
        });
        return rootView;
    }

    private void moveViewToScreenCenter( final View view, final String stickerName )
    {

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics( dm );
        view.getLocationOnScreen( originalPos );

        int xDest = dm.widthPixels/2;
        xDest -= (view.getMeasuredWidth()/2);
        int yDest = dm.heightPixels/2 - (view.getMeasuredHeight()/2) ;

        TranslateAnimation center = new TranslateAnimation( 0, xDest - originalPos[0] , 0, yDest - originalPos[1] );
        center.setDuration(1000);
        center.setFillEnabled(true);
        center.setFillAfter(true);

        final float growTo = 1.8f;
        final long duration = 1600;
        ScaleAnimation grow = new ScaleAnimation(1, growTo, 1, growTo,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        grow.setDuration(duration / 2);
        grow.setFillAfter(true);
        grow.setFillEnabled(true);

        AnimationSet cenerAndGrow = new AnimationSet(true);
        cenerAndGrow.setInterpolator(new LinearInterpolator());
        cenerAndGrow.addAnimation(grow);
        cenerAndGrow.addAnimation(center);
        cenerAndGrow.setFillAfter(true);
        cenerAndGrow.setFillEnabled(true);
        cenerAndGrow.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showSticker(stickerName, view);
            }
        });

        view.startAnimation(cenerAndGrow);
    }

    private void animateStickerBack(View view)
    {
        //  RelativeLayout root = (RelativeLayout) getActivity().findViewById( R.id.fragment_stickers_main );
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics( dm );
        //     int statusBarOffset = dm.heightPixels );

        view.getLocationOnScreen( originalPos );

        int xDest = dm.widthPixels/2;
        xDest -= (view.getMeasuredWidth()/2);
        int yDest = dm.heightPixels/2 - (view.getMeasuredHeight()/2) ;

        TranslateAnimation moveBack = new TranslateAnimation( xDest - originalPos[0], 0,yDest - originalPos[1] , 0  );
        moveBack.setDuration(1000);

        final float growTo = 1.8f;
        final long duration = 1600;
        ScaleAnimation shrink = new ScaleAnimation(growTo, 1, growTo, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(duration / 2);
        shrink.setStartOffset(duration / 2);
        AnimationSet moveBackAndShrink = new AnimationSet(true);
        moveBackAndShrink.setInterpolator(new LinearInterpolator());
        moveBackAndShrink.addAnimation(shrink);
        moveBackAndShrink.addAnimation(moveBack);
        moveBackAndShrink.setFillAfter(true);
        moveBackAndShrink.setFillEnabled(true);

        view.startAnimation(moveBackAndShrink);
    }


    private void showSticker(String name, final View view) {
        // custom dialog
        Database db= Database.getInstance(getActivity());
        Sticker clickerStikcer = db.getStickerForName(name);
        clickerStikcer.getName();
        Log.d("NAMe", clickerStikcer.getName());


        final Dialog dialog = new Dialog(getActivity());

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                animateStickerBack(view);
            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.sticker_dialog);
        ImageView image = (ImageView) (dialog).findViewById(R.id.image);

        //get the correct image
        String file= clickerStikcer.getImagesrc();
        file = file.substring(0, file.lastIndexOf(".")); //trim the extension
        Resources resources = getActivity().getResources();
        int resourceId = resources.getIdentifier(file, "drawable", getActivity().getPackageName());
        image.setImageBitmap(SampleImage.decodeSampledBitmapFromResource(getResources(), resourceId, 250, 250));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = width;
        lp.height = height;
        dialog.getWindow().setAttributes(lp);
        RelativeLayout mainLayout = (RelativeLayout) dialog.findViewById(R.id.showStickerLayout);
        dialog.show();
        // if button is clicked, close the custom dialog
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                animateStickerBack(view);
            }

        });
    }
}