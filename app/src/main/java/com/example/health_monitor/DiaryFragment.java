package com.example.health_monitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.health_monitor.DB.DateConverter;
import com.example.health_monitor.DB.Report;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;

public class DiaryFragment extends Fragment {
    ReportAdapter adapter = new ReportAdapter();
    private ReportViewModel reportViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View diaryView = inflater.inflate(R.layout.diary_layout, container, false);

        RecyclerView recyclerView = diaryView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);


        recyclerView.setAdapter(adapter);

        reportViewModel = new ViewModelProvider(this,
                ViewModelProvider
                        .AndroidViewModelFactory
                        .getInstance(getActivity().getApplication()))
                .get(ReportViewModel.class);

        reportViewModel.getAllReport().observe(getViewLifecycleOwner(), new Observer<List<Report>>() {
            @Override
            public void onChanged(List<Report> reports) {
                // Aggiorna automaticamente il RecyclerView
                Collections.sort(reports, new Comparator<Report>() {
                    public int compare(Report o1, Report o2) {
                        if (o1.getDate() == null || o2.getDate() == null)
                            return 0;
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });

                adapter.setReports(reports);

                adapter.getItemCount();

            }
        });

        // Gestisco il click di ogni elemento nella main activity in modo da poter modificare i valori inseriti
        adapter.setOnItemClickListener(new ReportAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Report report) {
                Intent intent = new Intent(getContext(), InfoReportActivity.class);
                intent.putExtra(AddEditReportActivity.EXTRA_ID, report.getId());
                intent.putExtra(AddEditReportActivity.EXTRA_TEMPERATURE, report.getTemperature());
                intent.putExtra(AddEditReportActivity.EXTRA_TEMPERATURE_SLIDER, report.getTPriority());
                intent.putExtra(AddEditReportActivity.EXTRA_BATTITO, report.getCardio());
                intent.putExtra(AddEditReportActivity.EXTRA_BATTITO_SLIDER, report.getBPriority());
                intent.putExtra(AddEditReportActivity.EXTRA_PRESSURE, report.getPressure());
                intent.putExtra(AddEditReportActivity.EXTRA_PRESSURE_SLIDER, report.getPPriority());
                intent.putExtra(AddEditReportActivity.EXTRA_GLICEMIA, report.getGlicemia());
                intent.putExtra(AddEditReportActivity.EXTRA_GLICEMIA_SLIDER, report.getGPriority());
                intent.putExtra(AddEditReportActivity.EXTRA_DATE, DateConverter.fromDate(report.getDate()));
                intent.putExtra(AddEditReportActivity.EXTRA_NOTE, report.getNote());

                startActivity(intent);
            }
        });

        return diaryView;
    }

    private void applyFilter(){

    }
}
