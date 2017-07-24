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

    private Drawer drawer;
    private Player player;
    private SpriteBatch sb;
    private Avatar avatar;

    public AvatarScreen(LibGdxInterface libGdxInterface, SpriteBatch sb) {
        avatar = libGdxInterface.getAvatar();

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
            //changeShirtColor(); //testing
        }
        player.setObject("hair", 1f, 1, avatar.getHairtype());
    }

    public void render() {
        update();

        sb.begin();
        avatar.drawAvatar(drawer, player);
        sb.end();
    }

    @Override
    public void dispose() {

    }

    public void changeShirtColor() {
        avatar.setShirtColor(avatar.getShirtColor() + 1);
    }

    public void changeHairStyle() {
        avatar.setHairtype(avatar.getHairtype() + 1);
    }

    public void changeHairColor() {
        avatar.setHairColor(avatar.getHairColor() + 1);
    }

    public void changeSkinColor() {
        avatar.setSkinColor(avatar.getSkinColor() + 1);
    }

    public Avatar getAvatar() {
        return avatar;
    }
}
