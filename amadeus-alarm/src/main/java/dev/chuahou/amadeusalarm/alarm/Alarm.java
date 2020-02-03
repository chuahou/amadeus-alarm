package dev.chuahou.amadeusalarm.alarm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Calendar;

import dev.chuahou.amadeusalarm.Ids;
import dev.chuahou.amadeusalarm.LaunchActivity;
import dev.chuahou.amadeusalarm.R;
import dev.chuahou.amadeusalarm.Receiver;

import static android.content.Context.ALARM_SERVICE;

/**
 * Implementation of model of the alarm.
 */
public class Alarm
{
    // shared preferences IDs
    private static final String _spName = "AmadeusSP";
    private static final String _spTimeLong = "alarmTime";

    // singleton pattern
    private static Alarm _instance = null;
    private Alarm() {}
    public static Alarm getInstance()
    {
        if (_instance == null) _instance = new Alarm();
        return _instance;
    }

    // whether the alarm is currently running
    private boolean _ringing = false;

    /**
     * Get currently set alarm time.
     *
     * @param context application context
     * @return currently set alarm time
     */
    public Calendar getAlarmTime(Context context)
    {
        Log.d("ALARM", "GET");

        // get saved alarm time
        SharedPreferences sp = context.getSharedPreferences(_spName,
                Context.MODE_PRIVATE);
        long alarmTimeMillis = sp.getLong(_spTimeLong, -1L);

        // not set
        if (alarmTimeMillis < 0L) return null;

        // set Calendar instance
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(alarmTimeMillis);

        // set alarm again in case of unexpected circumstances
        setAlarmTime(context, c);

        // return Calendar instance
        return c;
    }

    /**
     * Sets alarm time. Cancels alarm if time is null.
     *  @param context application context
     * @param time time to set alarm to
     */
    @SuppressLint("ApplySharedPref")
    // commit() to ensure alarm is set immediately
    public void setAlarmTime(Context context, Calendar time)
    {
        Log.d("ALARM", "SET");

        // stop alarm
        stopAlarm(context);

        SharedPreferences.Editor editor = context.getSharedPreferences(
                _spName, Context.MODE_PRIVATE).edit();

        // time is null, cancel alarm
        if (time == null)
        {
            cancelAlarm(context);
            editor.putLong(_spTimeLong, -1L);
            editor.commit();
            return;
        }

        // set alarm
        editor.putLong(_spTimeLong, time.getTimeInMillis());
        editor.commit();

        // post notifications
        _postAlarmNotification(context, time, true);
        _postAlarmSetNotification(context, true);
    }

    /**
     * Stops currently ringing alarm.
     *
     * @param context application context
     */
    public void stopAlarm(Context context)
    {
        // stops ringing
        _ringing = false;
        Ringer.getInstance().stop();

        // cancel notifications
        _postAlarmNotification(context, null, false);
        _postAlarmSetNotification(context, false);
        _postRingingNotification(context, false);
    }

    /**
     * Starts ringing alarm.
     *
     * @param context application context
     */
    public void startAlarm(Context context)
    {
        // starts ringing
        _ringing = true;
        Ringer.getInstance().start(context);

        // sends notification
        _postRingingNotification(context, true);
    }

    /**
     * Cancels set alarm.
     *
     * @param context application context
     */
    public void cancelAlarm(Context context)
    {
        // stop any currently ringing alarm
        stopAlarm(context);
    }

    /**
     * Checks whether alarm is currently ringing
     *
     * @return true if alarm is ringing
     */
    public boolean isRinging() { return _ringing; }

    /**
     * Posts/cancels alarm ringing notification with full screen intent.
     *
     * @param context application context
     * @param time time to ring alarm at
     * @param mode true to post, false to cancel
     */
    private void _postAlarmNotification(Context context, Calendar time,
                                   boolean mode)
    {
        // create alarm pending intent
        PendingIntent pi = PendingIntent.getBroadcast(
                context, Ids.RC_ALARM, new Intent(context, Receiver.class), 0
        );

        // get alarm manager
        AlarmManager am =
                (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (am != null)
        {
            // post
            if (mode)
            {
                // send alarm to Receiver
                am.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pi
                );
            }
            // cancel
            else
            {
                // cancel alarm
                am.cancel(pi);
            }
        }
        else
        {
            Log.e("ERROR", "COULD NOT GET ALARMMANAGER");
        }
    }

    /**
     * Posts/cancels "Alarm is set" notification.
     *
     * @param context application context
     * @param mode true to post, false to cancel
     */
    private void _postAlarmSetNotification(Context context, boolean mode)
    {
        // get notification manager
        NotificationManager nm =
                context.getSystemService(NotificationManager.class);
        if (nm == null)
        {
            Log.e("ERROR", "COULD NOT GET NOTIFICATION MANAGER");
            return;
        }

        // post
        if (mode)
        {
            // create notification channel
            NotificationChannel nc = new NotificationChannel(
                    context.getString(R.string.nc2_id),
                    context.getString(R.string.nc2_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            nc.setDescription(context.getString(R.string.nc2_desc));
            nc.setSound(null, null);

            // send notification
            PendingIntent pi = PendingIntent.getActivity(
                    context, Ids.RC_ALARM_SET, new Intent(),
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            Notification.Builder builder =
                    new Notification.Builder(context,
                            context.getString(R.string.nc2_id))
                    .setSmallIcon(R.drawable.xp2)
                    .setContentTitle(context.getString(R.string.status_on))
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setCategory(Notification.CATEGORY_PROGRESS);
            nm.notify(Ids.NOTIF_ALARM_SET, builder.build());
        }
        // cancel
        else
        {
            nm.cancel(Ids.NOTIF_ALARM_SET);
        }
    }

    /**
     * Posts/cancels ringing notification.
     *
     * @param context application context
     * @param mode true to post, false to cancel
     */
    private void _postRingingNotification(Context context, boolean mode)
    {
        // get permissions
        if ((context.checkSelfPermission(Manifest.permission.WAKE_LOCK)
                != PackageManager.PERMISSION_GRANTED) ||
                (context.checkSelfPermission(
                        Manifest.permission.USE_FULL_SCREEN_INTENT)
                        != PackageManager.PERMISSION_GRANTED))
            Log.e("ERROR", "NO PERMISSIONS");

        // get notification manager
        NotificationManager nm =
                context.getSystemService(NotificationManager.class);
        if (nm == null)
        {
            Log.e("ERROR", "COULD NOT GET NOTIFICATION MANAGER");
            return;
        }

        // post
        if (mode)
        {
            // create notification channel
            NotificationChannel nc = new NotificationChannel(
                    context.getString(R.string.nc_id),
                    context.getString(R.string.nc_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            nc.setDescription(context.getString(R.string.nc_desc));
            nc.setSound(null, null);

            // send notification
            Intent i = new Intent(context, LaunchActivity.class);
            PendingIntent pi = PendingIntent.getActivity(
                    context, Ids.RC_RINGING, i, 0
            );
            Notification.Builder builder =
                    new Notification.Builder(context,
                            context.getString(R.string.nc_id))
                        .setSmallIcon(R.drawable.incoming_call)
                        .setContentTitle(
                                context.getString(R.string.call_from_kurisu))
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setFullScreenIntent(pi, true)
                        .setCategory(Notification.CATEGORY_ALARM);
            nm.notify(Ids.NOTIF_RINGING, builder.build());
        }
        // cancel
        else
        {
            nm.cancel(Ids.NOTIF_RINGING);
        }
    }
}