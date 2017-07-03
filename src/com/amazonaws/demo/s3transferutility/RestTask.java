package com.amazonaws.demo.s3transferutility;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Android RestTask (REST) from the Android Recipes book.
 */
public class RestTask extends AsyncTask<HttpUriRequest, Void, String>
{
    private static final String TAG = "AsyncRestTask";
    public static final String HTTP_RESPONSE = "httpResponse";
    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;

    private Context mContext;
    private HttpClient mClient;
    private String mAction;

    public RestTask(Context context, String action) {
        mContext = context;
        mAction = action;
        mClient = new DefaultHttpClient();
    }

    public RestTask(Context context, String action, HttpClient client) {
        mContext = context;
        mAction = action;
        mClient = client;
    }



    @Override
    protected String doInBackground(HttpUriRequest... params) {
        try {

           // beginUpload();
            //Invoking youtube service Logic
            HttpUriRequest request = params[0];
            System.out.println("request.."+request.getURI().toString());
           HttpResponse serverResponse = mClient.execute(request);

            BasicResponseHandler handler = new BasicResponseHandler();
           return handler.handleResponse(serverResponse);
        }
        catch (Exception e) {
            // TODO handle this properly
            e.printStackTrace();
            return "";
        }
    }


    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println("line is "+line);
                sb.append((line + "\n"));
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
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "RESULT = " + result);
        Intent intent = new Intent(mAction);
        intent.putExtra(HTTP_RESPONSE, result);

        // broadcast the completion
        mContext.sendBroadcast(intent);
    }

}


