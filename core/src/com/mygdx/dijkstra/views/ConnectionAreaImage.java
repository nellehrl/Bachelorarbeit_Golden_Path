package com.mygdx.dijkstra.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ConnectionAreaImage extends Image {
    private double newX, newY;

    public ConnectionAreaImage(Vector2 sourceCity, Vector2 destCity, Stage stage, Table cardTableFinal, boolean addListeners) {
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
        if(addListeners) addlisteners(stage, cardTableFinal);
    }

    private void calculateNewCoordinates(double angle, Vector2 sourceCity, float height) {
        double rotatedAngle = angle - 90; // Rotate the angle 90 degrees

        double rotatedAngleRadians = Math.toRadians(rotatedAngle);
        double rotatedX = Math.cos(rotatedAngleRadians);
        double rotatedY = Math.sin(rotatedAngleRadians);

        newX = sourceCity.x + rotatedX * height / 2;
        newY = sourceCity.y + rotatedY * height / 2;
    }

    private void addlisteners(final Stage stage, final Table cardTableFinal){
        this.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                stage.addActor(cardTableFinal);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                cardTableFinal.remove();
            }
        });
    }
}
