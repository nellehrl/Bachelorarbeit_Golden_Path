package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class DrawLineOrArrow extends ShapeRenderer {
    public DrawLineOrArrow(int lineWidth, Matrix4 projectionMatrix, Color color, Vector2 start, Vector2 end , int mode){
        this.setProjectionMatrix(projectionMatrix);
        Gdx.gl.glLineWidth(lineWidth);
        if(mode == 1) {
            this.begin(ShapeRenderer.ShapeType.Line);
            this.setColor(color);
            this.line(start, end);
        }

        else{
            this.begin(ShapeRenderer.ShapeType.Filled);
            this.setColor(color); // Set the color of the arrow

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
            this.rectLine(startX, startY, endX, endY, arrowSize/4);

            // Draw & Calculate the arrow wings
            float wingX = -directionY;
            float wingY = directionX;

            this.triangle(endX, endY, endX - arrowSize * (directionX + wingX), endY - arrowSize * (directionY + wingY),
                    endX - arrowSize * (directionX - wingX), endY - arrowSize * (directionY - wingY));
        }
        this.end();
    }
}
