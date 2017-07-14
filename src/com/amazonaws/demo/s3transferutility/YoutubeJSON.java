package com.amazonaws.demo.s3transferutility;

import java.util.List;

/**
 * Created by ramprasadgoturkarnam on 6/26/17.
 */

public class YoutubeJSON {
    private String[] youtubelink;

    public String[] getYoutubelink ()
    {
        return youtubelink;
    }

    public void setYoutubelink (String[] youtubelink)
    {
        this.youtubelink = youtubelink;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [youtubelink = "+youtubelink+"]";
    }
}
