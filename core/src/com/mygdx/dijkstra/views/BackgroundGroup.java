package com.mygdx.dijkstra.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
    private final OrthographicCamera camera;
    private int offset, space;
    private AssetManager assetManager;

    public BackgroundGroup(final DijkstraAlgorithm game, final Stage stage, String text, final int level) {
        this.game = game;
        camera = game.getCamera();

        initializeGlobalGameVariables();
        initializeBackgroundUIElements();
        initializeMangoCounter();
        initializeMainMenuButton(level);
        initializeInfoBox(stage, text);
    }

    private void initializeGlobalGameVariables() {
        space = game.getSpace();
        offset = game.getOffset();
        parrotImage = game.getParrotImage();
        int parrotWidth = (int) (camera.viewportWidth * 0.1);
        parrotImage.setSize((float) parrotWidth, (float) (parrotWidth * 1.25));
        parrotImage.setPosition((float) (Gdx.graphics.getWidth() - parrotWidth - offset), (camera.viewportHeight / 3 - space));
        parrotImage.setName("parrotImage");
        addActor(parrotImage);
        assetManager = game.getAssetManager();
        infoImage = game.getInfoImage();
    }

    private void initializeBackgroundUIElements() {
        Image wood = game.createActor((int) camera.viewportWidth, camera.viewportHeight, 0, 0, assetManager.get("background.png", Texture.class));
        addActor(wood);
        wood.setName("wood");

        Image water = game.createActor((int) camera.viewportWidth, (float) (camera.viewportHeight * 0.65), 0, camera.viewportHeight / 3, assetManager.get("map.png", Texture.class));
        addActor(water);
        water.setName("water");

        Image mapImage = game.createActor((int) (camera.viewportWidth * 0.9), (float) (camera.viewportHeight * 0.6), (float) (2.5 * offset), camera.viewportHeight / 3 + game.getOffset(), assetManager.get("worldMap 1.png", Texture.class));
        addActor(mapImage);
        mapImage.setName("mapImage");

        Image box = game.createActor((int) (camera.viewportWidth - 2), camera.viewportHeight / 3 - 2, 1, 1, assetManager.get("box.png", Texture.class));
        addActor(box);
        box.setName("box");

        float boatWidth = camera.viewportWidth * 0.075f;
        Image boatImage = game.createActor((int) boatWidth, boatWidth, game.getCities().get(0).getX() - boatWidth / 2,
                game.getCities().get(0).getY() - boatWidth / 7, assetManager.get("ship.png", Texture.class));
        addActor(boatImage);
        boatImage.setName("boatImage");
    }

    private void initializeMangoCounter() {
        Image mangoCounterImage = game.createActor((int) (camera.viewportWidth * 0.1), (float) (camera.viewportHeight * 0.075),
                (float) (camera.viewportWidth - (camera.viewportWidth * 0.1)) - offset,
                (float) (camera.viewportHeight - (camera.viewportHeight * 0.075) - offset),
                assetManager.get("mangoCounter.png", Texture.class));
        Table mangoCounter = new Table(game.getFontSkin());
        mangoCounter.setSize((int) (camera.viewportWidth * 0.1), (float) (camera.viewportHeight * 0.075));
        mangoCounter.setPosition((float) (camera.viewportWidth - (camera.viewportWidth * 0.1)) - offset,
                (float) (camera.viewportHeight - (camera.viewportHeight * 0.075) - offset));
        mangoCounter.add(mangoCounterImage);
        Label mangoCounterLabel = new Label(String.valueOf(game.getMangos()), game.getFontSkin());
        mangoCounter.add(mangoCounterLabel).padLeft((float) (-0.6 * mangoCounter.getWidth()));
        addActor(mangoCounter);
        mangoCounter.setName("mangoCounter");
    }

    private void initializeMainMenuButton(final int level) {
        Button mainMenuButton = new TextButton("Menu", game.getMySkin(), "default");
        mainMenuButton.setSize(4 * offset, (float) (1.5 * offset));
        mainMenuButton.setPosition(game.getOffset(), camera.viewportHeight - mainMenuButton.getHeight() - game.getOffset());
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
        final InfoTextGroup infoText = new InfoTextGroup(game, text, camera);
        Button closeButton = infoText.getCloseButton();
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                infoText.remove();
                infoImage.setSize((float) (camera.viewportWidth * 0.025), (float) (camera.viewportWidth * 0.025));
                infoImage.setPosition(parrotImage.getX() - space, parrotImage.getY() + parrotImage.getHeight());
                stage.addActor(infoImage);
                stage.addActor(parrotImage);
            }
        });
        addActor(infoText);
        infoText.setName("infotext");
    }
}
