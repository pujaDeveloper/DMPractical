package com.dm.pratical.view.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dm.pratical.R;
import com.dm.pratical.db.model.User;
import com.dm.pratical.db.UserAppDatabase;
import com.dm.pratical.utils.Constants;
import com.dm.pratical.utils.Utils;
import com.dm.pratical.view.adapter.UserListAdapter;
import com.dm.pratical.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class UserListActivity extends AppCompatActivity implements Constants {
    private UserListActivity mActivity;

    //recyclerview
    private RecyclerView rvUsers;
    private UserListAdapter userListAdapter;
    private ArrayList<User> userArrayList = new ArrayList<>();

    //mvvm
    private UserViewModel userViewModel;

    //rxjava
    private CompositeDisposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mActivity = this;
//        setToolBar();
        setRecyclerView();
        setViewModel();
//        getDatabase();
        setFabButton();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userViewModel.clear();
    }


    //methods
    private void setViewModel() {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        userViewModel.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                Utils.printLogs("list",""+users.size());
                userArrayList.clear();
                userArrayList.addAll(users);
                userListAdapter.notifyDataSetChanged();

            }
        });
    }

    private void setRecyclerView() {
        rvUsers = findViewById(R.id.rvUsers);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvUsers.setLayoutManager(mLayoutManager);
        rvUsers.setItemAnimator(new DefaultItemAnimator());

        userListAdapter = new UserListAdapter(this, userArrayList);
        rvUsers.setAdapter(userListAdapter);

    }

    private void setFabButton() {
        findViewById(R.id.fabAddUser)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //go to next screen with status
                        gotToFormActivity(KEY_STATUS_ADD, null);
                    }
                });
    }

    public void gotToFormActivity(int status, User user) {
        Intent i = new Intent(mActivity, AddUserActivity.class);
        i.putExtra(KEY_STATUS, status);
        if (user != null) i.putExtra(KEY_DATA, user);
        startActivity(i);
        finish();
    }

}
