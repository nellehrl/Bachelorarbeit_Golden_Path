package com.mygdx.dijkstra.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.dijkstra.models.City;

public class ConnectionAreaImage extends Image {
    double newX, newY;

    public ConnectionAreaImage(City sourceCity, City destCity) {
        super(new Texture(Gdx.files.internal("transparent.png")));

        Vector2 point1 = new Vector2(sourceCity.getX(), sourceCity.getY());
        Vector2 point2 = new Vector2(destCity.getX(), destCity.getY());

        float width = point1.dst(point2);
        float height = 20f;
        double angle = Math.toDegrees(Math.atan2(destCity.getY() - sourceCity.getY(), (destCity.getX()) - sourceCity.getX()));

        calculateNewCoordinates(angle, sourceCity, height);

        this.setSize(width, height);
        this.rotateBy((float) angle);
        this.setPosition((float) newX, (float) newY);
    }

    public void calculateNewCoordinates(double angle, City sourceCity, float height) {
        double rotatedAngle = angle - 90; // Rotate the angle 90 degrees

        double rotatedAngleRadians = Math.toRadians(rotatedAngle);
        double rotatedX = Math.cos(rotatedAngleRadians);
        double rotatedY = Math.sin(rotatedAngleRadians);

        newX = sourceCity.getX() + rotatedX * height / 2;
        newY = sourceCity.getY() + rotatedY * height / 2;
    }
}
