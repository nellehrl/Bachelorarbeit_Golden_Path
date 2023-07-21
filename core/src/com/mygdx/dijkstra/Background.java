package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Background extends Group {
    final DijkstraAlgorithm game;
    Image mapImage, boatImage, cockpit, water, box;
    OrthographicCamera camera;
    int offset = 25, space = 10;
    float yWater;
    final FitViewport fitViewport;
    DropBox dropBox;
    Button mainMenuButton, doneButton;

    public Background(final DijkstraAlgorithm game, int level) {
        this.game = game;
        int row_height = offset;
        int col_width = 2 * offset;

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fitViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        yWater = camera.viewportWidth / 6 + offset;

        water = createActor(Gdx.graphics.getWidth(), (float) (Gdx.graphics.getHeight() * 0.65), 0, camera.viewportHeight/3, new Texture(Gdx.files.internal("map.png")));
        addActor(water);
        water.setName("water");

        mapImage = createActor((int) (Gdx.graphics.getWidth() * 0.9), (float) (Gdx.graphics.getHeight() * 0.6), (float) (2.5 * game.offset), (float) Gdx.graphics.getHeight()/3 + 25, new Texture(Gdx.files.internal("worldMap 1.png")));
        addActor(mapImage);
        mapImage.setName("mapImage");

        cockpit = createActor((int) (Gdx.graphics.getWidth()*1.08), (float) (Gdx.graphics.getHeight()*1.16), -52, -45, new Texture(Gdx.files.internal("cockpit.png")));
        addActor(cockpit);
        cockpit.setName("cockpit");

        /*box = createActor(Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight()/3, 1, 3, new Texture(Gdx.files.internal("white.png")));
        addActor(box);
        box.setName("box");*/

        //boat
        float boatWidth = (float) (Gdx.graphics.getWidth() * 0.075);
        boatImage = createActor((int) boatWidth, boatWidth, this.game.cities.get(0).getX() - boatWidth/2,
                this.game.cities.get(0).getY() - boatWidth/7, new Texture(Gdx.files.internal("ship.png")));
        addActor(boatImage);
        boatImage.setName("boatImage");

        mainMenuButton = new TextButton("Menu", game.mySkin, "default");
        mainMenuButton.setSize(2 * col_width, (float) (1.5 * row_height));
        mainMenuButton.setPosition(3*space, (float) (Gdx.graphics.getHeight() - (1.5 * row_height) - 3*space));
        addActor(mainMenuButton);
        mainMenuButton.setName("mainMenuButton");

        doneButton = new TextButton("Done", game.mySkin, "default");
        doneButton.setSize((float) (2 * col_width), (float) (1.5 * row_height));
        doneButton.setPosition(3*space, (float) (Gdx.graphics.getHeight() - (1.5 * row_height) - mainMenuButton.getHeight() - 3*space));
        addActor(doneButton);
        doneButton.setName("doneButton");
    }

    public Image createActor(int width, float height, float x, float y, Texture texture) {
        Image image = new Image(texture);
        image.setSize(width, height);
        image.setPosition(x, y);
        return image;
    }
}
