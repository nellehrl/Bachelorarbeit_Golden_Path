package com.mygdx.dijkstra.systems;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.dijkstra.DijkstraAlgorithm;

public class LevelWonScreen implements Screen {
    private final Texture levelWonImage;
    private final Texture bucketImage;
    private final Stage stage;
    private final Sound dropSound;
    private float screenStartTime;
    private int collectedMangos = 0, spawnedMangos = 0;
    private final Rectangle background, bucket;
    private final Array<Rectangle> mangos;
    private final int nextLevel;
    private long lastDropTime;
    private final Sound sound;
    
    private final DijkstraAlgorithm game;
    private final FitViewport fitViewport;

    //Code inspiration from https://libgdx.com/wiki/start/a-simple-game
    public LevelWonScreen(final DijkstraAlgorithm game, int nextLevel) {

        stage = new Stage();
        this.game = game;
        this.nextLevel = nextLevel;
        fitViewport = game.getFitViewport();

        sound = game.getAssetManager().get("ambiente.wav", Sound.class);
        sound.play(game.getVolume());

        levelWonImage = game.getAssetManager().get("levelWon.png", Texture.class);
        bucketImage = game.getAssetManager().get("bucket.png", Texture.class);

        dropSound = game.getDropSound();

        // create a Rectangle to logically represent the bucket
        bucket = new Rectangle(game.getCamera().viewportWidth / 2, 0, game.getCamera().viewportWidth/15, game.getCamera().viewportWidth/15);

        // create a Rectangle to logically represent the background
        background = new Rectangle();
        background.x = 0; // center the bucket horizontally
        background.y = 0; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
        background.width = game.getCamera().viewportWidth;
        background.height = game.getCamera().viewportHeight;

        // create the raindrops array and spawn the first raindrop
        mangos = new Array<>();
    }

    private void spawnMangos() {
        mangos.add(new Rectangle(MathUtils.random(0, 800 - game.getCamera().viewportWidth/20), game.getCamera().viewportHeight, game.getCamera().viewportWidth/20, game.getCamera().viewportWidth/20));
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.95f, 0.871f, 0.726f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getCamera().update();

        game.getBatch().setProjectionMatrix(game.getCamera().combined);

        game.getBatch().begin();
        game.getBatch().draw(levelWonImage, background.x, background.y, background.width, background.height);
        game.getBatch().draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height);
        for (Rectangle mango : mangos) {
            if (game.getAssetManager().isLoaded("mango.png", Texture.class)) {
                game.getBatch().draw(game.getAssetManager().get("mango.png", Texture.class), mango.x, mango.y, mango.getWidth(), mango.getHeight());
            }
        }
        game.getBatch().end();

        // process user input
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            game.getCamera().unproject(touchPos);
            bucket.x = touchPos.x - (float) 64 / 2;
        }

        if (Gdx.input.isKeyPressed(Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

        // make sure the bucket stays within the screen bounds
        if (bucket.x < 0) bucket.x = 0;
        if (bucket.x > 800 - 64) bucket.x = 800 - 64;

        float elapsedTime = (TimeUtils.nanoTime() - screenStartTime) / 1000000000f;
        if (elapsedTime >= 3f) {
            if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
                spawnMangos();
                if (spawnedMangos >= 10) {
                    // If 10 mangos were collected or lost, switch to a new screen
                    game.setMangos(game.getMangos() + collectedMangos);
                    if (nextLevel != 3.5) {
                        game.resetGlobalState();
                        game.setScreen(new MainMenuScreen(game, nextLevel));
                        sound.pause();
                        //music.setVolume(mainMenuScreen.getVolume());
                        dispose();
                    } else {
                        game.resetGlobalState();
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
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        screenStartTime = TimeUtils.nanoTime();
    }

    @Override
    public void resize(int width, int height) {
        fitViewport.update(width, height, true);
        game.getCamera().position.set((float) 1200 / 2, (float) 720 / 2, 0);
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
        stage.dispose();
    }
}