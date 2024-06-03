package com.example.spaceeva.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.spaceeva.DBHelper;
import com.example.spaceeva.R;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    Button btn_login, btn_register, btnRetry;
    EditText et_user, et_password;
    DBHelper dbHelper;
    TextView textConnectionLost;
    LottieAnimationView lottieProgressBar;
    LinearLayout login;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);

        et_user = findViewById(R.id.et_user);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        textConnectionLost = findViewById(R.id.connectionlost);
        btnRetry = findViewById(R.id.btn_retry);
        lottieProgressBar = findViewById(R.id.lottieProgressBar);
        login = findViewById(R.id.login);

        lottieProgressBar.setVisibility(View.GONE);
        textConnectionLost.setVisibility(View.GONE);
        btnRetry.setVisibility(View.GONE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        checkLoginStatus();

        setupButtonListeners(executor);

        if (!isNetworkAvailable()) {
            showConnectionLost();
        } else {
            showMainContent();
        }
    }

    private void setupButtonListeners(ExecutorService executor) {
        btnRetry.setOnClickListener(v -> retryNetworkCheck(executor));

        btn_register.setOnClickListener(view -> showProgressAndCheckNetwork(executor, () -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }));

        btn_login.setOnClickListener(view -> {
            String user = et_user.getText().toString().trim();
            String password = et_password.getText().toString().trim();

            if (user.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            showProgressAndCheckNetwork(executor, () -> {
                boolean isValid = isValidLogin(user, password);
                if (isValid) {
                    saveLoginStatus(user);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    runOnUiThread(this::handleRetryFailure);
                }
            });
        });
    }

    private void showProgressAndCheckNetwork(ExecutorService executor, Runnable onSuccess) {
        lottieProgressBar.setVisibility(View.VISIBLE);
        lottieProgressBar.playAnimation();
        login.setVisibility(View.GONE);

        executor.execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                lottieProgressBar.setVisibility(View.GONE);
                if (isNetworkAvailable()) {
                    onSuccess.run();
                } else {
                    showConnectionLost();
                }
            });
        });
    }

    private void showConnectionLost() {
        lottieProgressBar.setVisibility(View.GONE);
        btnRetry.setVisibility(View.VISIBLE);
        textConnectionLost.setVisibility(View.VISIBLE);
        login.setVisibility(View.GONE);
    }

    private void retryNetworkCheck(ExecutorService executor) {
        lottieProgressBar.setVisibility(View.VISIBLE);
        lottieProgressBar.playAnimation();
        btnRetry.setVisibility(View.GONE);
        textConnectionLost.setVisibility(View.GONE);

        executor.execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                lottieProgressBar.setVisibility(View.GONE);
                if (isNetworkAvailable()) {
                    showMainContent();
                } else {
                    showConnectionLost();
                }
            });
        });
    }

    private void showMainContent() {
        btnRetry.setVisibility(View.GONE);
        textConnectionLost.setVisibility(View.GONE);
        lottieProgressBar.setVisibility(View.GONE);
        login.setVisibility(View.VISIBLE);
    }
    private void handleRetryFailure() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            lottieProgressBar.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            Toast.makeText(LoginActivity.this, "Masukkan Akun Yang terdaftar", Toast.LENGTH_SHORT).show();
        }, 300);
    }

    @Override
    public void onBackPressed() {
        // Handle back press logic if needed
        super.onBackPressed();
    }

    private void checkLoginStatus() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.UserEntry.TABLE_NAME + " WHERE " + DBHelper.UserEntry.COLUMN_NAME_LOGGED_IN + " = 1", null);
        if (cursor.getCount() > 0) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        cursor.close();
    }

    private boolean isValidLogin(String user, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.UserEntry.TABLE_NAME + " WHERE " + DBHelper.UserEntry.COLUMN_NAME_USERNAME + " = ? AND " + DBHelper.UserEntry.COLUMN_NAME_PASSWORD + " = ?", new String[]{user, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    private void saveLoginStatus(String user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.UserEntry.COLUMN_NAME_LOGGED_IN, 1);
        db.update(DBHelper.UserEntry.TABLE_NAME, values, DBHelper.UserEntry.COLUMN_NAME_USERNAME + " = ?", new String[]{user});
    }
}

