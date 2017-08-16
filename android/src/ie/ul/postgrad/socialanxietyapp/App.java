package ie.ul.postgrad.socialanxietyapp;

import android.app.Application;

import ie.ul.postgrad.socialanxietyapp.game.GoogleApiHelper;

/**
 * Created by Robert on 14-Aug-17.
 */

public class App extends Application {
    private GoogleApiHelper googleApiHelper;
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        googleApiHelper = new GoogleApiHelper(mInstance);
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public GoogleApiHelper getGoogleApiHelperInstance() {
        return this.googleApiHelper;
    }
}