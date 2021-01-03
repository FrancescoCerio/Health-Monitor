package com.example.health_monitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.health_monitor.DB.DateConverter;
import com.example.health_monitor.DB.Report;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.example.health_monitor.AddEditReportActivity.DELETE_REPORT;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_REPORT_REQUEST = 1;
    public static final int EDIT_REPORT_REQUEST = 2;
    public static final String CHANNEL_NOTIFICATION = "Reminder giornaliero";
    private ReportViewModel reportViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionBarStyle(true);
        reportViewModel = new ViewModelProvider(this,
                ViewModelProvider
                        .AndroidViewModelFactory
                        .getInstance(this.getApplication()))
                .get(ReportViewModel.class);

        //createNotification();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                new HomeFragment()).commit();

        FloatingActionButton openReport = findViewById(R.id.button_add_note_home);
        openReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddEditReportActivity.class);
                startActivityForResult(i, ADD_REPORT_REQUEST);
            }
        });
    }

    //TODO: CONTROLLARE PER BENE
    private void createNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_NOTIFICATION,
                "Report giornaliero",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        channel.setDescription("Questo è un reminder giornaliero");

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_NOTIFICATION)
                .setSmallIcon(R.drawable.ic_icons8_health)
                .setContentTitle("UÈ! SCEMO DI MERDA")
                .setContentText("Suca")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Che cazzo ti ridi"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, notification);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch(item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            setActionBarStyle(true);
                            break;

                        case R.id.nav_diary:
                            selectedFragment = new DiaryFragment();
                            setActionBarStyle(false);
                            break;

                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, selectedFragment).commit();

                    return true;
                }
            };

    /**
     * Setto la UI della actionBar
     */
    private void setActionBarStyle(boolean isHome){
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#57944B"));

        if(isHome){
            actionBar.setTitle(Html.fromHtml("<font color=\"#f4f4f4\">" + "Health Monitor" + "</font>"));
        } else {
            actionBar.setTitle(Html.fromHtml("<font color=\"#f4f4f4\">" + "Diario dei report" + "</font>"));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settingsBtn) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Prendo i valori passati da AddReport Activity e chiamo le funzioni di DB
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_REPORT_REQUEST && resultCode == RESULT_OK){
            int tempInt = data.getIntExtra(AddEditReportActivity.EXTRA_TEMPERATURE, 36);
            int battInt = data.getIntExtra(AddEditReportActivity.EXTRA_BATTITO, 60);
            int pressInt = data.getIntExtra(AddEditReportActivity.EXTRA_PRESSURE, 40);
            int glicInt = data.getIntExtra(AddEditReportActivity.EXTRA_GLICEMIA, 60);
            Long date = data.getLongExtra(AddEditReportActivity.EXTRA_DATE, DateConverter.fromDate(Calendar.getInstance().getTime()));
            String note = Objects.requireNonNull(data.getStringExtra(AddEditReportActivity.EXTRA_NOTE));

            float tempSlider = data.getFloatExtra(AddEditReportActivity.EXTRA_TEMPERATURE_SLIDER, 3);
            float battSlider = data.getFloatExtra(AddEditReportActivity.EXTRA_BATTITO_SLIDER, 3);
            float pressSlider = data.getFloatExtra(AddEditReportActivity.EXTRA_PRESSURE_SLIDER, 3);
            float glicSlider = data.getFloatExtra(AddEditReportActivity.EXTRA_GLICEMIA_SLIDER, 3);
            Report report;
            long startDay;
            long endDay;

            Date dateToStore = DateConverter.toDate(date);

            // gestisco il caso in cui il report sia già presente nella data inserita
            Log.d("datetostore", String.valueOf(dateToStore));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = dateFormat.format(dateToStore);


            try {
                startDay = getStartOfDayInMillis(dateString);
                endDay = getEndOfDayInMillis(dateString);
                /*
                Log.d("startday", DateConverter.toDate(startDay).toString());
                Log.d("endday", DateConverter.toDate(endDay).toString());

                Log.d("startday", String.valueOf(startDay));
                Log.d("endday", String.valueOf(endDay));

                 */


                Date startDayDate = DateConverter.toDate(startDay);
                Date endDayDate = DateConverter.toDate(endDay);
                Log.d("startday", String.valueOf(startDayDate));
                Log.d("endday", String.valueOf(endDayDate));

                Report reportByDate = reportViewModel.getReportByDate(startDayDate, endDayDate);

                Log.d("reportByDate", String.valueOf(reportByDate));

                if(reportByDate == null) {
                    Log.d("REPORTDATE", "null");
                    report = new Report(dateToStore, tempInt, glicInt, pressInt, battInt, tempSlider, pressSlider, glicSlider, battSlider, note);
                    reportViewModel.insert(report);
                    Toast.makeText(getApplicationContext(), "Report salvato!", Toast.LENGTH_SHORT).show();
                } else {
                    // Modifico il report in quella data facendo una media tra i valori
                    Log.d("REPORTDATE", "not null");
                    int avgTemp, avgBatt, avgGlic, avgPress;
                    float avgTempSlider, avgBattSlider, avgPressSlider, avgGlicSlider;

                    //TODO: portare i valori da int a float
                    avgTemp = (reportByDate.getTemperature() + tempInt) / 2;
                    avgBatt = (reportByDate.getCardio() + battInt) / 2;
                    avgPress = (reportByDate.getPressure() + pressInt) / 2;
                    avgGlic = (reportByDate.getGlicemia() + glicInt) / 2;

                    avgTempSlider = (reportByDate.getTPriority() + tempSlider) / 2;
                    avgBattSlider = (reportByDate.getBPriority() + battSlider) / 2;
                    avgPressSlider = (reportByDate.getPPriority() + pressSlider) / 2;
                    avgGlicSlider = (reportByDate.getGPriority() + glicSlider) / 2;

                    Report updatedReport = new Report (dateToStore, avgTemp, avgGlic, avgPress, avgBatt, avgTempSlider, avgPressSlider, avgGlicSlider, avgBattSlider, "");
                    updatedReport.setId(reportByDate.getId());
                    reportViewModel.update(updatedReport);
                    Toast.makeText(getApplicationContext(), "Media report aggiornata!", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Report non salvato", Toast.LENGTH_SHORT).show();
        }

        /*
        if(requestCode == DELETE_REPORT){
            int id = data.getIntExtra(AddEditReportActivity.EXTRA_ID, -1);
            Report report = reportViewModel.getReportById(id);
            Log.d("REPORT DATE: ", String.valueOf(report.getDate()));
            Log.d("REPORT BATTITO: ", String.valueOf(report.getCardio()));
            try{
                reportViewModel.delete(reportViewModel.getReportById(id));
            } catch (Exception e){
                Log.d("DELETE:", "REPORT NOT DELETED");
                e.printStackTrace();
            }
        }

         */




    }

    /**
     * @param date the date in the format "yyyy-MM-dd"
     */
    public long getStartOfDayInMillis(String date) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(date));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * @param date the date in the format "yyyy-MM-dd"
     */
    public long getEndOfDayInMillis(String date) throws ParseException {
        // Add one day's time to the beginning of the day.
        // 24 hours * 60 minutes * 60 seconds * 1000 milliseconds = 1 day
        return getStartOfDayInMillis(date) + (24 * 60 * 60 * 1000);
    }


}