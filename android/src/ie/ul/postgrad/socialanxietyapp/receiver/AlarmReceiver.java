package ie.ul.postgrad.socialanxietyapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import ie.ul.postgrad.socialanxietyapp.service.MoodNotificationService;

public class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent service1 = new Intent(context, MoodNotificationService.class);
            service1.setData((Uri.parse("custom://"+System.currentTimeMillis())));
            context.startService(service1);
        }
}
