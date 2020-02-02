package dev.chuahou.amadeusalarm.alarm;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import dev.chuahou.amadeusalarm.R;

public class AlarmActivity extends Activity
{
    private Ringtone _ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // start alarm
        Uri alarmTone =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        _ringtone =
                RingtoneManager.getRingtone(getApplicationContext(), alarmTone);
        _ringtone.setAudioAttributes(new AudioAttributes.Builder().
                setUsage(AudioAttributes.USAGE_ALARM).build());
        _ringtone.setLooping(true);
        _ringtone.play();

        Log.d(toString(), "Alarm started");
    }

    /**
     * Handles alarm stop button being pressed.
     */
    public void onConnectClick(View view)
    {
        _ringtone.stop();

        Log.d(toString(), "Alarm stopped");
    }
}
