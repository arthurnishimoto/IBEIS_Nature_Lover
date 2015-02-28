package edu.uic.ibeis_tourist.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {

    private static final String pathToFile = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "IBEIS" + File.separator;

    /**
     * Generate a unique file path for a new image file to be stored in the external memory
     * @return Generated file
     */
    public static File generateImageFile(String fileName) {
        File ibeisDir = new File(pathToFile);
        if(!ibeisDir.exists()) {
            ibeisDir.mkdir();
        }
        return new File(pathToFile + fileName);
    }

    /**
     * Generate a unique name for an image file
     * @return Name of the file
     */
    public static String generateImageName() {
        return "IBEIS_" + new SimpleDateFormat("yyyyMMdd'_'hhmmss").format(new Date()) + ".png";
    }

    /**
     * Returns the bitmap of an image file contained in the app folder in the external memory
     * @param fileName
     * @return image Bitmap
     * @throws IOException
     */
    public static Bitmap getImageBitmap(String fileName, int requestedHeight, int requestedWidth,
                                        Bitmap.Config colorConfig) throws IOException {

        String filePath = pathToFile + fileName;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        final int height = options.outHeight;
        final int width = options.outWidth;

        options.inPreferredConfig = colorConfig;
        int inSampleSize = 1;

        if (height > requestedHeight)
        {
            inSampleSize = Math.round((float)height / (float)requestedHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > requestedWidth)
        {
            inSampleSize = Math.round((float)width / (float)requestedWidth);
        }

        options.inSampleSize = inSampleSize;

        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        ExifInterface exif = new ExifInterface(filePath);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
        }
        else if (orientation == 3) {
            matrix.postRotate(180);
        }
        else if (orientation == 8) {
            matrix.postRotate(270);
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
