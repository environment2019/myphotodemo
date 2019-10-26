package com.example.myphoto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class PhotoActivity extends AppCompatActivity {

    private static String INTENT_EXTRA_KEY_PHOTO_PATH = "intent_extra_key_photo_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        initView();
    }

    public static void invokeMe(Context context, String photoPath) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(INTENT_EXTRA_KEY_PHOTO_PATH, photoPath);
        context.startActivity(intent);
    }

    private void initView() {
        ImageView ivPhoto = findViewById(R.id.iv_photo);
        Intent intent = getIntent();
        if (intent != null) {
            String photoPath = intent.getStringExtra(INTENT_EXTRA_KEY_PHOTO_PATH);
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            ivPhoto.setImageBitmap(bitmap);
        }
    }
}
