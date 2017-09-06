package ie.ul.postgrad.socialanxietyapp.screens;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.LibGdxInterface;
import ie.ul.postgrad.socialanxietyapp.MainGame;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.XPLevels;

public class PlayerAvatarActivity extends AndroidApplication implements LibGdxInterface {

    private GameManager gm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_avatar);
        gm = GameManager.getInstance();
        setupStats();
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        MainGame game = new MainGame(this, MainGame.AVATAR_SCREEN);
        View v = initializeForView(game, config);
        ((LinearLayout) findViewById(R.id.avatar_view)).addView(v);
        App.setStatusBarColor(this);
    }

    private void setupStats() {
        Player player = gm.getPlayer();
        ((TextView) findViewById(R.id.name_field)).setText(player.getName());
        ((TextView) findViewById(R.id.level_num)).setText(String.valueOf(player.getLevel()));
        ((TextView) findViewById(R.id.xp_text)).setText(getString(R.string.xp_display, player.getXp(), XPLevels.XP_LEVELS[player.getLevel()]));
        ((TextView) findViewById(R.id.health_field)).setText(player.getCurrHealth() + "/" + player.getMaxHealth());
        ((TextView) findViewById(R.id.sword_field)).setText(String.valueOf(gm.getFoundWeapons().size()));
        ((TextView) findViewById(R.id.cash_field)).setText(getString(R.string.cash_display, player.getMoney()));
        ((TextView) findViewById(R.id.villages_field)).setText(String.valueOf(gm.getVillageCount()));
    }

    @Override
    public void saveAvatar(Avatar avatar) {

    }


    @Override
    public void collectResource() {

    }

    @Override
    public Avatar getAvatar() {
        return gm.getAvatar();
    }

    @Override
    public int getNPCId() {
        return 0;
    }

    @Override
    public void finishGame() {
        finish();
    }
}
