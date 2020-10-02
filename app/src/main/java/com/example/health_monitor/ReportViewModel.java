package com.example.health_monitor;

import android.app.Application;

import com.example.health_monitor.DB.Report;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ReportViewModel extends AndroidViewModel {
    private ReportRepository repository;
    private LiveData<List<Report>>  allReport;

    public ReportViewModel(@NonNull Application application) {
        super(application);
        repository = new ReportRepository(application);
        allReport = repository.getAllReport();
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

}
