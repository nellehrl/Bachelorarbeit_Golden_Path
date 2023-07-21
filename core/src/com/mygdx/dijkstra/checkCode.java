package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import static com.badlogic.gdx.utils.Align.left;

public class checkCode extends Group {
    private Table codeTable;
    public boolean isCorrect = false;
    private String input = "";
    final DijkstraAlgorithm game;
    public checkCode(final float x, final float y, final float width, final float height, final String code, final DijkstraAlgorithm game, final int mode){
        this.game = game;

        codeTable = new Table(game.fontSkin);
        codeTable.setSize(width, (float) (height*0.66));
        codeTable.setPosition(x - width/2, y - height/2);

        String text = "Did you find the correct Code?\n";
        int parrottWidth = (int) (Gdx.graphics.getWidth() * 0.04);
        final Image image = new Image(new Texture(Gdx.files.internal("treasure.png")));
        Label codeLabel = new Label(text, game.fontSkin);
        codeLabel.setWrap(true);
        codeLabel.setAlignment(left);
        codeLabel.setFontScale(1.0f);
        codeTable.add(codeLabel).expand().fill().center().padLeft(10f);
        codeTable.add(image).size(parrottWidth, parrottWidth).padRight(10f);

        // Set the background color of the codeTable
        codeTable.setBackground(game.fontSkin.getDrawable("color"));

        // Add an input field to the codeTable
        final TextField codeInput = new TextField("", game.fontSkin);
        codeInput.setMessageText("Enter code here");
        codeTable.row();
        codeTable.add(codeInput).width(width/2).height(height/6).center().padTop(-60);

        // Handle input events or validation for the codeInput field as needed
        codeInput.addListener(new InputListener() {
            @Override
            public boolean keyTyped(InputEvent event, char character) {
                input += character;
                int nextMode = (mode + 1);
                if(input.equals(code)){
                    game.setScreen(new GameScreen_Level3(game, nextMode));
                    isCorrect = true;
                }
                return super.keyTyped(event, character);
            }
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return super.keyDown(event, keycode);
            }
        });

        Image shadowImage = new Image(new Texture(Gdx.files.internal("shadow.png")));
        shadowImage.setSize((float) (codeTable.getWidth()*1.25), (float) (codeTable.getHeight()*1.15));
        shadowImage.setPosition(codeTable.getX() - 40, codeTable.getY()-10);
        addActor(shadowImage);
        shadowImage.setName("shadowImage");

        addActor(codeTable);
    }
}
