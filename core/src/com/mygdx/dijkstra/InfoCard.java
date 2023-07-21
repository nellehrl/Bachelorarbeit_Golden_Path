package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.utils.Align.center;
import static com.badlogic.gdx.utils.Align.left;

public class InfoCard extends Actor {
    private Table cardTable;
    private boolean visible;
    public InfoCard(Skin skin, float x, float y, float width, float height, String source, String destination, int weight, boolean visible) {
        String text = "Connection\n From: " + source + "\n To: " + destination + "\n Costs: " + weight;

        cardTable = new Table(skin);
        cardTable.setSize(width, height);
        cardTable.setPosition(x, y);

        Label cardLabel = new Label(text, skin);
        cardLabel.setWrap(true);
        cardLabel.setAlignment(left);
        cardLabel.setFontScale(0.66f);
        cardTable.add(cardLabel).expand().fill().pad(10f);

        // Set the background color of the cardTable
        Drawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("box.png"))));
        cardTable.setBackground(backgroundDrawable);

        this.visible = visible;
    }

    public Table getTable() {
        return cardTable;
    }
}
