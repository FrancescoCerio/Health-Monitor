package com.example.health_monitor;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.WindowManager;

import com.example.health_monitor.DB.Report;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class Graph extends AppCompatActivity {
    private ReportViewModel reportViewModel;
    final private List<Report> rep = new ArrayList<>();
    LineDataSet dataSet;
    LineChart chart;
    LineData lineData;
    MaterialButtonToggleGroup toggleBtn;
    MaterialButton tempBtn;
    MaterialButton battBtn;
    MaterialButton glicBtn;
    MaterialButton pressBtn;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph);
        setActionBarStyle();
        chart = (LineChart) findViewById(R.id.chart);
        dataSet = null;
        toggleBtn = findViewById(R.id.toggle_button_group);
        toggleBtn.clearOnButtonCheckedListeners();
        tempBtn = findViewById(R.id.toggle_btn_temp);
        glicBtn = findViewById(R.id.toggle_btn_glic);
        battBtn = findViewById(R.id.toggle_btn_batt);
        pressBtn = findViewById(R.id.toggle_btn_press);

        reportViewModel = new ViewModelProvider(this,
                ViewModelProvider
                        .AndroidViewModelFactory
                        .getInstance(this.getApplication()))
                .get(ReportViewModel.class);

        reportViewModel.getAllReport().observe(this, new Observer<List<Report>>() {
            @Override
            public void onChanged(List<Report> reports) {
                rep.addAll(reports);
                Collections.sort(rep, new Comparator<Report>() {
                    public int compare(Report o1, Report o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });
                setChartValues(1);
            }
        });

        toggleBtn.check(tempBtn.getId());

        tempBtn.addOnCheckedChangeListener(new MaterialButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialButton button, boolean isChecked) {
                chart.clearValues();
                setChartValues(1);
            }
        });

        battBtn.addOnCheckedChangeListener(new MaterialButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialButton button, boolean isChecked) {
                chart.clearValues();
                setChartValues(2);
            }
        });

        pressBtn.addOnCheckedChangeListener(new MaterialButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialButton button, boolean isChecked) {
                chart.clearValues();
                setChartValues(3);
            }
        });

        glicBtn.addOnCheckedChangeListener(new MaterialButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialButton button, boolean isChecked) {
                chart.clearValues();
                setChartValues(4);
            }
        });


    }

    private void setActionBarStyle() {
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setHomeAsUpIndicator(R.drawable.arrow_back_white);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));

        ColorDrawable colorDrawable
                = new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent));

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "Statistiche" + "</font>"));
    }

    void setChartValues(final int mode){

        final ArrayList<String> xAxisValues = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("MM'/'dd'/'yy", Locale.ITALY);

        int dateCount = 0;
        ArrayList<Entry> entries = new ArrayList<>();

        switch(mode){
            case 1:
                for (Report report : rep) {
                    entries.add(new Entry(dateCount, report.getTemperature()));
                    dateCount++;
                    Log.d("VALORE TEMPERATURA:", String.valueOf(report.getTemperature()));
                    xAxisValues.add(dateFormat.format(report.getDate()));
                }
                break;

            case 2:
                for (Report report : rep) {
                    entries.add(new Entry(dateCount, report.getCardio()));
                    dateCount++;
                    Log.d("VALORE CARDIO:", String.valueOf(report.getCardio()));
                    xAxisValues.add(dateFormat.format(report.getDate()));
                }
                break;

            case 3:
                for (Report report : rep) {
                    entries.add(new Entry(dateCount, report.getPressure()));
                    dateCount++;
                    Log.d("VALORE PRESS:", String.valueOf(report.getPressure()));
                    xAxisValues.add(dateFormat.format(report.getDate()));
                }
                break;

            case 4:
                for (Report report : rep) {
                    entries.add(new Entry(dateCount, report.getGlicemia()));
                    dateCount++;
                    Log.d("VALORE GLIC:", String.valueOf(report.getGlicemia()));
                    xAxisValues.add(dateFormat.format(report.getDate()));
                }
                break;

            default:
                chart.setNoDataText("Nessun valore presente.");
                break;
        }

        dataSet = new LineDataSet(entries, ""); // add entries to dataset
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.RED); // styling, ...
        dataSet.setCircleColor(Color.RED);

        dataSet.setDrawCircleHole(false);
        dataSet.setDrawIcons(false);
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setCubicIntensity(0.5f);

        lineData = new LineData(dataSet);


        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(new IndexAxisValueFormatterHelper(xAxisValues));
        xAxis.setLabelRotationAngle(0.5f);

        chart.clearAllViewportJobs();
        chart.clear();

        //chart.setDrawGridBackground(false);
        chart.setDragEnabled(true);
        chart.setPinchZoom(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);

        chart.setData(lineData);
        chart.setNoDataText("Nessun valore presente.");
        chart.notifyDataSetChanged();
        chart.invalidate(); // refresh

    }

}

class IndexAxisValueFormatterHelper extends ValueFormatter
{
    private String[] mValues = new String[] {};
    private int mValueCount = 0;

    /**
     * An empty constructor.
     * Use `setValues` to set the axis labels.
     */
    public IndexAxisValueFormatterHelper() {
    }

    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    public IndexAxisValueFormatterHelper(String[] values) {
        if (values != null)
            setValues(values);
    }

    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    public IndexAxisValueFormatterHelper(Collection<String> values) {
        if (values != null)
            setValues(values.toArray(new String[0]));
    }

    @Override
    public String getFormattedValue(float value) {
        int index = Math.round(value);

        Log.d("mValue ", String.valueOf(index));
        if (index < 0 || index >= mValueCount || index != (int)value)
            return "";

        Log.d("mValue ", mValues[index]);
        return mValues[index];
    }

    public String[] getValues()
    {
        return mValues;
    }

    public void setValues(String[] values)
    {
        if (values == null)
            values = new String[] {};

        this.mValues = values;
        this.mValueCount = values.length;
    }
}