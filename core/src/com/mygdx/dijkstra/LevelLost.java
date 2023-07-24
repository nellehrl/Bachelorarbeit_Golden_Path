package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;

import static com.badlogic.gdx.utils.Align.left;

public class LevelLost extends Group {
    Image parrottImage;
    Button closeButton;
    final DijkstraAlgorithm game;
    public LevelLost(final DijkstraAlgorithm game){

        this.game = game;

        int row_height = 25;
        int col_width = 50;

        String text = "Awwwww....I am so hungry but we have no more mangos. We lost them all with wreck!\n\nHelp!!!!Help!!!" +
                "\n\n Keep your head held high, Captain. The Crew is rebuilding the ship already. Soon we will back on the sea."+
                "Remember that you can always ask me if you need to revisit the instructions.";

        GlyphLayout layout = new GlyphLayout();
        layout.setText(game.fontSkin.getFont("font"), text, Color.BLACK, (float) (Gdx.graphics.getWidth()*0.62), left, true);


        Table table = new Table();
        table.setBackground(game.fontSkin.getDrawable("color"));
        table.setSize(layout.width , (float) ((layout.height + row_height*1.5 + 100) * 1.5));
        table.setPosition((float) Gdx.graphics.getWidth()/2 - table.getWidth()/2, (float) Gdx.graphics.getHeight()/2 - table.getHeight()/2);

        Image shipWreckImage = new Image(game.assetManager.get("shipWreck.png", Texture.class));
        table.add(shipWreckImage).size((float) (table.getWidth()*0.25), (float) (table.getHeight()*0.5)).padTop(5f).row();

        Label codeLabel = new Label(text, game.fontSkin);
        codeLabel.setAlignment(left);
        codeLabel.setWrap(true);
        codeLabel.setFontScale(1f);
        table.add(codeLabel).expand().fill().padLeft(10f).padRight(10f).top().padTop(-50);

        addActor(table);
        table.setName("table");

        //parrott
        int parrottWidth = (int) (Gdx.graphics.getWidth() * 0.1);
        parrottImage = createActor(parrottWidth, (float) (parrottWidth * 1.25), table.getX() + table.getWidth() - parrottWidth,
                table.getY() + table.getHeight() - 18, game.assetManager.get("parrott.png", Texture.class));
        addActor(parrottImage);
        parrottImage.setName("parrottImage");

        closeButton = new TextButton("Let's do it", game.mySkin, "default");
        closeButton.setSize((float) (3 * col_width) + 20, (float) (1.5 * row_height));
        closeButton.setPosition(table.getX() + table.getWidth() - closeButton.getWidth() - 25, table.getY() + 15);
        addActor(closeButton);
        closeButton.setName("closeButton");

    }
    public Image createActor(int width, float height, float x, float y, Texture texture) {
        Image image = new Image(texture);
        image.setSize(width, height);
        image.setPosition(x, y);
        return image;
    }
}
