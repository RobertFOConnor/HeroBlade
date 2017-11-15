package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import ie.ul.postgrad.socialanxietyapp.game.factory.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.factory.WeaponFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.FoodItem;
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
    private LinearLayout battleMenu;
    private TextView dialogueText;
    private TextView enemyNameText;
    private TextView enemiesLeft;
    private ImageView weaponType, enemyType;
    private MainGame game;
    private BattleScreen battleDisplay;
    private PlayerUI playerUI;
    private PlayerUI enemyUI;
    private final BattleContext battleContext = new BattleContext();

    //Game stats.
    private int turnCount = 0;
    private int rewardedXP = 800;
    private int rewardMoney = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        initViews();

        gm = GameManager.getInstance();
        gm.initDatabaseHelper(this);
        player = gm.getPlayer();
        ((TextView) findViewById(R.id.player_name)).setText(getString(R.string.name_with_level, player.getName(), player.getLevel()));

        enemies = new ArrayList<>();
        setupEnemyArray();

        ArrayList<WeaponItem> weapons = gm.getInventory().getEquippedWeapons();
        if (weapons.size() > 0 && player.getCurrHealth() > 0 && gm.getInventory().hasUsableWeapons()) {

            for (int i = weapons.size() - 1; i > -1; i--) {
                if (weapons.get(i).getCurrHealth() > 0) {
                    weapon = weapons.get(i);
                    weaponType.setImageResource(weapons.get(i).getTypeDrawableRes());
                }
            }
        } else {
            weapon = WeaponFactory.buildWeapon(this, 1);
        }

        ProgressBar userHealthBar = (ProgressBar) findViewById(R.id.user_bar);
        TextView userHealthText = (TextView) findViewById(R.id.player_health);
        userHealthBar.setMax(player.getMaxHealth());
        userHealthBar.setProgress(player.getCurrHealth());
        userHealthText.setText(getString(R.string.health_amount, player.getCurrHealth(), player.getMaxHealth()));
        playerUI = new PlayerUI(userHealthBar, userHealthText);

        ProgressBar enemyHealthBar = (ProgressBar) findViewById(R.id.enemy_bar);
        TextView enemyHealthText = (TextView) findViewById(R.id.enemy_health);
        enemyHealthBar.setRotation(180f);
        enemyUI = new PlayerUI(enemyHealthBar, enemyHealthText);
        initEnemyUI();

        StartState startState = new StartState();
        battleContext.setState(startState);

        setupListeners();

        game = new MainGame(this, MainGame.BATTLE_SCREEN);
        View v = initializeForView(game, new AndroidApplicationConfiguration());
        ((LinearLayout) findViewById(R.id.character_layout)).addView(v);
    }

    private void setupEnemyArray() {
        int enemyCount = 5;
        if (player.getLevel() < 4) {
            enemyCount = 1;
        } else if (player.getLevel() < 8) {
            enemyCount = 2;
        } else if (player.getLevel() < 12) {
            enemyCount = 3;
        }
        enemyCount = ((int) (Math.random() * enemyCount)) + 1;

        for (int i = 0; i < enemyCount; i++) {
            enemies.add(EnemyFactory.buildEnemy(this, player, (int) (Math.random() * EnemyFactory.ENEMY_COUNT) + 1));
        }
        enemy = enemies.get(0);
    }

    private void setupListeners() {
        findViewById(R.id.attack_button).setOnClickListener(this);
        findViewById(R.id.change_weapon_button).setOnClickListener(this);
        findViewById(R.id.item_button).setOnClickListener(this);
        findViewById(R.id.run_button).setOnClickListener(this);
        findViewById(R.id.next_button).setOnClickListener(this);
    }

    private void initViews() {
        dialogueText = (TextView) findViewById(R.id.dialogue);
        textDisplay = (LinearLayout) findViewById(R.id.text_display);
        battleMenu = (LinearLayout) findViewById(R.id.battle_menu);
        weaponType = (ImageView) findViewById(R.id.user_type_img);
        enemyType = (ImageView) findViewById(R.id.enemy_type_img);
        enemyNameText = ((TextView) findViewById(R.id.enemy_name));
        enemiesLeft = ((TextView) findViewById(R.id.enemy_count));
    }

    private void updateWeaponDisplay() {
        battleDisplay = (BattleScreen) game.getScreen();
        battleDisplay.updateWeapon(weapon.getId() - 1);
    }

    private void playAttackAnim(boolean isPlayer) {
        battleDisplay = (BattleScreen) game.getScreen();
        battleDisplay.swordStrike(isPlayer);
    }

    private void initEnemyUI() {
        enemyUI.getHealthBar().setMax(enemy.getMaxHealth());
        enemyUI.getHealthBar().setProgress(enemy.getCurrHealth());
        enemyUI.getHealthText().setText(getString(R.string.health_amount, enemy.getCurrHealth(), enemy.getMaxHealth()));
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
                startChangeWeaponActivity();
                break;
            case R.id.item_button:
                startUseItemActivity();
                break;
            case R.id.run_button:
                showExitDialog();
                break;
            case R.id.next_button:
                battleContext.getState().advance(battleContext);
                break;
        }
    }

    private void showExitDialog() {
        finish();
    }

    private void startUseItemActivity() {
        Intent intent = new Intent(this, ItemSelectActivity.class);
        intent.putExtra(ItemSelectActivity.SELECT_TYPE, SELECT_ITEM);
        startActivityForResult(intent, ITEM_REQUEST);
    }

    private void startChangeWeaponActivity() {
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
        int damage = basePower * ((level / 3) + 1);

        damage = calculateTypes(weaponItem.getType(), enemy.getType(), damage);//Calculate type damage (fire, water, grass)
        String attackMessage = getString(R.string.name_with_damage, player.getName(), weaponItem.getName(), damage);

        if ((int) (Math.random() * 12) == 0) { //Critical hit chance
            damage = damage * 2;
            attackMessage = getString(R.string.name_with_crit_damage, player.getName(), weaponItem.getName(), damage);
        }
        damage += damage;

        enemy.setCurrHealth(enemy.getCurrHealth() - damage);
        enemyUI.updateUI(enemy.getCurrHealth(), enemy.getMaxHealth());
        weaponItem.setCurrHealth(weaponItem.getCurrHealth() - 1);

        if (weaponItem.getCurrHealth() <= 0) {
            attackMessage += " " + getString(R.string.broken_weapon);
            weapon = null;
            v.setEnabled(false);
        }
        gm.updateWeaponInDatabase(weaponItem);

        turnCount++;
        showText();
        dialogueText.setText(attackMessage);
        playAttackAnim(true);
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
                    Player player = gm.getPlayer();
                    player.setCurrHealth(player.getCurrHealth());
                    playerUI.updateUI(player.getCurrHealth(), player.getMaxHealth());
                    showText();
                    dialogueText.setText(getString(R.string.used_item, player.getName(), item.getName()));
                } else if (item.getEnergy() < 0) {
                    enemy.setCurrHealth(enemy.getCurrHealth() + item.getEnergy());
                    enemyUI.updateUI(enemy.getCurrHealth(), enemy.getMaxHealth());
                    showText();
                    dialogueText.setText(getString(R.string.used_item, player.getName(), item.getName()));
                }
            }
        }
    }

    private int calculateTypes(String attType, String defType, int damage) {
        double typeBonus = 1.5f;

        if (attType.equals(getString(R.string.grass_type))) {
            if (defType.equals(getString(R.string.water_type))) {
                damage = (int) (damage * typeBonus);
            } else if (defType.equals(getString(R.string.fire_type))) {
                damage = (int) (damage / typeBonus);
            }
        } else if (attType.equals(getString(R.string.water_type))) {
            if (defType.equals(getString(R.string.fire_type))) {
                damage = (int) (damage * typeBonus);
            } else if (defType.equals(getString(R.string.grass_type))) {
                damage = (int) (damage / typeBonus);
            }
        } else if (attType.equals(getString(R.string.fire_type))) {
            if (defType.equals(getString(R.string.grass_type))) {
                damage = (int) (damage * typeBonus);
            } else if (defType.equals(getString(R.string.water_type))) {
                damage = (int) (damage / typeBonus);
            }
        }

        if (damage < 0) {
            damage = 0;
        }
        return damage;
    }

    private class PlayerUI {

        private ProgressBar healthBar;
        private TextView healthText;

        PlayerUI(ProgressBar healthBar, TextView healthText) {
            this.healthBar = healthBar;
            this.healthText = healthText;
            this.healthBar.setScaleY(3f);
        }

        ProgressBar getHealthBar() {
            return healthBar;
        }

        TextView getHealthText() {
            return healthText;
        }

        void updateUI(int currHealth, int maxHealth) {
            healthBar.setProgress(currHealth);
            healthText.setText(getString(R.string.health_amount, currHealth, maxHealth));
        }
    }

    private interface BattleState {
        void advance(BattleContext context);
    }

    private class StartState implements BattleState {

        private StartState() {
            showText();
            dialogueText.setText(getString(R.string.enemy_appeared, enemy.getName()));
        }

        public void advance(BattleContext context) {
            context.setState(new PlayersTurnState());
        }
    }

    private class PlayersTurnState implements BattleState {

        private PlayersTurnState() { //PLAYERS TURN TO MAKE A MOVE
            showMenu();
        }

        public void advance(BattleContext context) {
            if (enemyUI.getHealthBar().getProgress() <= 0) {
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
            //} else if (!gm.getInventory().hasUsableWeapons()) {
                //context.setState(new FinishState(FinishState.OUT_OF_WEAPONS));
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
            int damage = basePower * ((level / 3) + 1);

            damage = calculateTypes(enemy.getType(), playerType, damage);
            damage += level % 2;

            int random = (int) (Math.random() * 15);

            if (random == 0 || (player.getCurrHealth() < 5 && random < 5)) {
                dialogueText.setText(getString(R.string.attack_missed, enemy.getName()));
            } else if (random == 1) {
                dialogueText.setText(enemy.getName() + " throws a CRITICAL strike dealing " + damage + " damage.");
                hitPlayer(damage);
                playAttackAnim(false);
            } else {
                dialogueText.setText(enemy.getName() + " strikes dealing " + damage + " damage.");
                hitPlayer(damage);
                playAttackAnim(false);
            }
        }

        public void advance(BattleContext context) {
            if (playerUI.getHealthBar().getProgress() > 0) {
                context.setState(new PlayersTurnState());
            } else {
                context.setState(new FinishState(FinishState.LOST));
            }
        }
    }

    private void hitPlayer(int damage) {
        player.setCurrHealth(player.getCurrHealth() - damage);
        playerUI.updateUI(player.getCurrHealth(), player.getMaxHealth());
        gm.updatePlayerInDatabase(player);
    }

    private class FinishState implements BattleState {

        static final String WON = "won";
        static final String LOST = "lost";
        static final String OUT_OF_WEAPONS = "no_weapons";

        private FinishState(String state) {
            showText();
            switch (state) {
                case LOST:
                    dialogueText.setText(getString(R.string.pass_out, player.getName()));
                    break;
                case WON:
                    dialogueText.setText(player.getName() + " has won the battle. (" + rewardedXP + "XP) " + player.getName() + " received $" + rewardMoney + ".");
                    gm.awardXP(getApplicationContext(), rewardedXP);
                    gm.awardMoney(rewardMoney);
                    gm.addWin();
                    break;
                case OUT_OF_WEAPONS:
                    dialogueText.setText(getString(R.string.out_of_weapons, player.getName()));
                    break;
            }
        }

        public void advance(BattleContext context) {
            finish();
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