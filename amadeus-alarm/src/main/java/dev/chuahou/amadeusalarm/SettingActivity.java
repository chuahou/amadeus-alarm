package dev.chuahou.amadeusalarm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import dev.chuahou.amadeusalarm.alarm.Alarm;

public class SettingActivity extends Activity
{
    private TextView _text;
    private TimePicker _picker;

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat _fmt = new SimpleDateFormat("MM-dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Get widgets
        _text = findViewById(R.id.setting_text);
        _picker = findViewById(R.id.setting_timepicker);

        // Set picker to 24h
        _picker.setIs24HourView(true);

        // Set time picker to currently set alarm time
        Calendar alarmTime = Alarm.getInstance().getAlarmTime(this);
        if (alarmTime == null)
        {
            _text.setText(R.string.status_off);
        }
        else
        {
            _text.setText(R.string.status_on);
            _picker.setHour(alarmTime.get(Calendar.HOUR_OF_DAY));
            _picker.setMinute(alarmTime.get(Calendar.MINUTE));
            _text.append("\n" + _fmt.format(alarmTime.getTime()));
        }
    }

    /**
     * Handles turn alarm on button clicked.
     * @param view clicked button
     */
    public void onOnClicked(@SuppressWarnings("unused") View view)
    {
        Log.d("SETTING", "ON");

        // get desired time
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, _picker.getHour());
        time.set(Calendar.MINUTE, _picker.getMinute());
        time.set(Calendar.SECOND, 0);

        // get current time and check if alarm should be next day
        if (time.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
        {
            time.add(Calendar.DAY_OF_MONTH, 1);
        }

        // set alarm
        Alarm.getInstance().setAlarmTime(this, time);

        // update text
        _text.setText(R.string.status_on);
        _text.append("\n" + _fmt.format(time.getTime()));
    }

    /**
     * Handles turn alarm off button clicked.
     * @param view clicked button
     */
    public void onOffClicked(@SuppressWarnings("unused") View view)
    {
        Log.d("SETTING", "OFF");

        // cancel alarm
        Alarm.getInstance().stopAlarm(this);

        // update text
        _text.setText(R.string.status_off);
    }
}
