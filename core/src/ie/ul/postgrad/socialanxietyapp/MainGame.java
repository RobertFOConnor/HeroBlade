package ie.ul.postgrad.socialanxietyapp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import ie.ul.postgrad.socialanxietyapp.screen.ScreenManager;

/**
 * Created by Robert on 24-Mar-17.
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
    public static final String TREE_GAME_SCREEN = "tree";

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
        batch = new SpriteBatch();

        if(screenName.equals(AVATAR_SCREEN)) {
            ScreenManager.setScreen(new AvatarDisplay(libGdxInterface, batch));
        } else {
            ScreenManager.setScreen(new CollectingGame(libGdxInterface, batch, camera));
        }
    }

    private void update() {
        camera.update();
    }

    @Override
    public void render() {
        update();
        Gdx.gl.glClearColor(119/255f, 213/255f, 195/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        ScreenManager.getCurrentScreen().render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}