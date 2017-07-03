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

import com.amazonaws.http.HttpResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static android.R.attr.data;
import static com.amazonaws.demo.s3transferutility.Util.parseWikipedia;


public class BlankFragment extends Fragment {

    private static final String TAG = "AsyncTestFragment";

    // get some fake data
    // private static final String TEST_URL                 = "http://jsonplaceholder.typicode.com/comments";
   // private static final String TEST_URL                   = "http://192.168.1.16:5000/search";
    private static final String TEST_URL                   = "http://54.236.214.104:5000/search";
    private static final String S3_URL                   = "{\"image_url\":\"https://s3.amazonaws.com/imageslens/";
    String S3_END_URL = "}";
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
       // ourTextView = (TextView)getActivity().findViewById(R.id.myTextView);
        getContent();
    }

    private void getContent() {
        // the request
        try {
          // TestCurlActivity curlactivity = (TestCurlActivity) getActivity();

          String imagename= this.getArguments().getString("imagename");
            Log.i("imagenameS3",imagename);
           // String imagescannerserviceURL = S3_URL+"deepika-padukone.jpg";
           // Log.i("imagescannerserviceURL",imagescannerserviceURL);
            //String imagename = "Raghu.jpeg";
            HttpPost httpPost = new HttpPost(new URI(TEST_URL));
           // String data =  "{\"image_url\":\"https://s3.amazonaws.com/imageslens/images/mickey-mouse-clubhouse-disney.jpg\"}";
            //String data1 =  "{\"image_url\":\"https://s3.amazonaws.com/imageslens/IMG_20170628_204710.jpg\"}";
            //Log.i("Service URL ",data1);
            String data =  S3_URL+imagename+"\"}";
            Log.i("Service URL ",data);
            HttpEntity entity = new ByteArrayEntity(data.getBytes("UTF-8"));
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
           RestTask task = new RestTask(getActivity(), ACTION_FOR_INTENT_CALLBACK);
           // HttpClient mClient = new DefaultHttpClient();
            task.execute(httpPost);
          //  org.apache.http.HttpResponse response = mClient.execute(httpPost);
           // String response = httpconnection();
           // System.out.println("Successfully Posted Data ... waiting ..Response"+response);
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
            Map<String, List> videoLinkMap = ConvertString2Json(response);
            List<String> youtubelinklist = videoLinkMap.get(Constants.YOUTUBE);
            List<String> wikipedialinklist = videoLinkMap.get(Constants.WIKIPEDIA);
            if(CollectionUtils.isNotEmpty(youtubelinklist)) {
                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubelinklist.get(0)));
                startActivity(intent1);
            } else
            if(CollectionUtils.isNotEmpty(wikipedialinklist)) {
                String videoServiceInputString = Util.parseWikipedia(wikipedialinklist);
                Log.i(TAG, "videoServiceInputString = " + videoServiceInputString);

                Intent youtubeserviceintent = new Intent(getActivity(), TestActivity.class);
                youtubeserviceintent.putExtra("videoname",videoServiceInputString);
                startActivity(youtubeserviceintent);
            } else{
                ourTextView.setText(response);
            }


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

    // convert InputStream to String
    private static String getStringFromInputStream(InputStreamReader is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(is);
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    public String httpconnection(){

        try {

            String url = "http://0.0.0.0:5000/search";

            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");

            //  String userpass = "user" + ":" + "pass";
            // String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
            // conn.setRequestProperty ("Authorization", basicAuth);

            String data =  "{\"image_url\":\"https://s3.amazonaws.com/imageslens/images/mickey-mouse-clubhouse-disney.jpg\"}";
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(data);
            out.close();

            InputStreamReader instream = new InputStreamReader(conn.getInputStream());
            String s = getStringFromInputStream(instream);
            //System.out.println(s);
            //ConvertString2Json(s);
            return s;

        } catch (Exception e) {
            e.printStackTrace();
        }
return null;
    }


    public Map<String, List> ConvertString2Json(String curlOutput){
        Map<String,List> videolinksMap = Maps.newHashMap();
        List<String> videoWikilist = Lists.newArrayList();
        List<String> youtubelist = Lists.newArrayList();
        Gson g = new Gson();
        MrisaJSON p = g.fromJson(curlOutput, MrisaJSON.class);
        System.out.println("Links---->"+p.getLinks());

        Pattern patternWikipedia = Pattern.compile("https://en.wikipedia.org/wiki",Pattern.CASE_INSENSITIVE); //incase u r not concerned about upper/lower case
        Pattern patternYoutube = Pattern.compile("https://www.youtube.com/watch",Pattern.CASE_INSENSITIVE); //incase u r not concerned about upper/lower case
        for (String string : p.getLinks()) {
            if(patternWikipedia.matcher(string).find()) {
                if(string.startsWith("https://en.wikipedia.org/wiki"))
                    videoWikilist.add(string);

            }else if(patternYoutube.matcher(string).find()) {
                youtubelist.add(string);
            }
        }
        videolinksMap.put(Constants.YOUTUBE,youtubelist);
        videolinksMap.put(Constants.WIKIPEDIA,videoWikilist);
        return videolinksMap;

    }
}







