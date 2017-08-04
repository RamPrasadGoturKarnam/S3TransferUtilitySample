package com.amazonaws.demo.s3transferutility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.content.pm.ActivityInfo;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.squareup.picasso.Picasso;

import static android.R.attr.data;
import static android.content.ContentValues.TAG;
import static com.amazonaws.demo.s3transferutility.UploadActivity.isDownloadsDocument;
import static com.amazonaws.demo.s3transferutility.UploadActivity.isExternalStorageDocument;
import static com.amazonaws.demo.s3transferutility.UploadActivity.isMediaDocument;


public class PhotoIntentActivity extends ListActivity {

	private static final int ACTION_TAKE_PHOTO_B = 1;
	private static final int ACTION_TAKE_PHOTO_S = 2;
	private static final int ACTION_TAKE_VIDEO = 3;

	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private ImageView mImageView;
	private Bitmap mImageBitmap;

	private static final String VIDEO_STORAGE_KEY = "viewvideo";
	private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
	private VideoView mVideoView;
	private Uri mVideoUri;
	// A List of all transfers
	private List<TransferObserver> observers;
	private ArrayList<HashMap<String, Object>> transferRecordMaps;



	private Uri picUri;

	private String mCurrentPhotoPath;

	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	// The TransferUtility is the primary class for managing transfer to S3
	private TransferUtility transferUtility;

	// The SimpleAdapter adapts the data about transfers to rows in the UI
	private SimpleAdapter simpleAdapter;

	// Which row in the UI is currently checked (if any)
	private int checkedIndex;



	private String bitmappath;

	public String getBitmappath() {
		return bitmappath;
	}

	public void setBitmappath(String bitmappath) {
		this.bitmappath = bitmappath;
	}

	/* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}

	
	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {
		
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
        Picasso.with(getApplicationContext()).load(mCurrentPhotoPath).into(mImageView);
		
		return f;
	}

	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		//int photoW = bmOptions.outWidth;
		//int photoH = bmOptions.outHeight;

        int photoW = 2204;
        int photoH = 2668;
//        System.out.println("targetW "+targetW+" targetH "+targetH);
//        System.out.println("photoW/targetW "+photoW/targetW);
//        System.out.println("photoH/targetH "+photoH/targetH);
//		/* Figure out which way needs to be reduced less */
//		int scaleFactor = 1;
//		if ((targetW > 0) || (targetH > 0)) {
//            System.out.println("targetW "+targetW+" targetH "+targetH);
//            System.out.println("photoW/targetW "+photoW/targetW);
//            System.out.println("photoH/targetH "+photoH/targetH);
//			scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//            System.out.println("scaleFactor "+scaleFactor);
//		}
        int scaleFactor = 0;
		/* Set bitmap options to scale the image decode target */
//		bmOptions.inJustDecodeBounds = false;
//		bmOptions.inSampleSize = scaleFactor;
//		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmappic = BitmapFactory.decodeFile(mCurrentPhotoPath);//, bmOptions);

        Bitmap bitmap = getResizedBitmap(bitmappic, 200, 200);

        //setimage.setImageBitmap(btm00);
		
		/* Associate the Bitmap to the ImageView */
		System.out.println("resized image "+bitmap);
		mImageView.setImageBitmap(bitmap);
		mVideoUri = null;
		mImageView.setVisibility(View.VISIBLE);
		mVideoView.setVisibility(View.INVISIBLE);
	}

	private void galleryAddPic() {
		    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
			File f = new File(mCurrentPhotoPath);
		Log.i("gallerypic-->",f.getName());
		    Uri contentUri = Uri.fromFile(f);

			this.setPicUri(contentUri);
		try {
			String path = getPath(contentUri);
			beginUpload(path);
		} catch (URISyntaxException e) {
			Toast.makeText(this,
					"Unable to get the file from the given URI.  See error log for details",
					Toast.LENGTH_LONG).show();
			Log.e(TAG, "Unable to upload file from the given uri", e);
		}
		    mediaScanIntent.setData(contentUri);
		    this.sendBroadcast(mediaScanIntent);
	}

	private void galleryAddPic(String filepath) {
		Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		mCurrentPhotoPath = filepath;
		File f = new File(mCurrentPhotoPath);
		Log.i("gallerypic small -->",f.getName());
		Uri contentUri = Uri.fromFile(f);

		this.setPicUri(contentUri);
		try {
			String path = getPath(contentUri);
			beginUpload(path);
		} catch (URISyntaxException e) {
			Toast.makeText(this,
					"Unable to get the file from the given URI.  See error log for details",
					Toast.LENGTH_LONG).show();
			Log.e(TAG, "Unable to upload file from the given uri", e);
		}
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	private void dispatchTakePictureIntent(int actionCode) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch(actionCode) {
		case ACTION_TAKE_PHOTO_B:
			File f = null;
			
			try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			break;

		default:
			break;			
		} // switch

		startActivityForResult(takePictureIntent, actionCode);


	}

	private void dispatchTakeVideoIntent() {
		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
	}

	private void handleSmallCameraPhoto(Intent intent) {
		Bundle extras = intent.getExtras();

		mImageBitmap = (Bitmap) extras.get("data");
		mImageView.setImageBitmap(mImageBitmap);
		mVideoUri = null;
		mImageView.setVisibility(View.INVISIBLE);
		mVideoView.setVisibility(View.INVISIBLE);

		// CALL THIS METHOD TO GET THE URI FROM THE BITMAP
		Uri tempUri = getImageUri(getApplicationContext(), mImageBitmap);

		// CALL THIS METHOD TO GET THE ACTUAL PATH
		File finalFile = new File(getRealPathFromURI(tempUri));

		System.out.println("smallImage Uri "+finalFile.getPath());
		galleryAddPic(finalFile.getPath());
	}

	private void handleBigCameraPhoto() {

		if (mCurrentPhotoPath != null) {
			setPic();
			galleryAddPic();


			mCurrentPhotoPath = null;
		}

	}

	private void handleCameraVideo(Intent intent) {
		mVideoUri = intent.getData();
		mVideoView.setVideoURI(mVideoUri);
		mImageBitmap = null;
		mVideoView.setVisibility(View.VISIBLE);
		mImageView.setVisibility(View.INVISIBLE);
	}

	Button.OnClickListener mTakePicOnClickListener = 
		new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
		}
	};

	Button.OnClickListener mTakePicSOnClickListener = 
		new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			//dispatchTakePictureIntent(ACTION_TAKE_PHOTO_S);
		}
	};



	/*Button.OnClickListener mTakeVidOnClickListener =
		new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			dispatchTakeVideoIntent();
		}
	};*/

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


		mImageView = (ImageView) findViewById(R.id.imageView1);
		mVideoView = (VideoView) findViewById(R.id.videoView1);
		mImageBitmap = null;
		mVideoUri = null;

		Button picBtn = (Button) findViewById(R.id.btnIntend);
		setBtnListenerOrDisable( 
				picBtn, 
				mTakePicOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE
		);

		//Hiding the BigPicture Button
		picBtn.setVisibility(View.INVISIBLE);

		/*Button picSBtn = (Button) findViewById(R.id.btnIntendS);
		setBtnListenerOrDisable( 
				picSBtn, 
				mTakePicSOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE
		);*/

		dispatchTakePictureIntent(ACTION_TAKE_PHOTO_S);

		/*Button vidBtn = (Button) findViewById(R.id.btnIntendV);
		setBtnListenerOrDisable( 
				vidBtn, 
				mTakeVidOnClickListener,
				MediaStore.ACTION_VIDEO_CAPTURE
		);*/
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}

		//Screen orientation code
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


       //Get current screen orientation
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getOrientation();
         switch(orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
                break;
        }
		//Screen orientation code

		// Initializes TransferUtility, always do this before using it.
		transferUtility = Util.getTransferUtility(this);
		transferRecordMaps = new ArrayList<HashMap<String, Object>>();
		initUI();
	}


	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}

	public String getRealPathFromURI(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		return cursor.getString(idx);
	}

	// Check screen orientation or screen rotate event here
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
        System.out.println("orientation-->"+newConfig.orientation);
		// Checks the orientation of the screen for landscape and portrait and set portrait mode always
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case ACTION_TAKE_PHOTO_B: {
			if (resultCode == RESULT_OK) {
				handleBigCameraPhoto();
			}
			break;
		} // ACTION_TAKE_PHOTO_B

		case ACTION_TAKE_PHOTO_S: {
			if (resultCode == RESULT_OK) {
				handleSmallCameraPhoto(data);
			}
			break;
		} // ACTION_TAKE_PHOTO_S

		case ACTION_TAKE_VIDEO: {
			if (resultCode == RESULT_OK) {
				handleCameraVideo(data);
			}
			break;
		} // ACTION_TAKE_VIDEO
		} // switch
	}

	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
		outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri != null) );
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
		mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
		mImageView.setImageBitmap(mImageBitmap);
		mImageView.setVisibility(
				savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? 
						ImageView.VISIBLE : ImageView.INVISIBLE
		);
		mVideoView.setVideoURI(mVideoUri);
		mVideoView.setVisibility(
				savedInstanceState.getBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY) ? 
						ImageView.VISIBLE : ImageView.INVISIBLE
		);
	}

	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
			packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	private void setBtnListenerOrDisable( 
			Button btn, 
			Button.OnClickListener onClickListener,
			String intentName
	) {
		if (isIntentAvailable(this, intentName)) {
			btn.setOnClickListener(onClickListener);        	
		} else {
			btn.setText( 
				getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setClickable(false);
		}
	}

	//Newly Added Methods

    /*
    * Gets the file path of the given Uri.
    */
    @SuppressLint("NewApi")
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[] {
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

	/*
    * Begins to upload the file specified by the file path.
    */
	private void beginUpload(String filePath) {
		if (filePath == null) {
			Toast.makeText(this, "Could not find the filepath of the selected file",
					Toast.LENGTH_LONG).show();
			return;
		}

		File file = new File(filePath);
		Log.d("FileName..",file.getName());
		this.setImageName(file.getName());
		TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, file.getName(),
				file);
		Log.d("After..completed ",file.getName());


	}
    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm,(int)(bm.getWidth()*0.8), (int)(bm.getHeight()*0.8), true);
        return resizedBitmap;
    }

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	private  String imageName;

	public Uri getPicUri() {
		return picUri;
	}

	public void setPicUri(Uri picUri) {
		this.picUri = picUri;
	}


	/*
    * A TransferListener class that can listen to a upload task and be notified
    * when the status changes.
    */
	private class UploadListener implements TransferListener {

		// Simply updates the UI list when notified.
		@Override
		public void onError(int id, Exception e) {
			Log.e(TAG, "Error during upload: " + id, e);
			//updateList();
		}

		@Override
		public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
			Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
					id, bytesTotal, bytesCurrent));
			//updateList();
		}

		@Override
		public void onStateChanged(int id, TransferState newState) {
			Log.d(TAG, "onStateChangedPhoto: " + id + ", " + newState);
			if(newState==TransferState.COMPLETED) {
				invokeCurlActivity();
			}
			//updateList();

		}
	}

	private void invokeCurlActivity(){
		Bundle bundle = new Bundle();
		bundle.putString("imagename",this.getImageName());

		ImageParseFragment testFragment = new ImageParseFragment();
		testFragment.setArguments(bundle);
		getFragmentManager().beginTransaction().add(android.R.id.content, testFragment).commit();
		setContentView(R.layout.activity_upload);
	}


	@Override
	protected void onResume() {
		super.onResume();
		// Get the data from any transfer's that have already happened,
		initData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Clear transfer listeners to prevent memory leak, or
		// else this activity won't be garbage collected.
		if (observers != null && !observers.isEmpty()) {
			for (TransferObserver observer : observers) {
				observer.cleanTransferListener();
			}
		}
	}

	/**
	 * Gets all relevant transfers from the Transfer Service for populating the
	 * UI
	 */
	private void initData() {
		transferRecordMaps.clear();
		// Use TransferUtility to get all upload transfers.
		observers = transferUtility.getTransfersWithType(TransferType.UPLOAD);
		TransferListener listener = new UploadListener();
		for (TransferObserver observer : observers) {

			// For each transfer we will will create an entry in
			// transferRecordMaps which will display
			// as a single row in the UI
			HashMap<String, Object> map = new HashMap<String, Object>();
			Util.fillMap(map, observer, false);
			transferRecordMaps.add(map);

			// Sets listeners to in progress transfers
			if (TransferState.WAITING.equals(observer.getState())
					|| TransferState.WAITING_FOR_NETWORK.equals(observer.getState())
					|| TransferState.IN_PROGRESS.equals(observer.getState())) {
				observer.setTransferListener(listener);
			}
		}
		//simpleAdapter.notifyDataSetChanged();
	}

	private void initUI() {
		/**
		 * This adapter takes the data in transferRecordMaps and displays it,
		 * with the keys of the map being related to the columns in the adapter
		 */
		simpleAdapter = new SimpleAdapter(this, transferRecordMaps,
				R.layout.record_item, new String[] {
				"checked", "fileName","thumbnail", "progress", "bytes", "state", "percentage"
		},
				new int[] {
						R.id.radioButton1, R.id.textFileName,R.id.thumbnail, R.id.progressBar1, R.id.textBytes,
						R.id.textState, R.id.textPercentage
				});
		simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
										String textRepresentation) {
				switch (view.getId()) {
					case R.id.radioButton1:
						RadioButton radio = (RadioButton) view;
						radio.setChecked((Boolean) data);
						return true;
					case R.id.textFileName:
						TextView fileName = (TextView) view;
						// fileName.setText((String) data);
						System.out.println("Textview-->"+(data));
						setBitmappath((String) data);
						return true;
					case R.id.thumbnail:
						System.out.println("Data-->"+getBitmappath());
						// Uri uri = Uri.fromFile(new File("/storage/sdcard/Pictures/CameraSample/IMG_20170701_190206_-1955301407.jpg"));

						ImageView thumbnail1 = (ImageView) view;
						thumbnail1.setImageBitmap(ThumbnailUtils.createImageThumbnail(getBitmappath(),3));
						return true;
					case R.id.progressBar1:
						ProgressBar progress = (ProgressBar) view;
						progress.setProgress((Integer) data);
						return true;
					case R.id.textBytes:
						TextView bytes = (TextView) view;
						bytes.setText((String) data);
						return true;
					case R.id.textState:
						TextView state = (TextView) view;
						state.setText(((TransferState) data).toString());
						return true;
					case R.id.textPercentage:
						TextView percentage = (TextView) view;
						percentage.setText((String) data);
						return true;
				}
				return false;
			}
		});
		setListAdapter(simpleAdapter);

		// Updates checked index when an item is clicked
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {

				if (checkedIndex != pos) {
					transferRecordMaps.get(pos).put("checked", true);
					if (checkedIndex >= 0) {
						transferRecordMaps.get(checkedIndex).put("checked", false);
					}
					checkedIndex = pos;
					//updateButtonAvailability();
					simpleAdapter.notifyDataSetChanged();
				}
			}
		});


	}

	/*
     * Updates the ListView according to the observers.
     */
	private void updateList() {
		TransferObserver observer = null;
		HashMap<String, Object> map = null;
		for (int i = 0; i < observers.size(); i++) {
			observer = observers.get(i);
			map = transferRecordMaps.get(i);
			Util.fillMap(map, observer, i == checkedIndex);
		}
		simpleAdapter.notifyDataSetChanged();

	}


}