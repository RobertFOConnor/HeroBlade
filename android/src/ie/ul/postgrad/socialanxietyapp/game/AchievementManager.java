package ie.ul.postgrad.socialanxietyapp.game;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponFactory;

/**
 * Created by Robert on 16-Aug-17.
 */

public class AchievementManager {
    private static final int REQUEST_ACHIEVEMENTS = 101;

    public static void checkSwordAchievements(Context context, GoogleApiClient mGoogleApiClient) {
        GameManager gm = GameManager.getInstance();
        //Unlock any appropriate achievements.

        if (gm.getFoundWeapons().size() >= WeaponFactory.SWORD_COUNT) {
            unlockAchievement(context.getString(R.string.achievement_the_true_sword_master), mGoogleApiClient);
        } else if (gm.getFoundWeapons().size() >= 20) {
            unlockAchievement(context.getString(R.string.achievement_20_sword_ninja), mGoogleApiClient);
        } else if (gm.getFoundWeapons().size() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_10_sword_knight), mGoogleApiClient);
        } else if (gm.getFoundWeapons().size() >= 5) {
            unlockAchievement(context.getString(R.string.achievement_5_sword_apprentice), mGoogleApiClient);
        }
    }

    public static void checkBattleAchievements(Context context, GoogleApiClient mGoogleApiClient) {
        GameManager gm = GameManager.getInstance();

        if (gm.getStats().getWins() >= 100) {
            unlockAchievement(context.getString(R.string.achievement_the_legend_is_real), mGoogleApiClient);
        } else if (gm.getStats().getWins() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_this_is_too_easy), mGoogleApiClient);
        } else if (gm.getStats().getWins() >= 1) {
            unlockAchievement(context.getString(R.string.achievement_a_hero_is_born), mGoogleApiClient);
        }
    }

    public static void checkChestAchievements(Context context, GoogleApiClient mGoogleApiClient) {
        GameManager gm = GameManager.getInstance();

        if (gm.getStats().getChestsOpened() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_you_are_a_pirate), mGoogleApiClient);
        } else if (gm.getStats().getChestsOpened() >= 1) {
            unlockAchievement(context.getString(R.string.achievement_inside_the_chest), mGoogleApiClient);
        }
    }

    public static void checkLevelAchievements(Context context, GoogleApiClient mGoogleApiClient) {
        GameManager gm = GameManager.getInstance();
        if (gm.getPlayer().getLevel() >= 40) {
            unlockAchievement(context.getString(R.string.achievement_impossible_hero), mGoogleApiClient);
        } else if (gm.getPlayer().getLevel() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_quartersized_hero), mGoogleApiClient);
        } else if (gm.getPlayer().getLevel() >= 5) {
            unlockAchievement(context.getString(R.string.achievement_experience_is_knowledge), mGoogleApiClient);
        } else if (gm.getPlayer().getLevel() >= 2) {
            unlockAchievement(context.getString(R.string.achievement_moving_on_up), mGoogleApiClient);
        }
    }

    public static void checkMarkerAchievements(Context context, GoogleApiClient mGoogleApiClient) {
        GameManager gm = GameManager.getInstance();
        if (gm.getVillageCount() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_mr_ms__popular), mGoogleApiClient);
        }

        if (gm.getBlacksmithCount() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_repairs_all_round), mGoogleApiClient);
        }
    }


    public static void checkAllAchievements(Context context, GoogleApiClient mGoogleApiClient) {
        unlockAchievement(context.getString(R.string.achievement_the_journey_begins), mGoogleApiClient);
        checkLevelAchievements(context, mGoogleApiClient);
        checkChestAchievements(context, mGoogleApiClient);
        checkBattleAchievements(context, mGoogleApiClient);
        checkSwordAchievements(context, mGoogleApiClient);
        checkMarkerAchievements(context, mGoogleApiClient);
    }

    private static void unlockAchievement(String s, GoogleApiClient mGoogleApiClient) {
        Games.Achievements.unlock(mGoogleApiClient, s);
    }

    public static void showAchievements(Context context, GoogleApiClient mGoogleApiClient) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            ((FragmentActivity) context).startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), REQUEST_ACHIEVEMENTS);
        } else {
            App.showToast(context, context.getString(R.string.no_google_play_achievements));
        }
    }
}
