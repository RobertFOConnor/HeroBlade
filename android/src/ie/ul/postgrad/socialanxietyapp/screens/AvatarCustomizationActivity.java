package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.LibGdxInterface;
import ie.ul.postgrad.socialanxietyapp.MainGame;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.screen.AvatarScreen;

public class AvatarCustomizationActivity extends AndroidApplication implements LibGdxInterface, View.OnClickListener {

    MainGame game;
    private AvatarScreen avatarDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_customization);
        findViewById(R.id.hair_style_button).setOnClickListener(this);
        findViewById(R.id.hair_color_button).setOnClickListener(this);
        findViewById(R.id.skin_color_button).setOnClickListener(this);
        findViewById(R.id.shirt_color_button).setOnClickListener(this);
        findViewById(R.id.continue_button).setOnClickListener(this);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        game = new MainGame(this, MainGame.AVATAR_SCREEN);
        View v = initializeForView(game, config);
        ((LinearLayout) findViewById(R.id.avatar_view)).addView(v);
    }

    @Override
    public void onClick(View v) {
        avatarDisplay = (AvatarScreen) game.getScreen();
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
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                finish();
                break;

        }
    }

    @Override
    public void saveAvatar(Avatar avatar) {
        GameManager.getDatabaseHelper().updateAvatar(avatar);
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
    public void collectResource() {

    }

    @Override
    public void finishGame() {

    }
}
