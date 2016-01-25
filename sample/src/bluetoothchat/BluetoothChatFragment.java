/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bluetoothchat;






import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.viewpager.extensions.fragment.Database;
import com.astuetz.viewpager.extensions.fragment.MainActivity;
import com.astuetz.viewpager.extensions.fragment.StepsFragment;
import com.astuetz.viewpager.extensions.fragment.Sticker;
import com.astuetz.viewpager.extensions.sample.R;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.SweetSheet;
import com.mingle.sweetpick.ViewPagerDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends Fragment {

    private static final String TAG = "BluetoothChatFragment";

    private static final String ARG_POSITION = "position";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private ArrayList<MenuEntity> list = new ArrayList<>();

    // Layout Views
    private Button mSendButton;
    private SweetSheet mSweetSheet3;
    private boolean givePic;
    private boolean getPic;
    private boolean otherAccepted;
    private boolean Iaccepted;
    private ImageView imageReceive ;
    private ImageView imageGive;
    private String stickerNameGive;
    private String stickerNameRecieve;
    private boolean connectionLost;
    private StepsFragment.OnStickerChange notifyActivityStickerStatusChange;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;
    private TextView statusTextView;
    private RelativeLayout rl;
    private Button btnAccept;
    private Button btnDecline;
    private Button btnEnableSwap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        givePic=false;
        getPic=false;
        otherAccepted=false;
        Iaccepted=false;
        setUpListStickers();
        connectionLost= false;

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//            // Otherwise, setup the chat session
//        } else if (mChatService == null) {
//            setupChat();
//        }
    }

    private void EnableBluetooth(){
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        statusTextView= (TextView) view.findViewById(R.id.bluetoothStatus);
        imageGive = (ImageView) view.findViewById(R.id.imageViewGive);
        imageReceive = (ImageView) view.findViewById(R.id.imageView2);
        btnAccept= (Button) view.findViewById(R.id.imageButton);
        btnDecline = (Button) view.findViewById(R.id.imageButton2);
        rl = (RelativeLayout) view.findViewById(R.id.rlBlth );

        btnEnableSwap = (Button) view.findViewById(R.id.enableSwap);
        if(mBluetoothAdapter.isEnabled()) btnEnableSwap.setText("Connect a device");

        btnEnableSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //enable bluetooth if not enabled
               if(! mBluetoothAdapter.isEnabled()) {
                   EnableBluetooth();
               }
                else{
                // Launch the DeviceListActivity to see devices and do scan
               Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
               }
            }
        });



        btnAccept.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                    Iaccepted=true;
                    imageGive.buildDrawingCache();
                    imageGive.setImageBitmap(bitmapOverlay(imageGive.getDrawingCache()));
                    sendMessage(toJSon("accept", null, true));
                    //if both accepted change the sticker status
                    if(otherAccepted){
                        changeStickerdbStatus(stickerNameRecieve,false);
                        changeStickerdbStatus(stickerNameGive,true);
                        resultGUI("The stickers were successfully swapped", true);
                        notifyActivityStickerStatusChange.notifyChange();
                        setUpListStickers();
                        setupCustomView();
                        updateSwapCount();
                    }

            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                resultGUI("The stickers were not swapped, Transaction was cancelled",false);
                sendMessage(toJSon("accept", null, false));

            }
        });

        rl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mChatService == null)
                    Toast.makeText(getActivity(), "Connect to a device first",
                        Toast.LENGTH_LONG).show();

            }
        });


        btnAccept.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        btnDecline.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

       // if(! mBluetoothAdapter.isEnabled())
        btnAccept.setVisibility(View.INVISIBLE);
        btnDecline.setVisibility(View.INVISIBLE);


        if(rl == null) android.util.Log.d("HEREEEE","IT IS NUULLL");
        else{
            android.util.Log.d("HEREEEE"," not null");
        }
        view.findViewById(R.id.imageViewGive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mChatService != null && connectionLost== false) {
                    if (!otherAccepted && (!Iaccepted || otherAccepted)) {
                          if(list.size() != 0) {
                              setupCustomView();
                              mSweetSheet3.toggle();
                        }else{
                              Toast.makeText(getActivity(), "You don't have any stickers to swap", Toast.LENGTH_LONG).show();
                          }

                    }
                }else{
                    Toast.makeText(getActivity(), "Connect to a device first",
                            Toast.LENGTH_LONG).show();
                }
            }

            ;
        });}

    private void updateSwapCount() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int count = prefs.getInt("swap count", 0);
        prefs.edit().putInt("swap count", count + 1).apply();
    }


    private void setUpListStickers(){
        list.clear();
        Database db = Database.getInstance(getActivity());
        List<Sticker> stickers = db.getStickersWithCountGreatherOrEqualTo(1);
        android.util.Log.d("File id ", Integer.toString(stickers.size()));
        for (ListIterator<Sticker > iter = stickers.listIterator(); iter.hasNext(); ) {
            Sticker element = iter.next();
            String file= element.getImagesrc();
            file = file.substring(0, file.lastIndexOf(".")); //trim the extension
            android.util.Log.d("File ", file);

            Resources resources = getActivity().getResources();
            int resourceId = resources.getIdentifier(file, "drawable",
                    getActivity().getPackageName());

            //set the sampled sized of the image with the given dimensions
            Bitmap image=  decodeSampledBitmapFromResource(getResources(),resourceId, 30, 30);
            Drawable drawable = new BitmapDrawable(getResources(), image);
            MenuEntity menuEntity1 = new MenuEntity();
            menuEntity1.icon = drawable;
            menuEntity1.title = element.getName();
            list.add(menuEntity1);


        }

        db.close();

    }

    private void resultGUI(String message, boolean success) {
        givePic= false;
        getPic= false;
        otherAccepted=false;
        Iaccepted=false;
        imageGive.setImageResource(R.drawable.questionmark);
        imageReceive.setImageResource(R.drawable.questionmark);
        btnAccept.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        btnDecline.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        btnAccept.setEnabled(false);
        btnDecline.setEnabled(false);
        showAlertDialog("Result Message", message, success);
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

      //  mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key


        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable.
     */
    public void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        android.util.Log.i("BLUETOOTH","SubTitle2");
        statusTextView.setText(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        android.util.Log.i("BLUETOOTH","SubTitle");
        statusTextView.setText(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            btnEnableSwap.setVisibility(View.GONE);
                            btnAccept.setVisibility(View.VISIBLE);
                            btnDecline.setVisibility(View.VISIBLE);
                            btnAccept.setEnabled(false);
                            btnDecline.setEnabled(false);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:

                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            btnEnableSwap.setVisibility(View.VISIBLE);
                            btnAccept.setVisibility(View.GONE);
                            btnDecline.setVisibility(View.GONE);
                            btnAccept.setEnabled(false);
                            btnDecline.setEnabled(false);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    fromJSon(readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        connectionLost=false;
                    }
                    break;
                //this is used when disconnection event happens
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                        Log.d("HEREE", "Disconestion");
                        if(msg.getData().getString(Constants.TOAST).equals("Device connection was lost")){
                            connectionLost = true;
                            Log.d("Print lost connection", "Lost connection");
                            resultGUI("Sorry, device connection was lost",false);
                        }
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect

                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    btnEnableSwap.setText("Connect a device");
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                   // getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        //in case blth is enabled prior the start of the application init mChatService
        if(mChatService == null)
            setupChat();

        mChatService.connect(device, secure);
    }




    public boolean onOptionsItemSelectedPrivate(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.secure_connect_scan: {
//                // Launch the DeviceListActivity to see devices and do scan
//                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
//                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
//                return true;
//            }
//            case R.id.insecure_connect_scan: {
//                // Launch the DeviceListActivity to see devices and do scan
//                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
//                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
//                return true;
//            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }


            default:
            return ((MainActivity)getActivity()).optionsItemSelected(item);
        }

    }

    public static BluetoothChatFragment newInstance(int i) {

        BluetoothChatFragment f = new BluetoothChatFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, i);
        f.setArguments(b);

        return f;
    }


    private Bitmap bitmapOverlay(Bitmap bmp1)
    {
        Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.accept2);
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, new Matrix(), null);
        return bmOverlay;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public void setupCustomView() {
        mSweetSheet3=null;
        mSweetSheet3 = new SweetSheet(rl);

        //从menu
        mSweetSheet3.setMenuList(list);
        mSweetSheet3.setDelegate(new ViewPagerDelegate());
        mSweetSheet3.setBackgroundEffect(new DimEffect(8f));
        mSweetSheet3.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
            @Override
            public boolean onItemClick(int position, MenuEntity menuEntity1) {
                givePic=true;
                imageGive.setImageResource(getImageResourceIdForStickerName(String.valueOf(menuEntity1.title)));
                imageGive.buildDrawingCache();
                stickerNameGive = String.valueOf(menuEntity1.title);
                //send message, make it Json object and convert id to string
                sendMessage(toJSon("giveSticker", String.valueOf(menuEntity1.title), null));
                Toast.makeText(getActivity(), menuEntity1.title + "  " +menuEntity1.icon, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private int getImageResourceIdForStickerName(String stickerName){
        Database db = Database.getInstance(getActivity());
        Sticker sticker = db.getStickerForName(String.valueOf(stickerName));
        String file = sticker.getImagesrc();
        Resources resources = getActivity().getResources();
        //trim the extension
        file = file.substring(0, file.lastIndexOf("."));
        return resources.getIdentifier(file, "drawable", getActivity().getPackageName());
    }

    public boolean backPressed() {
        if (mSweetSheet3!= null && mSweetSheet3.isShow()) {
            mSweetSheet3.dismiss();
            return false;
        }else {
            return true;
        }

    }

    public  String toJSon(String type, String str,Boolean accept){
        JSONObject jsonObject= new JSONObject();
        try {
            if( type.equals("giveSticker")) {
                jsonObject.put("giveSticker",str);
                givePic=true;
                if (givePic && getPic){
                    btnAccept.getBackground().setColorFilter(null);
                    btnDecline.getBackground().setColorFilter(null);
                    btnAccept.setEnabled(true);
                    btnDecline.setEnabled(true);

                }
            }
            if( type.equals("accept"))  jsonObject.put("accept",accept);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  jsonObject.toString();

    }

    public  String fromJSon( String data){
        String stickername=null;
        Boolean accept = null;
        try {
            JSONObject jObj =  new JSONObject(data);
            if(jObj.has("giveSticker")){
                getPic=true;
                stickername = jObj.getString("giveSticker");
                imageReceive.setImageResource( getImageResourceIdForStickerName(stickername));
                imageReceive.buildDrawingCache();
                stickerNameRecieve= stickername;
                mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + data);
                if (givePic && getPic){
                    btnAccept.getBackground().setColorFilter(null);
                    btnDecline.getBackground().setColorFilter(null);
                    btnAccept.setEnabled(true);
                    btnDecline.setEnabled(true);
                }
                if ( getPic){ btnDecline.setEnabled(true);  btnDecline.getBackground().setColorFilter(null);}
            }
            if(jObj.has("accept")){
                accept = jObj.getBoolean("accept");
                if (accept){
                    otherAccepted=true;
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + data);
                    imageReceive.buildDrawingCache();
                    imageReceive.setImageBitmap(bitmapOverlay(imageReceive.getDrawingCache()));
                    //if both has accepted do the swap
                    if(Iaccepted){
                        changeStickerdbStatus(stickerNameGive,true);
                        changeStickerdbStatus(stickerNameRecieve, false);
                        resultGUI("The stickers were successfully swapped",true);
                        notifyActivityStickerStatusChange.notifyChange();
                        setUpListStickers();
                        setupCustomView();
                        updateSwapCount();
                    }
                }else{
                    resultGUI("The stickers were not swapped, Transaction was cancelled",false);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  stickername;
    }

    private void changeStickerdbStatus( String stickername, boolean give) {
        Database db = Database.getInstance(getActivity());
        Sticker sticker_1= db.getStickerForName(stickername);
        //give
        if(give){
            if(sticker_1.getCount()== 1){
                db.updateStatus(sticker_1.getId(), 0);
                db.updateCount(sticker_1.getId(),"decrease");
            }else{
                db.updateCount(sticker_1.getId(),"decrease");
            }

        }
        else { //receive
            if(sticker_1.getCount()== 0){
                db.updateStatus(sticker_1.getId(), 1);
                db.updateCount(sticker_1.getId(),"increase");
            }else{
                db.updateCount(sticker_1.getId(),"increase");
            }

        }

        db.close();
    }

    /**
     * Function to display simple Alert Dialog
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     * */
    public void showAlertDialog( String title, String message, Boolean status) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        // Setting Dialog Message
        builder.setMessage(message);
        // Setting alert dialog icon
        builder.setIcon((status) ? R.drawable.success : R.drawable.fail);

        builder.show();
    }

}
