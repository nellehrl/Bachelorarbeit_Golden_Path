package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.badlogic.gdx.utils.Align.center;

public class checkCode extends Group {
    private Table codeTable;
    public boolean isCorrect = false;
    private String input = "";
    final DijkstraAlgorithm game;

    public checkCode(final float x, final float y, final float width, final float height, final String code, final DijkstraAlgorithm game, final int mode) {
        this.game = game;

        codeTable = new Table(game.fontSkin);
        codeTable.setSize(width, (float) (height * 0.66));
        codeTable.setPosition(x, y - height / 2);

        String text = "Did you find the correct Code?\n";
        int parrottWidth = (int) (Gdx.graphics.getWidth() * 0.04);
        final Image image = new Image(game.assetManager.get("treasure.png", Texture.class));
        Label codeLabel = new Label(text, game.fontSkin);
        codeLabel.setWrap(true);
        codeLabel.setAlignment(center);
        codeLabel.setFontScale(1.0f);
        codeTable.add(codeLabel).expand().fill().center().padLeft(10f);
        codeTable.add(image).size(parrottWidth, parrottWidth).padRight(10f);

        // Set the background color of the codeTable
        codeTable.setBackground(game.fontSkin.getDrawable("color"));

        // Add an input field to the codeTable
        final TextField codeInput = new TextField("", game.fontSkin);
        codeInput.setMessageText("Enter code here");
        codeTable.row();
        codeTable.add(codeInput).width(width / 2).height(height / 6).center().padTop(-60);

        BackgroundGroup background = new BackgroundGroup(game);
        Table mangoCounter = background.mangoCounter;
        final Label mangoCounterLabel = (Label) mangoCounter.getChild(1);
        addActor(mangoCounter);

        // Handle input events or validation for the codeInput field as needed
        codeInput.addListener(new InputListener() {
            @Override
            public boolean keyTyped(InputEvent event, char key) {
                input += key;
                if (key == '\r' || key == '\n') {
                    if (input.trim().equals(code.trim())) {
                        mangoCounterLabel.setText(game.mangos);
                        game.setScreen(new LevelWonScreen(game, game.currentLevel));
                        isCorrect = true;
                    } else {
                        game.mangos -= 10;
                        mangoCounterLabel.setText(game.mangos);

                        Sound battle = Gdx.audio.newSound(Gdx.files.internal("battle.wav"));
                        battle.play();
                        if (game.mangos > 0) mangoCounterLabel.setText(game.mangos);
                        else {
                            final LevelLostGroup lost = new LevelLostGroup(game, new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
                            addActor(lost);
                            Button close = (Button) lost.getChild(2);
                            close.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    game.mangos = 30;
                                    mangoCounterLabel.setText(game.mangos);
                                    game.setScreen(new GameScreen_Level3(game, mode));
                                }
                            });
                        }
                    }
                }
                return super.keyTyped(event, key);
            }
        });
        Image shadowImage = new Image(game.assetManager.get("shadow.png", Texture.class));
        shadowImage.setSize((float) (codeTable.getWidth() * 1.25), (float) (codeTable.getHeight() * 1.15));
        shadowImage.setPosition(codeTable.getX() - 70, codeTable.getY() - 10);
        addActor(shadowImage);
        shadowImage.setName("shadowImage");

        addActor(codeTable);
    }
}
