package dev.chuahou.amadeusalarm;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class LaunchActivity extends Activity
{
    protected ImageView _logo;
    private Handler _handler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        // get relevant views
        _logo = findViewById(R.id.launch_logo);

        // start animation
        _handler = new Handler();
        _handler.post(new AnimationRunnable(this, _logo, _handler));
    }
}

class AnimationRunnable implements Runnable
{
    private Context _context;
    private ImageView _logo;
    private Handler _handler;
    private int _i = 1;
    private final int _frames;
    private final int _timeBetweenFrames;

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

    @Override
    public void run()
    {
        if (_i < _frames)
        {
            String filename = "logo" + _i;
            int id = _context.getResources().getIdentifier(filename, "drawable",
                    _context.getPackageName());
            _logo.setImageDrawable(_context.getDrawable(id));
            _i++;
            _handler.postDelayed(this, _timeBetweenFrames);
        }
    }
}
