package com.example.htpad.spread_thoughts_proj1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class Factory {
    static int transparency = 0xCC;






    public static int exifOrientationToDegrees(int exifOrientation){
        if(exifOrientation== ExifInterface.ORIENTATION_ROTATE_90){
            return 90;
        }else if(exifOrientation==ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        }else if(exifOrientation==ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
    }
    public static Bitmap rotate (Bitmap bitmap, float degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }



    public static String getmonth (String MM) {
        switch (MM) {
            case "01":
                MM = "Jan";
                break;
            case "02":
                MM = "Feb";
                break;
            case "03":
                MM = "Mar";
                break;
            case "04":
                MM = "Apr";
                break;
            case "05":
                MM = "May";
                break;
            case "06":
                MM = "Jun";
                break;
            case "07":
                MM = "Jul";
                break;
            case "08":
                MM = "Aug";
                break;
            case "09":
                MM = "Sep";
                break;
            case "10":
                MM = "Oct";
                break;
            case "11":
                MM = "Nov";
                break;
            case "12":
                MM = "Dec";
                break;
        }
        return MM;
    }

}
