package com.example.spaceeva.api;

import com.example.spaceeva.Articles;

import java.util.List;
public class ArticlesResponse {
    private List<Articles> data;
    public List<Articles> getData() {
        return data;
    }
    public void setData(List<Articles> data) {
        this.data = data;
    }
}
