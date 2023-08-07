package com.mygdx.dijkstra;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class LevelWonScreen implements Screen {
    private Texture mangoImage;
    private Texture levelWonImage;
    private Texture bucketImage;
    private Stage stage;
    private Sound dropSound;
    private float screenStartTime;
    private int collectedMangos = 0, spawnedMangos = 0;
    final DijkstraAlgorithm game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Rectangle background, bucket;
    private Array<Rectangle> mangos;
    int nextLevel;
    private float elapsedTime = 0f;
    private long lastDropTime;
    Music music;
    Sound sound;

    public LevelWonScreen(final DijkstraAlgorithm game, int nextLevel) {

        stage = new Stage();
        this.game = game;
        this.nextLevel = nextLevel;

        music = game.assetManager.get("pirates.mp3", Music.class);
        music.setVolume(0.5f);

        sound = game.assetManager.get("ambiente.wav", Sound.class);
        sound.play();

        levelWonImage = game.assetManager.get("levelWon.png", Texture.class);
        bucketImage = game.assetManager.get("bucket.png", Texture.class);

        dropSound = game.dropSound;

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        // create a Rectangle to logically represent the bucket
        bucket = new Rectangle(camera.viewportWidth/2,0, 64, 64);

        // create a Rectangle to logically represent the background
        background = new Rectangle();
        background.x = 0; // center the bucket horizontally
        background.y = 0; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
        background.width = camera.viewportWidth;
        background.height = camera.viewportHeight;

        // create the raindrops array and spawn the first raindrop
        mangos = new Array<>();
    }

    private void spawnMangos() {
        mangos.add(new Rectangle(MathUtils.random(0, 800 - 64), camera.viewportHeight, 48, 48));
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.95f, 0.871f, 0.726f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(levelWonImage, background.x, background.y, background.width, background.height);
        batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height);
        for (Rectangle mango : mangos) {
            if (game.assetManager.isLoaded("mango.png", Texture.class)) {
                batch.draw(game.assetManager.get("mango.png", Texture.class), mango.x, mango.y, mango.getWidth(), mango.getHeight());
            }
        }
        batch.end();

        // process user input
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64/2;
        }

        if (Gdx.input.isKeyPressed(Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

        // make sure the bucket stays within the screen bounds
        if (bucket.x < 0) bucket.x = 0;
        if (bucket.x > 800 - 64) bucket.x = 800 - 64;

        float elapsedTime = (TimeUtils.nanoTime() - screenStartTime) / 1000000000f;
        if (elapsedTime >= 1.5f) {
            if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
                spawnMangos();
                if (spawnedMangos >= 10) {
                    // If 10 mangos were collected or lost, switch to a new screen
                    game.mangos += collectedMangos;
                    if (nextLevel != 3.5) {
                        game.setScreen(new MainMenuScreen(game, nextLevel));
                        sound.pause();
                        music.setVolume(1f);
                        dispose();
                    } else {
                        game.setScreen(new GameWonScreen(game));
                    }
                }
            }
        }

        for (Iterator<Rectangle> iter = mangos.iterator(); iter.hasNext(); ) {
            Rectangle mango = iter.next();
            mango.y -= 200 * Gdx.graphics.getDeltaTime();
            if (mango.y + 64 < 0) {
                iter.remove();
                spawnedMangos++;
            }
            if (mango.overlaps(bucket)) {
                collectedMangos++;
                spawnedMangos++;
                dropSound.play();
                iter.remove();
            }
        }
    }

    @Override
    public void show() {
        screenStartTime = TimeUtils.nanoTime();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
    }
}