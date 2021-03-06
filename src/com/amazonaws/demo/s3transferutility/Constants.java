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

import com.amazonaws.regions.Regions;

public class Constants {

    /*
     * You should replace these values with your own. See the README for details
     * on what to fill in.
     */
    public static final String COGNITO_POOL_ID = "";

    /*
     * Region of your Cognito identity pool ID.
     */
    public static final String COGNITO_POOL_REGION = "";

    public static final String imagelens_url = "http://10.0.2.2:8082/imagelens/v1/image/upload/";

    /*
     * Note, you must first create a bucket using the S3 console before running
     * the sample (https://console.aws.amazon.com/s3/). After creating a bucket,
     * put it's name in the field below.
     */
    public static final String BUCKET_NAME = "imageslens";

    /*
     * Region of your bucket.
     */
    public static final String BUCKET_REGION = "";

    public static final String YOUTUBE = "youtube";
    public static final String KPI_NAME = "kpiname";
    public static final String KPI_DESCRIPTION = "kpidescription";
    public static final String SIMILAR_IMAGES = "similarimages";
    public static final String WIKIPEDIA = "wikipedia";
    public static final String VIMEO = "vimeo";
    public static final String AMAZON = "amazon";
    public static final String AMAZON_UK = "amazonuk";

    public static final String LINKEDIN = "linkedin";
    public static final String DAILY_MOTION = "dailymotion";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String IMAGEBESTGUESS = "imagebestguess";
    public static final String GENERIC_LINK = "genericlink";
    public static final String GETTY_IMAGES = "gettyimages";
    public static final String REST_SERVICE_URI = "";



    public static final String YOUTUBE_PATTERN = "https://www.youtube.com/watch";
    public static final String WIKIPEDIA_PATTERN = "https://en.wikipedia.org/wiki";
    public static final String LINKEDIN_PATTERN = "https://www.linkedin.com/in/";
    public static final String VIMEO_PATTERN = "https://vimeo.com/";
    public static final String DAILY_MOTION_PATTERN = "https://www.dailymotion.com/video/";
    public static final String AMAZON_PATTERN = "https://www.amazon.com/";
    public static final String AMAZON_UK_PATTERN = "https://www.amazon.co.uk/";
    public static final String GETTY_IMAGES_PATTERN = "http://www.gettyimages.com/photos/";
    public static final String GETTY_MEDIA_IMAGES_PATTERN = "http://media.gettyimages.com/photos/";
}
