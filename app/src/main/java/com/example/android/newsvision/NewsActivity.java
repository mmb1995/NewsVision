package com.example.android.newsvision;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.example.android.newsvision.utils.NewsSingleton;

import java.util.List;

public class NewsActivity extends AppCompatActivity
    implements NewsResultsListFragment.OnArticleSelectedListener, SearchFragment.OnSearchListener,
    PreviewFragment.OnFullArticleButtonListener, NewsResultsListFragment.OnArticleLongClickListener,
    WebViewModalFragment.OnWebViewClickListener {

    private static final String TOP_STORIES_TAG = "Top Stories";
    private static final String TAG = "NEWS_VISION";
    private static final String DETAIL_FRAGMENT_TAG = "detail";
    private static final String RESULTS_LIST_TAG = "results";
    private static final String DEFAULT_TOP_STORIES = "home";

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
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        // Initialize a Request queue to handle network requests
        mRequestQueue = NewsSingleton.getInstance(this).getRequestQueue();

        // Creates a new instance of the news results list fragment;
        NewsResultsListFragment mResultsFrag = NewsResultsListFragment.topStoriesInstance(NewsResultsListFragment.TOP_STORIES, "home");
        SearchFragment searchFragment = new SearchFragment();

        // Sets up default news list and search fragment
        FragmentTransaction initialTransaction = getSupportFragmentManager().beginTransaction();
        initialTransaction.add(R.id.news_results_container, mResultsFrag, RESULTS_LIST_TAG);
        initialTransaction.add(R.id.news_detail_container, searchFragment, DETAIL_FRAGMENT_TAG);
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
        WebViewModalFragment webFrag = WebViewModalFragment.newInstance(mUrl);

        webFrag.setUrl(mUrl);
        webFrag.show(getSupportFragmentManager(),"WebViewModalFragment");
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

    @Override
    public void onWebViewButtonClick() {
        createSearchFragment();
    }

    private void createSearchFragment() {
         SearchFragment searchFragment = new SearchFragment();
         FragmentTransaction searchTransaction = getSupportFragmentManager().beginTransaction();
         searchTransaction.replace(R.id.news_detail_container, searchFragment);
         searchTransaction.addToBackStack(null);
         searchTransaction.commit();
     }

    /**
     * Called when the user Selects the Top Stories action button on the app bar. This updates the
     * ui to display the top stories from the New York Times in the results list and updates the detail
     * container to be empty
     */
    private void displayTopStories() {
        NewsResultsListFragment topStoriesFrag = NewsResultsListFragment.topStoriesInstance(NewsResultsListFragment.TOP_STORIES,
                DEFAULT_TOP_STORIES);
        FragmentTransaction topStoriesTransaction = getSupportFragmentManager().beginTransaction();
        topStoriesTransaction.replace(R.id.news_results_container, topStoriesFrag);
        Fragment detailFrag = getSupportFragmentManager().findFragmentById(R.id.news_detail_container);

        // checks if there is a fragment being displayed in the detail container
        if (detailFrag != null) {
            topStoriesTransaction.remove(detailFrag);
        }
        topStoriesTransaction.addToBackStack(null);
        topStoriesTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                createSearchFragment();
                return true;
            case R.id.action_top_stories:
                displayTopStories();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
