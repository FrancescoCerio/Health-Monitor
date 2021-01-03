package com.example.health_monitor;

import android.app.Application;

import com.example.health_monitor.DB.Report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ReportViewModel extends AndroidViewModel {
    private ReportRepository repository;
    private LiveData<List<Report>>  allReport;
    private LiveData<Report> lastReport;

    public ReportViewModel(@NonNull Application application) {
        super(application);
        repository = new ReportRepository(application);
        allReport = repository.getAllReport();
        lastReport = repository.getLastReport();
    }

    public void insert(Report report){
        repository.insert(report);
    }

    public void update(Report report){
        repository.update(report);
    }

    public void delete(Report report){
        repository.delete(report);
    }

    public LiveData<List<Report>> getAllReport(){
        return allReport;
    }

    public LiveData<Report> getLastReport(){
        return lastReport;
    }

    public Report getReportById(int current_id) throws ExecutionException, InterruptedException {
        return repository.getReportById(current_id);
    }

    public Report getReportByDate(Date startDay, Date endDay) throws ExecutionException, InterruptedException { return repository.getReportByDate(startDay, endDay);}
}
