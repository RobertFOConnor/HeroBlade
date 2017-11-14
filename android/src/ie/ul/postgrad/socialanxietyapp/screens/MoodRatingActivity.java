package ie.ul.postgrad.socialanxietyapp.screens;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;

public class MoodRatingActivity extends AppCompatActivity implements View.OnClickListener {

    private int rating = -1; //Holder for mood rating.
    private String desc = ""; //Holder for mood description.

    private boolean animating = false; //Have mood views been animated.
    private boolean inserted = false; //Has mood rating and description been inserted into database.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_rating);

        //Create view array for mood face views.
        View[] moods = new View[5];
        moods[0] = findViewById(R.id.mood_0);
        moods[1] = findViewById(R.id.mood_1);
        moods[2] = findViewById(R.id.mood_2);
        moods[3] = findViewById(R.id.mood_3);
        moods[4] = findViewById(R.id.mood_4);

        //Apply opening offset animation to each and setup listeners.
        for (int i = 0; i < 5; i++) {
            moods[i].setOnClickListener(this);
            ScaleAnimation scale = getScaleAnim(0, 1);
            scale.setStartOffset(i * 120);
            moods[i].setAnimation(scale);
        }

        //Set up button listeners.
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.continue_button).setOnClickListener(this);
    }

    //Returns a scaling animation from first value to second (scale factor).
    private ScaleAnimation getScaleAnim(int fromS, int toS) {
        ScaleAnimation scale
                = new ScaleAnimation(fromS, toS, fromS, toS,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(500);
        return scale;
    }

    //Transitions between initial mood faces, to chosen mood and description box.
    public void animateMoods() {
        ScaleAnimation scale = getScaleAnim(1, 0);

        final View v = findViewById(R.id.mood_container);

        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        v.startAnimation(scale);

        Animation a = new AlphaAnimation(1.0f, 0.0f);
        a.setDuration(500);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.title_text).setVisibility(View.GONE);

                findViewById(R.id.result_confirm).setVisibility(View.VISIBLE);
                findViewById(R.id.description_container).setVisibility(View.VISIBLE);
                ScaleAnimation scale = getScaleAnim(0, 1);
                findViewById(R.id.result_confirm).startAnimation(scale);

                Animation a = new AlphaAnimation(0, 1);
                a.setDuration(500);
                findViewById(R.id.description_container).startAnimation(a);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        findViewById(R.id.title_text).startAnimation(a);
    }

    public void setImageResult(int res) {
        if (!animating) {
            playSound(SoundManager.Sound.CLICK);
            ((ImageView) findViewById(R.id.result_confirm)).setImageResource(res);
            animateMoods();
            animating = true;
            rating = res;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mood_0:
                setImageResult(R.drawable.mood_0);
                break;
            case R.id.mood_1:
                setImageResult(R.drawable.mood_1);
                break;
            case R.id.mood_2:
                setImageResult(R.drawable.mood_2);
                break;
            case R.id.mood_3:
                setImageResult(R.drawable.mood_3);
                break;
            case R.id.mood_4:
                setImageResult(R.drawable.mood_4);
                break;
            case R.id.back_button:
                playSound(SoundManager.Sound.BACK);
                finish();
                break;
            case R.id.continue_button:
                if (!inserted) {
                    desc = ((EditText) findViewById(R.id.feeling_field)).getText().toString();
                    insertMoodInDatabase();
                    playSound(SoundManager.Sound.CLICK);
                    finish();
                }
                break;
        }
    }

    //Shorten method for playing sounds.
    private void playSound(SoundManager.Sound s) {
        SoundManager.getInstance(this).playSound(s);
    }

    //Inserts mood data into database (only once).
    private void insertMoodInDatabase() {
        GameManager gm = GameManager.getInstance();
        gm.initDatabaseHelper(this).insertMoodRating(gm.getPlayer().getId(), rating, desc);
        inserted = true;
    }
}