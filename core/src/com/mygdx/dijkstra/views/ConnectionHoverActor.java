package com.mygdx.dijkstra.views;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.dijkstra.DijkstraAlgorithm;

import static com.badlogic.gdx.utils.Align.left;

public class ConnectionHoverActor extends Actor {
    private Table cardTable;

    public ConnectionHoverActor(final DijkstraAlgorithm game, float x, float y, float width, float height, String source, String destination, int weight) {
        String text = "Connection\n From: " + source + "\n To: " + destination + "\n Costs: " + weight;

        cardTable = new Table(game.getFontSkin());
        cardTable.setSize(width, height);
        cardTable.setPosition(x, y);

        Label cardLabel = new Label(text, game.getFontSkin());
        cardLabel.setWrap(true);
        cardLabel.setAlignment(left);
        cardLabel.setFontScale(0.66f);
        cardTable.add(cardLabel).expand().fill().pad(10f);

        // Set the background color of the cardTable
        Drawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(game.getAssetManager().get("white 1.png", Texture.class)));
        cardTable.setBackground(backgroundDrawable);
    }

    public Table getTable() {
        return cardTable;
    }
}
