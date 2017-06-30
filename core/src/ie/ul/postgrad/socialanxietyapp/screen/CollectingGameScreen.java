package ie.ul.postgrad.socialanxietyapp.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
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

public class CollectingGameScreen implements Screen {

    //Display variables
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture bg, resource, leftArrow, rightArrow;
    private Vector2 touch;

    //Gameplay variables
    private int[] sequence;
    private int seqPos;
    private int winCount;
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

    //Audio
    private Sound hitSound;
    private Sound collectSound;

    //Sprites
    private BitmapFont font;
    private Avatar avatar;
    private Drawer drawer;
    private Player player;
    private Sprite plusLog;
    private int type;
    private Array<Sprite> clouds;
    private float[] cloudSpeeds;

    private LibGdxInterface libGdxInterface;

    public CollectingGameScreen(LibGdxInterface libGdxInterface, SpriteBatch sb, OrthographicCamera camera, int type) {
        this.libGdxInterface = libGdxInterface;
        this.batch = sb;
        this.camera = camera;
        this.avatar = libGdxInterface.getAvatar();
        this.type = type;
    }

    @Override
    public void create() {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        padding = (int) (HEIGHT * 0.02);

        arrowSize = (int) (WIDTH * 0.07);
        arrowSpace = (int) (WIDTH * 0.03);

        shapeRenderer = new ShapeRenderer();
        touch = new Vector2();

        leftArrow = new Texture("l_arrow.png");
        rightArrow = new Texture("r_arrow.png");

        font = new BitmapFont();
        font.getData().setScale(HEIGHT * 0.002f);

        FileHandle handle = Gdx.files.internal("avatar/avatar.scml");
        SCMLReader reader = new SCMLReader(handle.read());
        Data data = reader.getData();

        player = new Player(data.getEntity(0));
        player.setScale((MainGame.HEIGHT / 1920f) * 1.3f);
        player.setAnimation("axe_idle");
        player.setPosition(MainGame.WIDTH / 3, MainGame.HEIGHT / 8);

        Player.PlayerListener myListener = new LibGdxAnimationListener() {
            @Override
            public void animationFinished(Animation animation) {
                if (player.getAnimation().name.equals("axe_strike") || player.getAnimation().name.equals("axe_super_strike")) {
                    player.setAnimation("axe_idle");
                }
            }
        };
        player.addListener(myListener);

        Loader<Sprite> loader = new LibGdxLoader(data);
        loader.load(handle.file()); //Load all sprites
        drawer = new LibGdxDrawer(loader, batch, shapeRenderer);

        initializeGame();
    }

    private void initializeGame() {
        initClouds();


        bg = new Texture("bg.png");
        if (type == 0) {
            resource = new Texture("tree.png");
            hitSound = loadSound("data/chop.ogg");
            collectSound = loadSound("data/wood_get.ogg");

            plusLog = new Sprite(new Texture("plus_wood_log.png"));
            plusLog.setPosition(WIDTH, HEIGHT);
        } else {
            resource = new Texture("rock.png");
            hitSound = loadSound("data/pick.ogg");
            collectSound = loadSound("data/rock_get.ogg");

            plusLog = new Sprite(new Texture("plus_rock.png"));
            plusLog.setPosition(WIDTH, HEIGHT);
        }

        startTime = System.nanoTime();
        currTime = System.nanoTime();
        roundTime = 30000;
        timeLeft = 30000;
        winCount = 0;
        seqPos = 0;
        paused = false;
        generateSequence();
    }

    public void update() {

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
            libGdxInterface.finishGame();
            paused = true;
        } else if (sequence.length == seqPos) {
            player.setAnimation("axe_super_strike");

            collectSound.play();
            winCount++;
            generateSequence();
            seqPos = 0;
            //startTime = currTime;

            libGdxInterface.collectResource(); //add resource to player inventory using interface

            if (roundTime > 1000) {
                roundTime -= 150;
            }

            plusLog.setPosition(WIDTH / 2, HEIGHT / 2);
            plusLog.setAlpha(1f);
            plusLog.setScale((MainGame.HEIGHT / 1920f) * 3f);
        }

        player.update();
        player.setObject("hair", 1f, 1, avatar.getHairtype());

        if (type == 1) {
            player.setObject("item_0064", 1f, 0, 10);
        }

        for (int i = 0; i < clouds.size; i++) {
            Sprite s = clouds.get(i);
            s.translateX(cloudSpeeds[i]);

            if (s.getX() < -400) {
                resetCloud(s);
            }
        }

        if (plusLog.getY() < HEIGHT / 1.5f) {
            plusLog.translateY(1f);

            if (plusLog.getColor().a > 0) {
                plusLog.setAlpha(plusLog.getColor().a - 0.01f);
            }
            plusLog.setScale(plusLog.getScaleX() + 0.01f);
        }
    }

    private void resetCloud(Sprite cloud) {
        cloud.setPosition(WIDTH, HEIGHT / 4 + (int) (Math.random() * (HEIGHT - (HEIGHT / 4 - 50))));
    }

    @Override
    public void render() {

        if (!paused) {

            update();

            batch.begin();
            batch.draw(bg, 0, 0, WIDTH, HEIGHT);

            for (Sprite s : clouds) {
                s.draw(batch);
            }

            batch.draw(resource, WIDTH / 3f, HEIGHT / 8, WIDTH * 0.56f, HEIGHT * 0.46f);

            avatar.drawAvatar(drawer, player);
            drawer.draw(player.getObject("item_0064"));

            int xPos = (WIDTH / 2) - (((sequence.length - seqPos) * (arrowSize + arrowSpace)) / 2);

            batch.setColor(Color.GOLD);

            for (int i = seqPos; i < sequence.length; i++) {

                if (sequence[i] == 0) {
                    batch.draw(leftArrow, xPos, HEIGHT - arrowSize - padding, arrowSize, arrowSize);
                } else {
                    batch.draw(rightArrow, xPos, HEIGHT - arrowSize - padding, arrowSize, arrowSize);
                }
                batch.setColor(Color.WHITE);

                xPos += (arrowSize + arrowSpace);
            }
            font.draw(batch, "Score: " + winCount, padding, padding * 2);

            if (plusLog.getY() < HEIGHT / 1.5f) {
                plusLog.draw(batch);
            }

            batch.end();
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.rect(padding, HEIGHT * 0.85f, WIDTH - (padding * 2), HEIGHT * 0.03f);
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(padding, HEIGHT * 0.85f, (timeLeft / roundTime) * ((float) (WIDTH - (padding * 2))), HEIGHT * 0.03f);
            shapeRenderer.end();

        } else {
            if (Gdx.input.justTouched()) {
                initializeGame();
            }

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            font.draw(batch, "Score: " + winCount, 20, HEIGHT / 2);
            font.draw(batch, "[TAP TO CONTINUE]", 20, HEIGHT / 2 - 100);

            batch.end();
        }
    }

    private Sound loadSound(String name) {
        return Gdx.audio.newSound(Gdx.files.internal(name));
    }

    private void generateSequence() {
        sequence = new int[4 + (winCount / 2)];
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = (int) (Math.random() * 2);
        }
    }

    @Override
    public void dispose() {

    }

    private void hitTree(int dir) {
        if (sequence.length != seqPos && !paused) {
            if (sequence[seqPos] == dir) {
                player.setAnimation("axe_idle");
                player.setAnimation("axe_strike");

                seqPos++;
                long id = hitSound.play();
                hitSound.setPitch(id, .9f + (float) (Math.random() * .4f));
            }
        }
    }

    //initializes the background clouds.
    private void initClouds() {
        clouds = new Array<Sprite>();

        int cloudNum = 8;
        cloudSpeeds = new float[cloudNum];

        for (int i = 0; i < cloudNum; i++) {
            Sprite cloud = new Sprite(new Texture("cloud_1.png"));
            cloud.setScale((MainGame.HEIGHT / 1920f) * 4f);
            clouds.add(cloud);

            cloudSpeeds[i] = -((WIDTH * 0.008f) + (float) (Math.random() * (WIDTH * 0.001f)));
        }

        for (Sprite s : clouds) {
            resetCloud(s);
        }
    }
}
