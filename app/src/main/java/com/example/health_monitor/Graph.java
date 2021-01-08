package com.example.health_monitor;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.health_monitor.DB.Report;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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
import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import static com.example.health_monitor.AddEditReportActivity.isMonitorValueOverThreshold;
import static com.example.health_monitor.AddEditReportActivity.valueToMonitorNumberInBackground;
import static com.example.health_monitor.AddEditReportActivity.valueToMonitorInBackground;

public class Graph extends AppCompatActivity {
    private ReportViewModel reportViewModel;
    final private List<Report> rep = new ArrayList<>();
    LineDataSet dataSet;
    LineChart valueChart;
    LineData lineData;

    BarDataSet averageDataSet;
    BarChart averageValueChart;
    BarData averageBarData;

    MaterialButtonToggleGroup toggleBtn;
    MaterialButton tempBtn;
    MaterialButton battBtn;
    MaterialButton glicBtn;
    MaterialButton pressBtn;

    TextView isMonitorValueOverThresholdText;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph);
        setActionBarStyle();
        valueChart = findViewById(R.id.valueChart);
        dataSet = null;
        toggleBtn = findViewById(R.id.toggle_button_group);
        toggleBtn.clearOnButtonCheckedListeners();
        tempBtn = findViewById(R.id.toggle_btn_temp);
        glicBtn = findViewById(R.id.toggle_btn_glic);
        battBtn = findViewById(R.id.toggle_btn_batt);
        pressBtn = findViewById(R.id.toggle_btn_press);
        isMonitorValueOverThresholdText = findViewById(R.id.averageOverThreshold);

        if(isMonitorValueOverThreshold){
            String toShow = "Il valore di " + valueToMonitorInBackground + " ha superato la soglia di " + valueToMonitorNumberInBackground;
            isMonitorValueOverThresholdText.setText(toShow);
            isMonitorValueOverThresholdText.setVisibility(View.VISIBLE);
        } else {
            isMonitorValueOverThresholdText.setVisibility(View.GONE);
        }

        averageValueChart = findViewById(R.id.averageChart);
        averageDataSet = null;

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

        /** Valori medi nell'ordine:
         * Temperatura
         * Battito
         * Pressione
         * Glicemia
         */
        ArrayList<Double> avgValues = new ArrayList<>();
        try {
            avgValues = reportViewModel.getAvgValues();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if(avgValues != null){
            setAvgChartValues(avgValues);
        } else {
            averageValueChart.setNoDataText("Valori al momento non disponibili");
        }
        for (Double value : avgValues) {
            Log.d("AVG VALUE", String.valueOf(value));
        }


        toggleBtn.check(tempBtn.getId());

        tempBtn.addOnCheckedChangeListener(new MaterialButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialButton button, boolean isChecked) {
                valueChart.clearValues();
                setChartValues(1);
            }
        });

        battBtn.addOnCheckedChangeListener(new MaterialButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialButton button, boolean isChecked) {
                valueChart.clearValues();
                setChartValues(2);
            }
        });

        pressBtn.addOnCheckedChangeListener(new MaterialButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialButton button, boolean isChecked) {
                valueChart.clearValues();
                setChartValues(3);
            }
        });

        glicBtn.addOnCheckedChangeListener(new MaterialButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialButton button, boolean isChecked) {
                valueChart.clearValues();
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
        DateFormat dateFormat = new SimpleDateFormat("dd'/'MM'/'YY", Locale.ITALY);

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
                valueChart.setNoDataText("Nessun valore presente.");
                break;
        }

        dataSet = new LineDataSet(entries, ""); // add entries to dataset
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.RED); // styling, ...
        dataSet.setCircleColor(Color.RED);
        dataSet.setValueTextSize(10);

        dataSet.setDrawCircleHole(false);
        dataSet.setDrawIcons(false);
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setGradientColor(Color.RED, Color.TRANSPARENT);
        dataSet.setCubicIntensity(0.5f);

        lineData = new LineData(dataSet);


        XAxis xAxis = valueChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(new IndexAxisValueFormatterHelper(xAxisValues));
        xAxis.setLabelRotationAngle(0.5f);

        valueChart.clearAllViewportJobs();
        valueChart.clear();

        //chart.setDrawGridBackground(false);
        valueChart.setDragEnabled(true);
        valueChart.setPinchZoom(false);
        valueChart.getAxisRight().setEnabled(false);
        valueChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        valueChart.getDescription().setEnabled(false);
        valueChart.setDrawGridBackground(false);
        valueChart.setDrawBorders(false);
        valueChart.animateX(500, Easing.EaseInSine);

        valueChart.setData(lineData);
        valueChart.setNoDataText("Nessun valore presente.");
        valueChart.notifyDataSetChanged();
        valueChart.invalidate(); // refresh

    }

    public void setAvgChartValues(ArrayList<Double> avgValues){
        ArrayList<String> xAxisAvgValues = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();

        xAxisAvgValues.add("Temperatura");
        xAxisAvgValues.add("Battito");
        xAxisAvgValues.add("Pressione");
        xAxisAvgValues.add("Glicemia");

        int valueCount = 0;

        for(Double avgValue : avgValues){
            entries.add(new BarEntry(valueCount, avgValue.floatValue()));
            valueCount++;
        }

        averageDataSet = new BarDataSet(entries, "");
        averageDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        averageDataSet.setColor(Color.CYAN);
        averageDataSet.setValueTextColor(Color.parseColor("#0D3E69"));
        averageDataSet.setValueTextSize(15);

        averageBarData = new BarData(averageDataSet);

        XAxis xAxis = averageValueChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(new IndexAxisValueFormatterHelper(xAxisAvgValues));
        xAxis.setLabelRotationAngle(0.5f);

        averageValueChart.clearAllViewportJobs();
        averageValueChart.setDrawValueAboveBar(true);
        averageValueChart.clear();
        averageValueChart.getAxisRight().setEnabled(false);
        averageValueChart.setDragEnabled(true);
        averageValueChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        averageValueChart.getDescription().setEnabled(false);
        averageValueChart.animateY(1000, Easing.EaseOutCirc);
        averageValueChart.setData(averageBarData);
        averageValueChart.notifyDataSetChanged();
        averageValueChart.invalidate();
    }

}

class IndexAxisValueFormatterHelper extends ValueFormatter {
    private String[] mValues = new String[] {};
    private int mValueCount = 0;

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

        if (index < 0 || index >= mValueCount || index != (int)value)
            return "";

        return mValues[index];
    }

    public String[] getValues() {
        return mValues;
    }

    public void setValues(String[] values) {
        if (values == null)
            values = new String[] {};

        this.mValues = values;
        this.mValueCount = values.length;
    }
}