package com.moegoto.wallpaper.util;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;

public class GdiUtils {

    public static LayoutParams layoutParam(int arg0, int arg1) {
        return new LayoutParams(arg0, arg1);
    }

    public static void setPaintARGB(Paint paint, int color) {
        paint.setARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
    }

    public static void setPaintFont(Paint paint, String fontPath) {
        if (new File(fontPath).canRead()) {
            try {
                paint.setTypeface(Typeface.createFromFile(fontPath));
            } catch (Exception e) {
            }
        } else {
            paint.setTypeface(Typeface.DEFAULT);
        }
    }

    public static Bitmap loadResizedImage(String imageFilePath, int width, int height, boolean scaleToOutside) {

        File file = new File(imageFilePath);
        if (!file.canRead() || file.isDirectory()) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        options.inPreferredConfig = Config.RGB_565;
        BitmapFactory.decodeFile(imageFilePath, options);

        int scaleW = options.outWidth / width;
        int scaleH = options.outHeight / height;
        int preScale = (Math.max(scaleW, scaleH)) - 1;

        options.inJustDecodeBounds = false;
        if (1 < preScale) {
            options.inSampleSize = preScale;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath, options);

        // 指定幅・高さの外接のサイズとなるように縮小する
        Paint paint = new Paint();
        if (bitmap != null) {
            Rect bitmapSrcRect = new Rect();
            Rect bitmapDstRect = new Rect();

            // JPGの角度を読み込む
            int bitmapDegree = GdiUtils.getJpegExifDegree(imageFilePath);
            int rotatedBitmapWidth = bitmap.getWidth();
            int rotatedBitmapHeight = bitmap.getHeight();
            if (bitmapDegree == 270 || bitmapDegree == 90) {
                int tmp = rotatedBitmapWidth;
                rotatedBitmapWidth = rotatedBitmapHeight;
                rotatedBitmapHeight = tmp;
            }

            double widthScale = (double) rotatedBitmapWidth / width;
            double heightScale = (double) rotatedBitmapHeight / height;
            double scale;
            if (scaleToOutside) {
                scale = Math.min(widthScale, heightScale);
            } else {
                scale = Math.max(widthScale, heightScale);
            }
            int dstWidth = (int) ((double) bitmap.getWidth() / scale);
            int dstHeight = (int) ((double) bitmap.getHeight() / scale);
            int rotatedSrcWidth = (int) ((double) rotatedBitmapWidth / scale);
            int rotatedSrcHeight = (int) ((double) rotatedBitmapHeight / scale);
            bitmapSrcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            bitmapDstRect.set(0, 0, dstWidth, dstHeight);

            // リスケール
            Bitmap newbitmap = Bitmap.createBitmap(rotatedSrcWidth, rotatedSrcHeight, Config.RGB_565);
            Canvas newCanvas = new Canvas(newbitmap);

            paint.setFilterBitmap(true);

            newCanvas.rotate(bitmapDegree, 0, 0);
            if (bitmapDegree == 90)
                bitmapDstRect.offset(0, -bitmapDstRect.height());
            if (bitmapDegree == 180)
                bitmapDstRect.offset(-bitmapDstRect.width(), -bitmapDstRect.height());
            if (bitmapDegree == 270)
                bitmapDstRect.offset(-bitmapDstRect.width(), 0);
            newCanvas.drawBitmap(bitmap, bitmapSrcRect, bitmapDstRect, paint);
            newCanvas.rotate(-bitmapDegree, 0, 0);

            bitmap = newbitmap;
        }
        return bitmap;
    }

    public static int getJpegExifDegree(String imageFilepath) {
        try {
            if (imageFilepath.endsWith("jpg")) {
                ExifInterface exif = new ExifInterface(imageFilepath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
                if (orientation != -1) {
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90: // 6
                            return 90;
                        case ExifInterface.ORIENTATION_ROTATE_180: // 3
                            return 180;
                        case ExifInterface.ORIENTATION_ROTATE_270: // 8
                            return 270;
                        default: // 1
                            return 0;
                    }
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }

    public static int realDp(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (metrics.scaledDensity * dp);
    }

    public static float dp(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return ((float) Math.min(metrics.heightPixels, metrics.widthPixels) / 320 * dp);
    }

    public static Typeface getFont(String fontPath) {
        if (new File(fontPath).canRead()) {
            return Typeface.createFromFile(fontPath);
        }
        return Typeface.DEFAULT;
    }

    public static int calcTextWidth(String text, Typeface typeface, int fontSize, boolean bold) {
        Paint paint = new Paint();
        paint.setTypeface(typeface);
        paint.setTextSize(fontSize);
        paint.setFakeBoldText(bold);
        return (int) paint.measureText(text);
    }

    public static int calcTextHeight(Typeface typeface, int fontSize) {
        Paint paint = new Paint();
        paint.setTypeface(typeface);
        paint.setTextSize(fontSize);
        return (int) (fontSize - paint.getFontMetrics().top + paint.getFontMetrics().ascent - paint.getFontMetrics().bottom);
    }
}
