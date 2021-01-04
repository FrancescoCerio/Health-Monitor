package com.example.health_monitor;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

public class SettingsActivity extends AppCompatActivity {
    private EditText notificationHour;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        /*
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
         */
        setActionBarStyle();

        notificationHour = findViewById(R.id.notification_time);

        final SwitchCompat notificationSwitch = findViewById(R.id.notificationToggleButton);
        notificationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationSwitch.isChecked()){
                    enableNotification();
                } else {
                    disableNotification();
                }
            }
        });

        notificationHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        notificationHour.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.show();
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

        actionBar.setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "Impostazioni" + "</font>"));
    }

    public void enableNotification(){
        notificationHour.setEnabled(true);
    }

    public void disableNotification(){
        notificationHour.setEnabled(false);
    }
}
