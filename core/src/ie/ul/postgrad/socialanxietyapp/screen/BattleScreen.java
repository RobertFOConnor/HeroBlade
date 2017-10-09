package ie.ul.postgrad.socialanxietyapp.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.Drawer;
import com.brashmonkey.spriter.Entity;
import com.brashmonkey.spriter.Loader;
import com.brashmonkey.spriter.Mainline;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.SCMLReader;

import java.util.ConcurrentModificationException;

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

    private int pos = 0;

    private Texture bg;
    private Drawer drawer;
    private Player player, npcPlayer;
    private SpriteBatch sb;
    private Avatar avatar, enemyAvatar;
    private int playerSwordId = 1;

    public BattleScreen(LibGdxInterface libGdxInterface, SpriteBatch sb) {
        avatar = libGdxInterface.getAvatar();
        playerSwordId = libGdxInterface.getNPCId() - 1;
        enemyAvatar = new Avatar();
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
        player.setPosition(WIDTH / 4, HEIGHT / 3);
        npcPlayer = initCharacter(data.getEntity(0));
        npcPlayer.flip(true, false); //flip NPC sprite
        npcPlayer.setPosition(WIDTH - (WIDTH / 4), HEIGHT / 3);

        Loader<Sprite> loader = new LibGdxLoader(data);
        loader.load(handle.file()); //Load all sprites
        drawer = new LibGdxDrawer(loader, sb, renderer);
    }

    public void update() {
        try {
            player.update();
            npcPlayer.update();
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }

        npcPlayer.setObject("hair", 1f, 1, enemyAvatar.getHairtype());
        player.setObject("hair", 1f, 1, avatar.getHairtype()); //set correct hair style for player avatar
        player.setObject("sword", 1f, 8, playerSwordId);

        //pos-=100;

        if (pos < -WIDTH) {
            pos = 0;
        }
    }

    public void render() {
        update();

        sb.begin();
        drawer.setColor(1, 1, 1, 1);
        sb.draw(bg, 0, 0, WIDTH, HEIGHT);
        avatar.drawAvatar(drawer, player);
        enemyAvatar.drawAvatar(drawer, npcPlayer);
        sb.end();
    }

    @Override
    public void dispose() {

    }

    private Player initCharacter(Entity entity) {
        Player player = new Player(entity);
        player.setScale((HEIGHT / 1920f) * 1.5f);
        player.setAnimation("sword_idle");

        return player;
    }

    public void swordStrike(boolean isPlayer) {
        if (isPlayer) {
            swordStrike(player);
        } else {
            swordStrike(npcPlayer);
        }
    }

    private void swordStrike(final Player p) {
        p.setAnimation("sword_strike");
        p.addListener(new Player.PlayerListener() {
            @Override
            public void animationFinished(Animation animation) {
                p.setAnimation("sword_idle");
            }

            @Override
            public void animationChanged(Animation oldAnim, Animation newAnim) {

            }

            @Override
            public void preProcess(Player player) {

            }

            @Override
            public void postProcess(Player player) {

            }

            @Override
            public void mainlineKeyChanged(Mainline.Key prevKey, Mainline.Key newKey) {

            }
        });
    }

    public void updateWeapon(int id) {
        playerSwordId = id;
    }
}