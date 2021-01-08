package com.example.health_monitor;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.example.health_monitor.DB.Report;
import com.example.health_monitor.DB.ReportDAO;
import com.example.health_monitor.DB.ReportDB;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LiveData;

public class ReportRepository {

    private ReportDAO report_dao;
    private LiveData<List<Report>> all_report;

    public ReportRepository(Application application){
        ReportDB db = ReportDB.getInstance(application);
        report_dao = db.reportDao();
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

    public ArrayList<Double> getAvgValues() throws ExecutionException, InterruptedException {
        return new GetAverageValuesAsyncTask(report_dao).execute().get();
    }

    public LiveData<List<Report>> getAllReport(){
        return all_report;
    }

    public LiveData<Report> getLastReport(){
        return report_dao.getLastReport();
    }

    public Report getReportById(int current_id) throws ExecutionException, InterruptedException {
        return new GetReportByIdAsyncTask(report_dao).execute(current_id).get();
    }


    public Report getReportByDate(Date startDay, Date endDay) throws ExecutionException, InterruptedException {
        return new GetReportByDateAsyncTask(report_dao).execute(startDay, endDay).get();
    }

    public LiveData<List<Report>> getReportWithImportance(int importance) throws ExecutionException, InterruptedException {
        return new GetReportByImportanceAsyncTask(report_dao).execute(importance).get();
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

    private static class GetReportByIdAsyncTask extends AsyncTask<Integer, Void, Report>{
        private ReportDAO reportDao;

        private GetReportByIdAsyncTask(ReportDAO reportdao){
            this.reportDao = reportdao;
        }

        @Override
        protected Report doInBackground(Integer... integers) {
            return reportDao.getReportById(integers[0]);
        }
    }

    private static class GetReportByDateAsyncTask extends AsyncTask<Date, Void, Report>{
        private ReportDAO reportDao;

        private GetReportByDateAsyncTask(ReportDAO reportdao){
            this.reportDao = reportdao;
        }

        @Override
        protected Report doInBackground(Date... dates) {
            return reportDao.getReportByDate(dates[0], dates[1]);
        }
    }

    private static class GetAverageValuesAsyncTask extends AsyncTask<Void, Void, ArrayList<Double>>{
        private ReportDAO reportDao;

        private GetAverageValuesAsyncTask(ReportDAO reportdao){
            this.reportDao = reportdao;
        }


        @Override
        protected ArrayList<Double> doInBackground(Void... voids) {
            ArrayList<Double> avgParams = new ArrayList<>();
            avgParams.add(reportDao.getAvgTemperature());
            avgParams.add(reportDao.getAvgCardio());
            avgParams.add(reportDao.getAvgPressure());
            avgParams.add(reportDao.getAvgGlucose());

            return avgParams;
        }
    }

    private static class GetReportByImportanceAsyncTask extends AsyncTask<Integer, Void, LiveData<List<Report>>>{
        private ReportDAO reportDao;

        private GetReportByImportanceAsyncTask(ReportDAO reportdao){
            this.reportDao = reportdao;
        }

        @Override
        protected LiveData<List<Report>> doInBackground(Integer... importance) {
            return reportDao.getReportWithImportance(importance[0]);
        }
    }
}
