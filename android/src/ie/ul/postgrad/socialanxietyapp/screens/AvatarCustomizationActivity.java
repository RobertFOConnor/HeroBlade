package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.LibGdxInterface;
import ie.ul.postgrad.socialanxietyapp.MainGame;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;
import ie.ul.postgrad.socialanxietyapp.screen.AvatarScreen;

public class AvatarCustomizationActivity extends AndroidApplication implements LibGdxInterface, View.OnClickListener {

    private MainGame game;
    private boolean edit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_customization);
        App.setStatusBarColor(this);


        //Populate LibGDX view.
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.r = cfg.g = cfg.b = cfg.a = 8;

        game = new MainGame(this, MainGame.AVATAR_SCREEN);
        View view = initializeForView(game, cfg);

        if (graphics.getView() instanceof SurfaceView) {
            SurfaceView glView = (SurfaceView) graphics.getView();
            glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            glView.setZOrderOnTop(true);
        }
        ((LinearLayout) findViewById(R.id.avatar_view)).addView(view);

        try { //Check for avatar edit.
            edit = getIntent().getExtras().getBoolean("edit", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Setup button listeners.
        findViewById(R.id.hair_style_button).setOnClickListener(this);
        findViewById(R.id.hair_color_button).setOnClickListener(this);
        findViewById(R.id.skin_color_button).setOnClickListener(this);
        findViewById(R.id.shirt_color_button).setOnClickListener(this);
        findViewById(R.id.continue_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AvatarScreen avatarDisplay = (AvatarScreen) game.getScreen();
        switch (v.getId()) {
            case R.id.hair_style_button:
                avatarDisplay.changeHairStyle();
                break;
            case R.id.hair_color_button:
                avatarDisplay.changeHairColor();
                break;
            case R.id.skin_color_button:
                avatarDisplay.changeSkinColor();
                break;
            case R.id.shirt_color_button:
                avatarDisplay.changeShirtColor();
                break;
            case R.id.continue_button:
                saveAvatar(avatarDisplay.getAvatar());
                if (!edit) {
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                    SoundManager.getInstance(this).playSound(SoundManager.Sound.CLICK);
                }
                finish();
                break;

        }
    }

    @Override
    public void saveAvatar(Avatar avatar) {
        GameManager.getDbHelper().updateAvatar(avatar);
    }

    @Override
    public Avatar getAvatar() {
        try {
            return GameManager.getInstance().getAvatar();
        } catch (Exception e) {
            return new Avatar(1, 1, 1, 1);
        }
    }

    @Override
    public int getNPCId() {
        return 0;
    }

    @Override
    public void collectResource() {

    }

    @Override
    public void finishGame() {

    }
}
