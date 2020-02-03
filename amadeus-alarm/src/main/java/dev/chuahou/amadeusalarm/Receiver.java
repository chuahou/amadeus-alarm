package dev.chuahou.amadeusalarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
        Ringer.getInstance().start(context);
        Alarm.getInstance().ringing = true;

        // create notification channel
        _createNotificationChannel(context);

        // send notification
        Intent i = new Intent(context, LaunchActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);
        Notification.Builder builder =
                new Notification.Builder(context,
                        context.getString(R.string.nc_id))
                    .setSmallIcon(R.drawable.incoming_call)
                    .setContentTitle(context.getString(
                            R.string.call_from_kurisu))
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setFullScreenIntent(pi, true)
                    .setCategory(Notification.CATEGORY_ALARM);
        NotificationManager nm =
                context.getSystemService(NotificationManager.class);
        nm.notify(0, builder.build());
    }

    /**
     * Creates notification channel.
     * @param context
     */
    private void _createNotificationChannel(Context context)
    {
        NotificationChannel nc = new NotificationChannel(
                context.getString(R.string.nc_id),
                context.getString(R.string.nc_name),
                NotificationManager.IMPORTANCE_HIGH
        );
        nc.setDescription(context.getString(R.string.nc_desc));
        nc.setSound(null, null);
        NotificationManager notificationManager =
                context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(nc);
    }
}
