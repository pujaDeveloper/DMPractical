package com.dm.pratical.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.dm.pratical.db.UserDAO;
import com.dm.pratical.db.model.Converters;
import com.dm.pratical.db.model.User;

@Database(entities = {User.class},version = 1)
@TypeConverters({Converters.class})
public abstract class UserAppDatabase extends RoomDatabase {

    public abstract UserDAO getUserDAO();

}
