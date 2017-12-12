package ie.ul.postgrad.socialanxietyapp.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;

import java.util.UUID;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;
import ie.ul.postgrad.socialanxietyapp.screens.HelpActivity;
import ie.ul.postgrad.socialanxietyapp.screens.MapsActivity;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        App.setStatusBarColor(this);
        generateUID();

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GameManager.getInstance().initDatabase(this);
        firstTime = GameManager.getInstance().getPlayer().getName().equals("");

        ScaleAnimation scale
                = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(600);
        scale.setStartOffset(500);
        scale.setInterpolator(new OvershootInterpolator());
        findViewById(R.id.linear_view).startAnimation(scale);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            if (!firstTime) {
                Intent i = new Intent(this, MapsActivity.class);
                startActivity(i);
            } else {
                Intent i = new Intent(this, HelpActivity.class);
                i.putExtra(HelpActivity.INFO_KEY, HelpActivity.WELCOME_INFO);
                i.putExtra(HelpActivity.REVIEW_KEY, false);
                i.putExtra(HelpActivity.TRANSPARENT_KEY, false);
                startActivity(i);
            }
            finish();
            SoundManager.getInstance(this).playSound(SoundManager.Sound.CLICK);
        }
    }

    public void generateUID() {
        SharedPreferences prefs = this.getSharedPreferences("ie.ul.postgrad.socialanxietyapp", Context.MODE_PRIVATE);

        final String key = "firstTimeUID";
        String uid = prefs.getString(key, "");
        if (uid.equals("")) {
            prefs.edit().putString(key, UUID.randomUUID().toString()).apply();
        }
    }
}

