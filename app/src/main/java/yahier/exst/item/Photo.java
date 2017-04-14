package yahier.exst.item;

import java.io.Serializable;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public class Photo implements Parcelable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5506321279836175217L;

	public Photo() {
		// TODO Auto-generated constructor stub
	}
	public int getPhotoid() {
		return photoid;
	}

	public void setPhotoid(int photoid) {
		this.photoid = photoid;
	}

	public String getThumburl() {
		return thumburl;
	}

	public void setThumburl(String thumburl) {
		this.thumburl = thumburl;
	}

	public String getMiddleurl() {
		return middleurl;
	}

	public void setMiddleurl(String middleurl) {
		this.middleurl = middleurl;
	}

	public String getOriginalurl() {
		return originalurl;
	}

	public void setOriginalurl(String originalurl) {
		this.originalurl = originalurl;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	int photoid;
	String thumburl;
	String middleurl;
	String originalurl;
	long createtime;
	Bitmap bitmap;

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeInt(photoid);
		out.writeString(thumburl);
		out.writeString(middleurl);
		out.writeString(originalurl);
		out.writeLong(createtime);
	}

	public Photo(Parcel in) {
		photoid = in.readInt();
		thumburl = in.readString();
		middleurl = in.readString();
		originalurl = in.readString();
		createtime = in.readLong();

	}

	public static final Creator<Photo> CREATOR = new Creator<Photo>() {
		@Override
		public Photo[] newArray(int size) {
			return new Photo[size];
		}

		@Override
		public Photo createFromParcel(Parcel in) {
			return new Photo(in);
		}
	};
}
