package com.example.health_monitor;

import android.app.Application;
import android.os.AsyncTask;

import com.example.health_monitor.DB.Report;
import com.example.health_monitor.DB.ReportDAO;
import com.example.health_monitor.DB.ReportDB;

import java.util.List;

import androidx.lifecycle.LiveData;

public class ReportRepository {

    private ReportDAO report_dao;
    private LiveData<List<Report>> all_report;

    public ReportRepository(Application application){
        ReportDB database = ReportDB.getInstance(application);
        report_dao = database.reportDao();
        all_report = report_dao.getAllReport();
    }

    public void insert(Report report){
        new InsertReportAsyncTask(report_dao).execute(report);
    }

    public void delete(Report report){
        new DeleteReportAsyncTask(report_dao).execute(report);
    }

    public void update(Report report){
        new UpdateReportAsyncTask(report_dao).execute(report);
    }

    public LiveData<List<Report>> getAllReport(){
        return all_report;
    }

    private static class InsertReportAsyncTask extends AsyncTask<Report, Void, Void>{
        private ReportDAO reportDao;

        private InsertReportAsyncTask(ReportDAO reportdao){
            this.reportDao = reportdao;
        }

        @Override
        protected Void doInBackground(Report... reports) {
            reportDao.insert(reports[0]);
            return null;
        }
    }

    private static class DeleteReportAsyncTask extends AsyncTask<Report, Void, Void>{
        private ReportDAO reportDao;

        private DeleteReportAsyncTask(ReportDAO reportdao){
            this.reportDao = reportdao;
        }

        @Override
        protected Void doInBackground(Report... reports) {
            reportDao.delete(reports[0]);
            return null;
        }
    }

    private static class UpdateReportAsyncTask extends AsyncTask<Report, Void, Void>{
        private ReportDAO reportDao;

        private UpdateReportAsyncTask(ReportDAO reportdao){
            this.reportDao = reportdao;
        }

        @Override
        protected Void doInBackground(Report... reports) {
            reportDao.update(reports[0]);
            return null;
        }
    }
}
