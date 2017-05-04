package ie.ul.postgrad.socialanxietyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.screen.AvatarScreen;

public class AvatarCustomizationActivity extends AndroidApplication implements LibGdxInterface, View.OnClickListener {

    MainGame game;
    private AvatarScreen avatarDisplay;

    private static final int HAIR_STYLE = 0;
    private static final int HAIR_COLOR = 1;
    private static final int SKIN_COLOR = 2;
    private static final int SHIRT_COLOR = 3;

    private int currState = 0;

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
                currState = HAIR_STYLE;
                avatarDisplay.changeHairStyle();
                break;
            case R.id.hair_color_button:
                currState = HAIR_COLOR;
                avatarDisplay.changeHairColor();
                break;
            case R.id.skin_color_button:
                currState = SKIN_COLOR;
                break;
            case R.id.shirt_color_button:
                currState = SHIRT_COLOR;
                break;
            case R.id.continue_button:
                saveAvatar(avatarDisplay.getAvatar());
                Intent intent = new Intent(getApplicationContext(), PlayerAvatarActivity.class);
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
        return GameManager.getDatabaseHelper().getAvatar(1);
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
