package ie.ul.postgrad.socialanxietyapp.account;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.screens.AvatarCustomizationActivity;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_color));
        }

        findViewById(R.id.guest).setOnClickListener(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
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
        /*if (view.getId() == R.id.guest) {
            GameManager.getInstance().initDatabaseHelper(getApplicationContext(), "guest1", "Guest", "guest@guest.com", "guest");
            Intent i = new Intent(getApplicationContext(), AvatarCustomizationActivity.class);
            startActivity(i);
        }*/

        if (view.getId() == R.id.sign_in_button) {
            // start the asynchronous sign in flow
            App.getInstance().getGoogleApiHelperInstance().connect();
        }
    }

}
