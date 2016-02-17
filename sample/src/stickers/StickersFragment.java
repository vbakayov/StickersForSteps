package stickers;

/**
 * Created by Viktor on 10/21/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ui.CustomGridViewAdapter;
import Database.Database;
import main.MainActivity;
import com.astuetz.viewpager.extensions.sample.R;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

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
    private int width, height;
    private int originalPos[] = new int[2];
    private Map stickerToAlbum = new HashMap<>();
    private NumberProgressBar bnp;
    private TextView stickers_count;

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
        initMapping();



        WindowManager manager = (WindowManager) getActivity().getSystemService(Activity.WINDOW_SERVICE);

            Point point = new Point();
            manager.getDefaultDisplay().getSize(point);
            width = point.x;
            height = point.y;

        Database db = Database.getInstance(getActivity());

       List<Sticker> stickers = db.getStickersWithCountGreatherOrEqualTo(1);
        Log.d("File id ", Integer.toString(stickers.size()));
        for (ListIterator<Sticker > iter = stickers.listIterator(); iter.hasNext(); ) {
            Sticker element = iter.next();
            String file= element.getImagesrc();
            file = file.substring(0, file.lastIndexOf(".")); //trim the extension

            Resources resources = getActivity().getResources();
            int resourceId = resources.getIdentifier(file, "drawable",
                    getActivity().getPackageName());

            //set the sampled sized of the image with the given dimensions
            Bitmap image= SampleImage.decodeSampledBitmapFromResource(getResources(), resourceId, 70, 70);
            boolean stick =  element.getStatus().equals(1);
            //set grid view item
            gridArray.add(new Item(image,element.getName(),stick));




        }

        db.close();

    }

    private void initMapping() {
        stickerToAlbum.put(0, new ArrayList<>(Arrays.asList(2,6,7,3,5,8,4,1)));
        stickerToAlbum.put(1, new ArrayList<>(Arrays.asList(9,13,14,10,15,11,12)));
        stickerToAlbum.put(2, new ArrayList<>(Arrays.asList(16, 17, 18, 19, 20, 21, 22)));
        stickerToAlbum.put(3, new ArrayList<>(Arrays.asList(23, 24, 25, 26, 27, 28, 29)));
        stickerToAlbum.put(4, new ArrayList<>(Arrays.asList(30,31,32,33,34,35,36,37)));
        stickerToAlbum.put(5, new ArrayList<>(Arrays.asList(38,39,40,42,41,43)));
        stickerToAlbum.put(6, new ArrayList<>(Arrays.asList(45,46,47,48,49,50,51,52)));
        stickerToAlbum.put(7, new ArrayList<>(Arrays.asList(53,54,55,56,57)));
        stickerToAlbum.put(8, new ArrayList<>(Arrays.asList(58,59,61,62,63,64,60)));
        stickerToAlbum.put(9, new ArrayList<>(Arrays.asList(65,66,67,68,69,70,71)));
        stickerToAlbum.put(10,new ArrayList<>(Arrays.asList(72,73,74,75,76,77,78,79)));
        stickerToAlbum.put(11,new ArrayList<>(Arrays.asList(80,81,82,83,84,85)));
        stickerToAlbum.put(12,new ArrayList<>(Arrays.asList(86,87,88,89)));
        stickerToAlbum.put(13, new ArrayList<>(Arrays.asList(90,91,92,93,94)));
        stickerToAlbum.put(14, new ArrayList<>(Arrays.asList(95,96,97,98,99,100)));
        stickerToAlbum.put(15, new ArrayList<>(Arrays.asList(100,101,102,103,104,105)));
        stickerToAlbum.put(16, new ArrayList<>(Arrays.asList(106,107,108,109,110,111)));
        stickerToAlbum.put(17, new ArrayList<>(Arrays.asList(112,113,114,115,116,117,118)));
        stickerToAlbum.put(18, new ArrayList<>(Arrays.asList(119,120,122,121)));
        stickerToAlbum.put(19, new ArrayList<>(Arrays.asList(123,124,125,126,127,128)));
        stickerToAlbum.put(20, new ArrayList<>(Arrays.asList(129,130,131,132,133)));
        stickerToAlbum.put(21, new ArrayList<>(Arrays.asList(134,135,136,137,138,139)));
        stickerToAlbum.put(22, new ArrayList<>(Arrays.asList(145, 140, 142, 143, 144, 141)));
        //Log.w("testmap",Integer.toString(getKeyFromValue(23)));
    }

    public  int getStickerToAlbumIndexMapping( int value) {
        Iterator it = stickerToAlbum.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            if(( (ArrayList) pair.getValue()).contains(value))
                return (int) pair.getKey();
            //it.remove(); // avoids a ConcurrentModificationException
        }
        return  -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stickers, container, false);
        ButterKnife.inject(this, rootView);
        ViewCompat.setElevation(rootView, 50);
        gridView = (GridView) rootView.findViewById(R.id.gridView1);

        customGridAdapter = new CustomGridViewAdapter(getActivity(), R.layout.row_grid, gridArray);
        customGridAdapter.notifyDataSetChanged();
        gridView.setAdapter(customGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                String stickerName = ((TextView) v.findViewById(R.id.item_text)).getText().toString();
                View picture = v.findViewById(R.id.test);
                int imagePosition[] = new int[2];
                picture.getLocationOnScreen(imagePosition);

                //stick_image
                DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
                int width = metrics.widthPixels / 2;
                int height = metrics.heightPixels / 2;
                moveViewToScreenCenter(picture, stickerName);

            }
        });


        Database db = Database.getInstance(getActivity());
        bnp = (NumberProgressBar)rootView.findViewById(R.id.number_progress_bar);
        bnp.setMax(145);
        //Log.w("number",Integer.toString(db.getNumberGluedStickers()));
        int gluedCount = db.getNumberGluedStickers();
        bnp.setProgress(gluedCount);
        stickers_count = (TextView)rootView.findViewById(R.id.stickers_count);
        stickers_count.setText(Integer.toString(gluedCount) + "/145 Stickers");

        db.close();
        return rootView;
    }

    private void moveViewToScreenCenter( final View view, final String stickerName )
    {

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        view.getLocationOnScreen(originalPos);

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
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        //     int statusBarOffset = dm.heightPixels );

        view.getLocationOnScreen(originalPos);

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
        final Sticker clickedSticker = db.getStickerForName(name);
        clickedSticker.getName();
        Log.d("NAMe", clickedSticker.getName());


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
        String file= clickedSticker.getImagesrc();
        file = file.substring(0, file.lastIndexOf(".")); //trim the extension
        Resources resources = getActivity().getResources();
        int resourceId = resources.getIdentifier(file, "drawable", getActivity().getPackageName());
        image.setImageBitmap(SampleImage.decodeSampledBitmapFromResource(getResources(), resourceId, 250, 250));

        //load the additional details and information
        TextView id = (TextView) (dialog).findViewById(R.id.sticker_id);
        id.setText("#" + Integer.toString(clickedSticker.getId()));

        TextView status = (TextView) (dialog).findViewById(R.id.sticker_status);
        //at this poinrt only glued and notSticker available glued=1 notGlued=0
         String statuss =  clickedSticker.getStatus().equals(2)? "1": "0";
        Integer count =  clickedSticker.getCount();
        status.setText("("+ statuss+" glued, "+ count+" left)");

        //change here to one
        Button stickButton = (Button) (dialog).findViewById(R.id.button_stick);
       if (clickedSticker.getStatus().equals(1))  stickButton.setVisibility(View.VISIBLE);
        stickButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                animateStickerBack(view);
                dialog.dismiss();
                ((MainActivity)getActivity()).getMainPager().setCurrentItem(1);
                        ((MainActivity) getActivity()).
                        getViewPager().setCurrentItem(getStickerToAlbumIndexMapping(clickedSticker.getId()));
            }
        });




    TextView title = (TextView) (dialog).findViewById(R.id.sticker_title);
        title.setText( clickedSticker.getName());

        TextView rarity = (TextView) (dialog).findViewById(R.id.rarity);
        rarity.setText( clickedSticker.getPopularity());

        //listen for the inf tab
        ImageView info = (ImageView) (dialog).findViewById(R.id.info_image);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog(clickedSticker);
            }

        });

        TextView movie = (TextView) (dialog).findViewById(R.id.sticker_movie);
        movie.setText( clickedSticker.getMovie());
        //set the layout to have the same widh and height as the  windows screen
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

    private void showInfoDialog(Sticker clickedSticker) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle("More Information");
        builder.setMessage(clickedSticker.getDescription());
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    //recieve listChange from the mainActivity
    public void updateList() {
        updateGluedStickerCount();
        new LongOperation().execute("");
    }


    private void updateGluedStickerCount(){
        Database db =Database.getInstance(getActivity());
        int gluedCount = db.getNumberGluedStickers();
        bnp.setProgress(gluedCount);
        stickers_count.setText(Integer.toString(gluedCount) + "/145 Stickers");

        db.close();
    }

    //update the list in asyncTask
    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            gridArray.clear();
            Database db = Database.getInstance(getActivity());

            List<Sticker> stickers = db.getStickersWithCountGreatherOrEqualTo(1);
            Log.d("File id ", Integer.toString(stickers.size()));
            for (Sticker element : stickers) {
                String file = element.getImagesrc();
                file = file.substring(0, file.lastIndexOf(".")); //trim the extension

                Resources resources = getActivity().getResources();
                int resourceId = resources.getIdentifier(file, "drawable",
                        getActivity().getPackageName());

                //set the sampled sized of the image with the given dimensions
                Bitmap image = SampleImage.decodeSampledBitmapFromResource(getResources(), resourceId, 70, 70);
                boolean stick =  element.getStatus().equals(1);
                //set grid view item
                gridArray.add(new Item(image, element.getName(),stick));
                customGridAdapter = null;
                customGridAdapter = new CustomGridViewAdapter(getActivity(), R.layout.row_grid, gridArray);


            }
            db.close();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
           gridView.setAdapter(customGridAdapter);
            customGridAdapter.notifyDataSetChanged();
            gridView.invalidate();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}