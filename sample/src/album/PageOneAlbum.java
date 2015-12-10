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

public class PageOneAlbum extends Fragment  implements View.OnDragListener, View.OnLongClickListener {

    private static final String TAG = "Drag";
    private static final String ARG_POSITION = "position";

    private  ImageView adam;
    private  ImageView berry;
    private ImageView janet;
    private ImageView larry;
    private ImageView layton;
    private ImageView mooseblood;
    private ImageView lou;
    private ImageView fergus;


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
        adam = (ImageView) view.findViewById(R.id.adam_flayman_o);
        adam.setOnLongClickListener(this);;
        berry = (ImageView) view.findViewById(R.id.berry_b_benson_o);
        berry.setOnLongClickListener(this);
        janet = (ImageView) view.findViewById(R.id.janet_o);
        janet.setOnLongClickListener(this);
        larry = (ImageView) view.findViewById(R.id.larry_king_o);
        larry.setOnLongClickListener(this);
        layton = (ImageView) view.findViewById(R.id.layton_o);
        layton.setOnLongClickListener(this);
        mooseblood = (ImageView) view.findViewById(R.id.mooseblood_o);
        mooseblood.setOnLongClickListener(this);
        lou = (ImageView) view.findViewById(R.id.lou_o);
        lou.setOnLongClickListener(this);

//      register drag event listeners for the target layout containers
        view.findViewById(R.id.lou).setOnDragListener(this);
        view.findViewById(R.id.adam_flayman).setOnDragListener(this);
        view.findViewById(R.id.berry_b_benson).setOnDragListener(this);
        view.findViewById(R.id.janetContainer).setOnDragListener(this);
        view.findViewById(R.id.larry_king).setOnDragListener(this);
        view.findViewById(R.id.layton).setOnDragListener(this);
        view.findViewById(R.id.mooseblood).setOnDragListener(this);
        view.findViewById(R.id.stick_container).setOnDragListener(this);


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
                    case R.id.adam_flayman_o:
                        Log.i(TAG, "adam");
                        if(receivingLayoutView.getId()== R.id.adam_flayman) {
                            stickSticker(adam,draggedImageView,receivingLayoutView);
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }

                    case R.id.berry_b_benson_o:
                        Log.i(TAG, "berry");
                        if(receivingLayoutView.getId()== R.id.berry_b_benson) {
                            stickSticker(berry,draggedImageView,receivingLayoutView);
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }
                    case R.id.janet_o:
                        Log.i(TAG, "janet");
                        if(receivingLayoutView.getId()== R.id.janetContainer) {
                            stickSticker(janet,draggedImageView,receivingLayoutView);
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }
                    case R.id.larry_king_o:
                        Log.i(TAG, "larry");
                        if(receivingLayoutView.getId()== R.id.larry_king) {
                            stickSticker(larry,draggedImageView,receivingLayoutView);
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }
                    case R.id.layton_o:
                        Log.i(TAG, "layton");
                        if(receivingLayoutView.getId()== R.id.layton) {
                            stickSticker(layton,draggedImageView,receivingLayoutView);
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }
                    case R.id.mooseblood_o:
                        Log.i(TAG, "mooseblood");
                        if(receivingLayoutView.getId()== R.id.mooseblood) {
                            stickSticker(mooseblood,draggedImageView,receivingLayoutView);
                            return true;
                        }else{
                            AnimateStickerBack(draggedImageView,offsetX,offsetY,originalPos[0],originalPos[1]);
                            return false;
                        }

                    case R.id.lou_o:
                        Log.i(TAG, "lou_o");
                        if(receivingLayoutView.getId()== R.id.lou) {
                            stickSticker(lou,draggedImageView,receivingLayoutView);
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