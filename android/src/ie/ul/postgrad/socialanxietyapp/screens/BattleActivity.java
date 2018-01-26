package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.LibGdxInterface;
import ie.ul.postgrad.socialanxietyapp.MainGame;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.Enemy;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.battle.BattleMVP;
import ie.ul.postgrad.socialanxietyapp.game.battle.BattlePresenter;
import ie.ul.postgrad.socialanxietyapp.game.factory.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.FoodItem;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;
import ie.ul.postgrad.socialanxietyapp.screen.BattleScreen;

import static ie.ul.postgrad.socialanxietyapp.Constants.APP_PATH;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.BATTLE_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.INFO_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.REVIEW_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.TRANSPARENT_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.ItemSelectActivity.SELECT_ITEM;
import static ie.ul.postgrad.socialanxietyapp.screens.ItemSelectActivity.SELECT_WEAPON;

public class BattleActivity extends AndroidApplication implements LibGdxInterface, View.OnClickListener, BattleMVP.BattleView {

    private static final int WEAPON_REQUEST = 1;
    private static final int ITEM_REQUEST = 2;

    private GameManager gm;
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
    private BattlePresenter battlePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        gm = GameManager.getInstance();
        gm.initDatabase(this);
        initViews();
        setupListeners();
        game = new MainGame(this, MainGame.BATTLE_SCREEN);
        View v = initializeForView(game, new AndroidApplicationConfiguration());
        ((LinearLayout) findViewById(R.id.character_layout)).addView(v);
        showHelpInfo();
        battlePresenter = new BattlePresenter(this, gm);
    }

    @Override
    public void initUI(Enemy e, Player p, int enemyCount) {
        ((TextView) findViewById(R.id.player_name)).setText(getString(R.string.name_with_level, p.getName(), p.getLevel()));
        ProgressBar userHealthBar = findViewById(R.id.user_bar);
        TextView userHealthText = findViewById(R.id.player_health);
        userHealthBar.setMax(p.getMaxHealth());
        userHealthBar.setProgress(p.getCurrHealth());
        userHealthText.setText(getString(R.string.health_amount, p.getCurrHealth(), p.getMaxHealth()));
        playerUI = new PlayerUI(userHealthBar, userHealthText);

        ProgressBar enemyHealthBar = findViewById(R.id.enemy_bar);
        TextView enemyHealthText = findViewById(R.id.enemy_health);
        enemyHealthBar.setRotation(180f);
        enemyUI = new PlayerUI(enemyHealthBar, enemyHealthText);

        enemyUI.getHealthBar().setMax(e.getMaxHealth());
        enemyUI.getHealthBar().setProgress(e.getCurrHealth());
        enemyUI.getHealthText().setText(getString(R.string.health_amount, e.getCurrHealth(), e.getMaxHealth()));
        enemyType.setImageResource(e.getTypeDrawableRes());
        enemyNameText.setText(getString(R.string.name_with_level, e.getName(), e.getLevel()));
        enemiesLeft.setText(getString(R.string.enemies_left, enemyCount));

    }

    private void showHelpInfo() {
        SharedPreferences prefs = this.getSharedPreferences(APP_PATH, Context.MODE_PRIVATE);

        final String key = "firstTimeBattle";
        boolean firstTimeMap = prefs.getBoolean(key, true);
        if (firstTimeMap) {
            Intent tutorialIntent = new Intent(this, HelpActivity.class);
            //bundle here...
            tutorialIntent.putExtra(INFO_KEY, BATTLE_INFO);
            tutorialIntent.putExtra(REVIEW_KEY, false);
            tutorialIntent.putExtra(TRANSPARENT_KEY, true);
            startActivity(tutorialIntent);
            prefs.edit().putBoolean(key, false).apply();
        }
    }

    private void setupListeners() {
        setClickListener(R.id.attack_button);
        setClickListener(R.id.change_weapon_button);
        setClickListener(R.id.item_button);
        setClickListener(R.id.run_button);
        setClickListener(R.id.next_button);
    }

    private void setClickListener(int id) {
        findViewById(id).setOnClickListener(this);
    }

    private void initViews() {
        dialogueText = findViewById(R.id.dialogue);
        textDisplay = findViewById(R.id.text_display);
        battleMenu = findViewById(R.id.battle_menu);
        weaponType = findViewById(R.id.user_type_img);
        enemyType = findViewById(R.id.enemy_type_img);
        enemyNameText = findViewById(R.id.enemy_name);
        enemiesLeft = findViewById(R.id.enemy_count);
    }

    public void updateWeaponDisplay(WeaponItem weapon) {
        battleDisplay = (BattleScreen) game.getScreen();
        battleDisplay.updateWeapon(weapon.getId() - 1);
    }

    @Override
    public void setAttackButtonEnabled(boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.attack_button).setEnabled(false);
            }
        });
    }

    @Override
    public void showAttackMiniGame(boolean isPlayer) {
        if (isPlayer) {
            findViewById(R.id.container).setVisibility(View.GONE);
        }
        WeaponItem w = battlePresenter.getWeapon();
        if (w != null) {
            battleDisplay = (BattleScreen) game.getScreen();
            battleDisplay.swordStrike(isPlayer, w.getDamage(), w.getType());
        } else {
            battleDisplay = (BattleScreen) game.getScreen();
            battleDisplay.swordStrike(isPlayer, 0, "FIRE");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attack_button:
                if (battlePresenter.getWeapon() != null) {
                    showAttackMiniGame(true);
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
                battlePresenter.advanceState();
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
        WeaponItem weapon = battlePresenter.getWeapon();
        Intent intent = new Intent(this, ItemSelectActivity.class);
        intent.putExtra(ItemSelectActivity.SELECT_TYPE, SELECT_WEAPON);
        String UID = "";
        if (weapon != null) {
            UID = weapon.getUUID();
        }
        intent.putExtra(ItemSelectActivity.CURR_WEAPON, UID);
        startActivityForResult(intent, WEAPON_REQUEST);
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

        void updateUI(final int currHealth, final int maxHealth) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    healthBar.setProgress(currHealth);
                    healthText.setText(getString(R.string.health_amount, currHealth, maxHealth));
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check request to which we're responding
        if (requestCode == WEAPON_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a weapon.
                WeaponItem weaponItem = gm.getInventory().getWeapon(data.getStringExtra(getString(R.string.result)));
                battlePresenter.changeWeapon(weaponItem);
                weaponType.setImageResource(weaponItem.getTypeDrawableRes());
                showText(getString(R.string.weapon_change, gm.getPlayer().getName(), weaponItem.getName()));
                findViewById(R.id.attack_button).setEnabled(true);
                updateWeaponDisplay(weaponItem);
            }
        } else if (requestCode == ITEM_REQUEST) {
            if (resultCode == RESULT_OK) {
                // The user used an item.
                FoodItem item = (FoodItem) ItemFactory.buildItem(this, data.getIntExtra(getString(R.string.result), 0));
                battlePresenter.useItem(item);
            }
        }
    }

    @Override
    public void showText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.container).setVisibility(View.VISIBLE);
                battleMenu.setVisibility(View.INVISIBLE);
                textDisplay.setVisibility(View.VISIBLE);
                dialogueText.setText(text);
            }
        });
    }

    @Override
    public void showMenu() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.container).setVisibility(View.VISIBLE);
                battleMenu.setVisibility(View.VISIBLE);
                textDisplay.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void updateUI(Enemy e, Player p) {
        enemyUI.updateUI(e.getCurrHealth(), e.getMaxHealth());
        playerUI.updateUI(p.getCurrHealth(), p.getMaxHealth());
    }

    @Override
    public void endBattle() {
        finish();
    }

    @Override
    public void saveAvatar(Avatar avatar) {
        //empty method. (This isn't called from this activity. (used for changing avatar appearance.))
    }

    @Override
    public void swordGameWon(boolean success) {
        if (success) {
            battlePresenter.strikeEnemy(battlePresenter.getWeapon());
        } else {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.container).setVisibility(View.VISIBLE);
                    String failMessage = "Attack Failed.";
                    showText(failMessage);
                }
            });
        }
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
        return battlePresenter.getWeapon().getId();
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