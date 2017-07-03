package com.amazonaws.demo.s3transferutility;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.amazonaws.demo.s3transferutility.TestFragment;
import com.amazonaws.demo.s3transferutility.Util;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TestCurlActivity extends Activity {


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
            BlankFragment testFragment = new BlankFragment();
            getFragmentManager().beginTransaction().add(android.R.id.content, testFragment).commit();
        }
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
