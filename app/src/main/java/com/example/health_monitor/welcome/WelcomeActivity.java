package com.example.health_monitor.welcome;

import android.os.Bundle;

import com.example.health_monitor.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;

public class WelcomeActivity extends AppCompatActivity {
    public ViewPager viewpager;

    Adapter_walkthrough adapter_walkthrough;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome);

        viewpager = findViewById(R.id.viewpager);

        CircleIndicator indicator = findViewById(R.id.indicator);

        adapter_walkthrough = new Adapter_walkthrough(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        viewpager.setAdapter(adapter_walkthrough);

        indicator.setViewPager(viewpager);

        adapter_walkthrough.registerDataSetObserver(indicator.getDataSetObserver());

        /*
        Button openMain = findViewById(R.id.openMain);


        openMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("com.example.health_monitor", Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("isFirstTime", false).apply();
                Intent openMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(openMain);
            }
        });

         */
    }
}
