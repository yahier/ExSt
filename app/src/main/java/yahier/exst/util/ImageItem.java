package yahier.exst.util;

import android.graphics.Bitmap;

import com.stbl.stbl.common.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;


public class ImageItem implements Serializable {

    public String imagePath;
    public File file;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = null;
        try {
            bitmap = Bimp.revitionImageSize(imagePath);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            try {
                FileInputStream inputStream = new FileInputStream(imagePath);
                bitmap = ImageUtils.decodeBitmapSafety(MyApplication.getContext().getResources(), 0, inputStream);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return bitmap;
    }
}
