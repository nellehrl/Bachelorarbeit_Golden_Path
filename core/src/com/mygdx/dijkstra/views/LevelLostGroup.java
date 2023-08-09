package com.mygdx.dijkstra.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygdx.dijkstra.DijkstraAlgorithm;

import static com.badlogic.gdx.utils.Align.left;

public class LevelLostGroup extends Group {
    private static final float TABLE_TEXT_PADDING_SIDE = 10f;
    private static final float TABLE_TEXT_PADDING_TOP = -50f;
    private String text;
    private final DijkstraAlgorithm game;

    public LevelLostGroup(final DijkstraAlgorithm game, OrthographicCamera camera) {
        this.game = game;

        Table table = createTable(camera);
        addActor(table);

        int parrotWidth = (int) (camera.viewportWidth * 0.1);
        Image parrotImage = game.createActor(parrotWidth, parrotWidth * 1.25f, table.getX() + table.getWidth() - parrotWidth,
                table.getY() + table.getHeight() - 18, game.getAssetManager().get("parrott.png", Texture.class));
        addActor(parrotImage);

        Button closeButton = new TextButton("Let's do it", game.getMySkin(), "default");
        closeButton.setSize(170, 37.5f); // Adjusted size calculation
        closeButton.setPosition(table.getX() + table.getWidth() - closeButton.getWidth() - game.getOffset(), table.getY() + 15);
        addActor(closeButton);
    }

    private Table createTable(OrthographicCamera camera) {
        GlyphLayout layout = createLayout(camera);
        Table table = new Table();
        table.setBackground(game.getFontSkin().getDrawable("color"));
        table.setSize(layout.width, layout.height + 225); // Adjusted height calculation
        table.setPosition(camera.viewportWidth / 2 - table.getWidth() / 2, camera.viewportHeight / 2 - table.getHeight() / 2);

        Image shipWreckImage = new Image(game.getAssetManager().get("shipWreck.png", Texture.class));
        table.add(shipWreckImage).size(table.getWidth() * 0.25f, table.getHeight() * 0.5f).padTop(5f).row();

        Label codeLabel = new Label(text, game.getFontSkin());
        codeLabel.setAlignment(left);
        codeLabel.setWrap(true);
        table.add(codeLabel).expand().fill().padLeft(TABLE_TEXT_PADDING_SIDE).padRight(TABLE_TEXT_PADDING_SIDE).top().padTop(TABLE_TEXT_PADDING_TOP);

        return table;
    }

    private GlyphLayout createLayout(OrthographicCamera camera) {
        text = "Awwwww....I am so hungry but we have no more mangos. We lost them all with the wreck!\n\nHelp!!!!Help!!!" +
                "\n\n Keep your head held high, Captain. The Crew is rebuilding the ship already. Soon we will be back on the sea." +
                "Remember that you can always ask me if you need to revisit the instructions.";

        GlyphLayout layout = new GlyphLayout();
        layout.setText(game.getFontSkin().getFont("font"), text, Color.BLACK, camera.viewportWidth * 0.62f, left, true);
        return layout;
    }
}





