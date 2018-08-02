package com.example.android.newsvision;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.example.android.newsvision.utils.NewsSingleton;

import java.util.List;

public class NewsActivity extends AppCompatActivity
    implements NewsResultsListFragment.OnArticleSelectedListener, SearchFragment.OnSearchListener,
    PreviewFragment.OnFullArticleButtonListener, NewsResultsListFragment.OnArticleLongClickListener{

    private static final String TOP_STORIES_TAG = "Top Stories";
    private static final String TAG = "NEWS_VISION";
    private static final String WEBVIEW_FRAGMENT_TAG = "webview";

    private List<NewsArticle> mNewsArticleList;

    public RequestQueue mRequestQueue;

    private NewsResultsListFragment mResultsFrag;
    private PreviewFragment mPrevFrag;
    private SearchFragment mSearchFrag;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Initialize a Request queue to handle network requests
        mRequestQueue = NewsSingleton.getInstance(this).getRequestQueue();

        // Creates a new instance of the news results list fragment;
        NewsResultsListFragment mResultsFrag = NewsResultsListFragment.topStoriesInstance(NewsResultsListFragment.TOP_STORIES, "home");
        SearchFragment searchFragment = new SearchFragment();

        // Sets up default news list and search fragment
        FragmentTransaction initialTransaction = getSupportFragmentManager().beginTransaction();
        initialTransaction.add(R.id.news_results_container, mResultsFrag, TOP_STORIES_TAG);
        initialTransaction.add(R.id.news_detail_container, searchFragment);
        initialTransaction.addToBackStack(null);
        initialTransaction.commit();

    }

    /**
     * Implements the onArticleSelected method specified for the OnArticleSelectedListener interface.
     * Displays a Preview of the selected article.
     * @param currentArticle the NewsArticle that was clicked on
     */
    @Override
    public void onArticleSelected(NewsArticle currentArticle) {
        //Log.d(TAG,"clicked position:" + position);

        // Get the NewsArticle that was clicked on
        //NewsArticle currentArticle = getNewsArticle(position);

        // sets the url for the selected article
        setNewsArticleUrl(currentArticle.webUrl);

        // Sets arguments to be passed to the preview fragment
        String headline = currentArticle.headline;
        String snippet = currentArticle.snippet;
        String imageUrl = currentArticle.imageUrl;

        // Creates a new instance of the preview fragment
        PreviewFragment mPrevFrag = PreviewFragment.newInstance(headline, snippet, imageUrl);
        FragmentTransaction previewTransaction = getSupportFragmentManager().beginTransaction();
        previewTransaction.replace(R.id.news_detail_container, mPrevFrag);
        previewTransaction.addToBackStack(null);
        previewTransaction.commit();

    }

    /**
     * Sets the current web url for a selected article
     * @url a String representing the web url of the selected article
     */
    private void setNewsArticleUrl(String url) {
        mUrl = url;
    }

    /**
     * Gets the news article at the given position
     * @param position the index of the selected news article
     * @return The selected NewsArticle object or null if it doesn't exist
     */
    private NewsArticle getNewsArticle(int position) {
        if (mNewsArticleList != null) {
            return mNewsArticleList.get(position);
        }
        return null;
    }


    /**
     * Implements the onSearch method specified for the OnSearchListener interface
     * @param searchTerm the query entered by the user
     */
    @Override
    public void onSearch(String searchTerm, String beginDate, String endDate) {
       // Create a new search results instance and display it
       NewsResultsListFragment mResultsFrag = NewsResultsListFragment.searchResultsInstance(NewsResultsListFragment.SEARCH_RESULTS,
               searchTerm, beginDate, endDate);
       FragmentTransaction searchResultsTransaction = getSupportFragmentManager().beginTransaction();
       searchResultsTransaction.replace(R.id.news_results_container, mResultsFrag);
       searchResultsTransaction.addToBackStack(null);
       searchResultsTransaction.commit();
   }

    /**
     * Implements the onFullArticleButtonClicked method specified by the OnFullArticleButtonListener
     * Displays the full webpage for the current url;
     */
    @Override
    public void onFullArticleButtonClicked() {
        displayFullArticle();
    }


    /**
     *  Displays a full news article in a WebView
     */
     public void displayFullArticle() {
        Log.d(TAG,"url to display: " + mUrl);
        WebViewFragment webFrag = WebViewFragment.newInstance(mUrl);
        FragmentTransaction webTransaction = getSupportFragmentManager().beginTransaction();
        webTransaction.replace(R.id.news_detail_container, webFrag, WEBVIEW_FRAGMENT_TAG);
        webTransaction.addToBackStack(null);
        webTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment detailFrag = getSupportFragmentManager().findFragmentByTag(WEBVIEW_FRAGMENT_TAG);

        // Checks if the detail container contains a WebViewFragment and if there are any webpages in
        // its backstack
        if (detailFrag instanceof WebViewFragment && ((WebViewFragment) detailFrag).canGoBack()) {
            ((WebViewFragment) detailFrag).goBack();
        } else {
            // Use default behavior
            super.onBackPressed();
        }
    }

    @Override
    public void OnArticleLongClick(NewsArticle currentArticle) {
        // Gets the news article that was clicked on
        //NewsArticle currentArticle = getNewsArticle(position);

        // Sets the url of the selected article
        setNewsArticleUrl(currentArticle.webUrl);

        // Displays the full NewsArticle in a webview
        displayFullArticle();
    }
}
