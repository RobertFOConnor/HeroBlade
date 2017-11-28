package ie.ul.postgrad.socialanxietyapp.game;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.factory.WeaponFactory;

/**
 * Created by Robert on 16-Aug-17.
 */

public class AchievementManager {

    private static void checkSwordAchievements(Context context) {
        GameManager gm = GameManager.getInstance();
        //Unlock any appropriate achievements.

        if (gm.getFoundWeapons().size() >= WeaponFactory.SWORD_COUNT) {
            unlockAchievement(context.getString(R.string.achievement_the_true_sword_master), context);
        } else if (gm.getFoundWeapons().size() >= 20) {
            unlockAchievement(context.getString(R.string.achievement_20_sword_ninja), context);
        } else if (gm.getFoundWeapons().size() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_10_sword_knight), context);
        } else if (gm.getFoundWeapons().size() >= 5) {
            unlockAchievement(context.getString(R.string.achievement_5_sword_apprentice), context);
        }
    }

    private static void checkBattleAchievements(Context context) {
        GameManager gm = GameManager.getInstance();

        if (gm.getStats().getWins() >= 100) {
            unlockAchievement(context.getString(R.string.achievement_the_legend_is_real), context);
        } else if (gm.getStats().getWins() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_this_is_too_easy), context);
        } else if (gm.getStats().getWins() >= 1) {
            unlockAchievement(context.getString(R.string.achievement_a_hero_is_born), context);
        }
    }

    private static void checkChestAchievements(Context context) {
        GameManager gm = GameManager.getInstance();

        if (gm.getStats().getChestsOpened() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_you_are_a_pirate), context);
        } else if (gm.getStats().getChestsOpened() >= 1) {
            unlockAchievement(context.getString(R.string.achievement_inside_the_chest), context);
        }
    }

    private static void checkLevelAchievements(Context context) {
        GameManager gm = GameManager.getInstance();
        if (gm.getPlayer().getLevel() >= 40) {
            unlockAchievement(context.getString(R.string.achievement_impossible_hero), context);
        } else if (gm.getPlayer().getLevel() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_quartersized_hero), context);
        } else if (gm.getPlayer().getLevel() >= 5) {
            unlockAchievement(context.getString(R.string.achievement_experience_is_knowledge), context);
        } else if (gm.getPlayer().getLevel() >= 2) {
            unlockAchievement(context.getString(R.string.achievement_moving_on_up), context);
        }
    }

    private static void checkMarkerAchievements(Context context) {
        GameManager gm = GameManager.getInstance();
        if (gm.getVillageCount() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_mr_ms__popular), context);
        }

        if (gm.getBlacksmithCount() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_repairs_all_round), context);
        }
    }


    public static void checkAllAchievements(Context context) {
        unlockAchievement(context.getString(R.string.achievement_the_journey_begins), context);
        checkLevelAchievements(context);
        checkChestAchievements(context);
        checkBattleAchievements(context);
        checkSwordAchievements(context);
        checkMarkerAchievements(context);
    }

    private static void unlockAchievement(String s, Context context) {
        if(GoogleSignIn.getLastSignedInAccount(context) != null) {
            Games.getAchievementsClient(context, GoogleSignIn.getLastSignedInAccount(context)).unlock(s);
        }
    }
}
