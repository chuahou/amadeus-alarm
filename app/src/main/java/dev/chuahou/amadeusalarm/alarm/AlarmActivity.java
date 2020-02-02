package dev.chuahou.amadeusalarm.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import dev.chuahou.amadeusalarm.R;

public class AlarmActivity extends AppCompatActivity
{
    private MediaPlayer _player;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // create alarm ringtone player
        Uri alarmTone =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        _player = MediaPlayer.create(this, alarmTone);

        // start alarm
        _player.setLooping(true);
        _player.start();

        Log.d(toString(), "Alarm started");
    }

    /**
     * Handles alarm stop button being pressed.
     */
    public void onConnectClick(View view)
    {
        _player.stop();

        Log.d(toString(), "Alarm stopped");
    }
}
