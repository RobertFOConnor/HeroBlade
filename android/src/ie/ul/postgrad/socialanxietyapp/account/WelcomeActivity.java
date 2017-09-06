package ie.ul.postgrad.socialanxietyapp.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.screens.InputNameActvity;
import ie.ul.postgrad.socialanxietyapp.screens.MapsActivity;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        App.setStatusBarColor(this);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.loading).setVisibility(View.GONE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        GameManager.getInstance().initDatabaseHelper(this);
        firstTime = !GameManager.getInstance().getPlayer().getName().equals("");
        if (!firstTime) {
            mGoogleApiClient.connect();
            findViewById(R.id.loading).setVisibility(View.VISIBLE);
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
        /*if (view.getId() == R.id.guest) {
            GameManager.getInstance().initDatabaseHelper(getApplicationContext(), "guest1", "Guest", "guest@guest.com", "guest");
            Intent i = new Intent(getApplicationContext(), AvatarCustomizationActivity.class);
            startActivity(i);
        }*/

        if (view.getId() == R.id.sign_in_button) {
            // start the asynchronous sign in flow
            mGoogleApiClient.connect();
            findViewById(R.id.loading).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        findViewById(R.id.loading).setVisibility(View.GONE);
        App.getInstance().setmGoogleApiClient(mGoogleApiClient);

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String name = Games.Players.getCurrentPlayer(mGoogleApiClient).getDisplayName();
        GameManager.getInstance().initDatabaseHelper(this, playerId, name, "guest@guest.com", "");


        if (!firstTime) {
            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, InputNameActvity.class);
            startActivity(i);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        findViewById(R.id.loading).setVisibility(View.GONE);
    }
}

