package com.dm.pratical.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.dm.pratical.utils.Utils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.annotations.Nullable;

@Entity(tableName = "users")
public class User implements Serializable {

    @ColumnInfo(name = "id")
//    @PrimaryKey(autoGenerate =true)
    private long id;

    @Nullable
    @ColumnInfo(name = "name")
    private String name;

    @Nullable
    @TypeConverters({Converters.class})
    @ColumnInfo(name = "dob")
    private Date dob;

    @ColumnInfo(name = "gender")
    private String gender;

    @Nullable
    @ColumnInfo(name = "phone")
    private String phone;

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "email")
    private String email;

    @Nullable
    @ColumnInfo(name = "isPending")
    private boolean isPending;

    @Nullable
    @TypeConverters({Converters.class})
    @ColumnInfo(name = "photos")
    private ArrayList<Photo> photos = new ArrayList<>();

//photo pending
    //date converter

    @Ignore
    public User() {
    }


    public User(long id, String name, Date dob, String gender, String phone, String email, ArrayList<Photo> photos, boolean isPending) {

        this.id = id;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.photos = photos;
        this.isPending = isPending;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }


    public ArrayList<Photo> getPhotos() {
        Utils.printLogs("origin", "test " + photos.size());
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

}


