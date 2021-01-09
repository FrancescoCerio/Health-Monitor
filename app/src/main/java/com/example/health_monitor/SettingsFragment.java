package com.example.health_monitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import static com.example.health_monitor.AddEditReportActivity.isMonitorValueOverThreshold;
import static com.example.health_monitor.AddEditReportActivity.isMonitoringActive;
import static com.example.health_monitor.AddEditReportActivity.valueToMonitorInBackground;
import static com.example.health_monitor.AddEditReportActivity.valueToMonitorNumberInBackground;

public class SettingsFragment extends PreferenceFragmentCompat {
    private Preference hourSetting;
    private Calendar mcurrentTime;
    private DropDownPreference valueToMonitor;
    private Preference valueToMonitorNumber;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        final SharedPreferences sharedPreferences =
                getContext().getSharedPreferences("com.example.health_monitor", Context.MODE_PRIVATE);
        final SwitchPreferenceCompat notificationSwitch = findPreference("notifications");
        final SwitchPreferenceCompat valueMonitorSwitch = findPreference("valueMonitor");
        hourSetting = findPreference("settingHour");
        valueToMonitor = findPreference("valueToMonitor");
        valueToMonitorNumber = findPreference("valueToMonitorNumber");
        List<String> listItems = new ArrayList<>();
        listItems.add("Battito");
        listItems.add("Temperatura");
        listItems.add("Glicemia");
        listItems.add("Pressione");
        CharSequence[] entries = listItems.toArray(new CharSequence[0]);

        valueToMonitor.setEntries(entries);

        // Controllo i valori da impostare di default
        if(!sharedPreferences.contains("notification_time_milliseconds")){
            setDefaultCalendar();
            sharedPreferences.edit().putLong("notification_time_milliseconds", mcurrentTime.getTimeInMillis()).apply();
        }

        if(!sharedPreferences.contains("value_to_monitor")){
            valueToMonitor.setSummary(entries[0]);
            sharedPreferences.edit().putString("value_to_monitor", entries[0].toString()).apply();
        } else {
            valueToMonitor.setSummary(sharedPreferences.getString("value_to_monitor", entries[0].toString()));
        }

        if(!sharedPreferences.contains("value_to_monitor_number")){
            valueToMonitorNumber.setSummary("100");
            sharedPreferences.edit().putInt("value_to_monitor_number", 100).apply();
        } else {
            valueToMonitorNumber.setSummary(String.valueOf(sharedPreferences.getInt("value_to_monitor_number", 100)));
        }

        // Setto l'orario predefinito per la notifica
        long initialTime = sharedPreferences.getLong("notification_time_milliseconds", 1610125200678L);
        mcurrentTime = Calendar.getInstance();
        mcurrentTime.setTimeInMillis(initialTime);

        int initialHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int initialMinute = mcurrentTime.get(Calendar.MINUTE);

        hourSetting.setSummary(initialHour + ":" + initialMinute);

        //Abilito e disabilito le notifiche per l'inserimento del Report giornaliero
        notificationSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean turned = (Boolean) newValue;
                if (turned){
                    enableNotification();
                } else {
                    disableNotification();
                }
                return true;
            }
        });

        // Abilito e disabilito il monitoraggio dei valori
        valueMonitorSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean turned = (Boolean) newValue;
                if (turned){
                    enableMonitoring();
                } else {
                    disableMonitoring();
                }
                return true;
            }
        });

        hourSetting.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDialog();
                return true;
            }
        });

        valueToMonitor.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                valueToMonitor.setSummary(newValue.toString());
                sharedPreferences.edit().putString("value_to_monitor", newValue.toString()).apply();
                return true;
            }
        });

        valueToMonitorNumber.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                valueToMonitorNumber.setSummary(newValue.toString());
                sharedPreferences.edit().putInt("value_to_monitor_number", Integer.parseInt(newValue.toString())).apply();
                return true;
            }
        });
    }

    private void setDefaultCalendar(){
        mcurrentTime = Calendar.getInstance();
        mcurrentTime.set(Calendar.HOUR_OF_DAY, 18);
        mcurrentTime.set(Calendar.MINUTE, 0);
        mcurrentTime.set(Calendar.SECOND, 0);
    }

    private void showDialog(){
        SharedPreferences sp = getContext().getSharedPreferences("com.example.health_monitor", Context.MODE_PRIVATE);
        long timeMillis = sp.getLong("notification_time_milliseconds", Calendar.getInstance().getTimeInMillis());
        mcurrentTime = Calendar.getInstance();
        mcurrentTime.setTimeInMillis(timeMillis);
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                mcurrentTime.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                mcurrentTime.set(Calendar.MINUTE, timePicker.getMinute());
                hourSetting.setSummary(selectedHour + ":" + selectedMinute);
                hourSetting.setDefaultValue(selectedHour + ":" + selectedMinute);
                SharedPreferences sharedPref = getContext().getSharedPreferences("com.example.health_monitor", Context.MODE_PRIVATE);
                sharedPref.edit().putLong("notification_time_milliseconds", mcurrentTime.getTimeInMillis()).apply();
                startAlarmBroadcastReceiver( selectedHour, selectedMinute);
            }
        }, hour, minute, true);
        mTimePicker.show();
    }


    private void enableNotification(){
        disableAlarm();
        startAlarmBroadcastReceiver(mcurrentTime.get(Calendar.HOUR_OF_DAY), mcurrentTime.get(Calendar.MINUTE));
        hourSetting.setEnabled(true);
    }

    private void disableNotification(){
        disableAlarm();
        hourSetting.setEnabled(false);
    }

    private void enableMonitoring(){
        isMonitoringActive = true;
        SharedPreferences sharedPreferences =
                getContext().getSharedPreferences("com.example.health_monitor", Context.MODE_PRIVATE);
        valueToMonitorInBackground = sharedPreferences.getString("value_to_monitor", "Battito");
        valueToMonitorNumberInBackground = sharedPreferences.getInt("value_to_monitor_number", 100);
    }

    private void disableMonitoring(){
        isMonitoringActive = false;
        isMonitorValueOverThreshold = false;
    }

    private void disableAlarm(){
        Intent intent = new Intent(getContext(), AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public void startAlarmBroadcastReceiver(int selectedHour, int selectedMinute) {
        Intent _intent = new Intent(getContext(), AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, _intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        calendar.set(Calendar.SECOND, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP , calendar.getTimeInMillis(), pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),   AlarmManager.INTERVAL_DAY , pendingIntent);
    }

}