package ie.ul.postgrad.socialanxietyapp.game.battle;

import android.content.Context;

import ie.ul.postgrad.socialanxietyapp.game.Enemy;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.item.FoodItem;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

/**
 * Created by Robert on 18-Dec-17.
 */

public interface BattleMVP {

    interface BattleView {
        void showText(String text);

        void showMenu();

        void updateUI(Enemy e, Player p);

        void endBattle();

        Context getContext();

        void initUI(Enemy e, Player p, int enemyCount);

        void showAttackMiniGame(boolean isPlayer);

        void updateWeaponDisplay(WeaponItem weapon);

        void setAttackButtonEnabled(boolean enabled);
    }

    interface BattlePresenter {

        void useItem(FoodItem item);

        void changeWeapon(WeaponItem weapon);

        WeaponItem getWeapon();

    }

}
