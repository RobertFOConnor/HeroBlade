package ie.ul.postgrad.socialanxietyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.tasks.OnSuccessListener;

import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;
import ie.ul.postgrad.socialanxietyapp.game.XPLevels;
import ie.ul.postgrad.socialanxietyapp.screens.CraftingActivity;
import ie.ul.postgrad.socialanxietyapp.screens.HelpMenuActivity;
import ie.ul.postgrad.socialanxietyapp.screens.IndexActivity;
import ie.ul.postgrad.socialanxietyapp.screens.InventoryActivity;
import ie.ul.postgrad.socialanxietyapp.screens.PlayerAvatarActivity;
import ie.ul.postgrad.socialanxietyapp.screens.WeaponActivity;

import static com.google.android.gms.auth.api.signin.GoogleSignIn.getLastSignedInAccount;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.ACHIEVEMENTS;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.CRAFTING;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.HELP;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.INDEX;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.INVENTORY;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.LEADERBOARD;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.PROFILE_TEXT;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.WEAPONS;

/**
 * Created by Robert on 14-Dec-17.
 * <p>
 * Navigation menu click listener.
 */

public class DrawerItemClickListener implements ListView.OnItemClickListener {

    private Context context;

    public DrawerItemClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case PROFILE_TEXT:
                startActivity(PlayerAvatarActivity.class);
                break;
            case INVENTORY:
                startActivity(InventoryActivity.class);
                break;
            case WEAPONS:
                startActivity(WeaponActivity.class);
                break;
            case CRAFTING:
                startActivity(CraftingActivity.class);
                break;
            case INDEX:
                startActivity(IndexActivity.class);
                break;
            case ACHIEVEMENTS:
                if (isSignedIn()) {
                    Games.getAchievementsClient(context, getLastSignedInAccount(context))
                            .getAchievementsIntent()
                            .addOnSuccessListener(new OnSuccessListener<Intent>() {
                                @Override
                                public void onSuccess(Intent intent) {
                                    ((Activity) context).startActivityForResult(intent, 1002);
                                }
                            });
                    playClick();
                } else {
                    App.showToast(context, context.getString(R.string.no_google_play_achievements));
                }
                break;
            case LEADERBOARD:

                if (isSignedIn()) {
                    int totalXP = 0;
                    GameManager gm = GameManager.getInstance();
                    for (int j = 0; j < gm.getPlayer().getLevel(); j++) {
                        totalXP += XPLevels.XP_LEVELS[j];
                    }
                    totalXP += gm.getPlayer().getXp();

                    LeaderboardsClient leaderboardsClient = Games.getLeaderboardsClient(context, getLastSignedInAccount(context));
                    leaderboardsClient.submitScore(context.getString(R.string.leaderboard_top_players), totalXP);
                    leaderboardsClient.getLeaderboardIntent(context.getString(R.string.leaderboard_top_players)).addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            ((Activity) context).startActivityForResult(intent, 1001);
                        }
                    });
                    playClick();

                } else {
                    App.showToast(context, context.getString(R.string.no_google_play_leaderboard));
                }
                break;

            case HELP:
                startActivity(HelpMenuActivity.class);
                break;
        }

        DrawerLayout drawer = ((AppCompatActivity) context).findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void startActivity(Class activity) {
        Intent i = new Intent(context, activity);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        playClick();
    }

    private void playClick() {
        SoundManager.getInstance(context).playSound(SoundManager.Sound.CLICK);
    }

    private boolean isSignedIn() {
        return getLastSignedInAccount(context) != null;
    }
}