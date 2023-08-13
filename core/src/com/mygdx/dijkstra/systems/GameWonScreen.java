package com.mygdx.dijkstra.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.dijkstra.DijkstraAlgorithm;

public class GameWonScreen implements Screen {
    private final DijkstraAlgorithm game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private final FitViewport fitViewport;
    private Stage stage;

    public GameWonScreen(final DijkstraAlgorithm game) {
        this.game = game;

        camera = game.getCamera();
        fitViewport = game.getFitViewport();

        stage = new Stage(fitViewport);
        batch = new SpriteBatch();

        Music music = game.getAssetManager().get("pirates.mp3", Music.class);
        music.pause();
        Sound sound = game.getAssetManager().get("ambiente.wav", Sound.class);
        sound.play();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.95f, 0.871f, 0.726f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);

        Rectangle gameWon = new Rectangle();
        gameWon.setSize(camera.viewportWidth, camera.viewportHeight);
        gameWon.setPosition(0, -10);

        String wonLabel = "Congrats Captain!!!!\n\nYou have mastered the art of the golden paths. Now we will always have enough mangooos.\n\n" +
                "You have collected " + game.getMangos() + " mangooos during our adventure. Thanks you owe me nothing anymore! \n\n Good luck with the pirates.";

        batch.begin();
        batch.draw(game.getAssetManager().get("gameWon.png", Texture.class), gameWon.x, gameWon.y, gameWon.width, gameWon.height);
        game.getFontSkin().getFont("font").draw(batch, wonLabel, camera.viewportWidth / 2 - camera.viewportWidth / 6,
                (float) (camera.viewportHeight - camera.viewportHeight * 0.28), camera.viewportWidth / 3, 1, true);
        batch.end();
    }

    @Override
    public void show() {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        fitViewport.update(width, height, true);
        camera.position.set((float) 1200 / 2, (float) 720 / 2, 0);
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
    }
}