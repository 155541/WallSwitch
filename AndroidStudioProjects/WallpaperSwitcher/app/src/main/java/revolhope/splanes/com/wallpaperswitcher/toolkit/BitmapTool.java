package revolhope.splanes.com.wallpaperswitcher.toolkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class BitmapTool {

    private Context context;

    public BitmapTool(Context context){
        this.context = context;
    }

    public ArrayList<Bitmap> checkRotation(HashMap<Uri,ExifInterface> datasets){

        ArrayList<Bitmap> resizedDataset = new ArrayList<>();
        for(Uri uri : datasets.keySet()) {

            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                if (datasets.get(uri) != null) {
                    int orientation = datasets.get(uri).getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            bmp = rotateImage(bmp, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            bmp = rotateImage(bmp, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            bmp = rotateImage(bmp, 270);
                            break;
                    }
                }
                resizedDataset.add(bmp);

            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return resizedDataset;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

//    private float convertDpToPixel(float dp){
//        Resources resources = context.getResources();
//        DisplayMetrics metrics = resources.getDisplayMetrics();
//        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
//    }
}
