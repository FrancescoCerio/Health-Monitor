package com.example.health_monitor;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.health_monitor.DB.DateConverter;
import com.example.health_monitor.DB.Report;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

public class AddEditReportActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public static final String EXTRA_ID =
            "com.example.health_monitor.EXTRA_ID";
    public static final String EXTRA_TEMPERATURE =
            "com.example.health_monitor.EXTRA_TEMPERATURE";
    public static final String EXTRA_PRESSURE =
            "com.example.health_monitor.EXTRA_PRESSURE";
    public static final String EXTRA_GLICEMIA =
            "com.example.health_monitor.EXTRA_GLICEMIA";
    public static final String EXTRA_BATTITO =
            "com.example.health_monitor.EXTRA_BATTITO";
    public static final String EXTRA_NOTE =
            "com.example.health_monitor.EXTRA_NOTE";
    public static final String EXTRA_TEMPERATURE_SLIDER =
            "com.example.health_monitor.EXTRA_TEMPERATURE_SLIDER";
    public static final String EXTRA_PRESSURE_SLIDER =
            "com.example.health_monitor.EXTRA_PRESSURE_SLIDER";
    public static final String EXTRA_GLICEMIA_SLIDER =
            "com.example.health_monitor.EXTRA_GLICEMIA_SLIDER";
    public static final String EXTRA_BATTITO_SLIDER =
            "com.example.health_monitor.EXTRA_BATTITO_SLIDER";
    public static final String EXTRA_DATE =
            "com.example.health_monitor.EXTRA_DATE";

    public static final String AVG_TOO_HIGH_CHANNEL = "AVG_TOO_HIGH_CHANNEL";
    public static final int AVG_NOTIFICATION_ID = 2;
    public static Boolean isMonitoringActive = false;
    public static Boolean isMonitorValueOverThreshold = false;
    public static String valueToMonitorInBackground;
    public static int valueToMonitorNumberInBackground;

    private TextInputEditText temperatureText;
    private TextInputEditText pressureText;
    private TextInputEditText glicemiaText;
    private TextInputEditText battitoText;

    private RangeSlider temperatureSlider;
    private RangeSlider pressureSlider;
    private RangeSlider glicemiaSlider;
    private RangeSlider battitoSlider;

    private String tSliderValue;
    private String pSliderValue;
    private String bSliderValue;
    private String gSliderValue;

    private MaterialButton saveButton;
    private MaterialButton deleteButton;
    private MaterialButton updateButton;
    private MaterialButton dateButton;
    private SimpleDateFormat dateFormat;
    private Long dateLong;

    private ConstraintLayout buttons_constraint;

    private Calendar calendar = Calendar.getInstance();

    private TextInputEditText noteText;

    private ReportViewModel reportViewModel;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        reportViewModel = new ViewModelProvider(this,
                ViewModelProvider
                        .AndroidViewModelFactory
                        .getInstance(this.getApplication()))
                .get(ReportViewModel.class);

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(AlarmBroadcastReceiver.NOTIFICATION_ID);

        // Imposto l'ora corrente nel pulsante della data
        setButtonDate(null);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.setStyle(DialogFragment.STYLE_NORMAL, 0);
                datePicker.dateLong = dateLong;

                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        // Prendo i valori inseriti dei parametri
        temperatureText = findViewById(R.id.temperatura_text);
        pressureText = findViewById(R.id.pressione_text);
        glicemiaText = findViewById(R.id.glicemia_text);
        battitoText = findViewById(R.id.battito_text);
        noteText = findViewById(R.id.noteText);
        saveButton = findViewById(R.id.save_report);
        deleteButton = findViewById(R.id.delete_report);
        updateButton = findViewById(R.id.update_report);
        buttons_constraint = findViewById(R.id.buttons_constraint);

        temperatureSlider = findViewById(R.id.tempSlider);
        pressureSlider = findViewById(R.id.pressureSlider);
        glicemiaSlider = findViewById(R.id.glicemiaSlider);
        battitoSlider = findViewById(R.id.battitoSlider);

        setActionBarStyle();

        tSliderValue = temperatureSlider.getValues().toString().replaceAll("[\\[\\].0]","");
        pSliderValue = pressureSlider.getValues().toString().replaceAll("[\\[\\].0]","");
        bSliderValue = battitoSlider.getValues().toString().replaceAll("[\\[\\].0]","");
        gSliderValue = glicemiaSlider.getValues().toString().replaceAll("[\\[\\].0]","");

        // Setto il valore corrente di ogni slider controllando eventuali cambiamenti di stato
        temperatureSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                Log.d("addOnChangeListener", slider.getValues().toString().replaceAll("[\\[\\].0]",""));
                tSliderValue = slider.getValues().toString().replaceAll("[\\[\\].0]","");
            }
        });

        pressureSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                Log.d("addOnChangeListener", slider.getValues().toString());
                pSliderValue = slider.getValues().toString().replaceAll("[\\[\\].0]","");
            }
        });

        glicemiaSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                Log.d("addOnChangeListener", slider.getValues().toString());
                gSliderValue = slider.getValues().toString().replaceAll("[\\[\\].0]","");
            }
        });

        battitoSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                Log.d("addOnChangeListener", slider.getValues().toString());
                bSliderValue = slider.getValues().toString().replaceAll("[\\[\\].0]","");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReport();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddEditReportActivity.this)
                        .setTitle("Eliminare il Report?")
                        .setMessage("Sei sicuro di voler continuare? Tutti i valori andranno persi.")
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteReport();
                                Toast.makeText(AddEditReportActivity.this, "Report eliminato", Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateReport();
            }
        });

    }



    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(AddEditReportActivity.this, MainActivity.class));
        finish();

    }

    private void setButtonDate(Long currentDate){
        dateFormat = new SimpleDateFormat("MMM' 'dd', 'yyyy", Locale.ITALY);
        dateButton = findViewById(R.id.dateButton);
        String currentDateString;
        
        if(currentDate == null){
            currentDateString = dateFormat.format(calendar.getTime());
            dateLong = DateConverter.fromDate(calendar.getTime());
        } else {
            currentDateString = dateFormat.format(DateConverter.toDate(currentDate));
            dateLong = currentDate;
            //calendar.setTime(DateConverter.toDate(currentDate));
        }
        dateButton.setText(currentDateString);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.setTimeInMillis(dateLong);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateLong = DateConverter.fromDate(calendar.getTime());
        setButtonDate(dateLong);
        view.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setActionBarStyle(){
        Intent intent = getIntent();
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back_black);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.whiteText));

        ActionBar actionBar;
        actionBar = getSupportActionBar();

        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#f4f4f4"));

        actionBar.setBackgroundDrawable(colorDrawable);

        if(intent.hasExtra(EXTRA_ID)){
            actionBar.setTitle(Html.fromHtml("<font color=\"#0D3E69\">" + "Modifica Report" + "</font>"));
            temperatureText.setText(String.valueOf(intent.getIntExtra(EXTRA_TEMPERATURE, 36)));
            temperatureSlider.setValues(intent.getFloatExtra(EXTRA_TEMPERATURE_SLIDER, 3));
            pressureText.setText(String.valueOf(intent.getIntExtra(EXTRA_PRESSURE, 40)));
            pressureSlider.setValues(intent.getFloatExtra(EXTRA_PRESSURE_SLIDER, 3));
            battitoText.setText(String.valueOf(intent.getIntExtra(EXTRA_BATTITO, 60)));
            battitoSlider.setValues(intent.getFloatExtra(EXTRA_BATTITO_SLIDER, 3));
            glicemiaText.setText(String.valueOf(intent.getIntExtra(EXTRA_GLICEMIA, 40)));
            glicemiaSlider.setValues(intent.getFloatExtra(EXTRA_GLICEMIA_SLIDER, 3));
            noteText.setText(intent.getStringExtra(EXTRA_NOTE));
            dateLong = intent.getLongExtra(EXTRA_DATE, DateConverter.fromDate(Calendar.getInstance().getTime()));
            calendar.setTimeInMillis(dateLong);
            setButtonDate(dateLong);
            saveButton.setVisibility(View.GONE);
            buttons_constraint.setVisibility(View.VISIBLE);
            //deleteButton.setVisibility(View.VISIBLE);
        } else {
            saveButton.setVisibility(View.VISIBLE);
            buttons_constraint.setVisibility(View.GONE);
            actionBar.setTitle(Html.fromHtml("<font color=\"#0D3E69\">" + "Aggiungi Report" + "</font>"));
        }
    }

    /**
     * Salvo il report nel database
     */
    private void saveReport() {
        if (temperatureText.getText().toString().isEmpty() || pressureText.getText().toString().isEmpty() || battitoText.getText().toString().isEmpty() || glicemiaText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Inserisci tutti i valori", Toast.LENGTH_SHORT).show();
            return;
        }

        int temperatureValue = Integer.parseInt(temperatureText.getText().toString());
        int pressioneValue = Integer.parseInt(pressureText.getText().toString());
        int battitoValue = Integer.parseInt(battitoText.getText().toString());
        int glicemiaValue = Integer.parseInt(glicemiaText.getText().toString());
        String noteText = Objects.requireNonNull(this.noteText.getText()).toString();

        if (temperatureValue < 33 || temperatureValue > 43) {
            temperatureText.setError("Inserisci un numero valido");
            return;
        }

        if (battitoValue < 40 || battitoValue > 200) {
            battitoText.setError("Inserisci un numero valido");
            return;
        }

        if (glicemiaValue < 60 || glicemiaValue > 110) {
            glicemiaText.setError("Inserisci un numero valido");
            return;
        }

        if (pressioneValue > 120) {
            pressureText.setError("Inserisci un numero valido");
            return;
        }

        float tempPriority = Float.parseFloat(tSliderValue);
        float pressPriority = Float.parseFloat(pSliderValue);
        float battitoPriority = Float.parseFloat(bSliderValue);
        float glicemiaPriority = Float.parseFloat(gSliderValue);
        float importance = (tempPriority + pressPriority + battitoPriority + glicemiaPriority) / 4;

        Report report;
        long startDay;
        long endDay;

        Date dateToStore = DateConverter.toDate(dateLong);

        // gestisco il caso in cui il report sia già presente nella data inserita
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(dateToStore);


        try {
            startDay = getStartOfDayInMillis(dateString);
            endDay = getEndOfDayInMillis(dateString);

            Date startDayDate = DateConverter.toDate(startDay);
            Date endDayDate = DateConverter.toDate(endDay);

            Report reportByDate = reportViewModel.getReportByDate(startDayDate, endDayDate);

            if (reportByDate == null) {
                report = new Report(dateToStore, temperatureValue, glicemiaValue, pressioneValue, battitoValue, tempPriority, pressPriority, glicemiaPriority, battitoPriority, noteText, importance);
                reportViewModel.insert(report);
                Toast.makeText(getApplicationContext(), "Report salvato!", Toast.LENGTH_SHORT).show();
            } else {
                // Modifico il report in quella data facendo una media tra i valori
                Log.d("REPORTDATE", "not null");
                int avgTemp, avgBatt, avgGlic, avgPress;
                float avgTempSlider, avgBattSlider, avgPressSlider, avgGlicSlider;

                avgTemp = (reportByDate.getTemperature() + temperatureValue) / 2;
                avgBatt = (reportByDate.getCardio() + battitoValue) / 2;
                avgPress = (reportByDate.getPressure() + pressioneValue) / 2;
                avgGlic = (reportByDate.getGlicemia() + glicemiaValue) / 2;

                avgTempSlider = Math.round((reportByDate.getTPriority() + tempPriority) / 2);
                avgBattSlider = Math.round((reportByDate.getBPriority() + battitoPriority) / 2);
                avgPressSlider = Math.round((reportByDate.getPPriority() + pressPriority) / 2);
                avgGlicSlider = Math.round((reportByDate.getGPriority() + glicemiaPriority) / 2);

                Report updatedReport = new Report(dateToStore, avgTemp, avgGlic, avgPress, avgBatt, avgTempSlider, avgPressSlider, avgGlicSlider, avgBattSlider, "", importance);
                updatedReport.setId(reportByDate.getId());
                reportViewModel.update(updatedReport);
                Toast.makeText(getApplicationContext(), "Report giornaliero aggiornato!", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        checkMonitoring();
        finish();
    }

    private void checkAvgValue() throws ExecutionException, InterruptedException {
        ArrayList<Double> avgValues = reportViewModel.getAvgValues();
        switch(valueToMonitorInBackground){
            case "Temperatura":
                Double temp = avgValues.get(1);
                if(temp > valueToMonitorNumberInBackground) {
                    notifyAvgOverThreshold();
                }
                break;

            case "Battito":
                Double batt = avgValues.get(2);
                if(batt > valueToMonitorNumberInBackground){
                    notifyAvgOverThreshold();
                }
                break;

            case "Pressione":
                Double press = avgValues.get(3);
                if(press > valueToMonitorNumberInBackground){
                    notifyAvgOverThreshold();
                }
                break;

            case "Glicemia":
                Double glic = avgValues.get(4);
                if(glic > valueToMonitorNumberInBackground){
                    notifyAvgOverThreshold();
                }
                break;

            default:
                break;

        }
    }

    private void notifyAvgOverThreshold(){
        CharSequence name = getResources().getString(R.string.app_name);

        isMonitorValueOverThreshold = true;
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent rescheduleIntent = new Intent(this, Graph.class);
        rescheduleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent showAvgValuesIntent = PendingIntent.getActivity(this, 0, rescheduleIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationChannel mChannel = new NotificationChannel(AVG_TOO_HIGH_CHANNEL, name, NotificationManager.IMPORTANCE_HIGH);
        mNotificationManager.createNotificationChannel(mChannel);
        mBuilder = new NotificationCompat.Builder(this, AVG_TOO_HIGH_CHANNEL)
                .setSmallIcon(R.drawable.ic_icons8_health)
                .setLights(Color.RED, 300, 300)
                .setChannelId(AVG_TOO_HIGH_CHANNEL)
                .setContentTitle("Ops... qualcosa non va")
                .setContentText("Il valore medio " + valueToMonitorInBackground + " ha superato la soglia impostata di " + valueToMonitorNumberInBackground)
                .setContentIntent(showAvgValuesIntent)
                .setAutoCancel(true);

        mNotificationManager.notify(AVG_NOTIFICATION_ID, mBuilder.build());
    }


    /**
     * Update dei valori del Report
     */
    private void updateReport(){
        if (temperatureText.getText().toString().isEmpty() || pressureText.getText().toString().isEmpty() || battitoText.getText().toString().isEmpty() || glicemiaText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Inserisci tutti i valori", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = getIntent().getIntExtra(AddEditReportActivity.EXTRA_ID, -1);
        if(id == -1){
            Toast.makeText(getApplicationContext(), "Report non aggiornato", Toast.LENGTH_SHORT).show();
            return;
        }

        int temperatureValue = Integer.parseInt(temperatureText.getText().toString());
        int pressioneValue = Integer.parseInt(pressureText.getText().toString());
        int battitoValue = Integer.parseInt(battitoText.getText().toString());
        int glicemiaValue = Integer.parseInt(glicemiaText.getText().toString());
        String noteText = Objects.requireNonNull(this.noteText.getText()).toString();

        if (temperatureValue < 33 || temperatureValue > 43) {
            temperatureText.setError("Inserisci un numero valido");
            return;
        }

        if (battitoValue < 40 || battitoValue > 200) {
            battitoText.setError("Inserisci un numero valido");
            return;
        }

        if (glicemiaValue < 60 || glicemiaValue > 110) {
            glicemiaText.setError("Inserisci un numero valido");
            return;
        }

        if (pressioneValue > 120) {
            pressureText.setError("Inserisci un numero valido");
            return;
        }

        float tempPriority = Float.parseFloat(tSliderValue);
        float pressPriority = Float.parseFloat(pSliderValue);
        float battitoPriority = Float.parseFloat(bSliderValue);
        float glicemiaPriority = Float.parseFloat(gSliderValue);
        float importance = (tempPriority + pressPriority + battitoPriority + glicemiaPriority) / 4;
        // Effettuo la modifica dei parametri in DB con una media dei valori attuali
        try {
            Report toUpdate = reportViewModel.getReportById(id);

            String noteToStore;

            if(!noteText.isEmpty()){
                noteToStore = noteText;
            } else {
                noteToStore = toUpdate.getNote();
            }

            Date dateToStore = DateConverter.toDate(dateLong);
            Report updatedReport = new Report (dateToStore, temperatureValue, glicemiaValue, pressioneValue, battitoValue, tempPriority, pressPriority, glicemiaPriority, battitoPriority, noteToStore, importance);
            updatedReport.setId(id);
            reportViewModel.update(updatedReport);

            Toast.makeText(getApplicationContext(), "Report aggiornato", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        checkMonitoring();

        finish();
    }

    // Attivo il monitoraggio del valore inserito nelle impostazioni
    private void checkMonitoring(){
        if (isMonitoringActive) {
            try {
                checkAvgValue();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param date the date in the format "yyyy-MM-dd"
     */
    public long getStartOfDayInMillis(String date) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(date));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * @param date the date in the format "yyyy-MM-dd"
     */
    public long getEndOfDayInMillis(String date) throws ParseException {
        // Add one day's time to the beginning of the day.
        // 24 hours * 60 minutes * 60 seconds * 1000 milliseconds = 1 day
        return getStartOfDayInMillis(date) + (24 * 60 * 60 * 1000);
    }

    public void deleteReport(){
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1){
            try{
                reportViewModel.delete(reportViewModel.getReportById(id));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Report non salvato!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
