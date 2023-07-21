package com.mygdx.dijkstra;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.badlogic.gdx.utils.Align.left;

public class InfoText extends Group {
    Image parrottImage, shadowImage;
    boolean visible = true;
    Button closeButton;
    final DijkstraAlgorithm game;
    public InfoText(final DijkstraAlgorithm game, String text){

        this.game = game;

        int row_height = 25;
        int col_width = 50;

        Table table = new Table();
        table.setBackground(game.fontSkin.getDrawable("color"));
        table.setSize((float) Gdx.graphics.getWidth()/2 , (float) (Gdx.graphics.getHeight()*0.4));
        table.setPosition((float) Gdx.graphics.getWidth()/2 - table.getWidth()/2, (float) Gdx.graphics.getHeight()/2 - table.getHeight()/2);

        Label codeLabel = new Label(text, game.fontSkin);
        codeLabel.setAlignment(left);
        codeLabel.setWrap(true);
        codeLabel.setFontScale(1f);
        table.add(codeLabel).expand().fill().padLeft(10f).padRight(10f).top().padTop(-50);

        shadowImage = createActor((int) (Gdx.graphics.getWidth()*0.62), (float) (Gdx.graphics.getHeight()/2), 235, 180, new Texture(Gdx.files.internal("shadow.png")));
        addActor(shadowImage);
        shadowImage.setName("shadowImage");

        addActor(table);
        table.setName("table");

        //parrott
        int parrottWidth = (int) (Gdx.graphics.getWidth() * 0.1);
        parrottImage = createActor(parrottWidth, (float) (parrottWidth * 1.25), table.getX() + table.getWidth() - parrottWidth,
                table.getY() + table.getHeight() - 18, new Texture(Gdx.files.internal("parrott.png")));
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
