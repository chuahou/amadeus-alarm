package dev.chuahou.amadeusalarm.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import dev.chuahou.amadeusalarm.R;

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(toString(), "Alarm received");

        // ring alarm
        Ringer.getInstance().start(context);

        // start alarm activity
        Intent newIntent = new Intent(context, AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, newIntent, 0);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context,
                        context.getString(R.string.nc_id))
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setSound(null)
                        .setContentTitle("Call from Kurisu.")
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setFullScreenIntent(pendingIntent, true);
        NotificationManager nm =
                context.getSystemService(NotificationManager.class);
        nm.notify(0, builder.build());
    }
}
