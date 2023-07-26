package com.mygdx.dijkstra;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.utils.Align.left;

public class InfoCardActor extends Actor {
    private Table cardTable;
    public InfoCardActor(final DijkstraAlgorithm game, float x, float y, float width, float height, String source, String destination, int weight, boolean visible) {
        String text = "Connection\n From: " + source + "\n To: " + destination + "\n Costs: " + weight;

        cardTable = new Table(game.fontSkin);
        cardTable.setSize(width, height);
        cardTable.setPosition(x, y);

        Label cardLabel = new Label(text, game.fontSkin);
        cardLabel.setWrap(true);
        cardLabel.setAlignment(left);
        cardLabel.setFontScale(0.66f);
        cardTable.add(cardLabel).expand().fill().pad(10f);

        // Set the background color of the cardTable
        Drawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(game.assetManager.get("white 1.png", Texture.class)));
        cardTable.setBackground(backgroundDrawable);
    }

    public Table getTable() {
        return cardTable;
    }
}
