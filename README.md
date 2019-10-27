# myphotodemo
Repository for a photo demo

## Approaches
### How to take a photo
We start the camera by sending a system intent 'android.media.action.IMAGE_CAPTURE'.
```Java
    // Start camera
    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
    startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
```

### How to save photos and share the photos directory
Since Android 7.0, the Uris that take the local dir directly will be thought unsafe, we have to wrap the Uris with a 
content file provider, instead of using Uri.fromFile() that working on earlier APIs.
```Java
    // Generate image uri considering SDK compatibility issues
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        imageUri = FileProvider.getUriForFile(MainActivity.this,
                "com.example.myphoto.fileprovider", outputImage);
    } else {
        imageUri = Uri.fromFile(outputImage);
    }
```
We need to add a declaration for the FileProvider in the AndroidManifest.xml.
```Xml
    <provider
        android:name="android.support.v4.content.FileProvider"
        android:authorities="com.example.myphoto.fileprovider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths" />
    </provider>
```        
To simplify the operations, we take a public SD cache directory to save photos, which can be obtained by getExternalCacheDir(). In this case we have no need to request runtime SD access permissions.

### Make user able to specify an image name for the photo he/she has just taken
We override the method onActivityResult() of the activity, in which we prompt an input dialog to give the user the chance 
to specify a name for the photo he/she has just taken, while going back from camera screen.
```Java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                showInputDialog();
            }
        }
    }
```
If the user has input a name and pressed OK button, the name will be connected with the photo and be saved in device file system.

### Persistent storage of the album information
First we define the structure of a photo record which includes at least 4 fields, original image path name, thumbnail path 
name, creation time and custom image name, if any.<br>
In the method of onActivityResult() we add a new record of photo and save it in the album object. The album object will 
then be serialized and saved as a Shared Preferences item. 
```Java
    private void addToPhotoAlbum(String originalPhotoPath, String thumbnailPath, String dateTime,
                                 String customImageName) {
        // Read the album object from Shared Preferences
        List<PhotoRecord> listPhoto = SPPhotoAlbum.read(MainActivity.this);
        // Add a new record to the album, and save it back to Shared Preferences
        PhotoRecord photoRecord = new PhotoRecord(originalPhotoPath, thumbnailPath, dateTime, customImageName);
        listPhoto.add(photoRecord);
        SPPhotoAlbum.save(MainActivity.this, listPhoto);
    }
```
When we say if the album is not so big, this persistent storage solution is sufficient to meet the requirements and easy to implement.

### View photo list
We can read and deserialize the album object from the Shared Preferences and create a list of photo record object in the 
memory, which can serve as the view model for a recycler view in the screen.<br>
Creation time and custom image name in a photo record can be shown directly in the corresponding item in the recycler view.<br>
Thumbnails will be decoded first to downsized levels to relieve the memory pressure before they can be shown in the items. 
```Java
    private Bitmap optimizeImageSize(String imagePath, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565; // Use low level config
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options,reqWidth, reqHeight);
        return BitmapFactory.decodeFile(imagePath, options);
    }
```
To avoid the OOM situation, we have also saved thumbnails for the photos separately when we get back from the camera 
screen. 
```Java
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
```
### View the original image of a single photo
The original image path name in the photo record will be sent as intent-extra data to the PhotoActivity when we click each 
item in the photo list screen. The image will be decoded in its original quality and shown in this activity.

## Components of the app
### MainActivity
MainActivity manages the main page with 2 buttons. Clicking on 'Take a photo' button will launch camera to capture a photo. 
Clicking on 'View photos' button will open photo list page.

### PhotoListActivity
PhotoListActivity will first load photo album data from Shared Preferences, and then generate a list object and render it 
on screen by means of a recycler view with a PhotoRecyclerViewAdapter.

### PhotoActivity
When we click on a certain item in photo list page, the PhotoActivity will be started with an extra parameter 
intent_extra_key_photo_path, which represents the real path of the original photo. PhotoActivity just show this photo via an 
ImageView in the page. Clicking on system back button we will go back to PhotoListActivity.

### Persistence 
PhotoRecord + SPPhotoAlbum<br>
SPPhotoAlbum provides static methods to save List<PhotoRecord> object to Shared Preferences and read it out.
  
## Things to be improved
### Local database
A local database may need to be implemented as the persistent storage solution of the album, considering the large amount 
of photos.
### Image cache
Image cache solution need to be introduced to improve the user experience while browsing the photo list.
### Photo management
Some photo management features should be implemented for the album such as photo scanning, deleting and renaming.


