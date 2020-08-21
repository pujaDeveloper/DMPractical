package com.dm.pratical.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.dm.pratical.R;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils implements Constants {
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
    public static NumberFormat nf = new DecimalFormat("00");



    public static void printLogs(String key, String msg){
        Log.e(key,msg);
    }

    public static boolean isEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidAge(Date dob) {
        try {
            printLogs("Date",sdf.format(dob) + " "+dob.before(sdf.parse(sdf.format(new Date()))));
            return dob.before(sdf.parse(sdf.format(new Date())));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String convertIntoTwoDigit(int s){
        return nf.format(s);
    }


    public static File createTemporaryFile(String part, String ext) throws Exception
    {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                //Log.wtf("TAG", "Oops! Failed create "
                //        + AppConfig.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

}
