package com.example.health_monitor;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

public class InfoReportActivity extends AppCompatActivity {

    private ReportViewModel reportViewModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_report_layout);
        setActionBarStyle();

        reportViewModel = new ViewModelProvider(this,
                ViewModelProvider
                        .AndroidViewModelFactory
                        .getInstance(this.getApplication()))
                .get(ReportViewModel.class);

        final Intent i = getIntent();

        TextView cardTemperatureValue = findViewById(R.id.card_temperatura_value_last_report);
        TextView cardBattitoValue = findViewById(R.id.card_battito_value_last_report);
        TextView cardGlicemiaValue = findViewById(R.id.card_glicemia_value_last_report);
        TextView cardPressioneValue = findViewById(R.id.card_pressione_value_last_report);
        TextView cardNote = findViewById(R.id.card_note_last_report);
        TextView currentDate = findViewById(R.id.last_report_date);

        cardTemperatureValue.setText(String.valueOf(i.getIntExtra(AddEditReportActivity.EXTRA_TEMPERATURE, 36)));
        cardBattitoValue.setText(String.valueOf(i.getIntExtra(AddEditReportActivity.EXTRA_BATTITO, 60)));
        cardGlicemiaValue.setText(String.valueOf(i.getIntExtra(AddEditReportActivity.EXTRA_GLICEMIA, 60)));
        cardPressioneValue.setText(String.valueOf(i.getIntExtra(AddEditReportActivity.EXTRA_PRESSURE, 36)));
        cardNote.setText(String.valueOf(i.getStringExtra(AddEditReportActivity.EXTRA_NOTE)));
        Long dateLong = i.getLongExtra(AddEditReportActivity.EXTRA_DATE, DateConverter.fromDate(Calendar.getInstance().getTime()));
        Date date = DateConverter.toDate(dateLong);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd' 'MMMM' 'yyyy", Locale.ITALY);
        currentDate.setText(String.valueOf(dateFormat.format(date.getTime())));

        // Open edit report activity
        FloatingActionButton editReport = findViewById(R.id.editReportBtn);
        editReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddEditReportActivity.class);
                intent.putExtras(i.getExtras());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivityForResult(intent, MainActivity.EDIT_REPORT_REQUEST);
            }
        });
    }

    private void setActionBarStyle(){
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));

        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back_white);

        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#197ACF"));

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);
        //getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorAccent));

        actionBar.setTitle(Html.fromHtml("<font color=\"#f4f4f4\">" + "Dettagli" + "</font>"));



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
        if(requestCode == MainActivity.EDIT_REPORT_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditReportActivity.EXTRA_ID, -1);
            if(id == -1){
                Toast.makeText(getApplicationContext(), "Report non aggiornato", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(getApplicationContext(), "Report aggiornato", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(), "Report non salvato!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
