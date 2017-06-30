package ie.ul.postgrad.socialanxietyapp.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.Drawer;
import com.brashmonkey.spriter.Entity;
import com.brashmonkey.spriter.Loader;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.SCMLReader;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.LibGdxInterface;
import ie.ul.postgrad.socialanxietyapp.spriter.LibGdxDrawer;
import ie.ul.postgrad.socialanxietyapp.spriter.LibGdxLoader;

import static ie.ul.postgrad.socialanxietyapp.MainGame.HEIGHT;
import static ie.ul.postgrad.socialanxietyapp.MainGame.WIDTH;

/**
 * Created by Robert on 02-May-17.
 */

public class BattleScreen implements Screen {

    private Texture bg;
    private Drawer drawer;
    private Player player, npcPlayer;
    private SpriteBatch sb;
    private Avatar avatar;
    private LibGdxInterface libGdxInterface;

    public BattleScreen(LibGdxInterface libGdxInterface, SpriteBatch sb) {
        this.libGdxInterface = libGdxInterface;
        avatar = libGdxInterface.getAvatar();
        this.sb = sb;
    }

    @Override
    public void create() {
        ShapeRenderer renderer = new ShapeRenderer();
        bg = new Texture("convo_bg.png");

        FileHandle handle = Gdx.files.internal("avatar/avatar.scml");
        SCMLReader reader = new SCMLReader(handle.read());
        Data data = reader.getData();

        //initialize spriter characters.
        player = initCharacter(data.getEntity(0));
        player.setPosition(WIDTH / 4, HEIGHT/3);
        npcPlayer = initCharacter(data.getEntity(libGdxInterface.getNPCId()));
        npcPlayer.flip(true, false); //flip NPC sprite
        npcPlayer.setPosition(WIDTH - (WIDTH / 4), HEIGHT/3);

        Loader<Sprite> loader = new LibGdxLoader(data);
        loader.load(handle.file()); //Load all sprites
        drawer = new LibGdxDrawer(loader, sb, renderer);
    }

    public void update() {
        player.update();
        npcPlayer.update();

        player.setObject("hair", 1f, 1, avatar.getHairtype()); //set correct hair style for player avatar
    }

    public void render() {
        update();

        sb.begin();
        drawer.setColor(1, 1, 1, 1);
        sb.draw(bg, 0, 0, WIDTH, HEIGHT);
        avatar.drawAvatar(drawer, player);

        drawer.draw(npcPlayer);
        sb.end();
    }

    @Override
    public void dispose() {

    }

    private Player initCharacter(Entity entity) {
        Player player = new Player(entity);
        player.setScale((HEIGHT / 1920f) * 1.5f);
        player.setAnimation("idle");

        return player;
    }
}