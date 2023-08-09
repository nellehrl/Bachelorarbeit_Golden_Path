package com.mygdx.dijkstra.views;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.dijkstra.DijkstraAlgorithm;
import com.mygdx.dijkstra.models.City;

public class Ports extends Table {
    Image portImage;

    public Ports(City value, final DijkstraAlgorithm game) {
        if (game.getAssetManager().isLoaded("port.png", Texture.class))
            portImage = new Image(game.getAssetManager().get("port.png", Texture.class));
        portImage.setSize(50f, 50f);

        Label label = new Label(value.getName(), game.getFontSkin());
        label.setFontScale(0.66f);

        this.add(portImage).row();
        this.add(label);

        this.setSize(50f, 50f);
        this.setPosition(value.getX() - this.getWidth() / 2, value.getY() - this.getHeight() / 2);
    }
}
