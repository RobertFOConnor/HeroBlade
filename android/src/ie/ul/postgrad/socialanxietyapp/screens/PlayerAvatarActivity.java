package ie.ul.postgrad.socialanxietyapp.screens;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.LibGdxInterface;
import ie.ul.postgrad.socialanxietyapp.MainGame;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.Player;

public class PlayerAvatarActivity extends AndroidApplication implements LibGdxInterface {

    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player_avatar);
        player = GameManager.getInstance().getPlayer();


        ((TextView) findViewById(R.id.name_field)).setText(player.getName());
        ((TextView) findViewById(R.id.level_num)).setText(player.getLevel() + "");
        ((TextView) findViewById(R.id.xp_text)).setText("XP: " + player.getXp() + "/" + player.getXPNeeded());
        ((TextView) findViewById(R.id.health_field)).setText(player.getCurrHealth() + "/" + player.getMaxHealth());
        //((TextView) findViewById(R.id.gold_field)).setText(player.getMoney() + "");
        ((TextView) findViewById(R.id.sword_field)).setText(GameManager.getInstance().getFoundWeapons().size() + "");
        ((TextView) findViewById(R.id.cash_field)).setText("$" + player.getMoney());

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        MainGame game = new MainGame(this, MainGame.AVATAR_SCREEN);
        View v = initializeForView(game, config);
        ((LinearLayout) findViewById(R.id.avatar_view)).addView(v);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_color));
        }
    }

    @Override
    public void saveAvatar(Avatar avatar) {

    }


    @Override
    public void collectResource() {

    }

    @Override
    public Avatar getAvatar() {
        return GameManager.getInstance().getAvatar();
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
