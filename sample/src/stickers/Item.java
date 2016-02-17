package stickers;

import android.graphics.Bitmap;


public class Item {
	Bitmap image;
	String title;
	boolean stick;
	
	public Item(Bitmap image, String title, Boolean stick) {
		super();
		this.image = image;
		this.title = title;
		this.stick = stick;
	}
	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean getStick() {
		return stick;
	}

	public void setStick(boolean stick) {
		this.stick = stick;
	}
	

}
