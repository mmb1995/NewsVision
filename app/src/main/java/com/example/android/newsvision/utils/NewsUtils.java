package com.example.android.newsvision.utils;

import android.net.Uri;

public class NewsUtils {

    private static final String BASE_URL = "http://api.nytimes.com/svc/";

    private static final String TOP_STORIES_PARAM = "topstories/v2/%s.json?";

    private static final String ARTICLE_SEARCH_PARAM = "search/v2/articlesearch.json?";

    private static final String QUERY_PARAM = "q";

    private static final String ARG_BEGIN_DATE = "begin_date";

    private static final String ARG_END_DATE = "end_date";

    private static final String SORT_PARAM = "sort";

    private static final String SORT_VALUE = "newest";

    private static final String API_KEY_PARAM = "api-key";

    private static final String API_KEY_VALUE = "";

    public static String buildNytTopStoriesUrl(String newsSearchQuery) {

        // formats the Uri to include the users query
        String formattedQuery = String.format(TOP_STORIES_PARAM, newsSearchQuery);

        // builds the URI to query the top stories section on the New YorL times API
        Uri nytUri = Uri.parse((BASE_URL + formattedQuery)).buildUpon()
                .appendQueryParameter(SORT_PARAM, SORT_VALUE)
                .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                .build();

        return nytUri.toString();
    }

    public static String buildNytSearchResultsUrl(String newsSearchQuery, String beginDate,
                                                  String endDate) {

        // builds the URi to query the article search section of the New York Times API
        Uri nytArticlSearchUri = Uri.parse(BASE_URL + ARTICLE_SEARCH_PARAM).buildUpon()
                .appendQueryParameter(QUERY_PARAM, newsSearchQuery)
                .appendQueryParameter(ARG_BEGIN_DATE, beginDate)
                .appendQueryParameter(ARG_END_DATE, endDate)
                .appendQueryParameter(SORT_PARAM, SORT_VALUE)
                .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                .build();

        return  nytArticlSearchUri.toString();

    }
}