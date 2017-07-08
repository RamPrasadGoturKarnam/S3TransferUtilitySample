package com.amazonaws.demo.s3transferutility;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class YoutubeServiceFragment extends Fragment {
    
    private static final String TAG = "AsyncTestFragment";

    // get some fake data
   // private static final String TEST_URL                 = "http://jsonplaceholder.typicode.com/comments";
    private static final String TEST_URL                   = "http://54.236.214.104:8080/api/youtube/video/";
    private static final String ACTION_FOR_INTENT_CALLBACK = "THIS_IS_A_UNIQUE_KEY_WE_USE_TO_COMMUNICATE";

    ProgressDialog progress;
    private TextView ourTextView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    /**
     * any code to access activity fields must be handled in this method.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ourTextView = (TextView)getActivity().findViewById(R.id.myTextView);
        getContent();
    }

    private void getContent() {
        // the request
        try {
            TestActivity activity = (TestActivity) getActivity();
            String videoname= activity.getIntent().getExtras().getString("videoname");
            String youtubeserviceURL = TEST_URL+videoname;
            Log.i("youtubeserviceURL",youtubeserviceURL);
            HttpGet httpGet = new HttpGet(new URI(youtubeserviceURL));
            RestTask task = new RestTask(getActivity(), ACTION_FOR_INTENT_CALLBACK);
            task.execute(httpGet);
            System.out.println("Getting Data ... waiting ..Response");
            progress = ProgressDialog.show(getActivity(), "Getting Data ...", "Waiting For Results...", true);

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_FOR_INTENT_CALLBACK));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    /**
     * Our Broadcast Receiver. We get notified that the data is ready, and then we
     * put the content we receive (a string) into the TextView.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // clear the progress indicator
            if (progress != null) {
                progress.dismiss();
            }
            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            //ourTextView.setText(response);
            Log.i(TAG, "RESPONSE = " + response);
            List<String> items = Arrays.asList(response.split(","));
            /*Gson gson = new Gson();
            JsonElement element = gson.fromJson (response, JsonElement.class);
            JsonObject jsonObj = element.getAsJsonObject();
            String[] arraylink = jsonObj.toString().split(",");*/

            String youtubelink = items.get(1).replace("\"", "");
            Log.i("Youtubelink",youtubelink);
            Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubelink));
            startActivity(intent1);
            //
            // my old json code was here. this is where you would parse it.
            //
        }
    };

    private static ArrayList convertStreamToList(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        //StringBuilder sb = new StringBuilder();
        ArrayList arraylist = new ArrayList();

                String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println("line is "+line);
                //sb.append((line + "\n"));
                arraylist.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       // return sb.toString();
        return arraylist;
    }

}







