package com.mygdx.dijkstra.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.dijkstra.models.City;

public class ConnectionAreaImage extends Image {
    double newX, newY;

    public ConnectionAreaImage(Vector2 sourceCity, Vector2 destCity) {
        super(new Texture(Gdx.files.internal("transparent.png")));

        Vector2 point1 = new Vector2(sourceCity.x, sourceCity.y);
        Vector2 point2 = new Vector2(destCity.x, destCity.y);

        float width = point1.dst(point2);
        float height = 40f;
        double angle = Math.toDegrees(Math.atan2(destCity.y - sourceCity.y, (destCity.x) - sourceCity.x));

        calculateNewCoordinates(angle, sourceCity, height);

        this.setSize(width, height);
        this.rotateBy((float) angle);
        this.setPosition((float) newX, (float) newY);
    }

    public void calculateNewCoordinates(double angle, Vector2 sourceCity, float height) {
        double rotatedAngle = angle - 90; // Rotate the angle 90 degrees

        double rotatedAngleRadians = Math.toRadians(rotatedAngle);
        double rotatedX = Math.cos(rotatedAngleRadians);
        double rotatedY = Math.sin(rotatedAngleRadians);

        newX = sourceCity.x + rotatedX * height / 2;
        newY = sourceCity.y + rotatedY * height / 2;
    }
}
