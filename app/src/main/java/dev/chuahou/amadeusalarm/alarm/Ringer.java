package dev.chuahou.amadeusalarm.alarm;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class Ringer
{
    private static Ringer _instance = null;

    /**
     * Returns the singleton Ringer instance.
     * @return the singleton Ringer instance
     */
    public static Ringer getInstance()
    {
        if (_instance == null) _instance = new Ringer();
        return _instance;
    }

    private Ringtone _r = null;

    private Ringer() {}

    private void _getRingtone(Context context)
    {
        if (_r != null) return;

        Uri alarmTone =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        _r = RingtoneManager.getRingtone(context, alarmTone);
        _r.setAudioAttributes(new AudioAttributes.Builder().
                setUsage(AudioAttributes.USAGE_ALARM).build());
        _r.setLooping(true);
    }

    /**
     * Starts ringing.
     * @param context
     */
    public void start(Context context)
    {
        _getRingtone(context);
        _r.play();
    }

    /**
     * Stops ringing.
     */
    public void stop()
    {
        if (_r != null)
            _r.stop();
    }

    /**
     * Returns true if is currently ringing.
     * @return whether it is currently ringing
     */
    public boolean isRinging()
    {
        return _r != null && _r.isPlaying();
    }
}
