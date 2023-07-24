package com.mygdx.dijkstra;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class Ports extends Table {
    Image portImage;
    public Ports(City value, final DijkstraAlgorithm game){
        portImage = new Image(game.assetManager.get("port.png", Texture.class));
        portImage.setSize(50f, 50f);

        Label label = new Label(value.getName(), game.fontSkin);
        label.setFontScale(0.66f);

        this.add(portImage).row();
        this.add(label);

        this.setSize(50f, 50f);
        this.setPosition(value.getX() - this.getWidth() / 2, value.getY() - this.getHeight() / 2);
    }
}
