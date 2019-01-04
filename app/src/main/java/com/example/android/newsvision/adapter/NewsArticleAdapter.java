package com.example.android.newsvision.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.newsvision.NewsArticle;
import com.example.android.newsvision.R;
import com.example.android.newsvision.interfaces.onArticleLongClickListener;
import com.example.android.newsvision.interfaces.onArticleSelectedListener;

import java.util.List;

public class NewsArticleAdapter extends RecyclerView.Adapter<NewsArticleAdapter.NewsArticleViewHolder> {
    private static final String TAG = "NewsArticleAdapter";
    private List<NewsArticle> mNewsArticleList;
    private final onArticleLongClickListener mLongListener;
    private final onArticleSelectedListener mArticleSelectedListener;
    private int selectedPosition;

    /**
     * Constructor for empty NewsAdapter
     */
    public NewsArticleAdapter(onArticleLongClickListener mLongListener,
                              onArticleSelectedListener mArticleSelectedListener) {
        this.mLongListener = mLongListener;
        this.mArticleSelectedListener = mArticleSelectedListener;
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
    public NewsArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflates the custom layout
        View contentView = inflater.inflate(R.layout.fragment_news_list_content, parent, false);

        // Returns a new holder instance
        final NewsArticleViewHolder viewHolder = new NewsArticleViewHolder(contentView, mArticleSelectedListener, mLongListener);

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
    public void onBindViewHolder(@NonNull final NewsArticleViewHolder holder, int position) {
        holder.itemView.setSelected(selectedPosition == position);
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

    public NewsArticle getItemAtPosition(int position) {
        if (mNewsArticleList != null && position >= 0) {
            return mNewsArticleList.get(position);
        }
        return null;
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

    public class NewsArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
    View.OnLongClickListener{
        // reference for member views
        public TextView mHeadlineView;
        public TextView mDateView;
        private final onArticleSelectedListener mArticleListener;
        private final onArticleLongClickListener mLongListener;

        public NewsArticleViewHolder(View itemView,
                                     onArticleSelectedListener mArticleListener,
                                     onArticleLongClickListener mLongListener) {
            super(itemView);
            // Gets reference to member views
            mHeadlineView = (TextView) itemView.findViewById(R.id.news_headline_tv);
            mDateView = (TextView) itemView.findViewById(R.id.news_date_tv);
            this.mArticleListener = mArticleListener;
            this.mLongListener = mLongListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mArticleListener.onArticleSelected(getAdapterPosition());

            // Unmark previous selection and update list to highlight the current article that
            // has been selected by the user
            notifyItemChanged(selectedPosition);
            selectedPosition = getLayoutPosition();
            notifyItemChanged(selectedPosition);
        }

        @Override
        public boolean onLongClick(View view) {
            mLongListener.OnArticleLongClick(getAdapterPosition());
            return true;
        }
    }
}
