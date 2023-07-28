package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ConnectionAreaImage extends Image {

    public ConnectionAreaImage(City sourceCity, City destCity ){
        super(new Texture(Gdx.files.internal("transparent.png")));

        Vector2 point1 = new Vector2(sourceCity.x, sourceCity.y);
        Vector2 point2 = new Vector2(destCity.x, destCity.y);

        float width = point1.dst(point2);
        float height = 20f;
        double angle = Math.toDegrees(Math.atan2(destCity.y - sourceCity.y, (destCity.x) - sourceCity.x));

        double rotatedAngle = angle - 90; // Rotate the angle 90 degrees

        double rotatedAngleRadians = Math.toRadians(rotatedAngle);
        double rotatedX = Math.cos(rotatedAngleRadians);
        double rotatedY = Math.sin(rotatedAngleRadians);

        double newX = sourceCity.x + rotatedX * height/2;
        double newY = sourceCity.y + rotatedY * height/2;

        this.setSize(width, height);
        this.rotateBy((float) angle);
        this.setPosition((float) newX, (float) newY);
    }
}
