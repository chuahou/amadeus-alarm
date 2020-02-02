package dev.chuahou.amadeusalarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;

import dev.chuahou.amadeusalarm.alarm.AlarmReceiver;

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

        // get time picker
        _timePicker = (TimePicker) findViewById(R.id.alarmTimePicker);

        // get system alarm manager
        _alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    /**
     * Handles alarm toggle button being clicked.
     */
    public void onToggleClicked(View view)
    {
        if (((ToggleButton) view).isChecked())
        {
            // alarm turned on

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

            // set alarm
            Intent intent =
                    new Intent(SettingActivity.this, AlarmReceiver.class);
            _pendingIntent = PendingIntent.getBroadcast(
                    SettingActivity.this, 0, intent, 0);
//            _alarmManager.set(AlarmManager.RTC, alarmTime.getTimeInMillis(),
//                    _pendingIntent);
            _alarmManager.set(AlarmManager.RTC, currentTime.getTimeInMillis() + 2000, _pendingIntent);

            Log.d(toString(), "Alarm set at" + alarmTime.toString());
        }
        else
        {
            // alarm turned off
            _alarmManager.cancel(_pendingIntent);

            Log.d(toString(), "Alarm unset");
        }
    }
}
