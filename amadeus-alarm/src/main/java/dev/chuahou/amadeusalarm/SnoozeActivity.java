package dev.chuahou.amadeusalarm;

import android.app.Activity;
import android.app.KeyguardManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

import dev.chuahou.amadeusalarm.alarm.Alarm;

public class SnoozeActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze);

        // show above lock screen
        setTurnScreenOn(true);
        setShowWhenLocked(true);
        KeyguardManager km =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (km.isKeyguardLocked())
        {
            km.requestDismissKeyguard(this, null);
        }

        // set alarm
        Calendar time = Calendar.getInstance();
        time.add(Calendar.SECOND,
                getResources().getInteger(R.integer.snooze_duration_seconds));
        Alarm.getInstance().setAlarmTime(this, time);

        // wait and quit
        final int duration =
                getResources().getInteger(R.integer.ms_before_quit);
        Log.d("SNOOZE", "WAITING");
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    synchronized (this)
                    {
                        wait(duration);
                        SnoozeActivity.this.finish();
                    }
                }
                catch (InterruptedException e)
                {
                    Log.e("DONE", "INTERRUPTEDEXCEPTION");
                    SnoozeActivity.this.finish(); // quit immediately
                }
            }
        };
        thread.start();
    }
}
