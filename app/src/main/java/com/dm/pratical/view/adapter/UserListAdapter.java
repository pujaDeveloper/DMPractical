package com.dm.pratical.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.dm.pratical.BR;
import com.dm.pratical.R;
import com.dm.pratical.db.model.User;
import com.dm.pratical.utils.Constants;
import com.dm.pratical.view.ui.UserListActivity;
import com.dm.pratical.databinding.RowUserBinding;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> implements Constants {

    private Context mContext;
    private ArrayList<User> userList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

//        public TextView name;
//        public TextView emil;
//        public TextView phone;
        private final RowUserBinding binding;


        public MyViewHolder(RowUserBinding rowUserBinding) {
            super(rowUserBinding.getRoot());
            this.binding = rowUserBinding;
        }

        public void bind(User user) {
            binding.setVariable(BR.user, user);
        }

    }


    public UserListAdapter(Context context, ArrayList<User> users) {
        this.mContext = context;
        this.userList = users;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowUserBinding itemView = RowUserBinding.inflate(LayoutInflater.from(mContext), parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {


        final User user = userList.get(position);
        holder.bind(user);

//        holder.name.setText(user.getName());
//        holder.emil.setText(user.getEmail());
//        holder.phone.setText("size "+user.getPhotos().size());

        holder.itemView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                ((UserListActivity) mContext).gotToFormActivity(Constants.KEY_STATUS_VIEW, user);
//                userListActivity.addAndEditUsers(true, user, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}

