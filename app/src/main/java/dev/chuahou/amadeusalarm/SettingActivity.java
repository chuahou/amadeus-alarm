package dev.chuahou.amadeusalarm;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;

import dev.chuahou.amadeusalarm.alarm.AlarmReceiver;
import dev.chuahou.amadeusalarm.alarm.Ringer;

public class SettingActivity extends Activity
{
    private AlarmManager _alarmManager;
    private PendingIntent _pendingIntent;
    private TimePicker _timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // check permissions
        if (!_checkPermissions())
        {
            Log.d(toString(), "No permissions");
        };

        // create notification channel
        _createNotificationChannel();

        // get time picker
        _timePicker = (TimePicker) findViewById(R.id.alarmTimePicker);

        // get system alarm manager
        _alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (Ringer.getInstance().isRinging())
        {
            Ringer.getInstance().stop();
        }
    }

    /**
     * Checks for necessary permissions.
     */
    private boolean _checkPermissions()
    {
        return (checkSelfPermission(Manifest.permission.WAKE_LOCK)
                    == PackageManager.PERMISSION_GRANTED) &&
               (checkSelfPermission(Manifest.permission.USE_FULL_SCREEN_INTENT)
                    == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Creates notification channel.
     */
    private void _createNotificationChannel()
    {
        NotificationChannel nc = new NotificationChannel(
                getString(R.string.nc_id),
                getString(R.string.nc_name),
                NotificationManager.IMPORTANCE_HIGH
        );
        nc.setDescription(getString(R.string.nc_desc));
        NotificationManager notificationManager =
                getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(nc);
    }

    /**
     * Sets the alarm with current time picker settings.
     */
    private void _setAlarm()
    {
        // get desired alarm time
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, _timePicker.getHour());
        alarmTime.set(Calendar.MINUTE, _timePicker.getMinute());
        alarmTime.set(Calendar.SECOND, 0);

        // check against current time
        Calendar currentTime = Calendar.getInstance();
        if (alarmTime.getTimeInMillis() <= currentTime.getTimeInMillis())
        {
            // increase alarm time day by 1
            alarmTime.add(Calendar.DATE, 1);
        }

        // construct intent and high priority notification
        Intent intent =
                new Intent(SettingActivity.this, AlarmReceiver.class);
        _pendingIntent = PendingIntent.getBroadcast(
                SettingActivity.this, 0, intent, 0);

//            _alarmManager.set(AlarmManager.RTC, alarmTime.getTimeInMillis(),
//                    _pendingIntent);
        _alarmManager.set(AlarmManager.RTC, currentTime.getTimeInMillis() + 1000, _pendingIntent);

        Log.d(toString(), "Alarm set at" + alarmTime.toString());
    }

    /**
     * Handles alarm toggle button being clicked.
     */
    public void onToggleClicked(View view)
    {
        if (((ToggleButton) view).isChecked())
        {
            // alarm turned on
            _setAlarm();
        }
        else
        {
            // alarm turned off
            _alarmManager.cancel(_pendingIntent);

            Log.d(toString(), "Alarm unset");
        }
    }
}
