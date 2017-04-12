package ie.ul.postgrad.socialanxietyapp;

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

import ie.ul.postgrad.socialanxietyapp.screen.Screen;
import ie.ul.postgrad.socialanxietyapp.spriter.LibGdxDrawer;
import ie.ul.postgrad.socialanxietyapp.spriter.LibGdxLoader;

import static ie.ul.postgrad.socialanxietyapp.MainGame.HEIGHT;
import static ie.ul.postgrad.socialanxietyapp.MainGame.WIDTH;

/**
 * Created by Robert on 12-Apr-17.
 */

class ConversationDisplay implements Screen {

    private Texture bg;
    private Drawer drawer;
    private Player player, npcPlayer, currPlayer;
    private SpriteBatch sb;
    private Avatar avatar;
    private Color hairColor;
    private LibGdxInterface libGdxInterface;

    ConversationDisplay(LibGdxInterface libGdxInterface, SpriteBatch sb) {
        this.libGdxInterface = libGdxInterface;
        avatar = libGdxInterface.getAvatar();
        float[][] hcArr = AvatarDisplay.hairColorArray; //set up correct avatar hair color.
        hairColor = new Color(hcArr[avatar.getHairColor()][0], hcArr[avatar.getHairColor()][1], hcArr[avatar.getHairColor()][2], 1);
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
        npcPlayer = initCharacter(data.getEntity(libGdxInterface.getNPCId()));
        npcPlayer.flip(true, false); //flip NPC sprite

        Loader<Sprite> loader = new LibGdxLoader(data);
        loader.load(handle.file()); //Load all sprites
        drawer = new LibGdxDrawer(loader, sb, renderer);

        currPlayer = npcPlayer;
    }

    //For now, toggle characters during convo.
    void updateConvo(int charId) {
        if (charId == 0) {
            currPlayer = player;
        } else {
            currPlayer = npcPlayer;
        }
    }

    public void update() {
        currPlayer.update();

        if (isPlayerShown()) {
            player.setObject("hair", 1f, 1, avatar.getHairtype()); //set correct hair style for player avatar
        }
    }

    public void render() {
        update();

        sb.begin();
        drawer.setColor(1, 1, 1, 1);
        sb.draw(bg, 0, 0, WIDTH, HEIGHT);
        drawer.draw(currPlayer);

        if (isPlayerShown()) {
            drawer.setColor(hairColor.r, hairColor.g, hairColor.b, 1); //draw correct avatar hair color if user is shown.
            drawer.draw(player.getObject("hair"));
        }
        sb.end();
    }

    @Override
    public void dispose() {

    }

    private Player initCharacter(Entity entity) {
        Player player = new Player(entity);
        player.setScale((HEIGHT / 1920f) * 2f);
        player.setAnimation("idle");
        player.setPosition(WIDTH / 2, HEIGHT / 2 - 150);
        return player;
    }

    private boolean isPlayerShown() {
        return currPlayer.equals(player);
    }
}