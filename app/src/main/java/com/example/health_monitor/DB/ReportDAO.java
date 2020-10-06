package com.example.health_monitor.DB;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ReportDAO {

    @Insert
    void insert(Report report);

    @Update
    void update(Report report);

    @Delete
    void delete(Report report);

    @Query("SELECT * FROM reportdb ORDER BY tempPriority DESC")
    LiveData<List<Report>> getAllReport();

    @Query("SELECT pressure FROM reportdb")
    int[] getAllPressureValues();

    @Query("SELECT glicemia FROM reportdb")
    int[] getAllGlucoseValues();

    @Query("SELECT temperature FROM reportdb")
    int[] getAllTemperatureValues();

    @Query("SELECT cardio FROM reportdb")
    int[] getAllCardioValues();

    @Query("SELECT ROUND(AVG(pressure), 1) FROM reportdb")
    double getAvgPressure();

    @Query("SELECT ROUND(AVG(temperature), 1) FROM reportdb")
    double getAvgTemperature();

    @Query("SELECT ROUND(AVG(glicemia), 2) FROM reportdb")
    double getAvgGlucose();

    @Query("SELECT ROUND(AVG(cardio), 2) FROM reportdb")
    double getAvgCardio();

    @Query("SELECT * FROM reportdb GROUP BY date HAVING MAX(date)")
    Report getLastReport();
}
