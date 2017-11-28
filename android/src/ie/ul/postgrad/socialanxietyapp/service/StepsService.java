package ie.ul.postgrad.socialanxietyapp.service;

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
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;
import ie.ul.postgrad.socialanxietyapp.screens.ChestOpenActivity;
import ie.ul.postgrad.socialanxietyapp.screens.MapsActivity;

/**
 * Created by Robert on 16-Mar-17.
 * <p>
 * Android Service for detecting when the user is taking steps using the sensor.
 */

public class StepsService extends Service implements SensorEventListener {

    private boolean usingDetector = true;
    boolean firstReading = true;
    private int totalSteps;

    @Override
    public void onCreate() {
        super.onCreate();

        SensorManager mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        Sensor mStepDetectorSensor;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)) {
                mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
                mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
                mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
                mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
                usingDetector = false;
            }
        }

        firstReading = true;
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
        GameManager gm = GameManager.getInstance();
        float totalDistance;
        float dist;
        if (usingDetector) {
            dist = 0.8f;
        } else {
            int steps;
            if (firstReading) {
                totalSteps = (int) event.values[0];
                firstReading = false;
                dist = 0;
            } else {
                steps = (int) (event.values[0] - totalSteps);
                dist = 0.8f * steps;
                totalSteps += steps;
            }
        }
        totalDistance = gm.getDistance(getApplicationContext()) + dist;
        gm.recordStep(getApplicationContext(), totalDistance);

        ArrayList<ChestItem> removals = new ArrayList<>();
        ArrayList<ChestItem> chests = gm.getInventory().getChests();
        for (ChestItem chest : chests) {
            chest.setCurrDistance(chest.getCurrDistance() - dist);
            gm.updateChest(chest);

            if (chest.getCurrDistance() <= 0) {
                gm.addChestOpened();
                gm.awardMoney(chest.getRarity() * 500);
                WeaponItem weaponItem = gm.unlockWeapon(this, chest.getRarity());
                Intent result = chestOpenIntent(chest.getId(), weaponItem.getId());
                removals.add(chest);

                if (!MapsActivity.active) {
                    notifyOpenedChest(result, chest);
                } else {
                    startActivity(result);
                }
            }
        }

        for (ChestItem chest : removals) {
            chests.remove(chest);
            gm.removeChest(chest);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private Intent chestOpenIntent(int chestId, int swordId) {
        Intent resultIntent = new Intent(this, ChestOpenActivity.class);
        resultIntent.putExtra(ChestOpenActivity.CHEST_ID_KEY, chestId);
        resultIntent.putExtra(ChestOpenActivity.SWORD_ID_KEY, swordId);
        return resultIntent;
    }

    /* Example method for what chest open notification might look like.. */
    private void notifyOpenedChest(Intent resultIntent, ChestItem chest) {
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
                        .setSmallIcon(chest.getImageID())
                        .setContentTitle(getString(R.string.notify_chest, chest.getName()))
                        .setContentText(getString(R.string.notify_chest_body, (int) (chest.getMaxDistance() / 1000), chest.getName()))
                        .setVibrate(new long[]{1000, 1000})
                        .setLights(Color.BLUE, 3000, 3000)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = (int) (Math.random() * 1000);// Set an ID for the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);// Get an instance of NotificationManager service

        if (notificationManager != null) {
            notificationManager.notify(mNotificationId, mBuilder.build()); // Build the notification and issue it.
        }
    }
}
