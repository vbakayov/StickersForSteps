package com.astuetz.viewpager.extensions.fragment;

/**
 * Created by Viktor on 10/21/2015.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.viewpager.extensions.sample.R;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import album.SampleImage;
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
    private       int width, height;

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
                int test2[] = new int[2];
                picture.getLocationOnScreen(test2);
                animateStickerBack( picture, 300, 300, test2[0], test2[1] );
            //    showSticker(stickerName);

            }
        });
        return rootView;
    }

    private void animateStickerBack( View view, int offsetX, int offsetY, int originalPosX, int originalPosY ) {

        Animation animation = new TranslateAnimation(offsetX - originalPosX - 160, 0, offsetY -  originalPosY + 240, 0);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator(1));
        view.startAnimation(animation);
    }

    private void showSticker(String name) {
        // custom dialog
        Database db= Database.getInstance(getActivity());
        Sticker clickerStikcer = db.getStickerForName(name);
        clickerStikcer.getName();
        Log.d("NAMe", clickerStikcer.getName());


        final Dialog dialog = new Dialog(getActivity());
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
            }
        });
    }
}