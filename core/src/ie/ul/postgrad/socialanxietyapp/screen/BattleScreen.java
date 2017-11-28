package ie.ul.postgrad.socialanxietyapp.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
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

/**
 * Created by Robert on 02-May-17.
 * <p>
 * Draws the battle display in libGDX.
 */

public class BattleScreen implements Screen {

    private int pos = 0;

    private Texture bg;
    private Drawer drawer;
    private Player player, npcPlayer;
    private SpriteBatch sb;
    private Avatar avatar, enemyAvatar;
    private int playerSwordId = 1;

    //Display variables
    private ShapeRenderer shapeRenderer;
    private Texture leftArrow, rightArrow, dashes;
    private Vector2 touch;

    //Gameplay variables
    private int[] sequence;
    private int seqPos;
    private boolean paused;
    private long startTime, currTime;
    private float roundTime;
    private float timeLeft;

    //Measurements
    private static int WIDTH;
    private static int HEIGHT;
    private static int arrowSize;
    private static int arrowSpace;
    private static int padding;

    private boolean attacking;

    private int weaponDamage;
    private String type;

    private LibGdxInterface libGdxInterface;
    private OrthographicCamera camera;

    public BattleScreen(LibGdxInterface libGdxInterface, OrthographicCamera camera, SpriteBatch sb) {
        this.libGdxInterface = libGdxInterface;
        this.camera = camera;
        this.sb = sb;

        avatar = libGdxInterface.getAvatar();
        playerSwordId = libGdxInterface.getNPCId() - 1;
        enemyAvatar = new Avatar();
    }

    @Override
    public void create() {
        ShapeRenderer renderer = new ShapeRenderer();
        bg = new Texture("convo_bg.png");

        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();

        padding = (int) (HEIGHT * 0.1);

        arrowSize = (int) (WIDTH * 0.1);
        arrowSpace = (int) (WIDTH * 0.03);

        shapeRenderer = new ShapeRenderer();
        touch = new Vector2();

        leftArrow = new Texture("l_arrow.png");
        rightArrow = new Texture("r_arrow.png");
        dashes = new Texture("dashes.png");

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

        initializeGame();
    }

    private void initializeGame() {
        startTime = System.nanoTime();
        currTime = System.nanoTime();
        roundTime = 4000;
        timeLeft = 4000;
        seqPos = 0;
        paused = false;
        generateSequence();
    }

    public void update() {
        updateArrows();
        try {
            player.update();
            npcPlayer.update();
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
        npcPlayer.setObject("hair", 1f, 1, enemyAvatar.getHairtype());
        player.setObject("hair", 1f, 1, avatar.getHairtype()); //set correct hair style for player avatar
        player.setObject("sword", 1f, 8, playerSwordId);

        if (pos < -WIDTH) {
            pos = 0;
        }
    }

    private void updateArrows() {
        if (attacking) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                hitTree(0);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                hitTree(1);
            }
            if (Gdx.input.justTouched()) {
                touch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
                if (touch.x > WIDTH / 2) {
                    hitTree(1);
                } else {
                    hitTree(0);
                }
            }
            currTime = System.nanoTime();
            timeLeft = roundTime - ((currTime - startTime) / 1000000);
            if (timeLeft <= 0) {
                attacking = false;
                successfulStrike(false);
            } else if (sequence.length == seqPos) {
                successfulStrike(true);
            }
        }
    }

    private void generateSequence() {
        int seqLength = 8;
        if (weaponDamage < 8) {
            seqLength = weaponDamage;
        }
        if (weaponDamage < 2) {
            seqLength = 3;
        }
        sequence = new int[seqLength];
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = (int) (Math.random() * 2);
        }
    }

    private void hitTree(int dir) {
        if (sequence.length != seqPos && !paused) {
            if (sequence[seqPos] == dir) {
                seqPos++;
            } else {
                attacking = false;
                successfulStrike(false);
            }
        }
    }

    public void render() {
        update();
        sb.begin();
        drawer.setColor(1, 1, 1, 1);
        sb.draw(bg, 0, 0, WIDTH, HEIGHT);
        avatar.drawAvatar(drawer, player);
        enemyAvatar.drawAvatar(drawer, npcPlayer);
        renderArrows();
        sb.end();
        renderTimeBar();
    }

    private void renderTimeBar() {
        if (attacking) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.rect(padding, 0, WIDTH - (padding * 2), 50);
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(padding, 0, (timeLeft / roundTime) * ((float) (WIDTH - (padding * 2))), 50);
            shapeRenderer.end();
        }
    }

    private void renderArrows() {
        if (attacking) {

            sb.draw(dashes, (WIDTH / 2) - 4, 0, 8, HEIGHT);
            int xPos = (WIDTH / 2) - (((sequence.length - seqPos) * (arrowSize + arrowSpace)) / 2);

            if (type.equals("GRASS")) {
                sb.setColor(Color.GREEN);
            } else if (type.equals("WATER")) {
                sb.setColor(Color.CYAN);
            } else if (type.equals("FIRE")) {
                sb.setColor(Color.RED);
            }

            for (int i = seqPos; i < sequence.length; i++) {
                Texture arrow = leftArrow;
                if (sequence[i] != 0) {
                    arrow = rightArrow;
                }
                sb.draw(arrow, xPos, padding, arrowSize, arrowSize * 1.5f);
                sb.setColor(Color.WHITE);
                xPos += (arrowSize + arrowSpace);
            }
        }
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

    public void swordStrike(boolean isPlayer, int weaponDamage, String type) {
        if (isPlayer) {
            attacking = true;
            this.weaponDamage = weaponDamage;
            this.type = type;
            initializeGame();
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

    private void successfulStrike(boolean success) {
        attacking = false;
        swordStrike(player);
        libGdxInterface.swordGameWon(success);
    }
}