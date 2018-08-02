package com.example.android.newsvision;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.android.newsvision.utils.NewsSingleton;

public class PreviewFragment extends Fragment {
    final static String ARG_POSITION = "position";
    final static String ARG_HEAD = "headline";
    final static String ARG_SNIPPET = "snippet";
    final static String ARG_IMAGE_URL = "imageUrl";

    private View rootView;
    private NetworkImageView mArticleNetworkImageView;
    private TextView mHeadlinesView;
    private TextView mArticleSnippetView;
    private Button mFullArticleButton;

    String mArticleHeadline;
    String mArticleSnippet;
    String mImageUrl;

    ImageLoader mImageLoader;

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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Checks to see if arguments were passed into the fragment
        if (savedInstanceState != null) {
            mArticleHeadline = savedInstanceState.getString(ARG_HEAD);
            mArticleSnippet = savedInstanceState.getString(ARG_SNIPPET);
            mImageUrl = savedInstanceState.getString(ARG_IMAGE_URL);
        }
        rootView = inflater.inflate(R.layout.fragment_preview, container, false);

        // Gets references to member views
        mArticleNetworkImageView = (NetworkImageView) rootView.findViewById(R.id.network_article_image_view);
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
        mImageLoader = NewsSingleton.getInstance(context.getApplicationContext()).getImageLoader();

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnFullArticleButtonListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFullArticleButtonListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Checks to see if there have been any arguments passed into the fragment
        Bundle args = getArguments();
        if (args != null) {
            mArticleHeadline = args.getString(ARG_HEAD);
            mArticleSnippet = args.getString(ARG_SNIPPET);
            mImageUrl = args.getString(ARG_IMAGE_URL);
        }
        updatePreview(mArticleHeadline, mArticleSnippet, mImageUrl);
    }

    public void updatePreview(String headline, String snippet, String imageUrl) {
        if (rootView != null) {
            // Sets the text for the article preview
            mHeadlinesView.setText(headline);
            mArticleSnippetView.setText(snippet);

            // Sets the image associated with the article or displays no preview image if none are
            // available
            Context context = getActivity().getApplicationContext();
            mImageLoader.get(imageUrl, ImageLoader.getImageListener(mArticleNetworkImageView,
                    context.getResources().getIdentifier("preview_default", "drawable",
                            context.getPackageName()), context.getResources().getIdentifier("preview_no_image",
                            "drawable", context.getPackageName())));
            mArticleNetworkImageView.setImageUrl(imageUrl, mImageLoader);
        }
        mArticleHeadline = headline;
        mArticleSnippet = snippet;
        mImageUrl = imageUrl;
    }

    /**
     * Saves the current position
     * @param outState bundle that stores information for future states to access
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putString(ARG_HEAD, mArticleHeadline);
        outState.putString(ARG_SNIPPET, mArticleSnippet);
        outState.putString(ARG_IMAGE_URL, mImageUrl);
    }
}
