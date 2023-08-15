package com.mygdx.dijkstra.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.dijkstra.DijkstraAlgorithm;
import com.mygdx.dijkstra.models.CheckCodeModel;
import com.mygdx.dijkstra.models.City;
import com.mygdx.dijkstra.models.WrongUserInput;
import com.mygdx.dijkstra.systems.GameScreen_Level3;

import static com.badlogic.gdx.utils.Align.center;
import static com.badlogic.gdx.utils.Align.left;

public class CheckMoveBoatGroup extends Group {
    private Table codeTable;
    private String input = "";
    private final DijkstraAlgorithm game;
    private final int iteration;
    private String correctAnswer;
    private final Skin fontSkin;
    private final int level;
    private final Texture shadowTexture;
    private final Texture xCloseTexture;
    private Image shadowImage;
    private final Label mangoCounterLabel;
    private GameScreen_Level3 level3;

    public CheckMoveBoatGroup(GameScreen_Level3 level3,Label mangoCounterLabel, final float x, final float y, final float width, final float height, final DijkstraAlgorithm game, final Stage stage, String correctAnswer, int iteration, int level) {
        this.game = game;
        this.correctAnswer = correctAnswer;
        this.iteration = iteration;
        this.fontSkin = game.getFontSkin();
        this.level = level;
        this.mangoCounterLabel = mangoCounterLabel;
        this.level3 = level3;

        // Preload the assets
        shadowTexture = game.getAssetManager().get("shadow.png", Texture.class);
        xCloseTexture = game.getAssetManager().get("xClose.png", Texture.class);

        initializeCodeTable(x, y, width, height, stage);
        xCloseImage();

        final Image shadowImage = createShadowImage();
        addActor(shadowImage);
        shadowImage.toBack();
    }

    private void initializeCodeTable(final float x, final float y, final float width, final float height, final Stage stage) {

        String text = "Which city should we visit next?\nRemember: We always want to visit the city with the current lowest costs!";

        codeTable = new Table(fontSkin);
        codeTable.setSize(width, height);
        codeTable.setPosition(x, y);

        Label codeLabel = new Label(text, fontSkin);
        codeLabel.setWrap(true);
        codeLabel.setAlignment(center);
        codeLabel.setFontScale(1.0f);

        // Set the background color of the codeTable
        codeTable.setBackground(fontSkin.getDrawable("color"));

        // Handle input events or validation for the codeInput field as needed
        handleLevel3(codeLabel, width, height, stage);
        addActor(codeTable);
    }

    private void handleLevel3(Label codeLabel, float width, float height, final Stage stage) {
        // Add an input field to the codeTable
        codeTable.add(codeLabel).expand().fill().center().padTop(-20f).padLeft(game.getSpace()).padRight(game.getSpace());
        codeTable.row().colspan(3);
        final TextField codeInput = new TextField("", fontSkin);
        codeInput.setMessageText("Shortage of city");
        codeTable.add(codeInput).width(width/2).height(height/6).center().padTop(-50f);
        codeInput.addListener(new InputListener() {
            @Override
            public boolean keyTyped(InputEvent event, char key) {
                input += key;
                if (key == '\r' || key == '\n') {
                    checkIfLevelLost(input, stage, correctAnswer,codeInput);
                    input = "";
                }
                return super.keyTyped(event, key);
            }
        });
    }

    private Image createShadowImage() {
        shadowImage = new Image(shadowTexture);
        shadowImage.setSize(codeTable.getWidth(),codeTable.getHeight());
        shadowImage.setPosition(codeTable.getX(), codeTable.getY() - game.getSpace());
        shadowImage.setName("shadowImage");
        return shadowImage;
    }

    private void xCloseImage() {
        final Image xClose = new Image(xCloseTexture);
        xClose.setSize(20, 20);
        xClose.setPosition(codeTable.getX() + xClose.getWidth(),
                codeTable.getY() + codeTable.getHeight() - xClose.getHeight() - game.getSpace());
        xClose.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                removeActor(codeTable);
                removeActor(xClose);
                removeActor(shadowImage);
            }
        });
        addActor(xClose);
    }

    private void checkIfLevelLost(String input, Stage stage, String correctAnswer,TextField textField) {
        if (correctAnswer.trim().equalsIgnoreCase(input.trim())) level3.moveBoatToNextCity(iteration);
        else{
            textField.setText((""));
            new WrongUserInput(mangoCounterLabel,game, stage, level3.level);
        }
    }
}
