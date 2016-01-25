package album;

/**
 * Created by Viktor on 12/14/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.viewpager.extensions.fragment.Database;
import com.astuetz.viewpager.extensions.fragment.StepsFragment;
import com.astuetz.viewpager.extensions.fragment.Sticker;
import com.astuetz.viewpager.extensions.sample.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


import java.util.ArrayList;

public class AlbumPage extends Fragment implements View.OnDragListener, View.OnLongClickListener, View.OnClickListener {

    private static final String TAG = "Drag";
    private static final String ARG_POSITION = "position";

    private ImageView containerImg;
    private  ImageView merida;
    private ImageView dingwall;
    private ImageView triplets;
    private ImageView elinor;
    private ImageView macintosh;
    private ImageView macguffin;
    private int dimension=80;
    private int glued = 2;
    private int recieved = 1;
    private Database db;
    private Bitmap currentImg;
    private Integer status;
    private ImageView angus;
    private Sticker sticker8;
    private Sticker sticker7;
    private Sticker sticker6;
    private Sticker sticker5;
    private Sticker sticker4;
    private Sticker sticker3;
    private Sticker sticker2;
    private Sticker sticker;
    private int notReieved =0;
    private StepsFragment.OnStickerChange notifyActivityStickerStatusChange;


    public static AlbumPage newInstance(String movieName, ArrayList<Integer> stickers,ArrayList<Integer> size) {
        AlbumPage f = new AlbumPage();
        Bundle b = new Bundle();
        b.putString("movieName", movieName);
        b.putIntegerArrayList("stickers", stickers);
        b.putIntegerArrayList("stickerSize", size);
        f.setArguments(b);
        f.setArguments(b);

        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        db = Database.getInstance(getActivity());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.pagethree_layout, container, false);
        Log.w("I AM HEREEE", "HEEE");


        TextView tv = (TextView) view.findViewById(R.id.movieName);
        //set the title i.e movie name for this fragment
        tv.setText(getArguments().getString("movieName"));
        ArrayList<Integer> stickers = new ArrayList<Integer>();
        stickers.addAll(getArguments().getIntegerArrayList("stickers"));
        ArrayList<Integer> size = new ArrayList<Integer>();
        size.addAll(getArguments().getIntegerArrayList("stickerSize"));
        Log.w("Strickers", stickers.toString());

        for(int i =0 ; i< stickers.size();i++) {
            if (i == 0 && stickers.get(0)!= -1) {
                ViewGroup.LayoutParams params = view.findViewById(R.id.container_1).getLayoutParams();
                int sizeSticker = size.get(i);
                params.height = convertdipToPixel(sizeSticker);
                params.width = convertdipToPixel(sizeSticker);
                sticker = db.getSticker(stickers.get(i));
                status = sticker.getStatus();
                containerImg = (ImageView) view.findViewById(R.id.container1_unglued);
                containerImg.setOnLongClickListener(this);
                int resourceId = GetResourceIDForImage(sticker, false);
                //set the sampled sized of the image with the given dimensions
                currentImg = SampleImage.decodeSampledBitmapFromResource(getResources(), resourceId, dimension, dimension);
                currentImg = Bitmap.createScaledBitmap(currentImg, convertdipToPixel(sizeSticker), convertdipToPixel(sizeSticker), true);
                if (status == recieved || status == notReieved) {
                    if (status == recieved) containerImg.setImageBitmap(currentImg);
                    resourceId = GetResourceIDForImage(sticker, true);
                    Bitmap image = BitmapFactory.decodeResource(getResources(), resourceId);
                    image = Bitmap.createScaledBitmap(image, convertdipToPixel(sizeSticker), convertdipToPixel(sizeSticker), true);
                    ((ImageView) view.findViewById(R.id.container1_img)).setImageBitmap(image);
                    ((TextView) view.findViewById(R.id.container1_txt)).setText(sticker.getId().toString());
                } else if(status == glued) {
                    ((ImageView) view.findViewById(R.id.container1_img)).setImageBitmap(currentImg);
                    ((TextView) view.findViewById(R.id.container1_txt)).setText("");
                    view.findViewById(R.id.container_1).setOnClickListener(this);
                }
            }
            if (i == 1 && stickers.get(1)!= -1) {

                ViewGroup.LayoutParams params = view.findViewById(R.id.container_5).getLayoutParams();
                int sizeSticker = size.get(i);
                params.height = convertdipToPixel(sizeSticker);
                params.width = convertdipToPixel(sizeSticker);
                sticker2 = db.getSticker(stickers.get(i));
                status = sticker2.getStatus();
                merida = (ImageView) view.findViewById(R.id.container5_unglued);
                merida.setOnLongClickListener(this);
                int resourceId = GetResourceIDForImage(sticker2, false);
                //set the sampled sized of the image with the given dimensions
                currentImg = SampleImage.decodeSampledBitmapFromResource(getResources(), resourceId, dimension, dimension);
                currentImg = Bitmap.createScaledBitmap(currentImg, convertdipToPixel(sizeSticker), convertdipToPixel(sizeSticker), true);
                if (status == recieved || status == notReieved) {
                    if (status == recieved) merida.setImageBitmap(currentImg);
                    resourceId = GetResourceIDForImage(sticker2, true);
                    Bitmap image = BitmapFactory.decodeResource(getResources(), resourceId);
                    image = Bitmap.createScaledBitmap(image, convertdipToPixel(sizeSticker), convertdipToPixel(sizeSticker), true);
                    ((ImageView) view.findViewById(R.id.merida_img)).setImageBitmap(image);
                    ((TextView) view.findViewById(R.id.merida_txt)).setText(sticker2.getId().toString());
                } else if(  status == glued) {
                    ((ImageView) view.findViewById(R.id.merida_img)).setImageBitmap(currentImg);
                    ((TextView) view.findViewById(R.id.merida_txt)).setText("");
                    view.findViewById(R.id.container_2).setOnClickListener(this);

                }

            }
            if (i == 2  && stickers.get(2)!= -1) {

                ViewGroup.LayoutParams params = view.findViewById(R.id.container_8).getLayoutParams();
                int sizeSticker = size.get(i);
                params.height = convertdipToPixel(sizeSticker);
                params.width = convertdipToPixel(sizeSticker);
                sticker3 = db.getSticker(stickers.get(i));
                status = sticker3.getStatus();
                dingwall = (ImageView) view.findViewById(R.id.dingwall);
                dingwall.setOnLongClickListener(this);
                int resourceId = GetResourceIDForImage(sticker3, false);
                //set the sampled sized of the image with the given dimensions
                currentImg = SampleImage.decodeSampledBitmapFromResource(getResources(), resourceId, dimension, dimension);
                currentImg = Bitmap.createScaledBitmap(currentImg, convertdipToPixel(sizeSticker), convertdipToPixel(sizeSticker), true);
                if (status == recieved || status == notReieved) {
                     if (status == recieved) dingwall.setImageBitmap(currentImg);
                    resourceId = GetResourceIDForImage(sticker3, true);
                    Bitmap image = BitmapFactory.decodeResource(getResources(), resourceId);
                    image = Bitmap.createScaledBitmap(image, convertdipToPixel(90), convertdipToPixel(90), true);
                    ((ImageView) view.findViewById(R.id.dingwall_img)).setImageBitmap(image);
                    ((TextView) view.findViewById(R.id.dingwall_txt)).setText(sticker3.getId().toString());
                } else if(status==glued) {
                    ((ImageView) view.findViewById(R.id.dingwall_img)).setImageBitmap(currentImg);
                    ((TextView) view.findViewById(R.id.dingwall_txt)).setText("");
                    view.findViewById(R.id.container_8).setOnClickListener(this);

                }

            }


        if (i == 3  && stickers.get(3)!= -1) {

            ViewGroup.LayoutParams params = view.findViewById(R.id.container_7).getLayoutParams();
            int sizeSticker = size.get(i);
            params.height = convertdipToPixel(sizeSticker);
            params.width = convertdipToPixel(sizeSticker);
            sticker4 = db.getSticker(stickers.get(i));
            status = sticker4.getStatus();
            triplets = (ImageView) view.findViewById(R.id.triplets);
            triplets.setOnLongClickListener(this);
            int resourceId = GetResourceIDForImage(sticker4, false);
            //set the sampled sized of the image with the given dimensions
            currentImg = SampleImage.decodeSampledBitmapFromResource(getResources(), resourceId, dimension, dimension);
            currentImg = Bitmap.createScaledBitmap(currentImg, convertdipToPixel(sizeSticker), convertdipToPixel(sizeSticker), true);
            if (status == recieved || status == notReieved) {
                if (status == recieved) triplets.setImageBitmap(currentImg);
                resourceId = GetResourceIDForImage(sticker4, true);
                Bitmap image = BitmapFactory.decodeResource(getResources(), resourceId);
                image = Bitmap.createScaledBitmap(image, convertdipToPixel(sizeSticker), convertdipToPixel(sizeSticker), true);
                ((ImageView) view.findViewById(R.id.triplets_img)).setImageBitmap(image);
                ((TextView) view.findViewById(R.id.triplets_txt)).setText(sticker4.getId().toString());
            } else if(status==glued) {
                ((ImageView) view.findViewById(R.id.triplets_img)).setImageBitmap(currentImg);
                ((TextView) view.findViewById(R.id.triplets_txt)).setText("");
                view.findViewById(R.id.container_7).setOnClickListener(this);

            }

        }
            if (i == 4 && stickers.get(4)!= -1) {

                ViewGroup.LayoutParams params = view.findViewById(R.id.container_6).getLayoutParams();
                int sizeSticker = size.get(i);
                params.height = convertdipToPixel(sizeSticker);
                params.width = convertdipToPixel(sizeSticker);
                sticker5 = db.getSticker(stickers.get(i));
                status = sticker5.getStatus();
                elinor = (ImageView) view.findViewById(R.id.elinor);
                elinor.setOnLongClickListener(this);
                int resourceId = GetResourceIDForImage(sticker5, false);
                //set the sampled sized of the image with the given dimensions
                currentImg = SampleImage.decodeSampledBitmapFromResource(getResources(), resourceId, dimension, dimension);
                currentImg = Bitmap.createScaledBitmap(currentImg, convertdipToPixel(sizeSticker), convertdipToPixel(sizeSticker), true);
                if (status == recieved || status == notReieved) {
                    if (status == recieved) elinor.setImageBitmap(currentImg);
                    resourceId = GetResourceIDForImage(sticker5, true);
                    Bitmap image = BitmapFactory.decodeResource(getResources(), resourceId);
                    image = Bitmap.createScaledBitmap(image, convertdipToPixel(sizeSticker), convertdipToPixel(sizeSticker), true);
                    ((ImageView) view.findViewById(R.id.elinor_img)).setImageBitmap(image);
                    ((TextView) view.findViewById(R.id.elinor_txt)).setText(sticker5.getId().toString());
                } else if( status == glued) {
                    ((ImageView) view.findViewById(R.id.elinor_img)).setImageBitmap(currentImg);
                    ((TextView) view.findViewById(R.id.elinor_txt)).setText("");
                    view.findViewById(R.id.container_6).setOnClickListener(this);

                }

            }
            if (i == 5 && stickers.get(5)!= -1) {

                ViewGroup.LayoutParams params = view.findViewById(R.id.container_4).getLayoutParams();
                int sizeSticker = size.get(i);
                params.height = convertdipToPixel(sizeSticker);
                params.width = convertdipToPixel(sizeSticker);
                sticker6 = db.getSticker(stickers.get(i));
                status = sticker6.getStatus();
                macintosh = (ImageView) view.findViewById(R.id.lord_macintosh);
                macintosh.setOnLongClickListener(this);
                int resourceId = GetResourceIDForImage(sticker6, false);
                //set the sampled sized of the image with the given dimensions
                currentImg = SampleImage.decodeSampledBitmapFromResource(getResources(), resourceId, dimension, dimension);
                currentImg = Bitmap.createScaledBitmap(currentImg, convertdipToPixel(sizeSticker), convertdipToPixel(sizeSticker), true);
                if (status == recieved || status == notReieved) {
                    if (status == recieved) macintosh.setImageBitmap(currentImg);
                    resourceId = GetResourceIDForImage(sticker6, true);
                    Bitmap image = BitmapFactory.decodeResource(getResources(), resourceId);
                    image = Bitmap.createScaledBitmap(image, convertdipToPixel(sizeSticker), convertdipToPixel(sizeSticker), true);
                    ((ImageView) view.findViewById(R.id.macintosh_img)).setImageBitmap(image);
                    ((TextView) view.findViewById(R.id.macintosh_txt)).setText(sticker6.getId().toString());
                } else if(status==glued) {
                    ((ImageView) view.findViewById(R.id.macintosh_img)).setImageBitmap(currentImg);
                    ((TextView) view.findViewById(R.id.macintosh_txt)).setText("");
                    view.findViewById(R.id.container_4).setOnClickListener(this);

                }

            }
            if (i == 6 && stickers.get(6)!= -1) {

                ViewGroup.LayoutParams params = view.findViewById(R.id.container_3).getLayoutParams();
                int sizeSticker = size.get(i);
                params.height = convertdipToPixel(sizeSticker);
                params.width = convertdipToPixel(sizeSticker);
                 sticker7 = db.getSticker(stickers.get(i));
                status = sticker7.getStatus();
                macguffin = (ImageView) view.findViewById(R.id.macguffin);
                macguffin.setOnLongClickListener(this);
                int resourceId = GetResourceIDForImage(sticker7, false);
                //set the sampled sized of the image with the given dimensions
                currentImg = SampleImage.decodeSampledBitmapFromResource(getResources(), resourceId, dimension, dimension);
                currentImg = Bitmap.createScaledBitmap(currentImg, convertdipToPixel(sizeSticker), convertdipToPixel(sizeSticker), true);
                if (status == recieved || status == notReieved) {
                    if (status == recieved) macguffin.setImageBitmap(currentImg);
                    resourceId = GetResourceIDForImage(sticker7, true);
                    Bitmap image = BitmapFactory.decodeResource(getResources(), resourceId);
                    image = Bitmap.createScaledBitmap(image, convertdipToPixel(sizeSticker), convertdipToPixel(sizeSticker), true);
                    ((ImageView) view.findViewById(R.id.macguffin_img)).setImageBitmap(image);
                    ((TextView) view.findViewById(R.id.macguffin_txt)).setText(sticker7.getId().toString());
                } else if(status == glued){
                    ((ImageView) view.findViewById(R.id.macguffin_img)).setImageBitmap(currentImg);
                    ((TextView) view.findViewById(R.id.macguffin_txt)).setText("");
                    view.findViewById(R.id.container_3).setOnClickListener(this);

                }

            }
            if (i == 7 && stickers.get(7)!= -1) {

                ViewGroup.LayoutParams params = view.findViewById(R.id.container_2).getLayoutParams();
                int sizeSticker = size.get(i);
                params.height = convertdipToPixel(sizeSticker);
                params.width = convertdipToPixel(sizeSticker);
                sticker8 = db.getSticker(stickers.get(i));
                status = sticker8.getStatus();
                angus = (ImageView) view.findViewById(R.id.angus);
                angus.setOnLongClickListener(this);
                int resourceId = GetResourceIDForImage(sticker8, false);
                //set the sampled sized of the image with the given dimensions
                currentImg = SampleImage.decodeSampledBitmapFromResource(getResources(), resourceId, dimension, dimension);
                currentImg = Bitmap.createScaledBitmap(currentImg, convertdipToPixel(sizeSticker), convertdipToPixel(sizeSticker), true);
                if (status == recieved || status == notReieved) {
                    if (status == recieved) angus.setImageBitmap(currentImg);
                    resourceId = GetResourceIDForImage(sticker8, true);
                    Bitmap image = BitmapFactory.decodeResource(getResources(), resourceId);
                    image = Bitmap.createScaledBitmap(image, convertdipToPixel(sizeSticker), convertdipToPixel(sizeSticker), true);
                    ((ImageView) view.findViewById(R.id.angus_img)).setImageBitmap(image);
                    ((TextView) view.findViewById(R.id.angus_txt)).setText(sticker8.getId().toString());
                } else if(status == glued) {
                    ((ImageView) view.findViewById(R.id.angus_img)).setImageBitmap(currentImg);
                    ((TextView) view.findViewById(R.id.angus_txt)).setText("");
                    view.findViewById(R.id.container_2).setOnClickListener(this);

                }

            }

    }


//      register drag event listeners for the target layout containers
        view.findViewById(R.id.stick_container).setOnDragListener(this);
        view.findViewById(R.id.container_1).setOnDragListener(this);
        view.findViewById(R.id.container_2).setOnDragListener(this);
        view.findViewById(R.id.container_3).setOnDragListener(this);
        view.findViewById(R.id.container_4).setOnDragListener(this);
        view.findViewById(R.id.container_5).setOnDragListener(this);
        view.findViewById(R.id.container_6).setOnDragListener(this);
        view.findViewById(R.id.container_7).setOnDragListener(this);
        view.findViewById(R.id.container_8).setOnDragListener(this);




        return view;
    }
    private  int GetResourceIDForImage( Sticker sticker, boolean background) {
        String file = sticker.getImagesrc();
        Resources resources = getActivity().getResources();
        //trim the extension
        file = file.substring(0, file.lastIndexOf("."));
        int resourceId= 0;
        if(!background){
             resourceId = resources.getIdentifier(file, "drawable", getActivity().getPackageName());}
        else {
            file +="_bg";
            resourceId  = resources.getIdentifier(file, "drawable", getActivity().getPackageName());
        }
        return  resourceId;
    }

    private int convertdipToPixel(int dipValue){

        Resources r = getResources();
        int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
        return px;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            notifyActivityStickerStatusChange = (StepsFragment.OnStickerChange) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    //    called when ball has been touched and held
    @Override
    public boolean onLongClick(View imageView) {
        //        the ball has been touched
//            create clip data holding data of the type MIMETYPE_TEXT_PLAIN
        ClipData clipData = ClipData.newPlainText("", "");

        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(imageView);
            /*start the drag - contains the data to be dragged,
            metadata for this data and callback for drawing shadow*/
        imageView.startDrag(clipData, shadowBuilder, imageView, 0);
//        we're dragging the shadow so make the view invisible
        imageView.setVisibility(View.INVISIBLE);
        return true;
    }

    //    called when the ball starts to be dragged
//    used by top and bottom layout containers
    @Override
    public boolean onDrag(View receivingLayoutView, DragEvent dragEvent) {
        final View draggedImageView = (View) dragEvent.getLocalState();
        int offsetX = (int)dragEvent.getX();//(int)motionEvent.getX();
        int offsetY = (int)dragEvent.getY();//motionEvent.getY();
        int originalPos[] = new int[2];
        draggedImageView.getLocationOnScreen(originalPos);

        // Handles each of the expected events
        switch (dragEvent.getAction()) {

            case DragEvent.ACTION_DRAG_STARTED:
                Log.i(TAG, "drag action started");

                // Determines if this View can accept the dragged data
                if (dragEvent.getClipDescription()
                        .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    Log.i(TAG, "Can accept this data");

                    // returns true to indicate that the View can accept the dragged data.
                    return true;

                } else {
                    Log.i(TAG, "Can not accept this data");

                }

                // Returns false. During the current drag and drop operation, this View will
                // not receive events again until ACTION_DRAG_ENDED is sent.
                return false;

            case DragEvent.ACTION_DRAG_ENTERED:
                Log.i(TAG, "drag action entered");
//                the drag point has entered the bounding box
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                //  Log.i(TAG, "drag action location");
                offsetX = (int)dragEvent.getX();//(int)motionEvent.getX();
                offsetY = (int)dragEvent.getY();//motionEvent.getY();

                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                Log.i(TAG, "drag action exited");
//                the drag shadow has left the bounding box
                return true;

            case DragEvent.ACTION_DROP:
                  /* the listener receives this action type when
                  drag shadow released over the target view
            the action only sent here if ACTION_DRAG_STARTED returned true
            return true if successfully handled the drop else false*/
                switch (draggedImageView.getId()) {
                    case R.id.angus:
                        Log.i(TAG, "containerImg");
                        if(receivingLayoutView.getId()== R.id.container_2) {
                            stickSticker(angus,draggedImageView,receivingLayoutView);
                            updateStatusDatabase(sticker8);
                            updateCountForAchievements();
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }

                    case R.id.container5_unglued:
                        Log.i(TAG, "merida");
                        if(receivingLayoutView.getId()== R.id.container_5) {
                            stickSticker(merida,draggedImageView,receivingLayoutView);
                            updateStatusDatabase(sticker2);
                            updateCountForAchievements();
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }
                    case R.id.dingwall:
                        Log.i(TAG, "dingwall");
                        if(receivingLayoutView.getId()== R.id.container_8) {
                            stickSticker(dingwall,draggedImageView,receivingLayoutView);
                            updateStatusDatabase(sticker3);
                            updateCountForAchievements();
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }
                    case R.id.triplets:
                        Log.i(TAG, "triplets");
                        if(receivingLayoutView.getId()== R.id.container_7) {
                            stickSticker(triplets,draggedImageView,receivingLayoutView);
                            updateStatusDatabase(sticker4);
                            updateCountForAchievements();
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }
                    case R.id.elinor:
                        Log.i(TAG, "elinor");
                        if(receivingLayoutView.getId()== R.id.container_6) {
                            stickSticker(elinor, draggedImageView,receivingLayoutView);
                            updateStatusDatabase(sticker5);
                            updateCountForAchievements();
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }
                    case R.id.lord_macintosh:
                        Log.i(TAG, "macintosh");
                        if(receivingLayoutView.getId()== R.id.container_4) {
                            stickSticker(macintosh,draggedImageView,receivingLayoutView);
                            updateStatusDatabase(sticker6);
                            updateCountForAchievements();
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }

                    case R.id.macguffin:
                        Log.i(TAG, "macgguffin");
                        if(receivingLayoutView.getId()== R.id.container_3) {
                            stickSticker(macguffin,draggedImageView,receivingLayoutView);
                            updateStatusDatabase(sticker7);
                            updateCountForAchievements();
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }
                    case R.id.container1_unglued:
                        Log.i(TAG, "conainer1");

                        if(receivingLayoutView.getId()== R.id.container_1) {
                            stickSticker(containerImg,draggedImageView,receivingLayoutView);
                            updateStatusDatabase(sticker);
                            updateCountForAchievements();
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }
                    default:
                        Log.i(TAG, "in default");
                        return false;
                }

            case DragEvent.ACTION_DRAG_ENDED:

                Log.i(TAG, "drag action ended");
                Log.i(TAG, "getResult: " + dragEvent.getResult());

//                if the drop was not successful, set the ball to visible
                if (!dragEvent.getResult()) {
                    draggedImageView.post(new Runnable() {
                        @Override
                        public void run() {
                            draggedImageView.setVisibility(View.VISIBLE);
                        }
                    });

                }

                return true;
            // An unknown action type was received.
            default:
                Log.i(TAG, "Unknown action type received by OnDragListener.");
                break;
        }
        return false;
    }

    private void updateStatusDatabase(Sticker sticker) {
        db.updateStatus(sticker.getId(), 2);
        db.updateCount(sticker.getId(),"decrease");
        notifyActivityStickerStatusChange.notifyChange();
    }

    private void updateCountForAchievements() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int count = prefs.getInt("glued stickers", 0)+1;
        if (count ==5 || count ==10 || count==15){
             int availablePacks = prefs.getInt("packs",0);
            prefs.edit().putInt("packs", availablePacks+1).apply();
            Toast.makeText(getActivity(), "Achieved for gluing stickers completed. You received one free pack",
                    Toast.LENGTH_LONG).show();

        }
        prefs.edit().putInt("glued stickers", count).apply();
    }

    private void stickSticker(ImageView sticker, View draggedImageView, View receivingLayoutView){
        ViewGroup draggedImageViewParentLayout = (ViewGroup) draggedImageView.getParent();
        draggedImageViewParentLayout.removeView(draggedImageView);
        RelativeLayout bottomLinearLayout = (RelativeLayout) receivingLayoutView;
        bottomLinearLayout.addView(draggedImageView);
        draggedImageView.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.BounceIn).duration(700).playOn(sticker);
        draggedImageView.setOnLongClickListener(null);

    }

    private void AnimateStickerBack( View view, int offsetX, int offsetY, int originalPosX, int originalPosY ) {

        Animation animation = new TranslateAnimation(offsetX - originalPosX - 160, 0, offsetY -  originalPosY + 240, 0);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator(1));
        view.startAnimation(animation);
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.container_1:

                Log.w("picture Clicked", "1");
                showStickerMoreInfo(sticker);
                break;
            case R.id.container_2:
                Log.w("picture Clicked", "2");
                showStickerMoreInfo(sticker8);
                break;
            case R.id.container_3:
                Log.w("picture Clicked", "3");
                showStickerMoreInfo(sticker7);
                break;
            case R.id.container_4:
                Log.w("picture Clicked", "4");
                showStickerMoreInfo(sticker6);
                break;
            case R.id.container_5:
                Log.w("picture Clicked", "5");
                showStickerMoreInfo(sticker2);
                break;
            case R.id.container_6:
                Log.w("picture Clicked", "6");
                showStickerMoreInfo(sticker5);
                break;
            case R.id.container_7:
                Log.w("picture Clicked", "7");
                showStickerMoreInfo(sticker4);
                break;
            case R.id.container_8:
                Log.w("picture Clicked", "8");
                showStickerMoreInfo(sticker3);

                break;

        }
    }

    private void showStickerMoreInfo(final Sticker clickedSticker) {
        // custom dialog


        clickedSticker.getName();
        Log.d("NAMe", clickedSticker.getName());


        final Dialog dialog = new Dialog(getActivity());

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
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
        status.setText("(" + statuss + " glued, " + count + " left)");





        TextView title = (TextView) (dialog).findViewById(R.id.sticker_title);
        title.setText(clickedSticker.getName());

        TextView rarity = (TextView) (dialog).findViewById(R.id.rarity);
        rarity.setText(clickedSticker.getPopularity());

        TextView movie = (TextView) (dialog).findViewById(R.id.sticker_movie);
        movie.setText(clickedSticker.getMovie());
        //set the layout to have the same widh and height as the  windows screen


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


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


        //listen for the inf tab
        ImageView info = (ImageView) (dialog).findViewById(R.id.info_image);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog(clickedSticker);
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

}