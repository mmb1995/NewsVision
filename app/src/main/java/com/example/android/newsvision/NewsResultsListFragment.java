package com.example.android.newsvision;

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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.newsvision.utils.NewsSingleton;
import com.example.android.newsvision.utils.NewsUtils;

import org.json.JSONObject;

import java.util.List;



public class NewsResultsListFragment extends Fragment {
    OnArticleSelectedListener mCallback;
    OnArticleLongClickListener mLongCallback;

    NewsAdapter adapter;
    List<NewsArticle> mNewsList;
    LinearLayoutManager mLayoutManager;

    private static final String TAG = "NewsResultsListFragment";

    private static final String ARG_TYPE = "type";
    private static final String ARG_QUERY = "query";
    private static final String ARG_BEGIN_DATE = "begin date";
    private static final String ARG_END_DATE = "end date";
    public static final String TOP_STORIES = "Top Stories";
    public static final String SEARCH_RESULTS = "Search Results";

    // Stores what type of ResultsList should be displayed
    public String mInstance;

    // Stores the type of data to display
    private String mQuery;

    // date variables
    private String mBeginDate;
    private String mEndDate;

    /**
     * This interface must be implemented by the fragments parent activity to handle click
     * events when the user selects an article to view
     */
    public interface OnArticleSelectedListener {
        // Called when the user selects a list item from the NewsArticle results list
        public void onArticleSelected(NewsArticle article);
    }

    /**
     * This interface must be implemented by the fragments parent activity to handle
     * long click events
     */
    public interface OnArticleLongClickListener {
        public void OnArticleLongClick(NewsArticle article);
    }

    /**
     * Creates a new instance of a NewsResultsListFragment
     * @param type an argument specifying the type of results list to display
     * @query an argument specifying the category of articles to search for
     * @return an instance of a NewsResultsListFragment
     */
    public static NewsResultsListFragment topStoriesInstance(String type, String query) {
        NewsResultsListFragment fragment = new NewsResultsListFragment();

        // An empty bundle for the arguments the passed in arguments
        Bundle args = new Bundle();

        args.putString(ARG_TYPE, type);
        args.putString(ARG_QUERY, query);
        fragment.setArguments(args);

        // Returns the Fragment
        return fragment;
    }

    public static NewsResultsListFragment searchResultsInstance(String type, String query, String beginDate,
                                                                String endDate) {
        NewsResultsListFragment newsFrag = new NewsResultsListFragment();

        // An empty bundle for the arguments passed in to the instance
        Bundle args = new Bundle();

        args.putString(ARG_TYPE, type);
        args.putString(ARG_QUERY, query);
        args.putString(ARG_BEGIN_DATE, beginDate);
        args.putString(ARG_END_DATE, endDate);
        newsFrag.setArguments(args);

        return newsFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the type of list to display and the data to be queried
        Bundle args = getArguments();
        mInstance = args.getString(ARG_TYPE);
        mQuery = args.getString(ARG_QUERY);

        if (mInstance.equals(SEARCH_RESULTS)) {
            mBeginDate = args.getString(ARG_BEGIN_DATE);
            mEndDate = args.getString(ARG_END_DATE);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interfaces. If not, it throws an exception.
        try {
            mCallback = (OnArticleSelectedListener) context;
            mLongCallback = (OnArticleLongClickListener) context;
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

        newsArticleRv.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getActivity());
        Log.d("debugMode", "The application stopped after this");
        newsArticleRv.setLayoutManager(mLayoutManager);

        // Gets context of parent Activity
        Context context = getActivity().getApplicationContext();

        // Creates new NewsAdapter
        adapter = new NewsAdapter();

        // Attaches the adapter to the RecyclerView to populate items with news data
        newsArticleRv.setAdapter(adapter);

        // Get network response from New York Times
        if (mInstance.equals(TOP_STORIES)) {
            Log.d(TAG, "Getting Top Stories");
            getTopStoriesFromNyt(mQuery, mInstance, getActivity().getApplicationContext());
            //mNewsList = NytSearchHelper.getNewsArticleList();
            if (mNewsList != null) {
                Log.d(TAG, "obtained results from network request");
            } else {
                Log.d(TAG,"Failed to get network results");
            }

        } else {
            Log.d(TAG, "Getting Search Results");
            getArticleSearchFromNyt(mQuery, mBeginDate, mEndDate, mInstance, context);
        }

        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void downloadNytResponse(String nytUrl, final String type, Context context) {
        // Performs network request to NYT API
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, nytUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        List<NewsArticle> resultsList;
                        Log.d(TAG, "API response: " + response.toString());
                        if (type.equals(TOP_STORIES)) {
                            resultsList = NewsArticle.parseNYTTopStories(response);
                            Log.d(TAG,resultsList.toString());
                        } else {
                            resultsList = NewsArticle.parseNYTSearch(response);
                        }

                        if (resultsList != null) {
                            Log.d(TAG,"Obtained Network Results Sending to adapter");
                            setNewsList(resultsList);
                            adapter.setNewsList(resultsList);
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

    public void getTopStoriesFromNyt(String searchTerm, String type, Context context) {
        String nytSearchUrl = NewsUtils.buildNytTopStoriesUrl(searchTerm);
        Log.d(TAG,"Given url: " +  nytSearchUrl);
        downloadNytResponse(nytSearchUrl,type, context);

    }

    public void getArticleSearchFromNyt(String searchTerm, String beginDate, String endDate,
                                               String type, Context context) {
        String nytArticleSearchUrl = NewsUtils.buildNytSearchResultsUrl(searchTerm, beginDate, endDate);
        Log.d(TAG,"Given url: " + nytArticleSearchUrl);
        downloadNytResponse(nytArticleSearchUrl, type, context);

    }

    private void setNewsList(List<NewsArticle> newsList) {
        this.mNewsList = newsList;
    }

    public List<NewsArticle> getNewsList() {
        return mNewsList;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_TYPE, mInstance);
    }

    public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
        private List<NewsArticle> mNewsArticleList;

        /**
         * Constructor for empty NewsAdapter
         */
        public NewsAdapter() {}

        public NewsAdapter(List<NewsArticle> newsList) {
            mNewsArticleList = newsList;
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            // reference for member views
            public TextView mHeadlineView;
            public TextView mDateView;

            public ViewHolder(View itemView) {
                super(itemView);

                // Gets reference to member views
                mHeadlineView = (TextView) itemView.findViewById(R.id.news_headline_tv);
                mDateView = (TextView) itemView.findViewById(R.id.news_date_tv);
            }
        }

        /**
         * This gets called when each new ViewHolder is created. This happens when the RecyclerView
         * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
         *
         * @param parent The ViewGroup that these ViewHolders are contained within.
         * @param viewType  If your RecyclerView has more than one type of item (like ours does) you
         *                  can use this viewType integer to provide a different layout. See
         *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
         *                  for more details.
         * @return A new WeatherAdapter.ViewHolder that holds the View for each list item
         */
        @Override
        public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflates the custom layout
            View contentView = inflater.inflate(R.layout.fragment_news_list_content, parent, false);

            // Returns a new holder instance
            final ViewHolder viewHolder = new ViewHolder(contentView);

            // Sets up onClickListener
            contentView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mCallback.onArticleSelected(mNewsArticleList.get(viewHolder.getAdapterPosition()));
                }
            });

            // Sets up onLongClickListener
            contentView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mLongCallback.OnArticleLongClick(mNewsArticleList.get(viewHolder.getAdapterPosition()));
                    return true;
                }
            });
            return viewHolder;
        }


        /**
         * OnBindViewHolder is called by the RecyclerView to display the data at the specified
         * position. In this method, the contents of the ViewHolder are updated to display the weather
         * details for this particular position, using the "position" argument that is passed in.
         *
         * @param holder The ViewHolder which should be updated to represent the
         * contents of the item at the given position in the data set.
         * @param position  The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(NewsAdapter.ViewHolder holder, int position) {
            // Gets NewsArticle based off its position
            NewsArticle mNewsArticleItem = mNewsArticleList.get(position);

            // Sets Headline to be displayed
            TextView headlineView = holder.mHeadlineView;
            headlineView.setText(mNewsArticleItem.headline);
            headlineView.setVisibility(View.VISIBLE);

            // Sets Date to be displayed
            TextView dateView = holder.mDateView;
            dateView.setText(mNewsArticleItem.date);
            dateView.setVisibility(View.VISIBLE);
        }

        /**
         * Sets the news list and notifies the RecyclerView that the data set has changed
         * @param newsList a list of NewsArticle objects
         */
        public void setNewsList(List<NewsArticle> newsList) {
            Log.d(TAG, "Updating results list");
            mNewsArticleList = newsList;
            notifyDataSetChanged();
        }

        /**
         * Tells the recycler view the number of NewsArticles stored in mNewsarticleList
         * @return size of news article list
         */
        @Override
        public int getItemCount() {
            if (mNewsArticleList == null) {
                return 0;
            }
            return mNewsArticleList.size();
        }
    }

}
