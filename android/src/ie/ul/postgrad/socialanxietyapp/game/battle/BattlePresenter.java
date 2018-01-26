package ie.ul.postgrad.socialanxietyapp.game.battle;

import android.content.Context;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.Enemy;
import ie.ul.postgrad.socialanxietyapp.game.EnemyFactory;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.factory.WeaponFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.FoodItem;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

/**
 * Created by Robert on 18-Dec-17.
 */

public class BattlePresenter implements BattleMVP.BattlePresenter {

    private GameManager gm;
    private Player player;
    private ArrayList<Enemy> enemies;
    private Enemy enemy;
    private WeaponItem weapon;

    //Game stats.
    private int turnCount = 0;
    private int rewardedXP = 650;
    private int rewardMoney = 200;

    private final BattleContext battleContext = new BattleContext();
    private BattleMVP.BattleView view;
    private Context context;

    public BattlePresenter(BattleMVP.BattleView view, GameManager gm) {
        this.view = view;
        this.gm = gm;
        player = gm.getPlayer();
        context = view.getContext();
        setupEnemyArray();
        setupPlayerWeapon();
        StartState startState = new StartState();
        battleContext.setState(startState);
        view.initUI(enemy, player, enemies.size());
    }

    private void setupPlayerWeapon() {
        ArrayList<WeaponItem> weapons = gm.getInventory().getEquippedWeapons();
        if (weapons.size() > 0 && player.getCurrHealth() > 0 && gm.getInventory().hasUsableWeapons()) {

            for (int i = weapons.size() - 1; i >= 0; i--) {
                if (weapons.get(i).getCurrHealth() > 0) {
                    weapon = weapons.get(i);
                }
            }
        } else {
            weapon = WeaponFactory.buildWeapon(context, 1);
        }
    }

    private void setupEnemyArray() {
        enemies = new ArrayList<>();
        int enemyCount = 5;
        int playerLvl = player.getLevel();
        if (playerLvl < 4) {
            enemyCount = 1;
        } else if (playerLvl < 8) {
            enemyCount = 2;
        } else if (playerLvl < 12) {
            enemyCount = 3;
        }
        enemyCount = ((int) (Math.random() * enemyCount)) + 1;

        for (int i = 0; i < enemyCount; i++) {
            enemies.add(EnemyFactory.buildEnemy(context, player, (int) (Math.random() * EnemyFactory.ENEMY_COUNT) + 1));
        }
        enemy = enemies.get(0);
    }

    public void strikeEnemy(WeaponItem weaponItem) {

        int level = player.getLevel();
        int basePower = weaponItem.getDamage();
        int damage = basePower * ((level / 3) + 1);

        //Calculate type damage (fire, water, grass)
        damage = calculateTypes(weaponItem.getType(), enemy.getType(), damage);
        damage += damage;
        String attackMessage = context.getString(R.string.name_with_damage, player.getName(), weaponItem.getName(), damage);

        if ((int) (Math.random() * 12) == 0) { //Critical hit chance
            damage = damage * 2;
            attackMessage = context.getString(R.string.name_with_crit_damage, player.getName(), weaponItem.getName(), damage);
        }

        enemy.setCurrHealth(enemy.getCurrHealth() - damage);

        weaponItem.setCurrHealth(weaponItem.getCurrHealth() - 1);

        if (weaponItem.getCurrHealth() <= 0) {
            attackMessage += " " + context.getString(R.string.broken_weapon);
            weapon = null;
            view.setAttackButtonEnabled(false);
        }
        gm.updateWeaponInDatabase(weaponItem);

        turnCount++;
        final String displayMessage = attackMessage;
        view.updateUI(enemy, player);
        view.showText(displayMessage);
    }


    @Override
    public void useItem(FoodItem item) {
        if (item.getEnergy() > 0) {
            player.setCurrHealth(player.getCurrHealth() + item.getEnergy());
        } else if (item.getEnergy() < 0) {
            enemy.setCurrHealth(enemy.getCurrHealth() + item.getEnergy());
        }
        view.updateUI(enemy, player);
        view.showText(context.getString(R.string.used_item, player.getName(), item.getName()));
    }

    @Override
    public void changeWeapon(WeaponItem weapon) {
        this.weapon = weapon;
    }

    @Override
    public WeaponItem getWeapon() {
        return weapon;
    }

    private int calculateTypes(String attType, String defType, int damage) {
        double typeBonus = 1.5f;
        final String FIRE = "FIRE";
        final String WATER = "WATER";
        final String GRASS = "GRASS";

        switch (attType) {
            case GRASS:
                if (defType.equals(WATER)) {
                    damage = (int) (damage * typeBonus);
                } else if (defType.equals(FIRE)) {
                    damage = (int) (damage / typeBonus);
                }
                break;
            case WATER:
                if (defType.equals(FIRE)) {
                    damage = (int) (damage * typeBonus);
                } else if (defType.equals(GRASS)) {
                    damage = (int) (damage / typeBonus);
                }
                break;
            case FIRE:
                if (defType.equals(GRASS)) {
                    damage = (int) (damage * typeBonus);
                } else if (defType.equals(WATER)) {
                    damage = (int) (damage / typeBonus);
                }
                break;
        }

        if (damage < 0) {
            damage = 0;
        }
        return damage;
    }

    public void advanceState() {
        battleContext.getState().advance(battleContext);
    }

    private interface BattleState {
        void advance(BattleContext context);
    }

    private class StartState implements BattleState {

        private StartState() {
            view.showText(context.getString(R.string.enemy_appeared, enemy.getName()));
        }

        public void advance(BattleContext context) {
            context.setState(new PlayersTurnState());
        }
    }

    private class PlayersTurnState implements BattleState {

        private PlayersTurnState() { //PLAYERS TURN TO MAKE A MOVE
            view.showMenu();
        }

        public void advance(BattleContext context) {
            if (enemy.getCurrHealth() <= 0) {
                rewardedXP += enemy.getLevel() * 3;
                rewardedXP += turnCount;
                rewardMoney += enemy.getLevel();
                rewardMoney += (int) (Math.random() * 5);
                enemies.remove(0);
                if (enemies.size() > 0) {
                    enemy = enemies.get(0);
                    view.initUI(enemy, player, enemies.size());
                    context.setState(new StartState());
                } else {
                    context.setState(new FinishState(FinishState.WON));
                }
            } else {
                context.setState(new EnemyTurnState());
            }
        }
    }

    private class EnemyTurnState implements BattleState {

        private EnemyTurnState() { //AI TURN TO ATTACK


            String playerType = context.getString(R.string.grass_type);
            if (weapon != null) {
                playerType = weapon.getType();
            }

            int level = enemy.getLevel();
            int basePower = 5;
            int damage = basePower * ((level / 3) + 1);

            damage = calculateTypes(enemy.getType(), playerType, damage);
            damage += level % 2;

            int random = (int) (Math.random() * 15);
            String enemyMsg;

            if (random == 0 || (player.getCurrHealth() < 5 && random < 5)) {
                view.showText(context.getString(R.string.attack_missed, enemy.getName()));
            } else if (random == 1) {
                enemyMsg = enemy.getName() + " throws a CRITICAL strike dealing " + damage + " damage.";
                hitPlayer(damage, enemyMsg);
            } else {
                enemyMsg = enemy.getName() + " strikes dealing " + damage + " damage.";
                hitPlayer(damage, enemyMsg);
            }

        }

        public void advance(BattleContext context) {
            if (player.getCurrHealth() > 0) {
                context.setState(new PlayersTurnState());
            } else {
                context.setState(new FinishState(FinishState.LOST));
            }
        }
    }

    private void hitPlayer(int damage, String enemyMsg) {
        player.setCurrHealth(player.getCurrHealth() - damage);
        gm.updatePlayerInDatabase(player);
        view.updateUI(enemy, player);
        view.showText(enemyMsg);
        view.showAttackMiniGame(false);
    }

    private class FinishState implements BattleState {

        static final String WON = "won";
        static final String LOST = "lost";
        static final String OUT_OF_WEAPONS = "no_weapons";

        private FinishState(String state) {
            switch (state) {
                case LOST:
                    view.showText(context.getString(R.string.pass_out, player.getName()));
                    break;
                case WON:
                    String winString = player.getName() + " has won the battle. (" + rewardedXP + "XP) " + player.getName() + " received $" + rewardMoney + ".";
                    gm.awardXP(context, rewardedXP);
                    gm.awardMoney(rewardMoney);
                    gm.addWin();
                    view.showText(winString);
                    break;
                case OUT_OF_WEAPONS:
                    view.showText(context.getString(R.string.out_of_weapons, player.getName()));
                    break;
            }
        }

        public void advance(BattleContext context) {
            view.endBattle();
        }
    }

    private class BattleContext {
        private BattleState state;

        private BattleContext() {
            state = null;
        }

        private void setState(BattleState state) {
            this.state = state;
        }

        private BattleState getState() {
            return state;
        }
    }
}
