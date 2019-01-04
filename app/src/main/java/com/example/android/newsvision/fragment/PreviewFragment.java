package com.example.android.newsvision.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.newsvision.NewsArticle;
import com.example.android.newsvision.R;
import com.example.android.newsvision.viewmodel.SelectedArticleViewModel;
import com.squareup.picasso.Picasso;

public class PreviewFragment extends Fragment {
    final static String ARG_POSITION = "position";
    final static String ARG_HEAD = "headline";
    final static String ARG_SNIPPET = "snippet";
    final static String ARG_IMAGE_URL = "imageUrl";

    private View rootView;
    private ImageView mArticleImageView;
    private TextView mHeadlinesView;
    private TextView mArticleSnippetView;
    private Button mFullArticleButton;

    private OnFullArticleButtonListener mCallback;

    public interface OnFullArticleButtonListener {
        public void onFullArticleButtonClicked();
    }

    public PreviewFragment() {}

    public static PreviewFragment newInstance(String headline, String snippet, String imageUrl) {
        PreviewFragment prevFragment = new PreviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HEAD, headline);
        args.putString(ARG_SNIPPET, snippet);
        args.putString(ARG_IMAGE_URL, imageUrl);
        prevFragment.setArguments(args);
        return prevFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SelectedArticleViewModel mSharedViewModel = ViewModelProviders.of(getActivity()).get(SelectedArticleViewModel.class);
        mSharedViewModel.getSelectedArticle().observe(this, new Observer<NewsArticle>() {
            @Override
            public void onChanged(@Nullable NewsArticle article) {
                if (article != null) {
                    updatePreview(article);
                }
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_preview, container, false);

        // Gets references to member views
        mArticleImageView = (ImageView) rootView.findViewById(R.id.article_image_view);
        mHeadlinesView = (TextView) rootView.findViewById(R.id.headline_view);
        mArticleSnippetView = (TextView) rootView.findViewById(R.id.snippet_view);

        // Sets up onClickListener for the full article button
        mFullArticleButton = (Button) rootView.findViewById(R.id.full_article_button);
        mFullArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onFullArticleButtonClicked();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnFullArticleButtonListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFullArticleButtonListener");
        }
    }

    /**
     * Upadates the preview to display the currently selected article
     * @param article the currently article selected by the user
     */
    public void updatePreview(NewsArticle article) {
        if (rootView != null) {
            // Sets the text for the article preview
            mHeadlinesView.setText(article.headline);
            mArticleSnippetView.setText(article.snippet);

            // Sets the image associated with the article or displays no preview image if none are
            // available
            if (article.imageUrl == null || article.imageUrl.equals("")) {
                Picasso.get()
                        .load(R.drawable.preview_default)
                        .into(mArticleImageView);
            } else {
                Picasso.get()
                        .load(article.imageUrl)
                        .error(R.drawable.preview_no_image)
                        .placeholder(R.drawable.preview_default)
                        .into(mArticleImageView);
            }
        }

    }

}
