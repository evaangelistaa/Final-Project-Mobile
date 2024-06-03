package com.example.spaceeva.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.spaceeva.fragment.ArticleFragment;
import com.example.spaceeva.fragment.ProfileFragment;
import com.example.spaceeva.R;


public class MainActivity extends AppCompatActivity {
    ImageView iv_home,iv_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_home = findViewById(R.id.IV_Home);
        iv_profile = findViewById(R.id.IV_Logout);

        FragmentManager fragmentManager = getSupportFragmentManager();
        ArticleFragment articleFragment = new ArticleFragment();
        Fragment fragment = fragmentManager.findFragmentByTag(ArticleFragment.class.getSimpleName());

        if (!(fragment instanceof ArticleFragment)){
            fragmentManager
                    .beginTransaction()
                    .add(R.id.frame_container, articleFragment)
                    .commit();
        }
        iv_home.setOnClickListener(v -> {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_container,articleFragment)
                    .addToBackStack(null)
                    .commit();
        });
        iv_profile.setOnClickListener(v -> {
            ProfileFragment profileFragment = new ProfileFragment();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_container, profileFragment)
                    .addToBackStack(null)
                    .commit();
        });


    }

}