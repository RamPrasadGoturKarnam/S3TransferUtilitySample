package com.amazonaws.demo.s3transferutility;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TitleDescriptionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_description);
        String title= this.getIntent().getExtras().getString("title");
        String description= this.getIntent().getExtras().getString("description");

        final TextView simpleTextView = (TextView) findViewById(R.id.simpleTextView); //get the id for TextView
        simpleTextView.setPaintFlags(simpleTextView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        simpleTextView.setText("Sorry!! No videos available .."); //set the text after clicking button

        final TextView simpleTitleView = (TextView) findViewById(R.id.simpleTitleView); //get the id for TextView
        simpleTitleView.setText(title); //set the text after clicking button

        final TextView simpleDescriptionView = (TextView) findViewById(R.id.simpleDescriptionView); //get the id for TextView
        simpleDescriptionView.setText(description); //set the text after clicking button
    }

}
