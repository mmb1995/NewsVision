package com.example.android.newsvision.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.android.newsvision.R;

public class WebViewFragment extends Fragment {
    private final static String ARG_URL = "url";
    private final static String TAG = "WebViewFragment";

    // Stores the url of the webpage to be displayed
    private String currentUrl;

    // Stores a reference to the WebView
    WebView nytWebView;

    /**
     *  Creates a new WebViewFragment
     */
    public WebViewFragment() {}

    /**
     * Creates a new instance of a WebViewFragment and stores the given url which represents
     * a web page for the WebViewFragment to display
     * @param url the url of the web page to be displayed
     * @return WebViewFragment
     */
    public static WebViewFragment newInstance(String url) {
        WebViewFragment webFrag = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        webFrag.setArguments(args);
        return webFrag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // gets url passed in from the parent activity
        Bundle args = getArguments();
        currentUrl = args.getString(ARG_URL);

        View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
        Log.d(TAG, "onCreateView");

        // Gets reference to WebView and configures it
        nytWebView = (WebView) rootView.findViewById(R.id.nyt_webview);
        nytWebView.getSettings().setLoadsImagesAutomatically(true);
        nytWebView.setWebViewClient(new NewsWebViewClient());

        // laods the page specified by the current url
        nytWebView.loadUrl(currentUrl);

        // Returns the inflated view
        return rootView;
    }

    /**
     * Returns if there are previous webpages for the WebView to display
     * @return a boolean indicating if there are any webpages in the backstack
     */
     public boolean canGoBack() {
        return nytWebView.canGoBack();
     }

    /**
     * Goes back to the previous webpage in the backstack
     */
    public void goBack() {
         nytWebView.goBack();
     }

    /**
     * Saves the current url of the webpage to be displayed
     * @param outState bundle that stores information for future states to access
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (currentUrl != null) {
            // Save the current article selection in case we need to recreate the fragment
            outState.putString(ARG_URL, currentUrl);
        }
    }

    private class NewsWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            // load pages inside of the webview instead of in another application
            webView.loadUrl(url);
            return true;
        }
    }
}
