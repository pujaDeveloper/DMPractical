package com.dm.pratical.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.dm.pratical.db.model.Converters;
import com.dm.pratical.db.model.User;

import java.text.ParseException;

public class AppPreferences {


    private static final String PREF_NAME = "Practical";
    private static final String KEY_NAME = "Name";
    private static final String KEY_GENDER = "Gender";
    private static final String KEY_PHONE = "Phone";
    private static final String KEY_DOB = "DOB";
    private static final String KEY_EMAIL = "Email";
    private static final String KEY_PHOTO = "photo";
//    private static final String KEY_IS_KIOSK_ACTIVE = "IS_KIOSK_ACTIVE";

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEdit;

    public AppPreferences(Context context) {
        try {
            mPrefs = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
            mEdit = mPrefs.edit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getName() {
        return mPrefs.getString(KEY_NAME, "");
    }

    public void setName(String value) {
        mEdit.putString(KEY_NAME, value);
        mEdit.commit();
    }

    public String getGender() {
        return mPrefs.getString(KEY_GENDER, "");
    }

    public void setGender(String value) {
        mEdit.putString(KEY_GENDER, value);
        mEdit.commit();
    }

    public String getDOB() {
        return mPrefs.getString(KEY_DOB, "");
    }

    public void setDOB(String value) {
        mEdit.putString(KEY_DOB, value);
        mEdit.commit();
    }

    public String getPhotos() {
        return mPrefs.getString(KEY_PHOTO, "");
    }

    public void sePhotos(String value) {
        mEdit.putString(KEY_PHOTO, value);
        mEdit.commit();
    }

    public String getPhone() {
        return mPrefs.getString(KEY_PHONE, "");
    }

    public void setPhone(String value) {
        mEdit.putString(KEY_PHONE, value);
        mEdit.commit();
    }


    public String getEmail() {
        return mPrefs.getString(KEY_EMAIL, "");
    }

    public void setEmail(String value) {
        mEdit.putString(KEY_EMAIL, value);
        mEdit.commit();
    }


    public User getPendingUser() {
        User user = new User();
        user.setId(0);
        user.setName(getName());
        user.setGender(getGender());
        user.setDob(Converters.toDate(getDOB()));
        user.setPhone(getPhone());
        user.setEmail(getEmail());
        user.setPhotos(Converters.toPhotoList(getPhotos()));
        return user;
    }

    public User setPendingUser(User user) {
        setName(user.getName());
        setGender(user.getGender());
        setDOB(Converters.fromDate(user.getDob()));
        setPhone(user.getPhone());
        setEmail(user.getEmail());
        sePhotos(Converters.fromPhotoList(user.getPhotos()));
        return user;
    }

    public void deletePendingUser(){
        setName("");
        setGender("Male");
        setDOB("");
        setPhone("");
        setEmail("");
        sePhotos("[]");
    }


}
