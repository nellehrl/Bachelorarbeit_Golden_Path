package com.mygdx.dijkstra.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygdx.dijkstra.DijkstraAlgorithm;

import static com.badlogic.gdx.utils.Align.left;

public class InfoTextGroup extends Group {
    private Button closeButton;
    private final DijkstraAlgorithm game;
    private Table table;
    private final int row_height = 25;

    public InfoTextGroup(final DijkstraAlgorithm game, String text, OrthographicCamera camera) {

        this.game = game;

        GlyphLayout layout = new GlyphLayout();
        layout.setText(game.getFontSkin().getFont("font"), text, Color.BLACK, (float) (camera.viewportWidth * 0.62), left, true);

        initializeTable(layout, camera, text);
        initializeImagesAndButton(table, camera);
    }

    private void initializeImagesAndButton(Table table, OrthographicCamera camera) {
        Image shadowImage = game.createActor((int) (table.getWidth() * 1.25), (float) (table.getHeight() * 1.2), table.getX() - 90, table.getY() - 30, game.getAssetManager().get("shadow.png", Texture.class));
        shadowImage.setName("shadowImage");
        addActor(shadowImage);
        shadowImage.toBack();

        int parrottWidth = (int) (camera.viewportWidth * 0.1);
        Image parrottImage = game.createActor(parrottWidth, (float) (parrottWidth * 1.25), table.getX() + table.getWidth() - parrottWidth,
                table.getY() + table.getHeight() - 18, game.getAssetManager().get("parrott.png", Texture.class));
        parrottImage.setName("parrottImage");
        addActor(parrottImage);

        //button
        closeButton = new TextButton("Let's do it", game.getMySkin(), "default");
        int col_width = 50;
        closeButton.setSize((float) (3 * col_width) + 20, (float) (1.5 * row_height));
        closeButton.setPosition(table.getX() + table.getWidth() - closeButton.getWidth() - 25, table.getY() + 15);
        addActor(closeButton);
        closeButton.setName("closeButton");
    }

    private void initializeTable(GlyphLayout layout, OrthographicCamera camera, String text) {
        table = new Table();
        table.setBackground(game.getFontSkin().getDrawable("color"));
        table.setSize(layout.width, layout.height + row_height * 1.5f + 100);
        table.setPosition(camera.viewportWidth / 2 - table.getWidth() / 2, camera.viewportHeight / 2 - table.getHeight() / 2);
        addActor(table);

        Label codeLabel = new Label(text, game.getFontSkin());
        codeLabel.setAlignment(left);
        codeLabel.setWrap(true);
        codeLabel.setFontScale(1f);
        table.add(codeLabel).expand().fill().padLeft(10f).padRight(10f).top().padTop(-50);
    }

    public Button getCloseButton() {
        return this.closeButton;
    }
}