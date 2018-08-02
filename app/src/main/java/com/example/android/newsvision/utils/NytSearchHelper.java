package com.example.android.newsvision.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.newsvision.NewsArticle;

import org.json.JSONObject;

import java.util.List;

public class NytSearchHelper {
    private static List<NewsArticle> mNewsArticleList;
    private static final String TAG = "Network Request";
    private static final String TOP_STORIES = "Top Stories";
    private static final String SEARCH_RESULTS = "search results";

    public static void getTopStoriesFromNyt(String searchTerm, String type, Context context) {
        String nytSearchUrl = NewsUtils.buildNytTopStoriesUrl(searchTerm);
        Log.d(TAG,"Given url: " +  nytSearchUrl);
        downloadNytResponse(nytSearchUrl,type, context);

    }

    public static void getArticleSearchFromNyt(String searchTerm, String beginDate, String endDate,
                                               String type, Context context) {
        String nytArticleSearchUrl = NewsUtils.buildNytSearchResultsUrl(searchTerm, beginDate, endDate);
        Log.d(TAG,"Given url: " + nytArticleSearchUrl);
        downloadNytResponse(nytArticleSearchUrl, type, context);

    }

    private static void downloadNytResponse(String nytUrl, final String type, Context context) {
        // Performs network request to NYT API
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, nytUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "API response: " + response.toString());
                        if (type.equals(TOP_STORIES)) {
                            mNewsArticleList = NewsArticle.parseNYTTopStories(response);
                        } else {
                            mNewsArticleList = NewsArticle.parseNYTSearch(response);
                        }
                        setNewsArticleResults(mNewsArticleList);
                        if (mNewsArticleList != null) {
                            Log.d(TAG,"Obtained Network Results");
                        } else {
                            Log.d(TAG, "Failed to get Network Results");
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error" + error.getMessage());

                    }
                });

        // Access the RequestQueue through your singleton class.
        NewsSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void setNewsArticleResults(List<NewsArticle> newsArticleList) {
        mNewsArticleList = newsArticleList;
    }

    public static List<NewsArticle> getNewsArticleList() {
        return mNewsArticleList;
    }
}
