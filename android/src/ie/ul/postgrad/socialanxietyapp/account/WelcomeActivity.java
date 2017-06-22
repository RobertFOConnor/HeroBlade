package ie.ul.postgrad.socialanxietyapp.account;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import ie.ul.postgrad.socialanxietyapp.AvatarCustomizationActivity;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.map.MapsActivity;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        findViewById(R.id.guest).setOnClickListener(this);
        findViewById(R.id.sign_in).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_color));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in:
                signIn();
                break;
            case R.id.register:
                register();
                break;
            case R.id.guest:
                GameManager.getInstance().startGame(getApplicationContext(), "guest1", "Guest", "guest@guest.com", "guest");
                Intent i = new Intent(getApplicationContext(), AvatarCustomizationActivity.class);
                startActivity(i);

                break;
        }
    }

    private void signIn() {
        Intent i = new Intent(getApplicationContext(), LogInActivity.class);
        startActivity(i);
    }

    private void register() {
        Intent i = new Intent(getApplicationContext(), CreateAccountActivity.class);
        startActivity(i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
