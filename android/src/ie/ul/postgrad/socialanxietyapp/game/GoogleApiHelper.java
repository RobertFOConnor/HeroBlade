package ie.ul.postgrad.socialanxietyapp.game;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.screens.AvatarCustomizationActivity;
import ie.ul.postgrad.socialanxietyapp.screens.MapsActivity;

/**
 * Created by Robert on 14-Aug-17.
 */

public class GoogleApiHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = GoogleApiHelper.class.getSimpleName();
    Context context;
    GoogleApiClient mGoogleApiClient;
    boolean GAMES = false;

    public GoogleApiHelper(Context context) {
        this.context = context;
        buildGoogleApiClient();
        //connect();
    }

    public GoogleApiClient getGoogleApiClient() {
        return this.mGoogleApiClient;
    }

    public void connect() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                advanceScreen();
            } else {
                mGoogleApiClient.connect();
            }
        }
    }

    public void disconnect() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public boolean isConnected() {
        if (mGoogleApiClient != null) {
            return mGoogleApiClient.isConnected();
        } else {
            return false;
        }
    }

    private void buildGoogleApiClient() {
        if (GAMES) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                    .build();
        } else {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    //.addApi(Games.API).addScope(Games.SCOPE_GAMES)
                    .build();
        }

    }

    public void unlockAchievement(String achievementName) {
        if (mGoogleApiClient.isConnected() && isGAMES()) {
            Games.Achievements.unlock(mGoogleApiClient, achievementName);
        }
    }

    public boolean isGAMES() {
        return GAMES;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.d(TAG, context.getString(R.string.play_services_failed_connection) + result.getErrorCode());
        // Put code here to display the sign-in button
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // show sign-out button, hide the sign-in button
        advanceScreen();
    }

    private void advanceScreen() {
        boolean userExists;
        if (GAMES) {
            String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
            String name = Games.Players.getCurrentPlayer(mGoogleApiClient).getDisplayName();

            userExists = GameManager.getInstance().initDatabaseHelper(context, playerId, name, "guest@guest.com", "");
        } else {
            userExists = GameManager.getInstance().initDatabaseHelper(context, "guest1", "Guest", "guest@guest.com", "guest");
        }

        if (userExists) {
            Intent i = new Intent(context, MapsActivity.class);
            context.startActivity(i);
        } else {
            Intent i = new Intent(context, AvatarCustomizationActivity.class);
            context.startActivity(i);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, context.getString(R.string.play_services_suspended_connection));
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}