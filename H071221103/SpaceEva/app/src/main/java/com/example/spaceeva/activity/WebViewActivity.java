package com.example.spaceeva.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.spaceeva.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebViewActivity extends AppCompatActivity {
    Button btnretry;
    TextView textConnectionLost;
    LottieAnimationView lottieProgressBar;
    WebView webView;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void showConnectionLost() {
        runOnUiThread(() -> {
            webView.setVisibility(View.GONE);
            lottieProgressBar.setVisibility(View.GONE);
            textConnectionLost.setVisibility(View.VISIBLE);
            btnretry.setVisibility(View.VISIBLE);
        });
    }

    private void handleRetryFailure() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            runOnUiThread(() -> {
                lottieProgressBar.setVisibility(View.GONE);
                btnretry.setVisibility(View.VISIBLE);
                textConnectionLost.setVisibility(View.VISIBLE);
            });
        }, 500);
    }


    private void showWeb() {
        String url = getIntent().getStringExtra("url");
        if (url != null) {
            runOnUiThread(() -> {
                lottieProgressBar.setVisibility(View.VISIBLE);
                lottieProgressBar.playAnimation();
                webView.setVisibility(View.GONE);
            });

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                runOnUiThread(() -> {
                    webView.loadUrl(url);
                    webView.setVisibility(View.VISIBLE);
                    lottieProgressBar.setVisibility(View.GONE);
                });
            }, 2000);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        textConnectionLost = findViewById(R.id.connectionlost);
        btnretry = findViewById(R.id.btn_retry);
        lottieProgressBar = findViewById(R.id.lottieProgressBar);
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());

        lottieProgressBar.setVisibility(View.VISIBLE);
        lottieProgressBar.playAnimation();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isNetworkAvailable()) {
                showWeb();
            } else {
                showConnectionLost();
            }
        }, 2000);

        btnretry.setOnClickListener(v -> {
            lottieProgressBar.setVisibility(View.VISIBLE);
            lottieProgressBar.playAnimation();
            btnretry.setVisibility(View.GONE);
            textConnectionLost.setVisibility(View.GONE);

            executorService.execute(() -> {
                if (isNetworkAvailable()) {
                    showWeb();
                } else {
                    handleRetryFailure();
                }
            });
        });
    }
}
