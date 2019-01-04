package com.example.android.newsvision.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.newsvision.NewsArticle;
import com.example.android.newsvision.R;
import com.example.android.newsvision.adapter.NewsArticleAdapter;
import com.example.android.newsvision.interfaces.ArticleClickListener;
import com.example.android.newsvision.interfaces.onArticleLongClickListener;
import com.example.android.newsvision.interfaces.onArticleSelectedListener;
import com.example.android.newsvision.viewmodel.NewsArticleViewModel;
import com.example.android.newsvision.viewmodel.SelectedArticleViewModel;

import java.util.List;



public class NewsResultsListFragment extends Fragment implements onArticleSelectedListener, onArticleLongClickListener {
    ArticleClickListener mCallback;

    NewsArticleAdapter adapter;
    List<NewsArticle> mNewsList;
    LinearLayoutManager mLayoutManager;

    private static final String TAG = "NewsResultsListFragment";

    public static final String ARG_TYPE = "type";
    public static final String ARG_QUERY = "query";
    public static final String ARG_BEGIN_DATE = "begin date";
    public static final String ARG_END_DATE = "end date";
    public static final String TOP_STORIES = "Top Stories";
    public static final String SEARCH_RESULTS = "Search Results";

    // ViewModel shared by multiple fragments for keeping track of the currently selected NewsArticle
    SelectedArticleViewModel mSharedViewModel;

    // Stores what type of ResultsList should be displayed
    public String mInstance;

    // Stores the type of data to display
    private String mQuery;

    // date variables
    private String mBeginDate;
    private String mEndDate;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the type of list to display and the data to be queried
        Bundle args = getArguments();
        mInstance = args.getString(ARG_TYPE);
        mQuery = args.getString(ARG_QUERY);
        Log.i(TAG, "q = " + mQuery);
        Log.i(TAG, "type = " + mInstance);

        // get a reference to the shared ViewModel
        mSharedViewModel = ViewModelProviders.of(getActivity()).get(SelectedArticleViewModel.class);

        if (mInstance.equals(SEARCH_RESULTS)) {
            mBeginDate = args.getString(ARG_BEGIN_DATE);
            mEndDate = args.getString(ARG_END_DATE);
        }
        // Creates new NewsAdapter
        adapter = new NewsArticleAdapter(this, this);
        getArticles();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interfaces. If not, it throws an exception.
        try {
            mCallback = (ArticleClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news_results_list, container, false);

        RecyclerView newsArticleRv = (RecyclerView) rootView.findViewById(R.id.news_list);

        TextView listNameTv = (TextView) rootView.findViewById(R.id.list_name_tv);
        listNameTv.setText(mInstance);

        newsArticleRv.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getActivity());
        Log.d("debugMode", "The application stopped after this");
        newsArticleRv.setLayoutManager(mLayoutManager);

        // Attaches the adapter to the RecyclerView to populate items with news data
        newsArticleRv.setAdapter(adapter);

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onArticleSelected(int position) {
        NewsArticle selectedArticle = adapter.getItemAtPosition(position);
        mSharedViewModel.selectArticle(selectedArticle);
        mCallback.onArticleClicked(selectedArticle);

    }

    @Override
    public void OnArticleLongClick(int position) {
        NewsArticle selectedArticle = adapter.getItemAtPosition(position);
        mSharedViewModel.selectArticle(selectedArticle);
        mCallback.onArticleLongClick(selectedArticle);
    }

    /**
     * Connects fragment to the ViewModel and sets up an observer to listen for a response from
     * the new york times
     */
    private void getArticles() {
        NewsArticleViewModel mNewsViewModel = ViewModelProviders.of(this).get(NewsArticleViewModel.class);
        if (this.mInstance.equals(SEARCH_RESULTS)) {
            Log.i(TAG, "Getting article search");
            mNewsViewModel.getArticleSearchResults(mQuery, mBeginDate, mEndDate).observe(this,
                    new Observer<List<NewsArticle>>() {
                @Override
                public void onChanged(@Nullable List<NewsArticle> newsArticles) {
                    if (newsArticles != null) {
                        Log.i(TAG, "returning article search");
                        adapter.setNewsList(newsArticles);
                    }
                }
            });
        } else {
            Log.i(TAG, "Getting top stories from repo");
            mNewsViewModel.getTopStories(mQuery).observe(this, new Observer<List<NewsArticle>>() {
                @Override
                public void onChanged(@Nullable List<NewsArticle> newsArticles) {
                    if (newsArticles != null) {
                        Log.i(TAG, "returning top stories");
                        adapter.setNewsList(newsArticles);
                    }
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_TYPE, mInstance);
    }

}
