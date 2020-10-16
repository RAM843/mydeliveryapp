package com.example.mydelivery.Utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class CameraPhotoManager {

    public static String getRealPathFromURI(Context c,Uri uri) {
        String result;
        Cursor cursor = c.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
            cursor.close();
            return result;
        }
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        result = cursor.getString(idx);
        cursor.close();
        return result;
    }
    public static String saveToInternalStorage(Context c,Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(c);

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        Random r = new Random();
        String filename = "profile"+r.nextInt(50)+".jpg";

        File mypath=new File(directory,filename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);

            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String path = directory.getAbsolutePath() + "/"+filename;
        return path;

    }
}
