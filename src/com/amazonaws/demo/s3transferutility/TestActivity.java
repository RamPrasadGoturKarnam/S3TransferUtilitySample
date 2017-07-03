package com.amazonaws.demo.s3transferutility;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TestActivity extends Activity {
    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            // if the screen is in landscape mode, we can show the
//            // dialog in-line with the list so we don't need this activity.
//            finish();
//            return;
//        }

       // transferUtility = Util.getTransferUtility(this);
       // beginUpload();
        if (savedInstanceState == null) {
            TestFragment testFragment = new TestFragment();
            getFragmentManager().beginTransaction().add(android.R.id.content, testFragment).commit();
        }
    }

    private void beginUpload() {
       /* if (filePath == null) {
           // Toast.makeText(this, "Could not find the filepath of the selected file",Toast.LENGTH_LONG).show();
            return;
        }*/


        AmazonS3 s3 = Util.getS3Client(getApplicationContext());
        //TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
        System.out.println("Started Uploading the Image");
        // Create an S3 client
        ObjectMetadata myObjectMetadata = new ObjectMetadata();

//create a map to store user metadata
        Map<String, String> userMetadata = new HashMap<String,String>();
        userMetadata.put("AKIAJWVIN5QDQIMPF37A",
                "xUevtlbk/jEXogRfG55y3U0GnJhCVTZj35j4DsAD");



//call setUserMetadata on our ObjectMetadata object, passing it our map
        myObjectMetadata.setUserMetadata(userMetadata);
        String bucketName = "imageslens";
        File file = new File("/storage/sdcard/DCIM/Camera/IMG_20170621_212828.jpg");
        TransferObserver observer = transferUtility.upload(bucketName, file.getName(),
                file);
        Log.d("Completed Uplodaing",observer.getBucket());
        Log.d("Completed Uplodaing1", String.valueOf(observer.getState()));
        while(observer.getState() == TransferState.COMPLETED) {
            Log.d("TAG", "COMPLETED");
        }
        TransferObserver observerdownload = transferUtility.download(bucketName, file.getName(),
                file);
        Log.d("Completed Downloading",observerdownload.getKey().toString());
                /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
         * -> set listeners to in progress transfers.
         */
        // observer.setTransferListener(new UploadListener());
    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


}



