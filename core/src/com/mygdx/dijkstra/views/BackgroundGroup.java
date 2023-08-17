package com.mygdx.dijkstra.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.dijkstra.DijkstraAlgorithm;
import com.mygdx.dijkstra.systems.MainMenuScreen;

public class BackgroundGroup extends Group {
    private final DijkstraAlgorithm game;
    private Image parrotImage;
    private Image infoImage;
    private AssetManager assetManager;

    public BackgroundGroup(final DijkstraAlgorithm game, final Stage stage, String text, final int level) {
        this.game = game;

        initializeGlobalGameVariables();
        initializeBackgroundUIElements();
        initializeMangoCounter();
        initializeMainMenuButton(level);
        initializeInfoBox(stage, text);
    }

    private void initializeGlobalGameVariables() {
        parrotImage = game.getParrotImage();
        int parrotWidth = (int) (game.getCamera().viewportWidth * 0.1);
        parrotImage.setSize((float) parrotWidth, (float) (parrotWidth * 1.25));
        parrotImage.setPosition((float) (Gdx.graphics.getWidth() - parrotWidth - game.getOffset()), (game.getCamera().viewportHeight / 3 - game.getSpace()));
        parrotImage.setName("parrotImage");
        addActor(parrotImage);
        assetManager = game.getAssetManager();
        infoImage = game.getInfoImage();
        infoImage.setSize((float) (game.getCamera().viewportWidth * 0.025), (float) (game.getCamera().viewportWidth * 0.025));
        infoImage.setPosition(parrotImage.getX() - game.getSpace(), parrotImage.getY() + parrotImage.getHeight());
        infoImage.setName("infoImage");
        addActor(infoImage);
    }

    private void initializeBackgroundUIElements() {
        Image wood = game.createActor((int) game.getCamera().viewportWidth, game.getCamera().viewportHeight, 0, 0, assetManager.get("background.png", Texture.class));
        addActor(wood);
        wood.setName("wood");

        Image water = game.createActor((int) game.getCamera().viewportWidth, (float) (game.getCamera().viewportHeight * 0.65), 0, game.getCamera().viewportHeight / 3, assetManager.get("map.png", Texture.class));
        addActor(water);
        water.setName("water");

        Image mapImage = game.createActor((int) (game.getCamera().viewportWidth * 0.9), (float) (game.getCamera().viewportHeight * 0.6), (float) (2.5 * game.getOffset()), game.getCamera().viewportHeight / 3 + game.getOffset(), assetManager.get("worldMap.png", Texture.class));
        addActor(mapImage);
        mapImage.setName("mapImage");

        Image box = game.createActor((int) (game.getCamera().viewportWidth - 2), game.getCamera().viewportHeight / 3 - 2, 1, 1, assetManager.get("box.png", Texture.class));
        addActor(box);
        box.setName("box");

        float boatWidth = game.getCamera().viewportWidth * 0.075f;
        Image boatImage = game.createActor((int) boatWidth, boatWidth, game.getCities().get(0).getX() - boatWidth / 2,
                game.getCities().get(0).getY() - boatWidth / 7, assetManager.get("ship.png", Texture.class));
        addActor(boatImage);
        boatImage.setName("boatImage");
    }

    private void initializeMangoCounter() {
        Image mangoCounterImage = game.createActor((int) (game.getCamera().viewportWidth * 0.1), (float) (game.getCamera().viewportHeight * 0.075),
                (float) (game.getCamera().viewportWidth - (game.getCamera().viewportWidth * 0.1)) - game.getOffset(),
                (float) (game.getCamera().viewportHeight - (game.getCamera().viewportHeight * 0.075) - game.getOffset()),
                assetManager.get("mangoCounter.png", Texture.class));
        Table mangoCounter = new Table(game.getFontSkin());
        mangoCounter.setSize((int) (game.getCamera().viewportWidth * 0.1), (float) (game.getCamera().viewportHeight * 0.075));
        mangoCounter.setPosition((float) (game.getCamera().viewportWidth - (game.getCamera().viewportWidth * 0.1)) - game.getOffset(),
                (float) (game.getCamera().viewportHeight - (game.getCamera().viewportHeight * 0.075) - game.getOffset()));
        mangoCounter.add(mangoCounterImage);
        Label mangoCounterLabel = new Label(String.valueOf(game.getMangos()), game.getFontSkin());
        mangoCounter.add(mangoCounterLabel).padLeft((float) (-0.6 * mangoCounter.getWidth()));
        addActor(mangoCounter);
        mangoCounter.setName("mangoCounter");
    }

    private void initializeMainMenuButton(final int level) {
        Button mainMenuButton = new TextButton("Menu", game.getMySkin(), "default");
        mainMenuButton.setSize(4 * game.getOffset(), (float) (1.5 * game.getOffset()));
        mainMenuButton.setPosition(game.getOffset(), game.getCamera().viewportHeight - mainMenuButton.getHeight() - game.getOffset());
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.resetGlobalState();
                game.setScreen(new MainMenuScreen(game, level));
            }
        });
        addActor(mainMenuButton);
        mainMenuButton.setName("mainMenuButton");
    }

    private void initializeInfoBox(final Stage stage, String text) {
        final InfoTextGroup infoText = new InfoTextGroup(game, text);
        Button closeButton = infoText.getCloseButton();
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                infoText.remove();
                infoImage.setSize((float) (game.getCamera().viewportWidth * 0.025), (float) (game.getCamera().viewportWidth * 0.025));
                infoImage.setPosition(parrotImage.getX() - game.getSpace(), parrotImage.getY() + parrotImage.getHeight());
                infoImage.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        stage.addActor(infoText);
                    }
                });
                parrotImage.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        stage.addActor(infoText);
                    }
                });
                stage.addActor(infoImage);
                stage.addActor(parrotImage);
            }
        });
        addActor(infoText);
        infoText.setName("infotext");
    }
}
