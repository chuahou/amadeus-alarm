package dev.chuahou.amadeusalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("RECEIVER", "RECEIVED");

        // ring alarm
        Alarm.getInstance().startAlarm(context);
    }
}
