package com.astuetz.viewpager.extensions.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.viewpager.extensions.sample.R;

/**
 * 
 * @author manish.s
 *
 */
public class CustomGridViewAdapter extends ArrayAdapter<Item> {
	Context context;
	int layoutResourceId;
	ArrayList<Item> data = new ArrayList<Item>();

	public CustomGridViewAdapter(FragmentActivity context, int layoutResourceId,
			ArrayList<Item> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RecordHolder holder = null;

		Item item = data.get(position);

			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RecordHolder();
			holder.txtTitle = (TextView) row.findViewById(R.id.item_text);
			holder.imageItem = (ImageView) row.findViewById(R.id.item_image);
			holder.stickItem = (ImageView) row.findViewById(R.id.stick_image);
			row.setTag(holder);

		Log.w("Name",item.getTitle());
		Log.w("Item",Boolean.toString(item.getStick()));

		holder.txtTitle.setText(item.getTitle());
		holder.imageItem.setImageBitmap(item.getImage());
		if(!item.getStick())
			holder.stickItem.setVisibility(View.INVISIBLE);
		else {
			Animation pulse = AnimationUtils.loadAnimation(getContext(), R.anim.pulse);
			pulse.setRepeatCount(Animation.INFINITE);
			holder.stickItem.startAnimation(pulse);
		}
		return row;

	}

	static class RecordHolder {
		TextView txtTitle;
		ImageView imageItem;
		ImageView stickItem;

	}
}