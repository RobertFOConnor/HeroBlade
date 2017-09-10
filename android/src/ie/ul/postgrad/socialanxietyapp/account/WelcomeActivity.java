package ie.ul.postgrad.socialanxietyapp.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.UUID;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.screens.InputNameActvity;
import ie.ul.postgrad.socialanxietyapp.screens.MapsActivity;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        App.setStatusBarColor(this);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        GameManager.getInstance().initDatabaseHelper(this);
        firstTime = GameManager.getInstance().getPlayer().getName().equals("");
        if (!firstTime) {
            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);
            finish();
        }
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
            GameManager.getInstance().initDatabaseHelper(this, UUID.randomUUID().toString(), "Guest", "guest@guest.com", "");
            Intent i = new Intent(this, InputNameActvity.class);
            startActivity(i);
            finish();
        }
    }
}

