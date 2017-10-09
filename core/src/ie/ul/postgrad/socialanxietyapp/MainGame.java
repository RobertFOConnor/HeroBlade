package ie.ul.postgrad.socialanxietyapp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import ie.ul.postgrad.socialanxietyapp.screen.AvatarScreen;
import ie.ul.postgrad.socialanxietyapp.screen.BattleScreen;
import ie.ul.postgrad.socialanxietyapp.screen.ConversationScreen;
import ie.ul.postgrad.socialanxietyapp.screen.Screen;
import ie.ul.postgrad.socialanxietyapp.screen.ScreenManager;

/**
 * Created by Robert on 24-Mar-17.
 * <p>
 * This is the main Libgdx Application. This recieves a string which tells what screen should be displayed.
 */

public class MainGame extends ApplicationAdapter {

    private final LibGdxInterface libGdxInterface;

    //Display variables
    private OrthographicCamera camera;
    private SpriteBatch batch;

    //Measurements
    public static int WIDTH;
    public static int HEIGHT;

    private String screenName;
    public static final String AVATAR_SCREEN = "avatar";
    public static final String CONVERSATION_SCREEN = "convo";
    public static final String BATTLE_SCREEN = "battle";

    public MainGame(LibGdxInterface libGdxInterface, String screenName) {
        this.libGdxInterface = libGdxInterface;
        this.screenName = screenName;
    }

    @Override
    public void create() {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(WIDTH, HEIGHT);
        camera.translate(WIDTH / 2, HEIGHT / 2);
        camera.update();
        batch = new SpriteBatch();

        if (screenName.equals(AVATAR_SCREEN)) {
            ScreenManager.setScreen(new AvatarScreen(libGdxInterface, batch));
        } else if (screenName.equals(CONVERSATION_SCREEN)) {
            ScreenManager.setScreen(new ConversationScreen(libGdxInterface, batch));
        } else if (screenName.equals(BATTLE_SCREEN)) {
            ScreenManager.setScreen(new BattleScreen(libGdxInterface, batch));
        }
    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        batch.setProjectionMatrix(camera.combined);
        ScreenManager.getCurrentScreen().render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public void updateConvo(int charId, String type) {
        if (ScreenManager.getCurrentScreen() instanceof ConversationScreen) {
            ((ConversationScreen) (ScreenManager.getCurrentScreen())).updateConvo(charId, type);
        }
    }

    public Screen getScreen() {
        return ScreenManager.getCurrentScreen();
    }

}
