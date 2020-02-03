package dev.chuahou.amadeusalarm;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import dev.chuahou.amadeusalarm.alarm.Alarm;

public class LaunchActivity extends Activity
{
    private TextView _text;
    private MediaPlayer _mp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        // get text view
        _text = findViewById(R.id.launch_text);

        // set alarm text and show over lock screen
        if (Alarm.getInstance().isRinging())
        {
            _text.setText(R.string.call_from_kurisu);

            // show above lock screen
            setTurnScreenOn(true);
            setShowWhenLocked(true);
            KeyguardManager km =
                    (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (km.isKeyguardLocked())
            {
                km.requestDismissKeyguard(this, null);
            }
        }
        // set launch text
        else
        {
            _text.setText(R.string.connect_to_kurisu);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // start animation
        ImageView logo = findViewById(R.id.launch_logo);
        ((AnimationDrawable) logo.getDrawable()).start();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // enable buttons
        _setButtonsEnabled(true);

        // if just finished setting, set to disconnected
        if (_text.getText().equals(getString(R.string.connecting)))
        {
            _text.setText(R.string.disconnected);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        // release media player if necessary
        if (_mp != null) _mp.release();
    }

    // Prevent leaving when in alarm state
    @Override
    public void onBackPressed()
    {
        if (!Alarm.getInstance().isRinging()) super.onBackPressed();
    }

    public void onConnectClicked(View view)
    {
        Log.d("LAUNCH", "CONNECT CLICKED");

        // highlight button
        ((ImageView) view).setImageDrawable(
                getDrawable(R.drawable.connect_select));

        // handle if alarm
        if (Alarm.getInstance().isRinging())
        {
            Alarm.getInstance().stopAlarm(LaunchActivity.this);

            // start DoneActivity
            Intent i = new Intent(
                    LaunchActivity.this, DoneActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        else
        {
            // start connecting
            _connect();
        }
    }

    public void onCancelClicked(View view)
    {
        Log.d("LAUNCH", "CANCEL CLICKED");

        // if not alarm exit
        if (!Alarm.getInstance().isRinging())
        {
            finish();
            return;
        }

        // start SnoozeActivity
        Alarm.getInstance().stopAlarm(this);
        ((ImageView) view).setImageDrawable(
                getDrawable(R.drawable.cancel_select));
        Intent i = new Intent(this, SnoozeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    /**
     * Enables or disables both buttons.
     * @param enable whether to enable/disable
     */
    private void _setButtonsEnabled(boolean enable)
    {
        ImageView connect = findViewById(R.id.launch_connect);
        ImageView cancel = findViewById(R.id.launch_cancel);

        connect.setClickable(enable);
        cancel.setClickable(enable);

        if (enable)
        {
            // reset images
            connect.setImageDrawable(getDrawable(R.drawable.connect_unselect));
            cancel.setImageDrawable(getDrawable(R.drawable.cancel_unselect));
        }
    }

    /**
     * Starts connecting sequence and transitions to SettingActivity.
     */
    private void _connect()
    {
        Log.d("LAUNCH", "CONNECTING");
        _text.setText(R.string.connecting);
        _setButtonsEnabled(false);

        // play tone
        _mp = MediaPlayer.create(this, R.raw.tone);
        _mp.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener()
                {
                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        mp.release();
                        startActivity(new Intent(LaunchActivity.this,
                                SettingActivity.class));
                    }
                }
        );
        _mp.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener()
                {
                    @Override
                    public void onPrepared(MediaPlayer mp)
                    {
                        mp.start();
                    }
                }
        );
    }
}

