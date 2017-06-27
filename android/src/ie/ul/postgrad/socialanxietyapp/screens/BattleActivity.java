package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
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
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

public class BattleActivity extends AndroidApplication implements LibGdxInterface, View.OnClickListener {

    private static final int WEAPON_REQUEST = 1;

    private Player player;
    private Enemy enemy;
    private String weaponUUID;

    private LinearLayout textDisplay;
    private GridLayout battleMenu;
    private TextView dialogueText;
    private Button nextButton;
    private ProgressBar userHealthBar;
    private ProgressBar enemyHealthBar;
    private TextView userHealthText;
    private TextView enemyHealthText;
    private int turnCount = 0;


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


        MainGame game = new MainGame(this, MainGame.BATTLE_SCREEN);
        View v = initializeForView(game, new AndroidApplicationConfiguration());
        ((LinearLayout) findViewById(R.id.character_layout)).addView(v);

        player = GameManager.getInstance().getPlayer();
        ((TextView) findViewById(R.id.player_name)).setText(player.getName() + " LVL: " + player.getLevel());


        enemy = EnemyFactory.buildEnemy(player);
        ((TextView) findViewById(R.id.enemy_name)).setText(enemy.getName() + " LVL: " + enemy.getLevel());

        ArrayList<WeaponItem> weapons = GameManager.getInstance().getInventory().getWeapons();
        if (weapons.size() > 0 && player.getCurrHealth() > 0) {
            weaponUUID = weapons.get(0).getUUID();
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
        enemyHealthBar.setMax(enemy.getMaxHealth());
        enemyHealthBar.setProgress(enemy.getCurrHealth());
        enemyHealthBar.setRotation(180);
        enemyHealthText.setText(hPString(enemy.getCurrHealth(), enemy.getMaxHealth()));

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
            case R.id.run_button:
                finish();
                break;
        }
    }

    private void changeWeapon() {
        Intent intent = new Intent(this, WeaponSelectionActivity.class);
        intent.putExtra(WeaponSelectionActivity.CURR_WEAPON, weaponUUID);
        startActivityForResult(intent, WEAPON_REQUEST);
    }

    private void strikeEnemy(View v, WeaponItem weaponItem) {
        enemy.setCurrHealth(enemy.getCurrHealth() - weaponItem.getDamage());
        enemyHealthBar.setProgress(enemy.getCurrHealth());
        enemyHealthText.setText(hPString(enemy.getCurrHealth(), enemy.getMaxHealth()));
        weaponItem.setCurrHealth(weaponItem.getCurrHealth() - 1);

        String attackMessage = player.getName() + "'s " + weaponItem.getName() + " did " + weaponItem.getDamage() + " damage!";

        if (weaponItem.getCurrHealth() <= 0) {
            GameManager.getInstance().removeWeapon(weaponItem.getUUID());
            attackMessage += " " + player.getName() + "'s " + weaponItem.getName() + " is broken!";
            weaponUUID = null;
            v.setEnabled(false);
        } else {
            GameManager.getInstance().updateWeaponInDatabase(weaponItem.getUUID(), weaponItem.getId(), weaponItem.getCurrHealth());
        }

        turnCount++;
        showText();
        dialogueText.setText(attackMessage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check request to which we're responding
        if (requestCode == WEAPON_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a weapon.
                weaponUUID = data.getStringExtra("result");
                showText();
                dialogueText.setText(player.getName() + " changed weapon to " + GameManager.getInstance().getInventory().getWeapon(weaponUUID).getName() + ".");
                findViewById(R.id.attack_button).setEnabled(true);
            }
        }
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
            if (enemyHealthBar.getProgress() > 0) {
                context.setState(new EnemyTurnState());
            } else {
                context.setState(new FinishState());
            }
        }
    }

    private class EnemyTurnState implements BattleState {

        private EnemyTurnState() { //AI TURN TO ATTACK
            showText();
            int randomNum = (int) (Math.random() * 3);
            if (randomNum == 0) {
                dialogueText.setText(enemy.getName() + " has been stunned and does not attack.");
            } else if (randomNum == 1) {
                int damage = 1 + (enemy.getLevel() % 3);
                player.setCurrHealth(player.getCurrHealth() - damage);
                dialogueText.setText(enemy.getName() + " throws a punch dealing " + damage + " damage.");
            } else {
                int damage = 2 + (enemy.getLevel() % 3);
                player.setCurrHealth(player.getCurrHealth() - damage);
                dialogueText.setText(enemy.getName() + " throws a critical punch dealing " + damage + " damage.");
            }

            userHealthBar.setProgress(player.getCurrHealth());
            userHealthText.setText(hPString(player.getCurrHealth(), player.getMaxHealth()));

            GameManager.getInstance().updatePlayerInDatabase();
        }

        public void advance(Context context) {
            if (userHealthBar.getProgress() > 0) {
                context.setState(new PlayersTurnState());
            } else {
                context.setState(new FinishState());
            }
        }
    }

    private class FinishState implements BattleState {

        private FinishState() {
            showText();
            if (userHealthBar.getProgress() <= 0) {
                dialogueText.setText(player.getName() + " has lost the battle. " + player.getName() + " has passed out.");
            } else {
                int rewardedXP = 150;

                rewardedXP += enemy.getLevel() * 5;
                rewardedXP += turnCount * 2;

                dialogueText.setText(player.getName() + " has won the battle. " + player.getName() + " gained " + rewardedXP + "XP!");
                GameManager.getInstance().awardXP(getApplicationContext(), rewardedXP);
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
        return GameManager.getDatabaseHelper().getAvatar(1);
    }

    @Override
    public int getNPCId() {
        return 1;
    }

    @Override
    public void finishGame() {
        finish();
    }
}