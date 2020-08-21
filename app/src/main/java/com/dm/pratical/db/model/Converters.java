package com.dm.pratical.db.model;

import androidx.room.TypeConverter;

import com.dm.pratical.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date toDate(String value) {

       if(value != null && value.length() > 0){
           try {
               return Utils.sdf.parse(value);
           } catch (ParseException e) {
               e.printStackTrace();
           }
       }
        return null;
    }

    @TypeConverter
    public static String fromDate(Date date) {

        if (date != null) return Utils.sdf.format(date);
        else return "";
    }


    @TypeConverter
    public static String fromPhotoList(ArrayList<Photo> photos) {

        JSONArray jsonArray = new JSONArray();
        if (photos != null && photos.size() > 0)
            for (int i = 0; i < photos.size(); i++) {
                Photo photo = photos.get(i);
                JSONObject obj = new JSONObject();
                try {
                    obj.put("id", photo.getId());
                    obj.put("photoUrl", photo.getPhotoUrl());
                    obj.put("order", photo.getOrder());
                } catch (JSONException e) {
                    Utils.printLogs("crashes here", "here");
                    e.printStackTrace();
                }
                jsonArray.put(obj);
            }
        return jsonArray.toString();
    }

    @TypeConverter
    public static ArrayList<Photo> toPhotoList(String input) {
        ArrayList<Photo> photos = new ArrayList<>();

        if(input != null && input.length() > 0){
            try {
                Utils.printLogs("input",input);
                JSONArray jsonArray = new JSONArray(input);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Photo photo = new Photo(obj.getInt("id"), obj.getString("photoUrl"), obj.getInt("order"));
                    photos.add(photo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return photos;
    }


}
