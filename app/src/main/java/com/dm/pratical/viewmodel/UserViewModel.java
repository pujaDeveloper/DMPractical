package com.dm.pratical.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dm.pratical.db.model.User;
import com.dm.pratical.repository.UserRepository;

import java.util.Date;
import java.util.List;


public class UserViewModel extends AndroidViewModel {

    private UserRepository userRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);

        userRepository = new UserRepository(application);
    }

    public LiveData<List<User>> getAllUsers() {
        return userRepository.getUsersLiveData();
    }

    public LiveData< List<User>> getPendingUser() {
        return userRepository.getPendingUser();
    }

    public void createUser(User user) {
        userRepository.createUser(user);
    }

    public void updateUser(User user) {
        userRepository.updateUser(user);
    }

    public void deleteUser(User user) {

        userRepository.deleteUser(user);
    }

    public void clear() {
        userRepository.clear();
    }

}
