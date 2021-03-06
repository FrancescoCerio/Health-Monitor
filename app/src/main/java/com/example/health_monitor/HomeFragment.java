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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


public class HomeFragment extends Fragment {
    public static final int ADD_REPORT_REQUEST = 1;

    ConstraintLayout noReportLayout;
    ConstraintLayout mainHomeScreen;

    private ReportViewModel reportViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View homeView = inflater.inflate(R.layout.home_layout, container, false);
        noReportLayout = homeView.findViewById(R.id.no_report_layout);
        mainHomeScreen = homeView.findViewById(R.id.mainHomeScreen);
        noReportLayout.setVisibility(View.GONE);
        mainHomeScreen.setVisibility(View.GONE);

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
                    noReportLayout.setVisibility(View.GONE);
                    mainHomeScreen.setVisibility(View.VISIBLE);
                    lastReport[0] = report;
                    TextView cardTemperatureValue = homeView.findViewById(R.id.card_temperatura_value);
                    TextView cardBattitoValue = homeView.findViewById(R.id.card_battito_value);
                    TextView cardGlicemiaValue = homeView.findViewById(R.id.card_glicemia_value);
                    TextView cardPressioneValue = homeView.findViewById(R.id.card_pressione_value);
                    TextView currentDate = homeView.findViewById(R.id.report_date);

                    cardTemperatureValue.setText(Integer.toString(lastReport[0].getTemperature()));
                    cardBattitoValue.setText(Integer.toString(lastReport[0].getCardio()));
                    cardGlicemiaValue.setText(Integer.toString(lastReport[0].getGlicemia()));
                    cardPressioneValue.setText(Integer.toString(lastReport[0].getPressure()));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd' 'MMMM' 'yyyy", Locale.ITALY);
                    currentDate.setText(dateFormat.format(lastReport[0].getDate()));
                } else {
                    mainHomeScreen.setVisibility(View.GONE);
                    noReportLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        Button trendBtn = homeView.findViewById(R.id.trendBtn);
        trendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), Graph.class);
                startActivity(i);

            }
        });


        return homeView;
    }


}
