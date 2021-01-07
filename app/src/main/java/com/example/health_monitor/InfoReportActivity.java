package com.example.health_monitor;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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
import java.util.concurrent.ExecutionException;

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

    @Override
    protected void onResume() {
        super.onResume();
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
        finish();
    }


}
