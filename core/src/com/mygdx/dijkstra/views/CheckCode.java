package com.mygdx.dijkstra.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.dijkstra.DijkstraAlgorithm;
import com.mygdx.dijkstra.models.CheckCodeModel;
import com.mygdx.dijkstra.models.WrongUserInput;

import static com.badlogic.gdx.utils.Align.center;

public class CheckCode extends Group {
    private Table codeTable;
    private String input = "";
    private final DijkstraAlgorithm game;
    private String text;
    private final Skin fontSkin;
    private final int level;
    private CheckCodeModel model;
    private final Texture treasureTexture;
    private final Texture shadowTexture;
    private final Texture xCloseTexture;
    private Image shadowImage;
    private final Label mangoCounterLabel;

    public CheckCode(Label mangoCounterLabel, final float x, final float y, final float width, final float height, final String code, final DijkstraAlgorithm game, final Stage stage, final int level) {
        this.game = game;
        this.fontSkin = game.getFontSkin();
        this.level = level;
        this.mangoCounterLabel = mangoCounterLabel;

        model = new CheckCodeModel(game, level);

        // Preload the assets
        treasureTexture = game.getAssetManager().get("treasure.png", Texture.class);
        shadowTexture = game.getAssetManager().get("shadow.png", Texture.class);
        xCloseTexture = game.getAssetManager().get("xClose.png", Texture.class);

        initializeCodeTable(x, y, width, height, code, stage);
        xCloseImage();

        final Image shadowImage = createShadowImage();
        addActor(shadowImage);
        shadowImage.toBack();
    }

    private void initializeCodeTable(final float x, final float y, final float width, final float height, final String code, final Stage stage) {
        codeTable = new Table(fontSkin);
        codeTable.setSize(width, (float) (height * 0.66));
        codeTable.setPosition(x, y);

        text = model.getText();
        final Image treasure = new Image(treasureTexture);

        Label codeLabel = new Label(text, fontSkin);
        codeLabel.setWrap(true);
        codeLabel.setAlignment(center);
        codeLabel.setFontScale(1.0f);

        // Set the background color of the codeTable
        codeTable.setBackground(fontSkin.getDrawable("color"));
        int parrottWidth = (int) (Gdx.graphics.getWidth() * 0.04);

        // Handle input events or validation for the codeInput field as needed
        if (level > 4) handleLevel3(treasure, parrottWidth, codeLabel, width, height, code, stage);
        else handleLevel1And2(stage, treasure, parrottWidth, codeLabel);
        addActor(codeTable);
    }

    private void handleLevel1And2(final Stage stage, Image image, int parrottWidth, Label codeLabel) {
        codeTable.add(image).size(parrottWidth, parrottWidth).left().padTop(5f).padLeft(150f);
        codeTable.add(codeLabel).expand().fill().center().padLeft(-150f).padTop(20f);
        codeTable.row().colspan(3);

        model.setOptionsByLevel(level);
        final CheckBox option1Button = new CheckBox(model.getOption1(), fontSkin);
        final CheckBox option2Button = new CheckBox(model.getOption2(), fontSkin);
        final CheckBox option3Button = new CheckBox(model.getOption3(), fontSkin);

        option1Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (level == 1) {
                    model.correctInput();
                } else {
                    option1Button.setChecked(false);
                    new WrongUserInput(mangoCounterLabel,game, stage, level);
                }
            }
        });

        option2Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (level == 2 || level == 3) {
                    model.correctInput();
                } else {
                    option2Button.setChecked(false);
                    new WrongUserInput(mangoCounterLabel,game, stage, level);
                }
            }
        });

        option3Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (level == 4) {
                    model.correctInput();
                } else {
                    option3Button.setChecked(false);
                    new WrongUserInput(mangoCounterLabel,game, stage, level);
                }
            }
        });

        Table buttonTable = new Table(fontSkin);
        buttonTable.add(option1Button).padLeft(20f);
        buttonTable.add(option2Button).padLeft(20f);
        buttonTable.add(option3Button).padLeft(20f);
        codeTable.add(buttonTable).padBottom(20f);
    }

    private void handleLevel3(Image image, int parrottWidth, Label codeLabel, float width, float height, final String code, final Stage stage) {
        // Add an input field to the codeTable
        codeTable.add(image).size(parrottWidth, parrottWidth).left().padTop(-40f).padLeft(50f);
        codeTable.add(codeLabel).expand().fill().center().padLeft(-75f).padTop(-20f);
        codeTable.row().colspan(3);
        final TextField codeInput = new TextField("", fontSkin);
        codeInput.setMessageText("Code");
        codeTable.add(codeInput).width(width / 2).height(height / 6).center().padTop(-50f);
        codeInput.addListener(new InputListener() {
            @Override
            public boolean keyTyped(InputEvent event, char key) {
                input += key;
                if (key == '\r' || key == '\n') {
                    model.setInput(input);
                    checkIfLevelLost(code, stage, codeInput);
                    input = "";
                }
                return super.keyTyped(event, key);
            }
        });
    }

    private Image createShadowImage() {
        shadowImage = new Image(shadowTexture);
        shadowImage.setSize((float) (codeTable.getWidth() * 1.25), (float) (codeTable.getHeight() * 1.15));
        shadowImage.setPosition(codeTable.getX() - 70, codeTable.getY() - game.getSpace());
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

    private void checkIfLevelLost(String code, Stage stage, TextField textField) {
        if (model.checkInputAgainstCode(code)) model.correctInput();
        else{
            textField.setText("");
            new WrongUserInput(mangoCounterLabel,game, stage, level);
        }
    }
}
