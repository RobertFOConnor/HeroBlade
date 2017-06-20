package ie.ul.postgrad.socialanxietyapp;

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
    private ProgressBar myHealth;
    private ProgressBar enemyHealth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        dialogueText = (TextView) findViewById(R.id.dialogue);
        nextButton = (Button) findViewById(R.id.next_button);
        textDisplay = (LinearLayout) findViewById(R.id.text_display);
        battleMenu = (GridLayout) findViewById(R.id.battle_menu);


        MainGame game = new MainGame(this, MainGame.BATTLE_SCREEN);
        View v = initializeForView(game, new AndroidApplicationConfiguration());
        ((LinearLayout) findViewById(R.id.character_layout)).addView(v);

        enemy = EnemyFactory.buildEnemy();
        ((TextView) findViewById(R.id.enemy_name)).setText(enemy.getName());


        player = GameManager.getInstance().getPlayer();

        ArrayList<WeaponItem> weapons = GameManager.getInstance().getInventory().getWeapons();
        if (weapons.size() > 0) {
            weaponUUID = weapons.get(0).getUUID();
        } else {
            finish(); //TEMP /LEAVE BATTLE IF PLAYER HAS NO WEAPONS
        }

        myHealth = (ProgressBar) findViewById(R.id.user_health);
        myHealth.setScaleY(3f);
        myHealth.setMax(player.getMaxHealth());
        myHealth.setProgress(player.getCurrHealth());

        enemyHealth = (ProgressBar) findViewById(R.id.enemy_health);
        enemyHealth.setScaleY(3f);
        enemyHealth.setMax(enemy.getMaxHealth());
        enemyHealth.setProgress(enemy.getCurrHealth());
        enemyHealth.setRotation(180);

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
                    enemy.setCurrHealth(enemy.getCurrHealth() - weaponItem.getDamage());
                    enemyHealth.setProgress(enemy.getCurrHealth());
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

                    showText();
                    dialogueText.setText(attackMessage);
                }
                break;
            case R.id.change_weapon_button:
                Intent intent = new Intent(this, WeaponSelectionActivity.class);
                intent.putExtra(WeaponSelectionActivity.CURR_WEAPON, weaponUUID);
                startActivityForResult(intent, WEAPON_REQUEST);
                break;
            case R.id.run_button:
                finish();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == WEAPON_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a weapon.
                weaponUUID = data.getStringExtra("result");
                showText();
                dialogueText.setText(player.getName() + " changed weapon to " + GameManager.getInstance().getInventory().getWeapon(weaponUUID).getName() + ".");
                ((Button) findViewById(R.id.attack_button)).setEnabled(true);
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
            if (enemyHealth.getProgress() > 0) {
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
                myHealth.setProgress(myHealth.getProgress() - 3);
                dialogueText.setText(enemy.getName() + " throws a punch dealing 3 damage.");
            } else {
                myHealth.setProgress(myHealth.getProgress() - 4);
                dialogueText.setText(enemy.getName() + " throws a critical punch dealing 4 damage.");
            }
            player.setCurrHealth(myHealth.getProgress());
            GameManager.getInstance().updatePlayerInDatabase();
        }

        public void advance(Context context) {
            if (myHealth.getProgress() > 0) {
                context.setState(new PlayersTurnState());
            } else {
                context.setState(new FinishState());
            }
        }
    }

    private class FinishState implements BattleState {

        private FinishState() {
            showText();
            if (myHealth.getProgress() <= 0) {
                dialogueText.setText(player.getName() + " has lost the battle. " + player.getName() + " has passed out.");
            } else {
                dialogueText.setText(player.getName() + " has won the battle. " + player.getName() + " gained 100XP!");
                GameManager.getInstance().awardXP(100);
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