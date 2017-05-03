package ie.ul.postgrad.socialanxietyapp.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.Drawer;
import com.brashmonkey.spriter.Loader;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.SCMLReader;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.LibGdxInterface;
import ie.ul.postgrad.socialanxietyapp.MainGame;
import ie.ul.postgrad.socialanxietyapp.spriter.LibGdxAnimationListener;
import ie.ul.postgrad.socialanxietyapp.spriter.LibGdxDrawer;
import ie.ul.postgrad.socialanxietyapp.spriter.LibGdxLoader;

/**
 * Created by Robert on 24-Mar-17.
 * <p>
 * Libgdx screen. This is where the players avatar is drawn on screen.
 */

public class AvatarScreen implements Screen {

    private static final float[] BLOND = {255f / 255f, 231f / 255f, 70f / 255f};
    private static final float[] BLACK = {0.2f, 0.2f, 0.2f};
    private static final float[] BROWN = {115 / 255f, 85 / 255f, 59 / 255f};
    private static final float[] GINGER = {250 / 255f, 149 / 255f, 40 / 255f};
    private static final float[] GRAY = {0.6f, 0.6f, 0.6f};
    private static final float[] WHITE = {1f, 1f, 1f};

    public static float[][] hairColorArray = new float[][]{BLOND, BLACK, GRAY, BROWN, GINGER, WHITE};
    private int hairColorIndex = 0;

    private Drawer drawer;
    private Player player;
    private SpriteBatch sb;

    private int hairIndex = 0;

    private final LibGdxInterface libGdxInterface;
    private Avatar avatar;

    public AvatarScreen(LibGdxInterface libGdxInterface, SpriteBatch sb) {
        this.libGdxInterface = libGdxInterface;
        avatar = libGdxInterface.getAvatar();

        hairIndex = avatar.getHairtype();
        hairColorIndex = avatar.getHairColor();

        this.sb = sb;
        ShapeRenderer renderer = new ShapeRenderer();

        FileHandle handle = Gdx.files.internal("avatar/avatar.scml");
        SCMLReader reader = new SCMLReader(handle.read());
        Data data = reader.getData();

        player = new Player(data.getEntity(0));
        player.setScale((MainGame.HEIGHT / 1920f) * 4.5f);
        player.setAnimation("idle");
        player.setPosition(MainGame.WIDTH / 2, 0);

        Player.PlayerListener myListener = new LibGdxAnimationListener() {
            @Override
            public void animationFinished(Animation animation) {
                if (player.getAnimation().name.equals("scratch")) {
                    player.setAnimation("idle");
                }
            }
        };
        player.addListener(myListener);


        Loader<Sprite> loader = new LibGdxLoader(data);
        loader.load(handle.file()); //Load all sprites
        drawer = new LibGdxDrawer(loader, sb, renderer);
    }

    @Override
    public void create() {

    }

    public void update() {
        player.update();
        if (Gdx.input.justTouched()) {
            player.setAnimation("scratch");

            /*if (Gdx.input.getX() < MainGame.WIDTH / 2) {

                hairIndex++;
                if (hairIndex >= 9) { //No. of hairstyles
                    hairIndex = 0;
                }
            } else {

                hairColorIndex++;
                if (hairColorIndex >= hairColorArray.length) {
                    hairColorIndex = 0;
                }
            }
            avatar.setHairtype(hairIndex);
            avatar.setHairColor(hairColorIndex);
            libGdxInterface.saveAvatar(avatar);*/
        }
        player.setObject("hair", 1f, 1, hairIndex);
    }

    public void render() {
        update();

        sb.begin();
        drawer.setColor(1, 1, 1, 1);
        drawer.draw(player);
        drawer.setColor(hairColorArray[hairColorIndex][0], hairColorArray[hairColorIndex][1], hairColorArray[hairColorIndex][2], 1);
        drawer.draw(player.getObject("hair"));
        sb.end();
    }

    @Override
    public void dispose() {

    }
}
