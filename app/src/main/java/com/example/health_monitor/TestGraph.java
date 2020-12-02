package com.example.health_monitor;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;

import com.example.health_monitor.DB.DateConverter;
import com.example.health_monitor.DB.Report;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class TestGraph extends AppCompatActivity {
    private ReportViewModel reportViewModel;
    final private List<Report> rep = new ArrayList<>();
    LineDataSet dataSet;
    LineChart chart;
    LineData lineData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_graph);
        chart = (LineChart) findViewById(R.id.chart);

        reportViewModel = new ViewModelProvider(this,
                ViewModelProvider
                        .AndroidViewModelFactory
                        .getInstance(this.getApplication()))
                .get(ReportViewModel.class);

        reportViewModel.getAllReport().observe(this, new Observer<List<Report>>() {
            @Override
            public void onChanged(List<Report> reports) {
                rep.addAll(reports);
                setChartValues(1);
            }
        });



        MaterialButtonToggleGroup toggleBtn = findViewById(R.id.toggle_button_group);
        toggleBtn.setSingleSelection(true);
        toggleBtn.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                int id = group.getCheckedButtonId();
                chart.clearValues();
                setChartValues(id);

                Log.d("Check button id", String.valueOf(id));
            }
        });

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