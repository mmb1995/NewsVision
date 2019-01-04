package com.example.android.newsvision.interfaces;

import com.example.android.newsvision.NewsArticle;

public interface ArticleClickListener {
    public void onArticleClicked(NewsArticle article);
    public void onArticleLongClick(NewsArticle article);
}
