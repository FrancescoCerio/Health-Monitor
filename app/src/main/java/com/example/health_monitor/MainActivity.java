package com.example.health_monitor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.health_monitor.DB.DateConverter;
import com.example.health_monitor.DB.Report;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_REPORT_REQUEST = 1;
    public static final int EDIT_REPORT_REQUEST = 2;

    private ReportViewModel reportViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setActionBarStyle();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final ReportAdapter adapter = new ReportAdapter();
        recyclerView.setAdapter(adapter);

        reportViewModel = new ViewModelProvider(this,
                ViewModelProvider
                        .AndroidViewModelFactory
                        .getInstance(this.getApplication()))
                .get(ReportViewModel.class);

        reportViewModel.getAllReport().observe(this, new Observer<List<Report>>() {
            @Override
            public void onChanged(List<Report> reports) {
                // Aggiorna automaticamente il RecyclerView
                adapter.setReports(reports);
            }
        });

        FloatingActionButton openReport = findViewById(R.id.button_add_note);
        openReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddEditReportActivity.class);
                startActivityForResult(i, ADD_REPORT_REQUEST);
            }
        });

        // Gestisco il click di ogni elemento nella main activity in modo da poter modificare i valori inseriti
        adapter.setOnItemClickListener(new ReportAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Report report) {
                Intent intent = new Intent(MainActivity.this, AddEditReportActivity.class);
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
    }

    /**
     * Setto la UI della toolbar
     */
    private void setActionBarStyle(){
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#57944B"));

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color=\"#f4f4f4\">" + "Health Monitor" + "</font>"));
    }

    /**
     * Prendo i valori passati da AddReport Activity e li inserisco nel DB
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

            Toast.makeText(this, "Report salvato!", Toast.LENGTH_SHORT).show();

        } else if(requestCode == EDIT_REPORT_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditReportActivity.EXTRA_ID, -1);
            if(id == -1){
                Toast.makeText(this, "Report non aggiornato", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(this, "Report aggiornato", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Report non salvato", Toast.LENGTH_SHORT).show();
        }
    }
}