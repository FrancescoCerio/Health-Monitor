package com.example.health_monitor.welcome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.health_monitor.MainActivity;
import com.example.health_monitor.R;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class Fragment_walkthroughthree extends Fragment {
    MaterialButton startJourney;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View startView = inflater.inflate(R.layout.fragment_walkthroughthree, container, false);

        startJourney = startView.findViewById(R.id.startJourney);
        startJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getContext().getSharedPreferences("com.example.health_monitor", Context.MODE_PRIVATE);
                sp.edit().putBoolean("isFirstTime", false).apply();
                Intent openMain = new Intent(getContext(), MainActivity.class);
                startActivity(openMain);
            }
        });


        return startView;
    }
}