package com.example.health_monitor;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.health_monitor.DB.DateConverter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

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
    private MaterialButton dateButton;
    private SimpleDateFormat dateFormat;
    private Date date;
    private Long dateLong;

    private Calendar calendar = Calendar.getInstance();

    private TextInputEditText noteText;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Imposto l'ora corrente nel pulsante della data
        setButtonDate(null);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.setStyle(DialogFragment.STYLE_NORMAL, 0);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getApplicationContext(), 0);
                datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

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
            Log.d("SET DATE", currentDateString);
            dateLong = currentDate;
            Log.d("SET DATE LONG", dateLong.toString());
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

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);

        if(intent.hasExtra(EXTRA_ID)){
            actionBar.setTitle(Html.fromHtml("<font color=\"#212121\">" + "Modifica Report" + "</font>"));
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

        } else {
            actionBar.setTitle(Html.fromHtml("<font color=\"#212121\">" + "Aggiungi Report" + "</font>"));
        }

    }



    private void saveReport(){
        if(temperatureText.getText().toString().isEmpty() || pressureText.getText().toString().isEmpty() || battitoText.getText().toString().isEmpty() || glicemiaText.getText().toString().isEmpty() ){
            Toast.makeText(this, "Inserisci tutti i valori", Toast.LENGTH_SHORT).show();
            return;
        }

        int temperatureValue = Integer.parseInt(temperatureText.getText().toString());
        int pressioneValue = Integer.parseInt(pressureText.getText().toString());
        int battitoValue = Integer.parseInt(battitoText.getText().toString());
        int glicemiaValue = Integer.parseInt(glicemiaText.getText().toString());
        String noteText = Objects.requireNonNull(this.noteText.getText()).toString();

        if(temperatureValue < 33 || temperatureValue > 43){
            temperatureText.setError("Inserisci un numero valido");
            return;
        }

        if(battitoValue < 40 || battitoValue > 200){
            battitoText.setError("Inserisci un numero valido");
            return;
        }

        if(glicemiaValue < 60 || glicemiaValue > 110){
            glicemiaText.setError("Inserisci un numero valido");
            return;
        }

        if(pressioneValue > 120){
            pressureText.setError("Inserisci un numero valido");
            return;
        }

        float tempPriority = Float.parseFloat(tSliderValue);
        float pressPriority = Float.parseFloat(pSliderValue);
        float battitoPriority = Float.parseFloat(bSliderValue);
        float glicemiaPriority = Float.parseFloat(gSliderValue);

        // Inserisco i valori nell'intent da passare alla MainActivity
        Intent data = new Intent();
        data.putExtra(EXTRA_TEMPERATURE, temperatureValue);
        data.putExtra(EXTRA_TEMPERATURE_SLIDER, tempPriority);
        data.putExtra(EXTRA_PRESSURE, pressioneValue);
        data.putExtra(EXTRA_PRESSURE_SLIDER, pressPriority);
        data.putExtra(EXTRA_BATTITO, battitoValue);
        data.putExtra(EXTRA_BATTITO_SLIDER, battitoPriority);
        data.putExtra(EXTRA_GLICEMIA, glicemiaValue);
        data.putExtra(EXTRA_GLICEMIA_SLIDER, glicemiaPriority);
        data.putExtra(EXTRA_NOTE, noteText);
        data.putExtra(EXTRA_DATE, dateLong);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if(id != -1){
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }
}
