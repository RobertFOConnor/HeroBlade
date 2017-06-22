package ie.ul.postgrad.socialanxietyapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.auth.FirebaseAuth;

import ie.ul.postgrad.socialanxietyapp.account.WelcomeActivity;
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
        ((TextView) findViewById(R.id.gold_field)).setText(player.getMoney() + "");

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        MainGame game = new MainGame(this, MainGame.AVATAR_SCREEN);
        View v = initializeForView(game, config);
        ((LinearLayout) findViewById(R.id.avatar_view)).addView(v);

        findViewById(R.id.log_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

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
        return GameManager.getDatabaseHelper().getAvatar(1);
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
