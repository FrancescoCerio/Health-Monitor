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
    public static final int ADD_REPORT_REQUEST = 1;
    public static final int EDIT_REPORT_REQUEST = 2;

    private ReportViewModel reportViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View diaryView = inflater.inflate(R.layout.diary_layout, container, false);

        RecyclerView recyclerView = diaryView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        final ReportAdapter adapter = new ReportAdapter();
        recyclerView.setAdapter(adapter);

        reportViewModel = new ViewModelProvider(this,
                ViewModelProvider
                        .AndroidViewModelFactory
                        .getInstance(getActivity().getApplication()))
                .get(ReportViewModel.class);

        reportViewModel.getAllReport().observe(this, new Observer<List<Report>>() {
            @Override
            public void onChanged(List<Report> reports) {
                // Aggiorna automaticamente il RecyclerView
                adapter.setReports(reports);
            }
        });

        // Gestisco il click di ogni elemento nella main activity in modo da poter modificare i valori inseriti
        adapter.setOnItemClickListener(new ReportAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Report report) {
                Intent intent = new Intent(getContext(), AddEditReportActivity.class);
                intent.putExtra(AddEditReportActivity.EXTRA_ID, report.getId());
                intent.putExtra(AddEditReportActivity.EXTRA_TEMPERATURE, report.getTemperature());
                intent.putExtra(AddEditReportActivity.EXTRA_TEMPERATURE_SLIDER, report.getTPriority());
                intent.putExtra(AddEditReportActivity.EXTRA_BATTITO, report.getCardio());
                intent.putExtra(AddEditReportActivity.EXTRA_BATTITO_SLIDER, report.getBPriority());
                intent.putExtra(AddEditReportActivity.EXTRA_PRESSURE, report.getPressure());
                intent.putExtra(AddEditReportActivity.EXTRA_PRESSURE_SLIDER, report.getPPriority());
                intent.putExtra(AddEditReportActivity.EXTRA_GLICEMIA, report.getGlicemia());
                intent.putExtra(AddEditReportActivity.EXTRA_GLICEMIA_SLIDER, report.getGPriority());
                intent.putExtra(AddEditReportActivity.EXTRA_DATE, report.getDate());
                intent.putExtra(AddEditReportActivity.EXTRA_NOTE, report.getNote());

                startActivityForResult(intent, EDIT_REPORT_REQUEST);
            }
        });

        return diaryView;
    }

    /**
     * Prendo i valori passati da AddReport Activity e li inserisco nel DB
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_REPORT_REQUEST && resultCode == RESULT_OK){
            int tempInt = data.getIntExtra(AddEditReportActivity.EXTRA_TEMPERATURE, 36);
            int battInt = data.getIntExtra(AddEditReportActivity.EXTRA_BATTITO, 60);
            int pressInt = data.getIntExtra(AddEditReportActivity.EXTRA_PRESSURE, 40);
            int glicInt = data.getIntExtra(AddEditReportActivity.EXTRA_GLICEMIA, 60);
            Long date = data.getLongExtra(AddEditReportActivity.EXTRA_DATE, DateConverter.fromDate(Calendar.getInstance().getTime()));
            String note = Objects.requireNonNull(data.getStringExtra(AddEditReportActivity.EXTRA_NOTE));

            float tempSlider = data.getFloatExtra(AddEditReportActivity.EXTRA_TEMPERATURE_SLIDER, 3);
            float battSlider = data.getFloatExtra(AddEditReportActivity.EXTRA_BATTITO_SLIDER, 3);
            float pressSlider = data.getFloatExtra(AddEditReportActivity.EXTRA_PRESSURE_SLIDER, 3);
            float glicSlider = data.getFloatExtra(AddEditReportActivity.EXTRA_GLICEMIA_SLIDER, 3);
            Report report;

            Date dateToStore = DateConverter.toDate(date);

            report = new Report(dateToStore, tempInt, glicInt, pressInt, battInt, tempSlider, pressSlider, glicSlider, battSlider, note);
            reportViewModel.insert(report);

            Toast.makeText(getContext(), "Report salvato!", Toast.LENGTH_SHORT).show();

        } else if(requestCode == EDIT_REPORT_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditReportActivity.EXTRA_ID, -1);
            if(id == -1){
                Toast.makeText(getContext(), "Report non aggiornato", Toast.LENGTH_SHORT).show();
                return;
            }

            int tempInt = data.getIntExtra(AddEditReportActivity.EXTRA_TEMPERATURE, 36);
            int battInt = data.getIntExtra(AddEditReportActivity.EXTRA_BATTITO, 60);
            int pressInt = data.getIntExtra(AddEditReportActivity.EXTRA_PRESSURE, 40);
            int glicInt = data.getIntExtra(AddEditReportActivity.EXTRA_GLICEMIA, 60);
            Long date = data.getLongExtra(AddEditReportActivity.EXTRA_DATE, DateConverter.fromDate(Calendar.getInstance().getTime()));
            String note = Objects.requireNonNull(data.getStringExtra(AddEditReportActivity.EXTRA_NOTE));

            float tempSlider = data.getFloatExtra(AddEditReportActivity.EXTRA_TEMPERATURE_SLIDER, 3);
            float battSlider = data.getFloatExtra(AddEditReportActivity.EXTRA_BATTITO_SLIDER, 3);
            float pressSlider = data.getFloatExtra(AddEditReportActivity.EXTRA_PRESSURE_SLIDER, 3);
            float glicSlider = data.getFloatExtra(AddEditReportActivity.EXTRA_GLICEMIA_SLIDER, 3);
            Report report;

            Date dateToStore = DateConverter.toDate(date);
            report = new Report(dateToStore, tempInt, glicInt, pressInt, battInt, tempSlider, pressSlider, glicSlider, battSlider, note);
            report.setId(id);
            reportViewModel.update(report);

            Toast.makeText(getContext(), "Report aggiornato", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getContext(), "Report non salvato", Toast.LENGTH_SHORT).show();
        }
    }
}
