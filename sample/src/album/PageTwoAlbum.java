package album;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.astuetz.viewpager.extensions.sample.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class PageTwoAlbum extends Fragment  implements View.OnDragListener, View.OnLongClickListener {

    private static final String TAG = "Drag";
    private static final String ARG_POSITION = "position";

    private  ImageView angus;
    private  ImageView merida;
    private ImageView dingwall;
    private ImageView triplets;
    private ImageView elinor;
    private ImageView macintosh;
    private ImageView macguffin;
    private ImageView fergus;


    public static PageTwoAlbum newInstance(int position) {
        PageTwoAlbum f = new PageTwoAlbum();
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
        View view = inflater.inflate(R.layout.pagetwo_fragment, container, false);
        Log.w("I AM HEREEE", "HEEE");
        //        register a long click listener for the balls
        angus = (ImageView) view.findViewById(R.id.angus);
        angus.setOnLongClickListener(this);;
        merida = (ImageView) view.findViewById(R.id.merida);
        merida.setOnLongClickListener(this);
        dingwall = (ImageView) view.findViewById(R.id.dingwall);
        dingwall.setOnLongClickListener(this);
        triplets = (ImageView) view.findViewById(R.id.triplets);
        triplets.setOnLongClickListener(this);
        elinor = (ImageView) view.findViewById(R.id.elinor);
        elinor.setOnLongClickListener(this);
        macintosh = (ImageView) view.findViewById(R.id.lord_macintosh);
        macintosh.setOnLongClickListener(this);
        macguffin = (ImageView) view.findViewById(R.id.macguffin);
        macguffin.setOnLongClickListener(this);
        fergus = (ImageView) view.findViewById(R.id.fergus);
        fergus.setOnLongClickListener(this);


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
//                Log.i("MEASUREMENTS X",Integer.toString(offsetX));
//                Log.i("MEASUREMENTS  Y",Integer.toString(offsetX));
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
                        Log.i(TAG, "angus");
                        if(receivingLayoutView.getId()== R.id.container_2) {
                            stickSticker(angus,draggedImageView,receivingLayoutView);
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }

                    case R.id.merida:
                        Log.i(TAG, "merida");
                        if(receivingLayoutView.getId()== R.id.container_5) {
                            stickSticker(merida,draggedImageView,receivingLayoutView);
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }
                    case R.id.dingwall:
                        Log.i(TAG, "dingwall");
                        if(receivingLayoutView.getId()== R.id.container_8) {
                            stickSticker(dingwall,draggedImageView,receivingLayoutView);
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }
                    case R.id.triplets:
                        Log.i(TAG, "triplets");
                        if(receivingLayoutView.getId()== R.id.container_7) {
                            stickSticker(triplets,draggedImageView,receivingLayoutView);
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }
                    case R.id.elinor:
                        Log.i(TAG, "elinor");
                        if(receivingLayoutView.getId()== R.id.container_6) {
                            stickSticker(elinor,draggedImageView,receivingLayoutView);
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }
                    case R.id.lord_macintosh:
                        Log.i(TAG, "macintosh");
                        if(receivingLayoutView.getId()== R.id.container_4) {
                            stickSticker(macintosh,draggedImageView,receivingLayoutView);
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }

                    case R.id.macguffin:
                        Log.i(TAG, "macgguffin");
                        if(receivingLayoutView.getId()== R.id.container_3) {
                            stickSticker(macguffin,draggedImageView,receivingLayoutView);
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }
                    case R.id.fergus:
                        Log.i(TAG, "fergus");

                        if(receivingLayoutView.getId()== R.id.container_1) {
                            stickSticker(fergus,draggedImageView,receivingLayoutView);
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


}