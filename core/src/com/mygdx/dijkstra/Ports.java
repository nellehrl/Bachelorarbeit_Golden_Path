package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class Ports extends Table {
    Image portImage;
    public Ports(City value, Skin skin){
        portImage = new Image(new Texture(Gdx.files.internal("port.png")));
        portImage.setSize(50f, 50f);

        Label label = new Label(value.getName(), skin);
        label.setFontScale(0.66f);

        this.add(portImage).row();
        this.add(label);

        this.setSize(50f, 50f);
        this.setPosition(value.getX() - this.getWidth() / 2, value.getY() - this.getHeight() / 2);
    }
}
