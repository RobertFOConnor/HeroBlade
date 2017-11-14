package ie.ul.postgrad.socialanxietyapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.service.StepsService;
import ie.ul.postgrad.socialanxietyapp.sync.SyncManager;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SyncManager.getInstance().startSyncAdapter(context);
        scheduleAlarms(context);

        //Start step counter and location service
        Intent mStepsIntent = new Intent(context, StepsService.class);
        context.startService(mStepsIntent);
    }

    static void scheduleAlarms(Context context) {
        GameManager.getInstance().setMoodRatingAlarm(context);
    }
}
