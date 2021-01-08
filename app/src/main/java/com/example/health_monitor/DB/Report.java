package com.example.health_monitor.DB;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;


@Entity(tableName = "reportdb")
public class Report {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name="temperature")
    private int temperature;

    @ColumnInfo(name="glicemia")
    private int glicemia;

    @ColumnInfo(name="pressure")
    private int pressure;

    @ColumnInfo(name="cardio")
    private int cardio;

    @ColumnInfo(name="note")
    private String note;

    @ColumnInfo(name="temppriority")
    private float tPriority;

    @ColumnInfo(name="bpriority")
    private float bPriority;

    @ColumnInfo(name="ppriority")
    private float pPriority;

    @ColumnInfo(name="gpriority")
    private float gPriority;

    @ColumnInfo(name="importance")
    private float importance;

    @TypeConverters(DateConverter.class)
    @ColumnInfo(name="date")
    private Date date;

    public Report(Date date, int temperature, int glicemia, int pressure, int cardio, float tPriority, float pPriority, float gPriority, float bPriority, String note, float importance) {
        this.temperature = temperature;
        this.glicemia = glicemia;
        this.pressure = pressure;
        this.cardio = cardio;
        this.note = note;
        this.tPriority = tPriority;
        this.bPriority = bPriority;
        this.gPriority = gPriority;
        this.pPriority = pPriority;
        this.importance = importance;
        this.date = date;
    }


    public int getCardio() {
        return cardio;
    }

    public int getPressure() {
        return pressure;
    }

    public int getGlicemia() {
        return glicemia;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getNote() {
        return note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getTPriority() {
        return tPriority;
    }

    public float getBPriority() {
        return bPriority;
    }

    public float getPPriority() {
        return pPriority;
    }

    public float getGPriority() {
        return gPriority;
    }

    public float getImportance() {
        return importance;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
