package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class BackgroundGroup extends Group {
    final DijkstraAlgorithm game;
    Image mapImage, boatImage, cockpit, water, mangoCounterImage;
    OrthographicCamera camera;
    int offset = 25, space = 10;
    float yWater;
    final FitViewport fitViewport;
    Button mainMenuButton;
    Table mangoCounter;

    public BackgroundGroup(final DijkstraAlgorithm game, final Stage stage, String text) {
        this.game = game;
        int row_height = offset;
        int col_width = 2 * offset;

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, camera.viewportWidth, camera.viewportHeight);
        fitViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        yWater = camera.viewportWidth / 6 + offset;

        cockpit = createActor((int) (camera.viewportWidth * 1.1), (float) (camera.viewportHeight * 1.1), -52, -45, game.assetManager.get("background.png", Texture.class));
        addActor(cockpit);
        cockpit.setName("cockpit");

        water = createActor((int) camera.viewportWidth, (float) (camera.viewportHeight * 0.65), 0, camera.viewportHeight / 3, game.assetManager.get("map.png", Texture.class));
        addActor(water);
        water.setName("water");

        mapImage = createActor((int) (camera.viewportWidth * 0.9), (float) (camera.viewportHeight * 0.6), (float) (2.5 * game.offset), (float) camera.viewportHeight / 3 + 25, game.assetManager.get("worldMap 1.png", Texture.class));
        addActor(mapImage);
        mapImage.setName("mapImage");

        mangoCounterImage = createActor((int) (camera.viewportWidth * 0.1), (float) (camera.viewportHeight * 0.075),
                (float) (camera.viewportWidth - (camera.viewportWidth * 0.1)) - game.offset,
                (float) (camera.viewportHeight - (camera.viewportHeight * 0.075) - game.offset),
                game.assetManager.get("mangoCounter.png", Texture.class));
        mangoCounter = new Table(game.fontSkin);
        mangoCounter.setSize((int) (camera.viewportWidth * 0.1), (float) (camera.viewportHeight * 0.075));
        mangoCounter.setPosition((float) (camera.viewportWidth - (camera.viewportWidth * 0.1)) - game.offset,
                (float) (camera.viewportHeight - (camera.viewportHeight * 0.075) - game.offset));
        mangoCounter.add(mangoCounterImage);
        Label mangoCounterLabel = new Label(String.valueOf(game.mangos), game.fontSkin);
        mangoCounter.add(mangoCounterLabel).padLeft((float) (-0.6*mangoCounter.getWidth()));
        addActor(mangoCounter);
        mangoCounter.setName("mangoCounter");

        //boat
        float boatWidth = (float) (camera.viewportWidth * 0.075);
        boatImage = createActor((int) boatWidth, boatWidth, this.game.cities.get(0).getX() - boatWidth / 2,
                this.game.cities.get(0).getY() - boatWidth / 7, game.assetManager.get("ship.png", Texture.class));
        addActor(boatImage);
        boatImage.setName("boatImage");

        mainMenuButton = new TextButton("Menu", game.mySkin, "default");
        mainMenuButton.setSize(2 * col_width, (float) (1.5 * row_height));
        mainMenuButton.setPosition(3 * space, (float) (camera.viewportHeight - (1.5 * row_height) - 3 * space));
        addActor(mainMenuButton);
        mainMenuButton.setName("mainMenuButton");

        //init box
        Image box = new Image(game.assetManager.get("box.png", Texture.class));
        box.setSize(camera.viewportWidth - 2, camera.viewportHeight / 3 - 2);
        box.setPosition(1, 1);
        addActor(box);
        box.setName("box");

        final InfoTextGroup infotext = new InfoTextGroup(game, text);
        Button closeButton = infotext.closeButton;
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                infotext.remove();
                int parrotWidth = (int) (camera.viewportWidth * 0.1);
                game.parrotImage.setSize((float) parrotWidth, (float) (parrotWidth * 1.25));
                game.parrotImage.setPosition((float) (Gdx.graphics.getWidth() - parrotWidth - game.offset), (camera.viewportHeight / 3 - game.space));
                game.infoImage.setSize((float) (camera.viewportWidth*0.025), (float) (camera.viewportWidth*0.025));
                game.infoImage.setPosition(game.parrotImage.getX() - game.space, game.parrotImage.getY() + game.parrotImage.getHeight());
                game.parrotImage.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        stage.addActor(infotext);
                        game.parrotImage.remove();
                        game.infoImage.remove();
                    }
                });
                stage.addActor(game.infoImage);
                stage.addActor(game.parrotImage);
            }
        });
        addActor(infotext);
        infotext.setName("infotext");
    }

    public Image createActor(int width, float height, float x, float y, Texture texture) {
        Image image = new Image(texture);
        image.setSize(width, height);
        image.setPosition(x, y);
        return image;
    }
}
