package com.example.spaceeva.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.spaceeva.Articles;
import com.example.spaceeva.ArticlesAdapter;
import com.example.spaceeva.R;
import com.example.spaceeva.api.ApiService;
import com.example.spaceeva.api.RetrofitClient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArticlesAdapter articlesAdapter;
    private TextView connectionLostTextView;
    private Button retryButton;
    private LottieAnimationView progressBar;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        articlesAdapter = new ArticlesAdapter(getContext());
        recyclerView.setAdapter(articlesAdapter);

        connectionLostTextView = view.findViewById(R.id.connectionlost);
        retryButton = view.findViewById(R.id.btn_retry);
        progressBar = view.findViewById(R.id.lottieProgressBar);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.playAnimation();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isConnectedToInternet()) {
                fetchArticles();
            } else {
                showConnectionLost();
            }
        }, 2000);

        retryButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.playAnimation();
            retryButton.setVisibility(View.GONE);
            connectionLostTextView.setVisibility(View.GONE);

            executorService.execute(() -> {
                if (isConnectedToInternet()) {
                    getActivity().runOnUiThread(() -> {
                        recyclerView.setVisibility(View.VISIBLE);
                        fetchArticles();
                    });
                } else {
                    getActivity().runOnUiThread(this::handleRetryFailure);
                }
            });
        });
        return view;
    }

    private void fetchArticles() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Articles>> call = apiService.getArticles();
        call.enqueue(new Callback<List<Articles>>() {
            @Override
            public void onResponse(Call<List<Articles>> call, Response<List<Articles>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    List<Articles> articlesList = response.body();
                    if (articlesList != null && !articlesList.isEmpty()) {
                        articlesAdapter.setArticles(articlesList); // Set articles to adapter
                        Log.d("ArticleFragment", "Articles fetched: " + articlesList.size());
                    } else {
                        Toast.makeText(getContext(), "No articles found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch articles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Articles>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleRetryFailure() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            retryButton.setVisibility(View.VISIBLE);
            connectionLostTextView.setVisibility(View.VISIBLE);
        }, 500); // Set to 0.5 seconds as requested
    }

    private void showConnectionLost() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        connectionLostTextView.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
