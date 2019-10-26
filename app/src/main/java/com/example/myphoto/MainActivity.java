package com.example.myphoto;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.myphoto.persistence.PhotoRecord;
import com.example.myphoto.persistence.SPPhotoAlbum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_TAKE_PHOTO = 0x1001;

    /**
     * Uri for each photo image we take, used as a parameter to launch camera
     */
    private Uri imageUri;

    /**
     * Full path name for each photo to be saved on SD
     */
    private String imagePathName = "";

    /**
     * User specified name for the image
     */
    private String customImageName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                showInputDialog();
            }
        }
    }

    private void showInputDialog() {
        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.custom_image_name)).setView(inputServer);
        builder.setNegativeButton(
                getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customImageName = "";
                        syncPhotoInfo();
                    }
                });
        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String inputName = inputServer.getText().toString();
                        customImageName = inputName;
                        syncPhotoInfo();
                    }
                });
        builder.show();
    }

    private void syncPhotoInfo() {
        String timeStamp = getTimeStamp();
        String thumbnailPathName = getThumbnailPath(timeStamp);
        generateThumbnailImage(thumbnailPathName);
        addToPhotoAlbum(imagePathName, thumbnailPathName, timeStamp, customImageName);
    }

    private String getTimeStamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    private String getOriginalImageFileName(String timeStamp) {
        return "Image_" + timeStamp + ".jpg";
    }

    private String getThumbnailPath(String timeStamp) {
        String fileName = "Thumbnail_" + timeStamp + ".jpg";
        return getExternalCacheDir() + "/" + fileName;
    }

    private void generateThumbnailImage(String thumbnailPath) {
        FileOutputStream ops = null;
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            ops = new FileOutputStream(thumbnailPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 2, ops);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                ops.flush();
                ops.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addToPhotoAlbum(String originalPhotoPath, String thumbnailPath, String dateTime, String CustomImageName) {
        // Read the album object from Shared Preferences
        List<PhotoRecord> listPhoto = SPPhotoAlbum.read(MainActivity.this);
        // Add a new record to the album, and save it back to Shared Preferences
        PhotoRecord photoRecord = new PhotoRecord(originalPhotoPath, thumbnailPath, dateTime, CustomImageName);
        listPhoto.add(photoRecord);
        SPPhotoAlbum.save(MainActivity.this, listPhoto);
    }

    private void takePhoto() {
        // Create file to save the photo
        String imageFileName = getOriginalImageFileName(getTimeStamp());
        imagePathName = getExternalCacheDir() + "/" + imageFileName;
        File outputImage = new File(getExternalCacheDir(), imageFileName);
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Generate image uri considering SDK compatibility issues
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(MainActivity.this,
                    "com.example.myphoto.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }

        // Start camera
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
    }

    private void viewPhotos() {
        PhotoListActivity.invokeMe(MainActivity.this);
    }

    public void onClickTakePhoto(View view) {
        takePhoto();
    }

    public void onClickViewPhoto(View view) {
        viewPhotos();
    }
}
