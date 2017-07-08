/*
 * Copyright 2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.demo.s3transferutility;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.amazonaws.demo.s3transferutility.YoutubeServiceFragment;

/*
 * This is the beginning screen that lets the user select if they want to upload or download
 */
public class MainActivity extends Activity {

    private Button btnDownload;
    private Button btnUpload;
    private Button btnInvokeYoutube;
    private Button btnInvokeCurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
       // btnDownload = (Button) findViewById(R.id.buttonDownloadMain);
        btnUpload = (Button) findViewById(R.id.buttonUploadMain);
      //  btnInvokeYoutube = (Button) findViewById(R.id.buttonInvokeVideo);
       // btnInvokeCurl = (Button) findViewById(R.id.buttonCurl);


       /* btnDownload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                startActivity(intent);
            }
        });*/

        btnUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

       /* btnInvokeYoutube.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });

        btnInvokeCurl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, TestCurlActivity.class);
                startActivity(intent);
            }
        });*/

    }
}
