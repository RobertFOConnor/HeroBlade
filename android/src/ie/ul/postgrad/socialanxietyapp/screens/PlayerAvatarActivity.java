package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceView;
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
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;
import ie.ul.postgrad.socialanxietyapp.game.XPLevels;

public class PlayerAvatarActivity extends AndroidApplication implements LibGdxInterface, View.OnClickListener {

    private GameManager gm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_avatar);
        gm = GameManager.getInstance();
        setupStats();

        //Populate LibGDX view.
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.r = cfg.g = cfg.b = cfg.a = 8;

        MainGame game = new MainGame(this, MainGame.AVATAR_SCREEN);
        View view = initializeForView(game, cfg);

        if (graphics.getView() instanceof SurfaceView) {
            SurfaceView glView = (SurfaceView) graphics.getView();
            glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            glView.setZOrderOnTop(true);
        }
        ((LinearLayout) findViewById(R.id.avatar_view)).addView(view);
        App.setStatusBarColor(this);

        //Setup button listeners.
        findViewById(R.id.edit_avatar).setOnClickListener(this);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.mood_rating).setOnClickListener(this);
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
    public Avatar getAvatar() {
        return gm.getAvatar();
    }

    @Override
    public int getNPCId() {
        return 0;
    }

    @Override
    public void swordGameWon(boolean success) {

    }

    @Override
    public void finishGame() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_avatar:
                Intent i = new Intent(getApplicationContext(), AvatarCustomizationActivity.class);
                i.putExtra("edit", true);
                startActivity(i);
                finish();
                SoundManager.getInstance(this).playSound(SoundManager.Sound.CLICK);
                break;
            case R.id.mood_rating:
                i = new Intent(getApplicationContext(), MoodLogActivity.class);
                startActivity(i);
                finish();
                SoundManager.getInstance(this).playSound(SoundManager.Sound.CLICK);
                break;
            case R.id.back_button:
                finish();
                SoundManager.getInstance(this).playSound(SoundManager.Sound.BACK);
                break;
        }
    }
}
