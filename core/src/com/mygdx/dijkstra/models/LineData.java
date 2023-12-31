package com.mygdx.dijkstra.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class LineData {
    private Vector2 start;
    private Vector2 end;
    private Color color;

    public LineData(Vector2 start, Vector2 end, Color color) {
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public Vector2 getStart() {
        return start;
    }

    public Vector2 getEnd() {
        return end;
    }

    public Color getColor() {
        return color;
    }

    public void setStart(Vector2 start) {

        this.start = start;
    }

    public void setEnd(Vector2 end) {

        this.end = end;
    }

    public void setColor(Color color) {

        this.color = color;
    }
}



