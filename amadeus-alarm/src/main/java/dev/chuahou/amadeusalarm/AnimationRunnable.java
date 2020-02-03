package dev.chuahou.amadeusalarm;


import android.content.Context;
import android.os.Handler;
import android.widget.ImageView;

/**
 * Runnable class implementing the Amadeus logo animation.
 */
class AnimationRunnable implements Runnable
{
    private final Context _context; // application context
    private final ImageView _logo; // logo ImageView
    private final Handler _handler; // Handler to schedule frames
    private int _i = 1; // iterator through frames
    private final int _frames; // total number of frames
    private final int _timeBetweenFrames; // time between frames for Handler

    /**
     * Constructor for Amadeus logo animation Runnable.
     * @param context application context
     * @param logo logo ImageView
     * @param handler Handler to schedule frames
     */
    AnimationRunnable(
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
