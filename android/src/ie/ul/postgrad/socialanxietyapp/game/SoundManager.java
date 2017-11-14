package ie.ul.postgrad.socialanxietyapp.game;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import ie.ul.postgrad.socialanxietyapp.R;

/**
 * Created by Robert on 26-Oct-17.
 */

public class SoundManager {

    private static SoundManager soundManager;
    private static MediaPlayer clickPlayer, backPlayer, markerPlayer;

    public enum Sound {
        CLICK, BACK, MARKER
    }

    private SoundManager(Context context) {
        clickPlayer = MediaPlayer.create(context, R.raw.click);
        backPlayer = MediaPlayer.create(context, R.raw.back);
        markerPlayer = MediaPlayer.create(context, R.raw.marker_click);
    }

    public static SoundManager getInstance(Context context) {
        if (soundManager == null) {
            soundManager = new SoundManager(context);
        }
        return soundManager;
    }

    public void playSound(Sound s) {
        MediaPlayer mp;
        switch (s) {
            case CLICK:
                mp = clickPlayer;
                break;
            case BACK:
                mp = backPlayer;
                break;
            case MARKER:
                mp = markerPlayer;
                break;
            default:
                mp = clickPlayer;
                break;
        }

        if (mp != null) {

            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.start();
        } else {
            Log.e("SOUND", "Not playing sound effect, null");
        }
    }
}
