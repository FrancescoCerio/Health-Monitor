package com.example.health_monitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.health_monitor.DB.DateConverter;
import com.example.health_monitor.DB.Report;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;

public class DiaryFragment extends Fragment {
    ReportAdapter adapter = new ReportAdapter();
    private ReportViewModel reportViewModel;
    private Chip importanceChip;
    ImageButton filterButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View diaryView = inflater.inflate(R.layout.diary_layout, container, false);

        RecyclerView recyclerView = diaryView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        final View checkBoxView = View.inflate(getContext(), R.layout.filter_checkbox, null);
        final int[] filterValue = new int[1];
        importanceChip = diaryView.findViewById(R.id.importanceChip);

        RadioGroup radioGroup = checkBoxView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId){
                    case R.id.radioButton1:
                        filterValue[0] = 1;
                        Log.d("RADIO 2", String.valueOf(filterValue[0]));
                        break;
                    case R.id.radioButton2:
                        filterValue[0] = 2;
                        Log.d("RADIO 2", String.valueOf(filterValue[0]));
                        break;
                    case R.id.radioButton3:
                        filterValue[0] = 3;
                        Log.d("RADIO 3", String.valueOf(filterValue[0]));
                        break;
                    case R.id.radioButton4:
                        filterValue[0] = 4;
                        Log.d("RADIO 4", String.valueOf(filterValue[0]));
                        break;
                    case R.id.radioButton5:
                        filterValue[0] = 5;
                        Log.d("RADIO 5", String.valueOf(filterValue[0]));
                        break;

                    default:
                        break;
                }
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Filtro di ricerca");
        builder.setMessage("Applica un filtro di ricerca in base all'importanza dei report")
                .setView(checkBoxView)
                .setCancelable(false)
                .setPositiveButton("Applica", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((ViewGroup)checkBoxView.getParent()).removeView(checkBoxView);
                        try {
                            applyFilter(filterValue[0]);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((ViewGroup)checkBoxView.getParent()).removeView(checkBoxView);
                        dialog.cancel();
                    }
                });

        filterButton = diaryView.findViewById(R.id.filterButton);


        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
                //Toast.makeText(getContext(), "icsadcion", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);

        reportViewModel = new ViewModelProvider(this,
                ViewModelProvider
                        .AndroidViewModelFactory
                        .getInstance(getActivity().getApplication()))
                .get(ReportViewModel.class);

        setAllReports();

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

        importanceChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importanceChip.setVisibility(View.GONE);
                setAllReports();
            }
        });

        return diaryView;
    }

    private void setAllReports(){
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
    }

    private void applyFilter(int importance) throws ExecutionException, InterruptedException {
        String chipText = "Importanza " + importance;
        importanceChip.setText(chipText);
        importanceChip.setVisibility(View.VISIBLE);
        reportViewModel.getReportWithImportance(importance).observe(getViewLifecycleOwner(), new Observer<List<Report>>() {
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
            }
        });
    }
}
