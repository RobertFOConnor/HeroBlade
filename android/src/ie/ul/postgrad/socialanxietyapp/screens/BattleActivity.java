package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.LibGdxInterface;
import ie.ul.postgrad.socialanxietyapp.MainGame;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.Enemy;
import ie.ul.postgrad.socialanxietyapp.game.EnemyFactory;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.item.FoodItem;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;
import ie.ul.postgrad.socialanxietyapp.screen.BattleScreen;

import static ie.ul.postgrad.socialanxietyapp.screens.ItemSelectActivity.SELECT_ITEM;
import static ie.ul.postgrad.socialanxietyapp.screens.ItemSelectActivity.SELECT_WEAPON;

public class BattleActivity extends AndroidApplication implements LibGdxInterface, View.OnClickListener {

    private static final int WEAPON_REQUEST = 1;
    private static final int ITEM_REQUEST = 2;

    private GameManager gm;
    private Player player;
    private ArrayList<Enemy> enemies;
    private Enemy enemy;
    private WeaponItem weapon;
    private LinearLayout textDisplay;
    private GridLayout battleMenu;
    private TextView dialogueText;
    private ProgressBar userHealthBar;
    private ProgressBar enemyHealthBar;
    private TextView userHealthText;
    private TextView enemyNameText;
    private TextView enemyHealthText;
    private TextView enemiesLeft;
    private ImageView weaponType, enemyType;
    private int turnCount = 0;
    private int rewardedXP = 800;
    private int rewardMoney = 200;
    private final Context context = new Context();
    private DialogInterface.OnClickListener dialogClickListener;
    private MainGame game;
    private BattleScreen battleDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        dialogueText = (TextView) findViewById(R.id.dialogue);
        textDisplay = (LinearLayout) findViewById(R.id.text_display);
        battleMenu = (GridLayout) findViewById(R.id.battle_menu);
        userHealthText = (TextView) findViewById(R.id.player_health);
        enemyHealthText = (TextView) findViewById(R.id.enemy_health);
        weaponType = (ImageView) findViewById(R.id.user_type_img);
        enemyType = (ImageView) findViewById(R.id.enemy_type_img);
        enemyNameText = ((TextView) findViewById(R.id.enemy_name));
        enemiesLeft = ((TextView) findViewById(R.id.enemy_count));

        gm = GameManager.getInstance();
        gm.initDatabaseHelper(this);
        player = gm.getPlayer();
        ((TextView) findViewById(R.id.player_name)).setText(getString(R.string.name_with_level, player.getName(), player.getLevel()));


        enemies = new ArrayList<>();

        int enemyCount;
        if (player.getLevel() < 4) {
            enemyCount = 1;
        } else if (player.getLevel() < 8) {
            enemyCount = 2;
        } else if (player.getLevel() < 12) {
            enemyCount = 3;
        } else {
            enemyCount = ((int) (Math.random() * 5)) + 1;
        }
        for (int i = 0; i < enemyCount; i++) {
            enemies.add(EnemyFactory.buildEnemy(this, player, (int) (Math.random() * EnemyFactory.ENEMY_COUNT) + 1));
        }
        enemy = enemies.get(0);

        ArrayList<WeaponItem> weapons = gm.getInventory().getEquippedWeapons();
        if (weapons.size() > 0 && player.getCurrHealth() > 0 && gm.getInventory().hasUsableWeapons()) {

            for (int i = weapons.size() - 1; i > -1; i--) {
                if (weapons.get(i).getCurrHealth() > 0) {
                    weapon = weapons.get(i);
                    weaponType.setImageResource(weapons.get(i).getTypeDrawableRes());
                }
            }
        }

        userHealthBar = (ProgressBar) findViewById(R.id.user_bar);
        userHealthBar.setScaleY(3f);
        userHealthBar.setMax(player.getMaxHealth());
        userHealthBar.setProgress(player.getCurrHealth());
        userHealthText.setText(getString(R.string.health_amount, player.getCurrHealth(), player.getMaxHealth()));

        enemyHealthBar = (ProgressBar) findViewById(R.id.enemy_bar);
        enemyHealthBar.setScaleY(3f);
        enemyHealthBar.setRotation(180);
        initEnemyUI();

        StartState startState = new StartState();
        context.setState(startState);

        findViewById(R.id.attack_button).setOnClickListener(this);
        findViewById(R.id.change_weapon_button).setOnClickListener(this);
        findViewById(R.id.item_button).setOnClickListener(this);
        findViewById(R.id.run_button).setOnClickListener(this);
        findViewById(R.id.next_button).setOnClickListener(this);

        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        game = new MainGame(this, MainGame.BATTLE_SCREEN);
        View v = initializeForView(game, new AndroidApplicationConfiguration());
        ((LinearLayout) findViewById(R.id.character_layout)).addView(v);
    }

    private void updateWeaponDisplay() {
        battleDisplay = (BattleScreen) game.getScreen();
        battleDisplay.updateWeapon(weapon.getId() - 1);
    }

    private void swordStrike(boolean isPlayer) {
        battleDisplay = (BattleScreen) game.getScreen();
        battleDisplay.swordStrike(isPlayer);
    }

    private void initEnemyUI() {
        enemyHealthBar.setMax(enemy.getMaxHealth());
        enemyHealthBar.setProgress(enemy.getCurrHealth());
        enemyHealthText.setText(getString(R.string.health_amount, enemy.getCurrHealth(), enemy.getMaxHealth()));
        enemyType.setImageResource(enemy.getTypeDrawableRes());
        enemyNameText.setText(getString(R.string.name_with_level, enemy.getName(), enemy.getLevel()));
        enemiesLeft.setText(getString(R.string.enemies_left, enemies.size()));
    }

    @Override
    public void onClick(View v) {
        WeaponItem weaponItem = weapon;
        switch (v.getId()) {
            case R.id.attack_button:
                if (weaponItem != null) {
                    strikeEnemy(v, weaponItem);
                }
                break;
            case R.id.change_weapon_button:
                changeWeapon();
                break;
            case R.id.item_button:
                useItem();
                break;
            case R.id.run_button:
                showExitDialog();
                break;
            case R.id.next_button:
                context.getState().advance(context);
                break;
        }
    }

    private void showExitDialog() {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(BattleActivity.this);
        builder.setMessage("Are you sure you want to exit the battle?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();*/
        finish();
    }

    private void useItem() {
        Intent intent = new Intent(this, ItemSelectActivity.class);
        intent.putExtra(ItemSelectActivity.SELECT_TYPE, SELECT_ITEM);
        startActivityForResult(intent, ITEM_REQUEST);
    }

    private void changeWeapon() {
        Intent intent = new Intent(this, ItemSelectActivity.class);
        intent.putExtra(ItemSelectActivity.SELECT_TYPE, SELECT_WEAPON);
        if (weapon != null) {
            intent.putExtra(ItemSelectActivity.CURR_WEAPON, weapon.getUUID());
        } else {
            intent.putExtra(ItemSelectActivity.CURR_WEAPON, "");
        }
        startActivityForResult(intent, WEAPON_REQUEST);
    }

    private void strikeEnemy(View v, WeaponItem weaponItem) {

        int level = player.getLevel();
        int basePower = weaponItem.getDamage();
        int offence = player.getLevel() + 3;
        int defense = (enemy.getLevel() / 2) + 1;

        int damage = (int) Math.floor(Math.floor(Math.floor(2 * level / 5 + 2) * offence * basePower / defense) / 50) + 2;

        damage = calculateTypes(weaponItem.getType(), enemy.getType(), damage);//Calculate type damage (fire, water, grass)

        boolean crit = false;
        if ((int) (Math.random() * 12) == 0) { //Critical hit chance
            damage += damage;
            crit = true;
        }
        damage += damage;

        enemy.setCurrHealth(enemy.getCurrHealth() - damage);
        enemyHealthBar.setProgress(enemy.getCurrHealth());
        enemyHealthText.setText(getString(R.string.health_amount, enemy.getCurrHealth(), enemy.getMaxHealth()));
        weaponItem.setCurrHealth(weaponItem.getCurrHealth() - 1);

        String attackMessage = getString(R.string.name_with_damage, player.getName(), weaponItem.getName(), damage);

        if (crit) {
            attackMessage = getString(R.string.name_with_crit_damage, player.getName(), weaponItem.getName(), damage);
        }

        if (weaponItem.getCurrHealth() <= 0) {
            attackMessage += " " + getString(R.string.broken_weapon);
            weapon = null;
            v.setEnabled(false);
        }
        gm.updateWeaponInDatabase(weaponItem);

        turnCount++;
        showText();
        dialogueText.setText(attackMessage);
        swordStrike(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check request to which we're responding
        if (requestCode == WEAPON_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a weapon.
                WeaponItem weaponItem = gm.getInventory().getWeapon(data.getStringExtra(getString(R.string.result)));
                weaponType.setImageResource(weaponItem.getTypeDrawableRes());
                showText();
                dialogueText.setText(getString(R.string.weapon_change, player.getName(), weaponItem.getName()));
                findViewById(R.id.attack_button).setEnabled(true);
                weapon = weaponItem;
                updateWeaponDisplay();
            }
        } else if (requestCode == ITEM_REQUEST) {
            if (resultCode == RESULT_OK) {
                // The user used an item.
                FoodItem item = (FoodItem) ItemFactory.buildItem(this, data.getIntExtra(getString(R.string.result), 0));
                if (item.getEnergy() > 0) {
                    player.setCurrHealth(gm.getPlayer().getCurrHealth());
                    userHealthBar.setProgress(player.getCurrHealth());
                    userHealthText.setText(getString(R.string.health_amount, player.getCurrHealth(), player.getMaxHealth()));
                    showText();
                    dialogueText.setText(getString(R.string.used_item, player.getName(), item.getName()));
                } else if (item.getEnergy() < 0) {
                    enemy.setCurrHealth(enemy.getCurrHealth() + item.getEnergy());
                    enemyHealthBar.setProgress(enemy.getCurrHealth());
                    enemyHealthText.setText(getString(R.string.health_amount, enemy.getCurrHealth(), enemy.getMaxHealth()));
                    showText();
                    dialogueText.setText(getString(R.string.used_item, player.getName(), item.getName()));
                }
            }
        }
    }

    private int calculateTypes(String attType, String defType, int damage) {
        if (attType.equals(getString(R.string.grass_type))) {
            if (defType.equals(getString(R.string.water_type))) {
                damage = (int) (damage * 1.5f);
            } else if (defType.equals(getString(R.string.fire_type))) {
                damage = (int) (damage / 1.5f);
            }
        } else if (attType.equals(getString(R.string.water_type))) {
            if (defType.equals(getString(R.string.fire_type))) {
                damage = (int) (damage * 1.5f);
            } else if (defType.equals(getString(R.string.grass_type))) {
                damage = (int) (damage / 1.5f);
            }
        } else if (attType.equals(getString(R.string.fire_type))) {
            if (defType.equals(getString(R.string.grass_type))) {
                damage = (int) (damage * 1.5f);
            } else if (defType.equals(getString(R.string.water_type))) {
                damage = (int) (damage / 1.5f);
            }
        }

        if (damage < 0) {
            damage = 0;
        }
        return damage;
    }

    private interface BattleState {
        void advance(Context context);
    }

    private class StartState implements BattleState {

        private StartState() {
            showText();
            dialogueText.setText(getString(R.string.enemy_appeared, enemy.getName()));
        }

        public void advance(Context context) {
            context.setState(new PlayersTurnState());
        }
    }

    private class PlayersTurnState implements BattleState {

        private PlayersTurnState() { //PLAYERS TURN TO MAKE A MOVE
            showMenu();
        }

        public void advance(Context context) {
            if (enemyHealthBar.getProgress() <= 0) {
                rewardedXP += enemy.getLevel() * 3;
                rewardedXP += turnCount * 2;
                rewardMoney += enemy.getLevel();
                rewardMoney += (int) (Math.random() * 5);
                enemies.remove(0);
                if (enemies.size() > 0) {
                    enemy = enemies.get(0);
                    initEnemyUI();
                    context.setState(new StartState());
                } else {
                    context.setState(new FinishState(FinishState.WON));
                }
            } else if (!gm.getInventory().hasUsableWeapons()) {
                context.setState(new FinishState(FinishState.OUT_OF_WEAPONS));
            } else {
                context.setState(new EnemyTurnState());
            }
        }
    }

    private class EnemyTurnState implements BattleState {

        private EnemyTurnState() { //AI TURN TO ATTACK
            showText();

            String playerType = getString(R.string.grass_type);
            if (weapon != null) {
                playerType = weapon.getType();
            }

            int level = enemy.getLevel();
            int basePower = 5;
            int offence = enemy.getLevel() + 3;
            int defense = (player.getLevel() / 2) + 1;
            int damage = (int) Math.floor(Math.floor(Math.floor(2 * level / 5 + 2) * offence * basePower / defense) / 50) + 2;

            damage = calculateTypes(enemy.getType(), playerType, damage);
            damage += level % 2;

            int random = (int) (Math.random() * 15);

            if (random == 0 || (player.getCurrHealth() < 5 && random < 5)) {
                dialogueText.setText(getString(R.string.attack_missed, enemy.getName()));
            } else if (random == 1) {
                dialogueText.setText(enemy.getName() + " throws a CRITICAL punch dealing " + damage + " damage.");
                hitPlayer(damage);
                swordStrike(false);
            } else {
                dialogueText.setText(enemy.getName() + " throws a punch dealing " + damage + " damage.");
                hitPlayer(damage);
                swordStrike(false);
            }
        }

        public void advance(Context context) {
            if (userHealthBar.getProgress() > 0) {
                context.setState(new PlayersTurnState());
            } else {
                context.setState(new FinishState(FinishState.LOST));
            }
        }
    }

    private void hitPlayer(int damage) {
        player.setCurrHealth(player.getCurrHealth() - damage);
        userHealthBar.setProgress(player.getCurrHealth());
        userHealthText.setText(getString(R.string.health_amount, player.getCurrHealth(), player.getMaxHealth()));
        gm.updatePlayerInDatabase(player);
    }

    private class FinishState implements BattleState {

        static final String WON = "won";
        static final String LOST = "lost";
        static final String OUT_OF_WEAPONS = "no_weapons";

        private FinishState(String state) {
            showText();
            if (state.equals(LOST)) {
                dialogueText.setText(getString(R.string.pass_out, player.getName()));
            } else if (state.equals(WON)) {
                dialogueText.setText(player.getName() + " has won the battle. (" + rewardedXP + "XP) " + player.getName() + " received $" + rewardMoney + ".");
                gm.awardXP(getApplicationContext(), rewardedXP);
                gm.awardMoney(rewardMoney);
                gm.addWin();
            } else if (state.equals(OUT_OF_WEAPONS)) {
                dialogueText.setText(getString(R.string.out_of_weapons, player.getName()));
            }
        }

        public void advance(Context context) {
            finish();
        }
    }

    private class Context {
        private BattleState state;

        public Context() {
            state = null;
        }

        private void setState(BattleState state) {
            this.state = state;
        }

        private BattleState getState() {
            return state;
        }
    }

    private void showText() {
        battleMenu.setVisibility(View.INVISIBLE);
        textDisplay.setVisibility(View.VISIBLE);
    }

    private void showMenu() {
        battleMenu.setVisibility(View.VISIBLE);
        textDisplay.setVisibility(View.INVISIBLE);
    }

    @Override
    public void saveAvatar(Avatar avatar) {
        //empty method. (This isn't called from this activity. (used for changing avatar appearance.))
    }

    @Override
    public void collectResource() {
        //empty method. (This isn't called from this activity. (used for changing avatar appearance.))
    }

    @Override
    public Avatar getAvatar() {
        try {
            return gm.getAvatar();
        } catch (Exception e) {
            return new Avatar(1, 1, 1, 1);
        }
    }

    @Override
    public int getNPCId() {
        return weapon.getId();
    }

    @Override
    public void finishGame() {
        finish();
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }
}