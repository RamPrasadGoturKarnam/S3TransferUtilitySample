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
import com.amazonaws.util.StringUtils;
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
import static android.R.attr.description;
import static com.amazonaws.demo.s3transferutility.Util.parseWikipedia;


public class ImageParseFragment extends Fragment {

    private static final String TAG = "AsyncTestFragment";

    // get some fake data
    // private static final String TEST_URL                 = "http://jsonplaceholder.typicode.com/comments";
   // private static final String TEST_URL                   = "http://192.168.1.16:5000/search";
    private static final String TEST_URL                   = "http://54.236.214.104:5000/search";
    private static final String S3_URL                   = "{\"image_url\":\"https://s3.amazonaws.com/imageslens/";
    String S3_END_URL = "}";
    private static final String ACTION_FOR_INTENT_CALLBACK = "THIS_IS_A_UNIQUE_KEY_WE_USE_TO_COMMUNICATE";
    private static final String ACTION_FOR_INTENT_CALLBACK_1 = "THIS_IS_A_UNIQUE_KEY_WE_USE_TO_COMMUNICATE_1";

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

           /* HttpPost httpPost = new HttpPost(new URI(TEST_URL));
            String data =  S3_URL+imagename+"\"}";*/
            HttpPost httpPost = new HttpPost(new URI(Constants.REST_SERVICE_URI+imagename));
           /* String data =  imagename;

            Log.i("Service URL ",data);
            HttpEntity entity = new ByteArrayEntity(data.getBytes("UTF-8"));

            httpPost.setEntity(entity);*/
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
           RestTask task = new RestTask(getActivity(), ACTION_FOR_INTENT_CALLBACK_1);
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
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_FOR_INTENT_CALLBACK_1));
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
            Log.i(TAG, "onReceive entered");
            // clear the progress indicator
            if (progress != null) {
                progress.dismiss();
            }
            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            Log.i(TAG, "RESPONSE = " + response);
           // ourTextView.setText(response);

            Map<String, List> videoLinkMap = ConvertString2Json(response);
            List<String> youtubelinklist = videoLinkMap.get(Constants.YOUTUBE);
            List<String> wikipedialinklist = videoLinkMap.get(Constants.WIKIPEDIA);
            List<String> vimeolinklist = videoLinkMap.get(Constants.VIMEO);
            List<String> daiymotionlinklist = videoLinkMap.get(Constants.DAILY_MOTION);
            List<String> linkedinlist = videoLinkMap.get(Constants.LINKEDIN);
            List<String> titlelist = videoLinkMap.get(Constants.TITLE);
            List<String> descriptionlist = videoLinkMap.get(Constants.DESCRIPTION);
            List<String> amazonlinklist = videoLinkMap.get(Constants.AMAZON);
            List<String> amazonuklinklist = videoLinkMap.get(Constants.AMAZON_UK);
            List<String> gettyimageslist = videoLinkMap.get(Constants.GETTY_IMAGES);
            List<String> generallinklist = videoLinkMap.get(Constants.GENERIC_LINK);
            List<String> bestguesslist = videoLinkMap.get(Constants.IMAGEBESTGUESS);
            System.out.println("bestguesslist-->"+bestguesslist);

            if(CollectionUtils.isNotEmpty(bestguesslist)) {
                String videoServiceInputString = bestguesslist.get(0);
                Log.i(TAG, "videoServiceInputString with BestGuess = " + videoServiceInputString);

                //Call Fragment
                Bundle bundle = new Bundle();
                bundle.putString("videoname",videoServiceInputString);

                YoutubeServiceFragment  youtubeFragment = new YoutubeServiceFragment();
                youtubeFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().add(android.R.id.content, youtubeFragment).commit();

                //End
               /* Intent youtubeserviceintent = new Intent(getActivity(), TestActivity.class);
                youtubeserviceintent.putExtra("videoname",videoServiceInputString);
                startActivity(youtubeserviceintent);*/
            }else
            if(CollectionUtils.isNotEmpty(youtubelinklist)) {
                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubelinklist.get(0)));
                startActivity(intent1);
            } else
            if(CollectionUtils.isNotEmpty(wikipedialinklist)) {
                String videoServiceInputString = Util.parseWikipedia(wikipedialinklist);
                Log.i(TAG, "videoServiceInputString wiki = " + videoServiceInputString);

                //Call Fragment
                Bundle bundle = new Bundle();
                bundle.putString("videoname",videoServiceInputString);

                YoutubeServiceFragment  youtubeFragment = new YoutubeServiceFragment();
                youtubeFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().add(android.R.id.content, youtubeFragment).commit();

                //End
                /*Intent youtubeserviceintent = new Intent(getActivity(), TestActivity.class);
                youtubeserviceintent.putExtra("videoname",videoServiceInputString);
                startActivity(youtubeserviceintent);*/
            } else
            if(CollectionUtils.isNotEmpty(gettyimageslist)) {
                String videoServiceInputString = Util.parseWikipedia(gettyimageslist);
                Log.i(TAG, "videoServiceInputString getty = " + videoServiceInputString);

                //Call Fragment
                Bundle bundle = new Bundle();
                bundle.putString("videoname",videoServiceInputString);

                YoutubeServiceFragment  youtubeFragment = new YoutubeServiceFragment();
                youtubeFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().add(android.R.id.content, youtubeFragment).commit();

                //End
               /*Intent youtubeserviceintent = new Intent(getActivity(), TestActivity.class);
                youtubeserviceintent.putExtra("videoname",videoServiceInputString);
                startActivity(youtubeserviceintent);*/
            }else if(CollectionUtils.isNotEmpty(vimeolinklist)) {
                String url = vimeolinklist.get(0);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }else if(CollectionUtils.isNotEmpty(daiymotionlinklist)) {
                String url = daiymotionlinklist.get(0);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }else if(CollectionUtils.isNotEmpty(generallinklist)) {
                String url = generallinklist.get(0);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }else if(CollectionUtils.isNotEmpty(amazonuklinklist)) {
                String url = amazonuklinklist.get(0);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }else if(CollectionUtils.isNotEmpty(linkedinlist)) {
                String url = linkedinlist.get(0);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }else if(CollectionUtils.isNotEmpty(linkedinlist)) {
                String url = linkedinlist.get(0);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }else if(CollectionUtils.isNotEmpty(titlelist)||CollectionUtils.isNotEmpty(descriptionlist)) {
                String title = titlelist.get(0);
                String description = descriptionlist.get(0);
                Log.d("title->",title);
                Log.d("description->",description);
                Intent titledescriptionserviceintent = new Intent(getActivity(), TitleDescriptionActivity.class);
                titledescriptionserviceintent.putExtra("title",title);
                titledescriptionserviceintent.putExtra("description",description);
                startActivity(titledescriptionserviceintent);
            }else if(CollectionUtils.isEmpty(titlelist)||CollectionUtils.isEmpty(descriptionlist)) {
                Intent titledescriptionserviceintent = new Intent(getActivity(), TitleDescriptionActivity.class);
                titledescriptionserviceintent.putExtra("title","");
                titledescriptionserviceintent.putExtra("description","");
                startActivity(titledescriptionserviceintent);
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


    public Map<String, List> ConvertString2Json(String curlOutput) {
        Map<String, List> videolinksMap = Maps.newHashMap();
        List<String> videoWikilist = Lists.newArrayList();
        List<String> youtubelist = Lists.newArrayList();
        List<String> linkedinlist = Lists.newArrayList();
        List<String> vimeolist = Lists.newArrayList();
        List<String> amazonlist = Lists.newArrayList();
        List<String> amazonuklist = Lists.newArrayList();
        List<String> dailymotionlist = Lists.newArrayList();
        List<String> generallinklist = Lists.newArrayList();
        List<String> gettyimageslist = Lists.newArrayList();


        Gson g = new Gson();
        MrisaJSON p = g.fromJson(curlOutput, MrisaJSON.class);
        /*System.out.println("Links---->" + p.getLinks());
        System.out.println("titles---->" + p.getLinks());*/
       // System.out.println("BestGuess---->" + p.getImagebestguess());

        Pattern patternWikipedia = Pattern.compile(Constants.WIKIPEDIA_PATTERN, Pattern.CASE_INSENSITIVE); //incase u r not concerned about upper/lower case
        Pattern patternYoutube = Pattern.compile(Constants.YOUTUBE_PATTERN, Pattern.CASE_INSENSITIVE); //incase u r not concerned about upper/lower case
        Pattern patternLinkedIn = Pattern.compile(Constants.LINKEDIN_PATTERN, Pattern.CASE_INSENSITIVE);
        Pattern patternVimeo = Pattern.compile(Constants.VIMEO_PATTERN, Pattern.CASE_INSENSITIVE);
        Pattern patterndailyMotion = Pattern.compile(Constants.DAILY_MOTION_PATTERN, Pattern.CASE_INSENSITIVE);
        Pattern patternAmazon = Pattern.compile(Constants.AMAZON_PATTERN, Pattern.CASE_INSENSITIVE);
        Pattern patternAmazonUK = Pattern.compile(Constants.AMAZON_UK_PATTERN, Pattern.CASE_INSENSITIVE);
        Pattern patternGettyImages = Pattern.compile(Constants.GETTY_IMAGES_PATTERN, Pattern.CASE_INSENSITIVE);




        for (String string : p.getLinks()) {
            if (patternWikipedia.matcher(string).find()) {
                if (string.startsWith(Constants.WIKIPEDIA_PATTERN))
                    System.out.println("Wikipidea String... " + string);
                videoWikilist.add(string);

            } else if (patternYoutube.matcher(string).find()) {
                youtubelist.add(string);
            } else if (patternVimeo.matcher(string).find()) {
                if (string.startsWith(Constants.VIMEO_PATTERN))
                    vimeolist.add(string);

            } else if (patterndailyMotion.matcher(string).find()) {
                if (string.startsWith(Constants.DAILY_MOTION_PATTERN))
                    dailymotionlist.add(string);

            }else if (patternAmazon.matcher(string).find()) {
                if (string.startsWith(Constants.AMAZON_PATTERN))
                    amazonlist.add(string);

            }else if (patternAmazonUK.matcher(string).find()) {
                if (string.startsWith(Constants.AMAZON_UK_PATTERN))
                    amazonuklist.add(string);

            }else if (patternLinkedIn.matcher(string).find()) {
                if (string.startsWith(Constants.LINKEDIN_PATTERN))
                    linkedinlist.add(string);

            }else{
                generallinklist.add(string);
            }if (patternGettyImages.matcher(string).find()) {
                if (string.startsWith(Constants.GETTY_IMAGES_PATTERN))
                    gettyimageslist.add(string);

            }
        }
        //System.out.println("Links---->" + videoWikilist);
        videolinksMap.put(Constants.YOUTUBE, youtubelist);
        videolinksMap.put(Constants.WIKIPEDIA, videoWikilist);
        videolinksMap.put(Constants.VIMEO, vimeolist);
        videolinksMap.put(Constants.DAILY_MOTION, vimeolist);
        videolinksMap.put(Constants.AMAZON, amazonlist);
        videolinksMap.put(Constants.AMAZON_UK, amazonuklist);
        videolinksMap.put(Constants.LINKEDIN, linkedinlist);
        videolinksMap.put(Constants.GENERIC_LINK, generallinklist);
        videolinksMap.put(Constants.GETTY_IMAGES, gettyimageslist);

        if(CollectionUtils.isNotEmpty(p.getTitles())){
            videolinksMap.put(Constants.TITLE, p.getTitles());
        }

        if(CollectionUtils.isNotEmpty(p.getDescriptions())){
            videolinksMap.put(Constants.DESCRIPTION, p.getDescriptions());
        }

        if(!com.google.common.base.Strings.isNullOrEmpty(p.getImagebestguess())){
            videolinksMap.put(Constants.IMAGEBESTGUESS, Lists.newArrayList(p.getImagebestguess()));
        }




        return videolinksMap;

    }
}







