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

import ie.ul.postgrad.socialanxietyapp.game.Enemy;
import ie.ul.postgrad.socialanxietyapp.game.EnemyFactory;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.Player;

public class BattleActivity extends AndroidApplication implements LibGdxInterface, View.OnClickListener {

    private static final int WEAPON_REQUEST = 1;

    private Player player;
    private Enemy enemy;
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
        myHealth = (ProgressBar) findViewById(R.id.user_health);
        myHealth.setScaleY(3f);
        myHealth.setMax(player.getMaxHealth());
        myHealth.setProgress(player.getCurrHealth());
        myHealth.setMax(20);
        myHealth.setProgress(20);

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
        switch (v.getId()) {

            case R.id.attack_button:
                enemy.setCurrHealth(enemy.getCurrHealth() - 3);
                enemyHealth.setProgress(enemy.getCurrHealth());
                showText();
                dialogueText.setText("Your sword did 3 damage!");
                break;
            case R.id.change_weapon_button:
                Intent intent = new Intent(this, WeaponSelectionActivity.class);
                startActivityForResult(intent, WEAPON_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == WEAPON_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a weapon.
                int result = data.getIntExtra("result", 0);
                //player.setWeapon(result);
                //updateWeaponButton();
                showText();
                dialogueText.setText("You have changed weapon.");
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
                myHealth.setProgress(myHealth.getProgress() - 5);
                dialogueText.setText(enemy.getName() + " throws a critical punch dealing 5 damage.");
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
        return 2;
    }

    @Override
    public void finishGame() {
        finish();
    }
}