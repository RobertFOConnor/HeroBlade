package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class BattleActivity extends AndroidApplication implements LibGdxInterface, View.OnClickListener {

    private static final int WEAPON_REQUEST = 1;
    private static final int ITEM_REQUEST = 2;

    private Player player;
    private ArrayList<Enemy> enemies;
    private Enemy enemy;
    private String weaponUUID;

    private LinearLayout textDisplay;
    private GridLayout battleMenu;
    private TextView dialogueText;
    private Button nextButton;
    private ProgressBar userHealthBar;
    private ProgressBar enemyHealthBar;
    private TextView userHealthText;
    private TextView enemyNameText;
    private TextView enemyHealthText;
    private TextView enemiesLeft;
    private ImageView weaponType, enemyType;
    private int turnCount = 0;
    int rewardedXP = 150;
    int rewardMoney = 10;
    DialogInterface.OnClickListener dialogClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        dialogueText = (TextView) findViewById(R.id.dialogue);
        nextButton = (Button) findViewById(R.id.next_button);
        textDisplay = (LinearLayout) findViewById(R.id.text_display);
        battleMenu = (GridLayout) findViewById(R.id.battle_menu);
        userHealthText = (TextView) findViewById(R.id.player_health);
        enemyHealthText = (TextView) findViewById(R.id.enemy_health);
        weaponType = (ImageView) findViewById(R.id.user_type_img);
        enemyType = (ImageView) findViewById(R.id.enemy_type_img);
        enemyNameText = ((TextView) findViewById(R.id.enemy_name));
        enemiesLeft = ((TextView) findViewById(R.id.enemy_count));

        MainGame game = new MainGame(this, MainGame.BATTLE_SCREEN);
        View v = initializeForView(game, new AndroidApplicationConfiguration());
        ((LinearLayout) findViewById(R.id.character_layout)).addView(v);

        player = GameManager.getInstance().getPlayer();
        ((TextView) findViewById(R.id.player_name)).setText(player.getName() + " LVL: " + player.getLevel());


        enemies = new ArrayList<>();
        int enemyCount = ((int) (Math.random() * 6)) + 1;
        for (int i = 0; i < enemyCount; i++) {
            enemies.add(EnemyFactory.buildEnemy(player));
        }
        enemy = enemies.get(0);

        ArrayList<WeaponItem> weapons = GameManager.getInstance().getInventory().getEquippedWeapons();
        if (weapons.size() > 0 && player.getCurrHealth() > 0 && hasUsableWeapon()) {

            for (int i = weapons.size() - 1; i > -1; i--) {
                if (weapons.get(i).getCurrHealth() > 0) {
                    weaponUUID = weapons.get(i).getUUID();
                    weaponType.setImageResource(weapons.get(i).getTypeDrawableRes());
                }
            }
        } else {
            finish(); //TEMP /LEAVE BATTLE IF PLAYER HAS NO WEAPONS
        }

        userHealthBar = (ProgressBar) findViewById(R.id.user_bar);
        userHealthBar.setScaleY(3f);
        userHealthBar.setMax(player.getMaxHealth());
        userHealthBar.setProgress(player.getCurrHealth());
        userHealthText.setText(hPString(player.getCurrHealth(), player.getMaxHealth()));

        enemyHealthBar = (ProgressBar) findViewById(R.id.enemy_bar);
        enemyHealthBar.setScaleY(3f);
        enemyHealthBar.setRotation(180);
        initEnemyUI();


        final Context context = new Context();
        StartState startState = new StartState();
        context.setState(startState);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getState().advance(context);
            }
        });

        findViewById(R.id.attack_button).setOnClickListener(this);
        findViewById(R.id.change_weapon_button).setOnClickListener(this);
        findViewById(R.id.item_button).setOnClickListener(this);
        findViewById(R.id.run_button).setOnClickListener(this);

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
    }

    private void initEnemyUI() {
        enemyHealthBar.setMax(enemy.getMaxHealth());
        enemyHealthBar.setProgress(enemy.getCurrHealth());
        enemyHealthText.setText(hPString(enemy.getCurrHealth(), enemy.getMaxHealth()));
        enemyType.setImageResource(enemy.getTypeDrawableRes());
        enemyNameText.setText(enemy.getName() + " LVL: " + enemy.getLevel());
        enemiesLeft.setText("Enemies Left: " + enemies.size());
    }

    @Override
    public void onClick(View v) {
        WeaponItem weaponItem = GameManager.getInstance().getInventory().getWeapon(weaponUUID);
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
        }
    }

    private void showExitDialog() {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(BattleActivity.this);
        builder.setMessage("Are you sure you want to exit the battle?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();*/
        finish();
    }

    private void useItem() {
        Intent intent = new Intent(this, ItemSelectionActivity.class);
        startActivityForResult(intent, ITEM_REQUEST);
    }

    private void changeWeapon() {
        Intent intent = new Intent(this, WeaponSelectionActivity.class);
        intent.putExtra(WeaponSelectionActivity.CURR_WEAPON, weaponUUID);
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
        if ((int) (Math.random() * 15) == 0) { //Critical hit chance
            damage = damage * 2;
            crit = true;
        }

        enemy.setCurrHealth(enemy.getCurrHealth() - damage);
        enemyHealthBar.setProgress(enemy.getCurrHealth());
        enemyHealthText.setText(hPString(enemy.getCurrHealth(), enemy.getMaxHealth()));
        weaponItem.setCurrHealth(weaponItem.getCurrHealth() - 1);

        String attackMessage = player.getName() + "'s " + weaponItem.getName() + " did " + damage + " damage!";

        if (crit) {
            attackMessage = player.getName() + "'s " + weaponItem.getName() + " did " + damage + " CRITICAL damage!";
        }

        if (weaponItem.getCurrHealth() <= 0) {
            //GameManager.getInstance().removeWeapon(weaponItem.getUUID());
            attackMessage += " " + player.getName() + "'s " + weaponItem.getName() + " is broken!";
            weaponUUID = null;
            v.setEnabled(false);
        }
        GameManager.getInstance().updateWeaponInDatabase(weaponItem.getUUID(), weaponItem.getId(), weaponItem.getCurrHealth(), weaponItem.isEquipped());


        turnCount++;
        showText();
        dialogueText.setText(attackMessage);
    }

    private boolean hasUsableWeapon() {
        for (WeaponItem weapon : GameManager.getInstance().getInventory().getEquippedWeapons()) {
            if (weapon.getCurrHealth() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check request to which we're responding
        if (requestCode == WEAPON_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a weapon.
                weaponUUID = data.getStringExtra("result");
                WeaponItem weaponItem = GameManager.getInstance().getInventory().getWeapon(weaponUUID);
                weaponType.setImageResource(weaponItem.getTypeDrawableRes());
                showText();
                dialogueText.setText(player.getName() + " changed weapon to " + weaponItem.getName() + ".");
                findViewById(R.id.attack_button).setEnabled(true);
            }
        } else if (requestCode == ITEM_REQUEST) {
            if (resultCode == RESULT_OK) {
                // The user used an item.
                FoodItem item = (FoodItem) ItemFactory.buildItem(this, data.getIntExtra("result", 0));
                player.setCurrHealth(GameManager.getInstance().getPlayer().getCurrHealth());
                userHealthBar.setProgress(player.getCurrHealth());
                userHealthText.setText(hPString(player.getCurrHealth(), player.getMaxHealth()));
                showText();
                dialogueText.setText(player.getName() + " used " + item.getName() + ".");
            }
        }
    }

    private int calculateTypes(String attType, String defType, int damage) {
        if (attType.equals("GRASS")) {
            if (defType.equals("WATER")) {
                damage = (int) (damage * 1.5f);
            } else if (defType.equals("FIRE")) {
                damage = (int) (damage / 1.5f);
            }
        } else if (attType.equals("WATER")) {
            if (defType.equals("FIRE")) {
                damage = (int) (damage * 1.5f);
            } else if (defType.equals("GRASS")) {
                damage = (int) (damage / 1.5f);
            }
        } else if (attType.equals("FIRE")) {
            if (defType.equals("GRASS")) {
                damage = (int) (damage * 1.5f);
            } else if (defType.equals("WATER")) {
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
            dialogueText.setText("A wild " + enemy.getName() + " has appeared.");
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
            } else if (!hasUsableWeapon()) {
                context.setState(new FinishState(FinishState.OUT_OF_WEAPONS));
            } else {
                context.setState(new EnemyTurnState());
            }
        }
    }

    private class EnemyTurnState implements BattleState {

        private EnemyTurnState() { //AI TURN TO ATTACK
            showText();

            String playerType = "GRASS";
            if (weaponUUID != null) {
                playerType = GameManager.getInstance().getInventory().getWeapon(weaponUUID).getType();
            }

            int level = enemy.getLevel();
            int basePower = 5;
            int offence = enemy.getLevel() + 3;
            int defense = (player.getLevel() / 2) + 1;
            int damage = (int) Math.floor(Math.floor(Math.floor(2 * level / 5 + 2) * offence * basePower / defense) / 50) + 2;

            damage = calculateTypes(enemy.getType(), playerType, damage);

            int random = (int) (Math.random() * 15);

            if (random == 0 || (player.getCurrHealth() < 5 && random < 5)) {
                dialogueText.setText(enemy.getName() + " tried to attack but missed!");
            } else if (random == 1) {
                dialogueText.setText(enemy.getName() + " throws a CRITICAL punch dealing " + damage + " damage.");
                hitPlayer(damage);
            } else {
                dialogueText.setText(enemy.getName() + " throws a punch dealing " + damage + " damage.");
                hitPlayer(damage);
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
        userHealthText.setText(hPString(player.getCurrHealth(), player.getMaxHealth()));
        GameManager.getInstance().updatePlayerInDatabase(player);
    }

    private class FinishState implements BattleState {

        static final String WON = "won";
        static final String LOST = "lost";
        static final String OUT_OF_WEAPONS = "no_weapons";

        private FinishState(String state) {
            showText();
            if (state.equals(LOST)) {
                dialogueText.setText(player.getName() + " has lost the battle. " + player.getName() + " has passed out.");
            } else if (state.equals(WON)) {
                dialogueText.setText(player.getName() + " has won the battle. " + player.getName() + " gained " + rewardedXP + "XP! " + player.getName() + " recieved $" + rewardMoney + ".");
                GameManager.getInstance().awardXP(getApplicationContext(), rewardedXP);
                GameManager.getInstance().awardMoney(rewardMoney);
            } else if (state.equals(OUT_OF_WEAPONS)) {
                dialogueText.setText(player.getName() + " is out of weapons. " + player.getName() + " has lost.");
            }
        }

        public void advance(Context context) {
            finish();
        }
    }

    private String hPString(int curr, int max) {
        return "HP: " + curr + "/" + max;
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
        return GameManager.getInstance().getAvatar();
    }

    @Override
    public int getNPCId() {
        return 1;
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