package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.badlogic.gdx.utils.Align.center;

public class CheckCode extends Group {
    private Table codeTable;
    public boolean isCorrect = false;
    private String input = "";
    public Image xCloseImage;
    final DijkstraAlgorithm game;
    String text;
    private CheckBox option1Button;
    private CheckBox option2Button;
    private CheckBox option3Button;
    String option1;
    String option2;
    String option3;

    public CheckCode(final float x, final float y, final float width, final float height, final String code, final DijkstraAlgorithm game, final Stage stage, final OrthographicCamera camera, final int level) {
        this.game = game;

        codeTable = new Table(game.fontSkin);
        codeTable.setSize(width, (float) (height * 0.66));
        codeTable.setPosition(x, y);

        switch (level) {
            case 1:
                text = "What is an undirected graph:";
                break;
            case 2:
                text = "What is a directed graph:";
                break;
            case 3:
                text = "What is a weighted graph:";
                break;
            case 4:
                text = "What kind of graph was represented:";
                break;
            case 5:
                text = "Enter the Code (costs to each city) for the treasure:";
                break;
            case 6:
            case 7:
            case 8:
                text = "Please enter the exact route and the costs: " + game.cities.get(0).shortName + " -> " + game.cities.get(5).shortName + "?\n";
                break;
        }

        int parrottWidth = (int) (Gdx.graphics.getWidth() * 0.04);
        final Image image = new Image(game.assetManager.get("treasure.png", Texture.class));
        Label codeLabel = new Label(text, game.fontSkin);
        codeLabel.setWrap(true);
        codeLabel.setAlignment(center);
        codeLabel.setFontScale(1.0f);

        // Set the background color of the codeTable
        codeTable.setBackground(game.fontSkin.getDrawable("color"));

        BackgroundGroup background = new BackgroundGroup(game, stage, text);
        Table mangoCounter = background.mangoCounter;
        final Label mangoCounterLabel = (Label) mangoCounter.getChild(1);
        addActor(mangoCounter);

        // Handle input events or validation for the codeInput field as needed
        if (level > 4) {
            // Add an input field to the codeTable
            codeTable.add(image).size(parrottWidth, parrottWidth).left().padTop(-40f).padLeft(50f);
            codeTable.add(codeLabel).expand().fill().center().padLeft(-75f).padTop(-20f);
            codeTable.row().colspan(3);
            final TextField codeInput = new TextField("", game.fontSkin);
            codeInput.setMessageText("Code");
            codeTable.add(codeInput).width(width / 2).height(height / 6).center().padTop(-50f);
            codeInput.addListener(new InputListener() {
                @Override
                public boolean keyTyped(InputEvent event, char key) {
                    input += key;
                    if (key == '\r' || key == '\n') {
                        if (input.trim().equals(code.trim())) {
                            mangoCounterLabel.setText(game.mangos);
                            if (game.mangos < 30) game.mangos = 30;
                            game.dropSound.play();
                            switch (level) {
                                case 5:
                                    game.setScreen(new LevelWonScreen(game, 5));
                                    break;
                                case 6:
                                    game.setScreen(new LevelWonScreen(game, 6));
                                    break;
                                case 7:
                                    game.setScreen(new LevelWonScreen(game, 7));
                                    break;
                                case 8:
                                    game.setScreen(new LevelWonScreen(game, 8));
                                    break;
                            }
                            isCorrect = true;
                        } else {
                            game.mangos -= 10;
                            mangoCounterLabel.setText(game.mangos);
                            game.battle.play();
                            game.blood.setSize(camera.viewportWidth, camera.viewportHeight);
                            game.blood.setPosition(0, 0);
                            stage.addActor(game.blood);
                            game.blood.addAction(Actions.sequence(
                                    Actions.delay(1f),
                                    Actions.fadeOut(1f),
                                    Actions.removeActor()
                            ));
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
                                        game.setScreen(new GameScreen_Level3(game, level));
                                    }
                                });
                            }
                        }
                    }
                    return super.keyTyped(event, key);
                }
            });
        } else {
            codeTable.add(image).size(parrottWidth, parrottWidth).left().padTop(5f).padLeft(150f);
            codeTable.add(codeLabel).expand().fill().center().padLeft(-150f).padTop(20f);
            codeTable.row().colspan(3);
            Table buttonTable = new Table(game.fontSkin);
            switch (level) {
                case 1:
                    option1 = "Goes in both directions";
                    option2 = "Has no destination";
                    option3 = "Has no source";
                    break;
                case 2:
                    option1 = "Has a destination";
                    option2 = "Has a source and a destination";
                    option3 = "Has a source";
                    break;
                case 3:
                    option1 = "A directed graph";
                    option2 = "Each edge has a cost";
                    option3 = "The graph has a cost";
                    break;
                case 4:
                    option1 = "A weighted graph";
                    option2 = "A directed graph";
                    option3 = "A weighted and directed graph";
                    break;
            }
            option1Button = new CheckBox(option1, game.fontSkin);
            option2Button = new CheckBox(option2, game.fontSkin);
            option3Button = new CheckBox(option3, game.fontSkin);

            option1Button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (level == 1) {
                        mangoCounterLabel.setText(game.mangos);
                        if (game.mangos < 30) game.mangos = 30;
                        game.dropSound.play();
                        game.setScreen(new LevelWonScreen(game, level));
                        isCorrect = true;
                    } else {
                        game.mangos -= 10;
                        mangoCounterLabel.setText(game.mangos);
                        game.battle.play();
                        game.blood.setSize(camera.viewportWidth, camera.viewportHeight);
                        game.blood.setPosition(0, 0);
                        stage.addActor(game.blood);
                        game.blood.addAction(Actions.sequence(
                                Actions.delay(1f),
                                Actions.fadeOut(1f),
                                Actions.removeActor()
                        ));
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
                                    game.setScreen(new GameScreen_Level3(game, level));
                                }
                            });
                        }
                    }
                }
            });

            option2Button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (level == 2 || level == 3) {
                        mangoCounterLabel.setText(game.mangos);
                        if (game.mangos < 30) game.mangos = 30;
                        game.dropSound.play();
                        game.setScreen(new LevelWonScreen(game, level));
                        isCorrect = true;
                    } else {
                        game.mangos -= 10;
                        mangoCounterLabel.setText(game.mangos);
                        game.battle.play();
                        game.blood.setSize(camera.viewportWidth, camera.viewportHeight);
                        game.blood.setPosition(0, 0);
                        stage.addActor(game.blood);
                        game.blood.addAction(Actions.sequence(
                                Actions.delay(1f),
                                Actions.fadeOut(1f),
                                Actions.removeActor()
                        ));
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
                                    game.setScreen(new GameScreen_Level3(game, level));
                                }
                            });
                        }
                    }
                }
            });

            option3Button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (level == 4) {
                        mangoCounterLabel.setText(game.mangos);
                        if (game.mangos < 30) game.mangos = 30;
                        game.dropSound.play();
                        game.setScreen(new LevelWonScreen(game, 4));
                        isCorrect = true;
                    } else {
                        game.mangos -= 10;
                        mangoCounterLabel.setText(game.mangos);
                        game.battle.play();
                        game.blood.setSize(camera.viewportWidth, camera.viewportHeight);
                        game.blood.setPosition(0, 0);
                        stage.addActor(game.blood);
                        game.blood.addAction(Actions.sequence(
                                Actions.delay(1f),
                                Actions.fadeOut(1f),
                                Actions.removeActor()
                        ));
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
                                    game.setScreen(new GameScreen_Level3(game, level));
                                }
                            });
                        }
                    }
                }
            });
            buttonTable.add(option1Button).padLeft(20f);
            buttonTable.add(option2Button).padLeft(20f);
            buttonTable.add(option3Button).padLeft(20f);
            codeTable.add(buttonTable).padBottom(20f);
        }
        final Image shadowImage = new Image(game.assetManager.get("shadow.png", Texture.class));
        shadowImage.setSize((float) (codeTable.getWidth() * 1.25), (float) (codeTable.getHeight() * 1.15));
        shadowImage.setPosition(codeTable.getX() - 70, codeTable.getY() - 10);
        addActor(shadowImage);
        shadowImage.setName("shadowImage");
        addActor(codeTable);

        xCloseImage = new Image(game.assetManager.get("xClose.png", Texture.class));
        xCloseImage.setSize(20, 20);
        xCloseImage.setPosition(codeTable.getX() + xCloseImage.getWidth(),
                codeTable.getY() + codeTable.getHeight() - xCloseImage.getHeight() - game.space);
        xCloseImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                removeActor(codeTable);
                removeActor(xCloseImage);
                removeActor(shadowImage);
            }
        });
        addActor(xCloseImage);
    }
}
