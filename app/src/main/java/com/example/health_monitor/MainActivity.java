package com.example.health_monitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_REPORT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setActionBarStyle(true);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                new HomeFragment()).commit();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch(item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            setActionBarStyle(true);
                            break;

                        case R.id.nav_diary:
                            selectedFragment = new DiaryFragment();
                            setActionBarStyle(false);
                            break;

                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, selectedFragment).commit();

                    return true;
                }
            };

    /**
     * Setto la UI della actionBar
     */
    private void setActionBarStyle(boolean isHome){
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#57944B"));

        // Set BackgroundDrawable
        //actionBar.setBackgroundDrawable(colorDrawable);
        if(isHome){
            actionBar.setTitle(Html.fromHtml("<font color=\"#f4f4f4\">" + "Health Monitor" + "</font>"));
        } else {
            actionBar.setTitle(Html.fromHtml("<font color=\"#f4f4f4\">" + "Diario dei report" + "</font>"));
        }

    }


}