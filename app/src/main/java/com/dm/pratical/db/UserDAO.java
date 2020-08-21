package com.dm.pratical.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.dm.pratical.db.model.User;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public  long addUser(User user);

    @Update
    public void updateUser(User user);

    @Delete
    public void deleteUser(User user);

//    @Query("select * from contacts")
//    public List<User> getContacts();

    @Query("select * from users where isPending ==0")
    Flowable<List<User>> getUsers();

//    @Query("select * from users where id ==:id")
//    public User getUser(long id);

    @Query("select * from users where phone ==:phone")
    public User getUserByPhone(String phone);

    @Query("select * from users where isPending ==1")
    public Flowable<List<User>> getPendingUser();

    @Query("delete from users where isPending ==1")
    public void deletePendingUsers();


}
