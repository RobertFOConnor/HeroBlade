package ie.ul.postgrad.socialanxietyapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Robert on 16-Mar-17.
 * <p>
 * Android Service for detecting when the user is taking steps using the sensor.
 */

public class StepsService extends Service implements SensorEventListener, LocationListener {

    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;
    private DBHelper dbHelper;

    private LocationManager mLocationManager;
    private Location mCurrentLocation;

    //Calculating distance and steps
    private Location startLoc;
    private float totalDistance = 0f;
    private float prevSteps = 0;

    boolean chestOpened = false;

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            dbHelper = new DBHelper(this);

        }

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (5 * 1000), 0, this);
        totalDistance = dbHelper.getDistance();
        prevSteps = dbHelper.getSteps();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        System.out.println("STEPS ADDED:");
        dbHelper.insertStepsEntry(totalDistance);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (startLoc == null) {
            startLoc = location;
        }

        mCurrentLocation = location;


        float dist = startLoc.distanceTo(mCurrentLocation);
        float steps = dbHelper.getSteps() - prevSteps;

        if (steps > 0) {
            float stepDist = steps * 0.8f; //avg step is 0.8m
            float estDistance = (stepDist + dist) / 2;

            totalDistance += estDistance;
            prevSteps = dbHelper.getSteps();

            startLoc = mCurrentLocation;

            if (totalDistance > 100 && !chestOpened) {
                notifyOpenedChest();
                chestOpened = true;
            }
        }
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

    private void notifyOpenedChest() {
        Intent resultIntent = new Intent(this, MapsActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_chest_open)
                        .setContentTitle("You've unlocked a treasure chest!")
                        .setContentText("You walked 2km. The chest has been opened.")
                        .setVibrate(new long[]{1000, 1000})
                        .setLights(Color.BLUE, 3000, 3000)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
