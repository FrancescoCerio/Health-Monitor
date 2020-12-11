package com.example.health_monitor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.health_monitor.DB.DateConverter;
import com.example.health_monitor.DB.Report;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {
    public static final int ADD_REPORT_REQUEST = 1;

    private ReportViewModel reportViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View homeView = inflater.inflate(R.layout.home_layout, container, false);
        final Report[] lastReport = new Report[1];

        reportViewModel = new ViewModelProvider(this,
                ViewModelProvider
                        .AndroidViewModelFactory
                        .getInstance(getActivity().getApplication()))
                .get(ReportViewModel.class);

        reportViewModel.getLastReport().observe(getViewLifecycleOwner(), new Observer<Report>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(Report report) {
                if(report != null){
                    lastReport[0] = report;
                    TextView cardTemperatureValue = homeView.findViewById(R.id.card_temperatura_value);
                    TextView cardBattitoValue = homeView.findViewById(R.id.card_battito_value);
                    TextView cardGlicemiaValue = homeView.findViewById(R.id.card_glicemia_value);
                    TextView cardPressioneValue = homeView.findViewById(R.id.card_pressione_value);

                    cardTemperatureValue.setText(Integer.toString(lastReport[0].getTemperature()));
                    cardBattitoValue.setText(Integer.toString(lastReport[0].getCardio()));
                    cardGlicemiaValue.setText(Integer.toString(lastReport[0].getGlicemia()));
                    cardPressioneValue.setText(Integer.toString(lastReport[0].getPressure()));
                }
            }
        });

        Button testBtn = homeView.findViewById(R.id.trendBtn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), Graph.class);
                startActivity(i);

            }
        });


        return homeView;
    }


}
