package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class DrawLineOrArrow {

    public void drawLine(ShapeRenderer shapeRenderer, int lineWidth, Color color, Vector2 start, Vector2 end) {
        drawLine(shapeRenderer,color, lineWidth, start, end);
    }

    public void drawArrow(ShapeRenderer shapeRenderer,int lineWidth, Color color, Vector2 start, Vector2 end) {
        drawArrow(shapeRenderer,color, lineWidth, start, end);
    }

    private static void drawLine(ShapeRenderer shapeRenderer, Color color, int lineWidth, Vector2 start, Vector2 end) {
        shapeRenderer.setColor(color);
        shapeRenderer.rectLine(start.x, start.y, end.x, end.y, lineWidth);
    }

    private static void drawArrow(ShapeRenderer shapeRenderer, Color color, int lineWidth, Vector2 start, Vector2 end) {
        shapeRenderer.setColor(color); // Set the color of the arrow

        float startX = start.x;
        float startY = start.y;
        float endX = end.x;
        float endY = end.y;
        float arrowSize = 8;

        float directionX = endX - startX;
        float directionY = endY - startY;

        float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);

        directionX /= length;
        directionY /= length;

        // Shorten the arrow
        float shortenBy = 15;
        endX = startX + directionX * (length - shortenBy);
        endY = startY + directionY * (length - shortenBy);

        // Draw the main line of the shortened arrow
        shapeRenderer.rectLine(startX, startY, endX, endY, arrowSize/4);

        // Draw & Calculate the arrow wings
        float wingX = -directionY;
        float wingY = directionX;

        shapeRenderer.triangle(endX, endY, endX - arrowSize * (directionX + wingX), endY - arrowSize * (directionY + wingY),
                endX - arrowSize * (directionX - wingX), endY - arrowSize * (directionY - wingY));
    }
}
