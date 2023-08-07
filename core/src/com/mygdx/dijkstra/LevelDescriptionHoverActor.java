package com.mygdx.dijkstra;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.utils.Align.left;

public class LevelDescriptionHoverActor extends Actor {
    private Table cardTable;
    public LevelDescriptionHoverActor(final DijkstraAlgorithm game, float x, float y, float width, int level) {
        String text = "After this level you can:\n";
        switch(level){
            case 1:
                text += "Define and memorize what an undirected graph is";
                break;
            case 2:
                text += "Define and memorize what a directed graph is";
                break;
            case 3:
                text += "Define and memorize what a weighted graph is";
                break;
            case 4:
                text += "Classify graphs\nIdentify edges and weights";
                break;
            case 5:
                text += "Compare edges and weights\nSelect the edges with lowest costs";
                break;
            case 6:
                text += "Define the precursor of a node and the costs of their edge";
                break;
            case 7:
                text += "Select precursors and edges with the lowest costs";
                break;
            case 8:
                text += "Apply and interpret the Dijkstra Algorithm";
                break;
        }

        GlyphLayout layout = new GlyphLayout();
        layout.setText(game.fontSkin.getFont("font"), text, Color.BLACK, width, left, true);

        cardTable = new Table(game.fontSkin);
        cardTable.setSize(layout.width, layout.height);
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
