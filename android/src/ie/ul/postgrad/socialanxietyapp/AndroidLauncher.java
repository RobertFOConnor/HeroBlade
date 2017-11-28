package ie.ul.postgrad.socialanxietyapp;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import ie.ul.postgrad.socialanxietyapp.game.GameManager;

public class AndroidLauncher extends AndroidApplication implements LibGdxInterface {

    public static final String screenString = "screen";
    private String screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        Bundle bundle = getIntent().getExtras();
        screen = (String) bundle.get(screenString);

        initialize(new MainGame(this, screen), config);
    }

    @Override
    public void saveAvatar(Avatar avatar) {
        GameManager.getDbHelper().updateAvatar(avatar);
        //save to database
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
    public void swordGameWon(boolean success) {

    }

    @Override
    public void finishGame() {
        //GameManager.getInstance().awardXP(getApplicationContext(), 100);
        finish();
    }
}
