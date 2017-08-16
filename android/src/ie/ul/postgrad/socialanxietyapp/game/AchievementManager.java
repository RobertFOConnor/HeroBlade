package ie.ul.postgrad.socialanxietyapp.game;

import android.content.Context;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponFactory;

/**
 * Created by Robert on 16-Aug-17.
 */

public class AchievementManager {

    public static void checkSwordAchievements(Context context) {
        GameManager gm = GameManager.getInstance();
        //Unlock any appropriate achievements.
        if (gm.getFoundWeapons().size() >= 5) {
            unlockAchievement(context.getString(R.string.achievement_5_sword_apprentice));
        } else if (gm.getFoundWeapons().size() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_10_sword_knight));
        } else if (gm.getFoundWeapons().size() >= 20) {
            unlockAchievement(context.getString(R.string.achievement_20_sword_ninja));
        } else if (gm.getFoundWeapons().size() >= WeaponFactory.SWORD_COUNT) {
            unlockAchievement(context.getString(R.string.achievement_the_true_sword_master));
        }
    }

    public static void checkBattleAchievements(Context context) {
        GameManager gm = GameManager.getInstance();
        if (gm.getStats().getWins() >= 1) {
            unlockAchievement(context.getString(R.string.achievement_a_hero_is_born));
        } else if (gm.getStats().getWins() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_this_is_too_easy));
        } else if (gm.getStats().getWins() >= 100) {
            unlockAchievement(context.getString(R.string.achievement_the_legend_is_real));
        }
    }

    public static void checkChestAchievements(Context context) {
        GameManager gm = GameManager.getInstance();
        if (gm.getStats().getChestsOpened() >= 1) {
            unlockAchievement(context.getString(R.string.achievement_inside_the_chest));
        } else if (gm.getStats().getChestsOpened() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_you_are_a_pirate));
        }
    }

    public static void checkLevelAchievements(Context context) {
        GameManager gm = GameManager.getInstance();
        if (gm.getPlayer().getLevel() >= 2) {
            unlockAchievement(context.getString(R.string.achievement_moving_on_up));
        } else if (gm.getPlayer().getLevel() >= 5) {
            unlockAchievement(context.getString(R.string.achievement_experience_is_knowledge));
        } else if (gm.getPlayer().getLevel() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_quartersized_hero));
        } else if (gm.getPlayer().getLevel() >= 40) {
            unlockAchievement(context.getString(R.string.achievement_impossible_hero));
        }
    }

    public static void checkMarkerAchievements(Context context) {
        GameManager gm = GameManager.getInstance();
        if (gm.getVillageCount() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_mr_ms__popular));
        } else if (gm.getBlacksmithCount() >= 10) {
            unlockAchievement(context.getString(R.string.achievement_repairs_all_round));
        }
    }


    public static void checkAllAchievements(Context context) {
        unlockAchievement(context.getString(R.string.achievement_the_journey_begins));
        checkLevelAchievements(context);
        checkChestAchievements(context);
        checkBattleAchievements(context);
        checkSwordAchievements(context);
        checkMarkerAchievements(context);
    }

    private static void unlockAchievement(String s) {
        App.getInstance().getGoogleApiHelperInstance().unlockAchievement(s);
    }
}
