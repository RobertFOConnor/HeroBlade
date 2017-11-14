package ie.ul.postgrad.socialanxietyapp.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.screens.MoodRatingActivity;


public class MoodNotificationService extends IntentService {

    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private static int NOTIFICATION_ID = 1;
    Notification notification;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MoodNotificationService(String name) {
        super(name);
    }

    public MoodNotificationService() {
        super("DisplayNotification");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = this.getApplicationContext();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this, MoodRatingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("test", "test");
        mIntent.putExtras(bundle);
        pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Resources res = this.getResources();
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.mood_4)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.mood_4))
                .setTicker("ticker value")
                .setAutoCancel(true)
                .setPriority(8)
                .setSound(soundUri)
                .setContentTitle("How was your day?")
                .setContentText("Do you have a second? Rate your mood for today.").build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        notification.ledARGB = 0xFFFFA500;
        notification.ledOnMS = 800;
        notification.ledOffMS = 1000;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
        Log.i("notif", "Notifications sent.");

    }


}
