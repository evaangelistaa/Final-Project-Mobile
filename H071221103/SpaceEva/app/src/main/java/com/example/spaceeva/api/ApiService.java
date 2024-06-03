package com.example.spaceeva.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import com.example.spaceeva.Articles;

public interface ApiService {
    @GET("articles")
    Call<List<Articles>> getArticles();
}