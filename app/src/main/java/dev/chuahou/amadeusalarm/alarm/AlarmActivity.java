package dev.chuahou.amadeusalarm.alarm;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import dev.chuahou.amadeusalarm.R;

public class AlarmActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // draw over lock screen
        setTurnScreenOn(true);
        setShowWhenLocked(true);

        Log.d(toString(), "Alarm started");
    }

    /**
     * Handles alarm stop button being pressed.
     */
    public void onConnectClick(View view)
    {
        Ringer.getInstance().stop();

        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.cancel(0);
        finish();

        Log.d(toString(), "Alarm stopped");
    }
}
