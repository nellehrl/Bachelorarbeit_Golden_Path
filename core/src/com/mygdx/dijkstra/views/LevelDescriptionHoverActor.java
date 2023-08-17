package com.mygdx.dijkstra.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.dijkstra.DijkstraAlgorithm;

import static com.badlogic.gdx.utils.Align.left;

public class LevelDescriptionHoverActor extends Actor {
    private Table cardTable;
    private String text = "After this level you can:\n";
    private final int level;
    private final DijkstraAlgorithm game;

    public LevelDescriptionHoverActor(final DijkstraAlgorithm game, float x, float y, float width, int level) {
        this.level = level;
        this.game = game;
        initializeTextBasedOnLevel();

        GlyphLayout layout = new GlyphLayout();
        layout.setText(game.getFontSkin().getFont("font"), text, Color.BLACK, width, left, true);
        createTable(layout, x, y);
        Drawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(game.getAssetManager().get("white.png", Texture.class)));
        cardTable.setBackground(backgroundDrawable);
    }

    private void initializeTextBasedOnLevel() {
        switch (level) {
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
    }

    private void createTable(GlyphLayout layout, float x, float y) {
        cardTable = new Table(game.getFontSkin());
        cardTable.setSize(layout.width, layout.height);
        cardTable.setPosition(x, y);

        Label cardLabel = new Label(text, game.getFontSkin());
        cardLabel.setWrap(true);
        cardLabel.setAlignment(left);
        cardLabel.setFontScale(0.66f);
        cardTable.add(cardLabel).expand().fill().pad(10f);
    }

    public Table getTable() {
        return cardTable;
    }
}
