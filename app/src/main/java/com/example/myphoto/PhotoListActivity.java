package com.example.myphoto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.myphoto.adapter.PhotoRecyclerViewAdapter;
import com.example.myphoto.persistence.PhotoRecord;
import com.example.myphoto.persistence.SPPhotoAlbum;

import java.util.ArrayList;
import java.util.List;

public class PhotoListActivity extends AppCompatActivity {

    private RecyclerView rvPhoto;
    private PhotoRecyclerViewAdapter mAdapter;
    private List<PhotoRecord> mListPhoto = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        getPhotoListFromAlbum();
        initView();
    }

    public static void invokeMe(Context context) {
        Intent intent = new Intent(context, PhotoListActivity.class);
        context.startActivity(intent);
    }

    private void getPhotoListFromAlbum() {
        mListPhoto = SPPhotoAlbum.read(PhotoListActivity.this);
    }

    private void initView() {
        rvPhoto = findViewById(R.id.rv_photo);
        mAdapter = new PhotoRecyclerViewAdapter(PhotoListActivity.this, mListPhoto);
        mAdapter.setItemClickListener(new PhotoRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                PhotoActivity.invokeMe(PhotoListActivity.this, mListPhoto.get(pos).getOriginalImagePathName());
            }
        });
        rvPhoto.setAdapter(mAdapter);
        rvPhoto.setLayoutManager(new LinearLayoutManager(PhotoListActivity.this));
    }
}
