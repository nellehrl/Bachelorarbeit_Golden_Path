package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.dijkstra.DijkstraAlgorithm;
import com.mygdx.dijkstra.MainMenuScreen;

import java.util.Iterator;

public class GameWon implements Screen {
    final DijkstraAlgorithm game;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    public GameWon(final DijkstraAlgorithm game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        Music music = game.assetManager.get("pirates.mp3", Music.class);
        music.pause();

        Sound sound = game.assetManager.get("ambiente.wav", Sound.class);
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
                "You have collected " + game.mangos + " mangooos during our adventure. Thanks you owe me nothing anymore! \n\n Good luck with the pirates.";

        batch.begin();
        batch.draw(game.assetManager.get("gameWon.png", Texture.class), gameWon.x, gameWon.y, gameWon.width,  gameWon.height);
        game.fontSkin.getFont("font").draw(batch, wonLabel, camera.viewportWidth/2 - camera.viewportWidth/6,
                (float) (camera.viewportHeight - camera.viewportHeight*0.28), camera.viewportWidth/3, 1,true);
        batch.end();
    }

    @Override
    public void show() {

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
    }
}