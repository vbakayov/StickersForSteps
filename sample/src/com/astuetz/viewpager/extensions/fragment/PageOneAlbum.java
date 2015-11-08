package com.astuetz.viewpager.extensions.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.astuetz.viewpager.extensions.sample.R;

public class PageOneAlbum extends Fragment  implements View.OnDragListener, View.OnLongClickListener {

    private static final String TAG = "Drag";
    private static final String ARG_POSITION = "position";
    private  ImageView target;
    private  Bitmap image;
    private  Bitmap image2;


    public static PageOneAlbum newInstance(int position) {
        PageOneAlbum f = new PageOneAlbum();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.pageone_fragment, container, false);
        Log.w("I AM HEREEE", "HEEE");
        //        register a long click listener for the balls
       view.findViewById(R.id.soccer).setOnLongClickListener(this);
        view. findViewById(R.id.sherman).setOnLongClickListener(this);
        view.findViewById(R.id.rugby).setOnLongClickListener(this);

//        register drag event listeners for the target layout containers
        view.findViewById(R.id.top_container).setOnDragListener(this);
        view.findViewById(R.id.bottom_container).setOnDragListener(this);
        target=  (ImageView) view.findViewById((R.id.Sticker1Container));
       image = BitmapFactory.decodeResource(getResources(), R.drawable.sherman);
        //then create a copy of bitmap bmp1 into bmp2
         image2 = image.copy(image.getConfig(), true);
        convertImage();
        target.setImageBitmap(image2);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        View draggedImageView = (View) dragEvent.getLocalState();

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
                Log.i(TAG, "drag action location");
                /*triggered after ACTION_DRAG_ENTERED
                stops after ACTION_DRAG_EXITED*/
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
                    case R.id.soccer:
                        Log.i(TAG, "Soccer ball");
                        return false;
                    case R.id.sherman:
                        Log.i(TAG, "Sherman head");
                        ViewGroup draggedImageViewParentLayout
                                = (ViewGroup) draggedImageView.getParent();
                        draggedImageViewParentLayout.removeView(draggedImageView);
                        LinearLayout bottomLinearLayout = (LinearLayout) receivingLayoutView;
                        bottomLinearLayout.removeView(target);
                        bottomLinearLayout.addView(draggedImageView);
                        draggedImageView.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.rugby:
                        Log.i(TAG, "Rugby ball");
                        return false;
                    default:
                        Log.i(TAG, "in default");
                        return false;
                }

            case DragEvent.ACTION_DRAG_ENDED:

                Log.i(TAG, "drag action ended");
                Log.i(TAG, "getResult: " + dragEvent.getResult());

//                if the drop was not successful, set the ball to visible
                if (!dragEvent.getResult()) {
                    Log.i(TAG, "setting visible");
                    draggedImageView.setVisibility(View.VISIBLE);
                }

                return true;
            // An unknown action type was received.
            default:
                Log.i(TAG, "Unknown action type received by OnDragListener.");
                break;
        }
        return false;
    }

   private void convertImage() {
       
       for (int x = 0; x < image.getWidth(); x++) {
           for (int y = 0; y < image.getHeight(); y++) {
               if (image.getPixel(x, y) != Color.TRANSPARENT) {
                   image2.setPixel(x, y, Color.BLACK);
               }
           }
       }
   }
}