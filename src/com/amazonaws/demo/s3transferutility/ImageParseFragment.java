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

import com.amazonaws.demo.s3transferutility.cardview.Album;
import com.amazonaws.demo.s3transferutility.cardview.CardViewMainActivity;
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
import java.io.Serializable;
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
    private String imageName;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

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
            setImageName(imagename);

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

            Map<String, List> videolinkMap = frameVideolinks(response);
           /* YoutubeServiceFragment youtubeFragment = new YoutubeServiceFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("hashmap", (Serializable) videolinkMap);

            youtubeFragment.setArguments(bundle);

            getFragmentManager().beginTransaction().add(android.R.id.content, youtubeFragment).commit();*/

            // mMaplinks.put("youtubelink",youtubelink);
            Intent intent1 = new Intent(getActivity(),CardViewMainActivity.class);

            intent1.putExtra("imagelinks",(Serializable)videolinkMap);
            startActivity(intent1);


        }
    };



    private Map<String,List> frameVideolinks(String response){

       /* int[] covers = new int[]{
                "",
                getResources().getIdentifier("R.drawable.album2", null, "com.amazonaws.demo.s3transferutility"),
                R.drawable.album3,
                R.drawable.wikipedia,
                R.drawable.album5,
                R.drawable.dailymotion,
                R.drawable.album7,
                R.drawable.album8,
                R.drawable.album9,
                R.drawable.album10,
                R.drawable.album11};*/

       String[] covers = new String[]{
               "https://s3.amazonaws.com/imageslens/images/youtubeicon3.png",
               "http://4.bp.blogspot.com/-lWM-u_6PCPU/VN4eatUfY_I/AAAAAAAAED4/PgKq_6wugQY/s1600/Pursuit%2Bof%2Bhappiness%2B2.png",
               "https://s3.amazonaws.com/imageslens/images/wikipedia.jpg",
               "https://s3.amazonaws.com/imageslens/images/if_dailymotion_11008.png",
               "https://s3.amazonaws.com/imageslens/images/vimeoresize.png",
               "https://s3.amazonaws.com/imageslens/images/if_dailymotion_11008.png",
               "https://s3.amazonaws.com/imageslens/images/if_dailymotion_11008.png",
               "https://s3.amazonaws.com/imageslens/images/linkedin.png",
               "https://s3.amazonaws.com/imageslens/images/if_dailymotion_11008.png",
               "https://s3.amazonaws.com/imageslens/images/if_dailymotion_11008.png",
               "https://s3.amazonaws.com/imageslens/images/if_dailymotion_11008.png"
       };


        ArrayList albumList = Lists.newArrayList();

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
        List<String> kapinamelist = videoLinkMap.get(Constants.KPI_NAME);
        List<String> kapidescriptionlist = videoLinkMap.get(Constants.KPI_DESCRIPTION);
        List<String> similarimageslist = videoLinkMap.get(Constants.SIMILAR_IMAGES);
        System.out.println("bestguesslist-->"+bestguesslist);

        Map<String,List> linksMap = Maps.newHashMap();



        if(CollectionUtils.isNotEmpty(youtubelinklist)) {
               /* Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubelinklist.get(0)));
                startActivity(intent1);*/
            // int imageResource = R.drawable.icon;
            String bestguess = "";
            if(CollectionUtils.isNotEmpty(bestguesslist)){
                bestguess = bestguesslist.get(0);
            }

           Album a = new Album("Click here for latest videos in Youtube of "+bestguess, 8, covers[0], youtubelinklist.get(0));
            albumList.add(a);
            //linksMap.put("youtubelink",youtubelinklist.get(0));
        } //else
       /* if(CollectionUtils.isNotEmpty(bestguesslist)) {
            String bestGuessString = bestguesslist.get(0);
            Log.i(TAG, "videoServiceInputString with BestGuess = " + bestGuessString);
            Album a1 = new Album("KabhiNahi", 8, covers[1],"https://www.youtube.com/watch?v=qC977wZ2w4Q");
            albumList.add(a1);


        }*///else
        if(CollectionUtils.isNotEmpty(wikipedialinklist)) {
           // String wikilink = Util.parseWikipedia(wikipedialinklist);
            String wikilink = wikipedialinklist.get(0);
                    Log.i(TAG, "videoServiceInputString wiki = " + wikilink);

            Album a2 = new Album("Click here for details in WikiPedia ", 12, covers[2], wikilink);
            albumList.add(a2);

            //linksMap.put("wikipedialink",wikilink);
            //Call Fragment
               /* Bundle bundle = new Bundle();
                bundle.putString("videoname",videoServiceInputString);

                YoutubeServiceFragment  youtubeFragment = new YoutubeServiceFragment();
                youtubeFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().add(android.R.id.content, youtubeFragment).commit();*/

            //End
                /*Intent youtubeserviceintent = new Intent(getActivity(), TestActivity.class);
                youtubeserviceintent.putExtra("videoname",videoServiceInputString);
                startActivity(youtubeserviceintent);*/
        }// else
        if(CollectionUtils.isNotEmpty(gettyimageslist)) {
            //String gettyImageInputString = Util.parseWikipedia(gettyimageslist);
            String gettyImageInputString = gettyimageslist.get(0);
                    Log.i(TAG, "videoServiceInputString getty = " + gettyImageInputString);

            Album a3 = new Album("Maroon 5", 11, covers[4], gettyImageInputString);
            albumList.add(a3);
           // linksMap.put("gettyImagelink",gettyImageInputString);

            //Call Fragment
              /*  Bundle bundle = new Bundle();
                bundle.putString("videoname",videoServiceInputString);

                YoutubeServiceFragment  youtubeFragment = new YoutubeServiceFragment();
                youtubeFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().add(android.R.id.content, youtubeFragment).commit();*/

            //End
               /*Intent youtubeserviceintent = new Intent(getActivity(), TestActivity.class);
                youtubeserviceintent.putExtra("videoname",videoServiceInputString);
                startActivity(youtubeserviceintent);*/
        }//else
        if(CollectionUtils.isNotEmpty(vimeolinklist)) {
            String vimeourl = vimeolinklist.get(0);

            Album a4 = new Album("Click here for Videos in Vimeo ", 14, covers[4], vimeourl);
            albumList.add(a4);
           // linksMap.put("vimeolink",vimeourl);
                /*Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);*/
        }//else
        if(CollectionUtils.isNotEmpty(daiymotionlinklist)) {
            String dailymotionlinkurl = daiymotionlinklist.get(0);
                /*Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);*/

            Album a5 = new Album("I Need a Doctor", 1, covers[3], dailymotionlinkurl);
            albumList.add(a5);
           // linksMap.put("dailymotionlink",dailymotionlinkurl);

        }//else
        if(CollectionUtils.isNotEmpty(generallinklist)) {


            String generalurllink = generallinklist.get(0);
            Album a6 = new Album("Click here for General Links ", 11, "https://s3.amazonaws.com/imageslens/"+getImageName(), generalurllink);
            albumList.add(a6);
            //linksMap.put("generallink",generalurl);
        }//else
        if(CollectionUtils.isNotEmpty(amazonuklinklist)) {
            String amazonuklinkurl = amazonuklinklist.get(0);
                /*Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);*/

            //linksMap.put("amazonuklink",amazonuklinkurl);
        }//else

        if(CollectionUtils.isNotEmpty(linkedinlist)) {
            String linkedinurl = linkedinlist.get(0);
            Album a6 = new Album("Click here for LinkedIn Links ", 11, covers[7], linkedinurl);
            albumList.add(a6);
        }//else

        if(CollectionUtils.isNotEmpty(similarimageslist)) {
            if(similarimageslist.size()>=1) {

                String similarImage = similarimageslist.get(0);
                String similarImagelink = similarimageslist.get(1);

                Log.d("similarImage->", similarImage);
                Album a8 = new Album("SIMILAR IMAGES", 14, similarImage,similarImagelink);
                albumList.add(a8);
                // linksMap.put("descriptionlink",description);
                //linksMap.put("titlelink",title);
            }
            if(CollectionUtils.isNotEmpty(titlelist)||CollectionUtils.isNotEmpty(descriptionlist)) {
                String similarImagelink = " ";

                if(similarimageslist.size()>=1) {
                    similarImagelink = similarimageslist.get(1);
                } else{
                    similarImagelink = similarimageslist.get(0);
                }

                    String title = titlelist.get(0);
                String description = descriptionlist.get(0);
                Log.d("title->",title);
                Log.d("description->",description);
                Album a8 = new Album("Click here for Descriptions..", 14, similarImagelink,description+":"+title);
                albumList.add(a8);
                // linksMap.put("descriptionlink",description);
                //linksMap.put("titlelink",title);
            }
        }




        //Connect to CardViewLayoutActivity
           /* Intent youtubeserviceintent = new Intent(getActivity(), CardViewMainActivity.class);

            youtubeserviceintent.putExtra("videolinks",)
            startActivity(youtubeserviceintent);*/

        linksMap.put("link",albumList);
        linksMap.put("kgapiname",kapinamelist);
        linksMap.put("kgapidescription",kapidescriptionlist);
        linksMap.put("bestguess",bestguesslist);

           return linksMap;

    }



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
        Pattern patternGettyMediaImages = Pattern.compile(Constants.GETTY_MEDIA_IMAGES_PATTERN, Pattern.CASE_INSENSITIVE);




        for (String string : p.getLinks()) {
            if (patternWikipedia.matcher(string).find()) {
                if (string.startsWith(Constants.WIKIPEDIA_PATTERN))
                    System.out.println("Wikipidea String... " + string);
                videoWikilist.add(string);

            } /*else if (patternYoutube.matcher(string).find()) {
                youtubelist.add(string);
            } */else if (patternVimeo.matcher(string).find()) {
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
            }if (patternGettyImages.matcher(string).find()||patternGettyMediaImages.matcher(string).find()) {
                if (string.startsWith(Constants.GETTY_IMAGES_PATTERN)||string.startsWith(Constants.GETTY_MEDIA_IMAGES_PATTERN))
                    gettyimageslist.add(string);

            }
        }
        //System.out.println("Links---->" + videoWikilist);
        videolinksMap.put(Constants.YOUTUBE, youtubelist);
        videolinksMap.put(Constants.WIKIPEDIA, videoWikilist);
        videolinksMap.put(Constants.VIMEO, vimeolist);
        videolinksMap.put(Constants.DAILY_MOTION, dailymotionlist);
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

        if(!com.google.common.base.Strings.isNullOrEmpty(p.getYoutubelink())){
            videolinksMap.put(Constants.YOUTUBE, Lists.newArrayList(p.getYoutubelink()));
        }

        if(CollectionUtils.isNotEmpty(p.getSimilar_images())){
            videolinksMap.put(Constants.SIMILAR_IMAGES, Lists.newArrayList(p.getSimilar_images()));
        }

        if(!com.google.common.base.Strings.isNullOrEmpty(p.getKapiname())){
            System.out.println("KPIName --->"+p.getKapiname());
            videolinksMap.put(Constants.KPI_NAME, Lists.newArrayList(p.getKapiname()));
        }

        if(!com.google.common.base.Strings.isNullOrEmpty(p.getKapidescription())){
            System.out.println("KPIDescription --->"+p.getKapidescription());
            videolinksMap.put(Constants.KPI_DESCRIPTION, Lists.newArrayList(p.getKapidescription()));
        }


        return videolinksMap;

    }
}







