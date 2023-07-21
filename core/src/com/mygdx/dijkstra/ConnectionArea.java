package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ConnectionArea extends Image {

    public ConnectionArea(City sourceCity, City destCity ){
        super(new Texture(Gdx.files.internal("transparent.png")));

        Vector2 point1 = new Vector2(sourceCity.x, sourceCity.y);
        Vector2 point2 = new Vector2(destCity.x, destCity.y);

        float width = point1.dst(point2);

        this.setPosition(sourceCity.x, sourceCity.y);
        this.setSize(width, 8);
        this.rotateBy((float) Math.toDegrees(Math.atan2(destCity.y - sourceCity.y, (destCity.x) - sourceCity.x)));
    }
}
