package ie.ul.postgrad.socialanxietyapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.database.DBHelper;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;
import ie.ul.postgrad.socialanxietyapp.screens.ChestOpenActivity;
import ie.ul.postgrad.socialanxietyapp.screens.MapsActivity;

/**
 * Created by Robert on 16-Mar-17.
 * <p>
 * Android Service for detecting when the user is taking steps using the sensor.
 */

public class StepsService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;
    private DBHelper dbHelper;
    private float totalDistance;
    private ArrayList<ChestItem> chests;

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        dbHelper = new DBHelper(this);
        totalDistance = dbHelper.getDistance();

        chests = GameManager.getInstance().getPlayer().getChests();
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

        totalDistance += 0.8f;
        dbHelper.insertStepsEntry(totalDistance);

        for (ChestItem chest : chests) {
            chest.setCurrDistance(chest.getCurrDistance() - 0.8f);

            if (chest.getCurrDistance() < 0) {
                if(!MapsActivity.active) {
                    notifyOpenedChest(chest);
                }
                chests.remove(chest);
                GameManager.getInstance().getPlayer().getChests().remove(0);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /* Example method for what chest open notification might look like.. */
    private void notifyOpenedChest(ChestItem chest) {
        Intent resultIntent = new Intent(this, ChestOpenActivity.class);
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
                        .setContentTitle("You've unlocked a " + chest.getName())
                        .setContentText("You walked " + chest.getMaxDistance() + "km. The " + chest.getName() + " has been opened.")
                        .setVibrate(new long[]{1000, 1000})
                        .setLights(Color.BLUE, 3000, 3000)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = 1234;// Set an ID for the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);// Get an instance of NotificationManager service
        notificationManager.notify(mNotificationId, mBuilder.build()); // Build the notification and issue it.
    }
}
