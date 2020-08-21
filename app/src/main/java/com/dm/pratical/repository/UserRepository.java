package com.dm.pratical.repository;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.dm.pratical.R;
import com.dm.pratical.db.model.User;
import com.dm.pratical.db.UserAppDatabase;
import com.dm.pratical.utils.Utils;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.internal.Util;

public class UserRepository {

    private Application application;
    private UserAppDatabase usersAppDatabase;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
    private MutableLiveData<List<User>> pendingUserLiveData = new MutableLiveData<>();
    private long rowIdOfTheItemInserted;

    public UserRepository(Application application) {

        this.application = application;
        usersAppDatabase = Room.databaseBuilder(application.getApplicationContext(), UserAppDatabase.class, "UserDB").build();


        disposable.add(usersAppDatabase.getUserDAO().getPendingUser()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<User>>() {
                               @Override
                               public void accept(List<User> users) throws Exception {
                                   pendingUserLiveData.postValue(users);
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Utils.printLogs("UserRepository",throwable.getMessage());
                               }
                           }
                )
        );

        disposable.add(usersAppDatabase.getUserDAO().getUsers()
                  .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<User>>() {
                                       @Override
                                       public void accept(List<User> users) throws Exception {

                                           usersLiveData.postValue(users);

                                       }
                                   }, new Consumer<Throwable>() {
                                       @Override
                                       public void accept(Throwable throwable) throws Exception {


                                       }
                                   }
                        )
        );
    }

    public void createUser(final User user) {
        disposable.add(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {

                rowIdOfTheItemInserted = usersAppDatabase.getUserDAO().addUser(user);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(application.getApplicationContext(), " user added successfully " + rowIdOfTheItemInserted, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(application.getApplicationContext(), " error occurred " + e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }));


    }

    public void updateUser(final User user) {
        disposable.add(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {

                usersAppDatabase.getUserDAO().updateUser(user);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
//                        Toast.makeText(application.getApplicationContext(), " user updated successfully ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(application.getApplicationContext(), " error occurred ", Toast.LENGTH_LONG).show();

                    }
                }));


    }


    public void deleteUser(final User user) {


        disposable.add(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {

                usersAppDatabase.getUserDAO().deleteUser(user);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
//                        Toast.makeText(application.getApplicationContext(), " user deleted successfully ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(application.getApplicationContext(), " error occurred ", Toast.LENGTH_LONG).show();

                    }
                }));
    }


    public MutableLiveData<List<User>> getUsersLiveData() {
        return usersLiveData;
    }

    public MutableLiveData<List<User>> getPendingUser() {
        return pendingUserLiveData;
    }

    public void clear() {
        disposable.clear();
    }


}
