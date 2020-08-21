package com.dm.pratical.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dm.pratical.R;
import com.dm.pratical.db.model.Photo;
import com.dm.pratical.utils.Constants;
import com.dm.pratical.utils.Utils;
import com.dm.pratical.utils.dragHelper.ItemTouchHelperAdapter;
import com.dm.pratical.utils.dragHelper.ItemTouchHelperViewHolder;
import com.dm.pratical.utils.dragHelper.OnStartDragListener;
import com.dm.pratical.view.ui.UserListActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.ItemViewHolder> implements ItemTouchHelperAdapter, Constants {

    private Context mContext;
    private ArrayList<Photo> photoList;
    private final OnStartDragListener mDragStartListener;


    public PhotoListAdapter(Context context, ArrayList<Photo> photoList, OnStartDragListener dragStartListener) {
        mContext = context;
        mDragStartListener = dragStartListener;
        this.photoList = photoList;

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_photo, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {

        Photo photo = photoList.get(position);
        Picasso.get()
                .load("file://" + photo.getPhotoUrl())
                .resize(80, 100)
                .onlyScaleDown()
                .into(holder.ivPhoto);

        holder.ivPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }


    @Override
    public void onItemDismiss(int position) {
        photoList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Log.e("move", "move");
        Collections.swap(photoList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        //        public final TextView textView;
        public final ImageView ivPhoto;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }


}

