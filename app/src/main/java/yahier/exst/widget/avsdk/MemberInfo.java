package yahier.exst.widget.avsdk;

import android.graphics.Bitmap;

import java.io.Serializable;

public class MemberInfo implements Serializable {
	public String identifier = "";
	public boolean hasAudio = false;
	public boolean hasCameraVideo = false;
	public boolean hasScreenVideo = false;
	public boolean isShareMovie = false;
	public boolean hasGetInfo = false;
	public String name = null;
	public Bitmap faceBitmap = null;

	@Override
	public String toString() {
		return "MemberInfo identifier = " + identifier + ", hasAudio = " + hasAudio
				+ ", hasCameraVideo = " + hasCameraVideo + ", hasScreenVideo = " + hasScreenVideo
				+ ", isShareMovie = " + isShareMovie + ", hasGetInfo = "
				+ hasGetInfo + ", name = " + name;
	}

}
