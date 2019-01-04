package com.example.android.newsvision.apis;

import com.example.android.newsvision.model.ArticleSearchResponse;
import com.example.android.newsvision.model.TopStoriesResponse;
import com.example.android.newsvision.utils.NewsUtils;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("search/v2/articlesearch.json")
    Call<ArticleSearchResponse> getArticlesBySearchTerm(@Query(NewsUtils.API_KEY_PARAM) String apiKey,
                                                        @Query(NewsUtils.QUERY_PARAM) String searchTerm,
                                                        @Query(NewsUtils.SORT_PARAM) String sortOrder,
                                                        @Query(NewsUtils.ARG_BEGIN_DATE) String beginDate,
                                                        @Query(NewsUtils.ARG_END_DATE) String endDate);
    @GET("topstories/v2/{search_query}")
    Call<TopStoriesResponse> getTopStories(@Path("search_query") String searchTerm,
                                           @Query(NewsUtils.API_KEY_PARAM) String apiKey,
                                           @Query(NewsUtils.SORT_PARAM) String sortOrder);
}
