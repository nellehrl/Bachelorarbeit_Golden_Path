package com.mygdx.dijkstra.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.dijkstra.DijkstraAlgorithm;

public class GameWonScreen implements Screen {
    private final DijkstraAlgorithm game;
    private final Stage stage;

    public GameWonScreen(final DijkstraAlgorithm game) {
        this.game = game;
        stage = new Stage(game.getFitViewport());

        Music music = game.getAssetManager().get("pirates.mp3", Music.class);
        music.pause();
        Sound sound = game.getAssetManager().get("ambiente.wav", Sound.class);
        sound.play();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.95f, 0.871f, 0.726f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getCamera().update();

        game.getBatch().setProjectionMatrix(game.getCamera().combined);

        Rectangle gameWon = new Rectangle();
        gameWon.setSize(game.getCamera().viewportWidth, game.getCamera().viewportHeight);
        gameWon.setPosition(0, -10);

        String wonLabel = "Congrats Captain!!!!\n\nYou have mastered the art of the golden paths. Now we will always have enough mangooos.\n\n" +
                "You have collected " + game.getMangos() + " mangooos during our adventure. Thanks you owe me nothing anymore! \n\n Good luck with the pirates.";

        game.getBatch().begin();
        game.getBatch().draw(game.getAssetManager().get("gameWon.png", Texture.class), gameWon.x, gameWon.y, gameWon.width, gameWon.height);
        game.getFontSkin().getFont("font").draw(game.getBatch(), wonLabel, game.getCamera().viewportWidth / 2 - game.getCamera().viewportWidth / 6,
                (float) (game.getCamera().viewportHeight - game.getCamera().viewportHeight * 0.28), game.getCamera().viewportWidth / 3, 1, true);
        game.getBatch().end();
    }

    @Override
    public void show() {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        game.getFitViewport().update(width, height, true);
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
    }
}