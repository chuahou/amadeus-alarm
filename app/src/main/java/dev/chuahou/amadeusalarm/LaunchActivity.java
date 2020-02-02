package dev.chuahou.amadeusalarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LaunchActivity extends Activity
{
    public enum Status {
        STATUS_LAUNCH,      // first launch:    "Connect to Kurisu?"
        STATUS_CONNECTING,  // connect button:  "Connecting..."
        STATUS_DISCONNECT,  // from settings:   "Disconnected."
        STATUS_ALARM        // alarm ringing:   "Call from Kurisu."
    }
    private Status _status;

    private TextView _text;
    private MediaPlayer _mp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        // start animation
        ImageView logo = findViewById(R.id.launch_logo);
        Handler handler = new Handler();
        handler.post(new AnimationRunnable(this, logo, handler));

        // get text view
        _text = findViewById(R.id.launch_text);

        // set initial state
        setStatusLaunch();

        // add button listeners
        findViewById(R.id.launch_connect).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Log.d(_status.toString(), "CONNECT CLICKED");

                        // highlight button
                        ((ImageView) view).setImageDrawable(
                                getDrawable(R.drawable.connect_select));

                        // set status
                        setStatusConnecting();
                    }
                }
        );
        findViewById(R.id.launch_cancel).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Log.d(_status.toString(), "CANCEL CLICKED");

                        // if not alarm exit
                        if (_status != Status.STATUS_ALARM)
                            finish();

                        // highlight button
                        ((ImageView) view).setImageDrawable(
                                getDrawable(R.drawable.cancel_select));
                    }
                }
        );
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // if just finished setting, set to disconnected
        if (_status == Status.STATUS_CONNECTING)
            setStatusDisconnected();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        // release media player if necessary
        if (_mp != null) _mp.release();
    }

    /**
     * Set status to initial launch.
     */
    public void setStatusLaunch()
    {
        Log.d("STATUS", "LAUNCH");
        _status = Status.STATUS_LAUNCH;
        _text.setText(R.string.connect_to_kurisu);
        _setButtonsEnabled(true);
    }

    /**
     * Set status to connecting.
     */
    public void setStatusConnecting()
    {
        Log.d("STATUS", "CONNECTING");
        _status = Status.STATUS_CONNECTING;
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

    /**
     * Set status to disconnected.
     */
    public void setStatusDisconnected()
    {
        Log.d("STATUS", "DISCONNECTED");
        setStatusLaunch();
        _text.setText(R.string.disconnected);
        _status = Status.STATUS_DISCONNECT;
    }

    /**
     * Set status to alarm ringing.
     */
    public void setStatusAlarm()
    {
        Log.d("STATUS", "ALARM");
        _setButtonsEnabled(true);
        _status = Status.STATUS_ALARM;
        // TODO: to implement
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
}

/**
 * Runnable class implementing the Amadeus logo animation.
 */
class AnimationRunnable implements Runnable
{
    private Context _context; // application context
    private ImageView _logo; // logo ImageView
    private Handler _handler; // Handler to schedule frames
    private int _i = 1; // iterator through frames
    private final int _frames; // total number of frames
    private final int _timeBetweenFrames; // time between frames for Handler

    /**
     * Constructor for Amadeus logo animation Runnable.
     * @param context application context
     * @param logo logo ImageView
     * @param handler Handler to schedule frames
     */
    public AnimationRunnable(
            Context context, ImageView logo, Handler handler)
    {
        _context = context;
        _logo = logo;
        _handler = handler;
        _frames = _context.getResources().getInteger(R.integer.frames);
        _timeBetweenFrames =
                _context.getResources().getInteger(R.integer.time_between_frames);
    }

    /**
     * Runs the animation.
     */
    @Override
    public void run()
    {
        // get next frame
        String filename = "logo" + _i;
        int id = _context.getResources().getIdentifier(filename, "drawable",
                _context.getPackageName());

        // set ImageView to next frame
        _logo.setImageDrawable(_context.getDrawable(id));
        _i++;

        // schedule next frame
        if (_i < _frames) {
            _handler.postDelayed(this, _timeBetweenFrames);
        }
    }
}
