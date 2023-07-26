package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

public class MainMenuScreen implements Screen {
    final DijkstraAlgorithm game;
    InfoTextGroup infotext;
    Image boatImage;
    private Stage stage;
    OrthographicCamera camera;
    Button closeButton;
    private final FitViewport fitViewport;
    int row_height, col_width;
    float boatWidth;

    public MainMenuScreen(final DijkstraAlgorithm game, double currentLevel) {
        this.game = game;

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fitViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        camera.setToOrtho(false, camera.viewportWidth, camera.viewportHeight);
        stage = new Stage();

        row_height = 25;
        col_width = 75;

        String text = "Hey Captain,\n" +
                "\n" +
                "Welcome on board - I am papou. Your help is much needed!\n\nThis crew gets lost on the routes and cant " +
                "read cards let alone finding fast paths. Let`s get on it and find the hidden treasures.\n\n" +
                "If you are new here start with level 1.1. If you are already familiar with Graphs you can start with " +
                "level 1.3. If you are pro go to level 3 than we can get to the treasures even faster and I will finally get my mangooooooos.";

        infotext = new InfoTextGroup(game, text, camera);
        closeButton = infotext.closeButton;
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                infotext.remove();
            }
        });

        Image background = new Image(game.assetManager.get("background.png", Texture.class));
        background.setSize((float) (camera.viewportWidth * 1.1), (float) (camera.viewportHeight * 1.1));
        background.setPosition(-52, -45);
        stage.addActor(background);

        final Image image = new Image(game.assetManager.get("mainMenuScreen.png", Texture.class));
        image.setSize(camera.viewportWidth, camera.viewportHeight);
        image.setPosition(0, 0);
        stage.addActor(image);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.mySkin.getFont("title"), game.mySkin.getColor("color"));  // Replace 'otherStyle' with the desired style

        Label welcomeLabel = new Label("Welcome to\n     Golden Path ", game.mySkin);
        welcomeLabel.setStyle(titleStyle);
        welcomeLabel.setPosition((float) (camera.viewportWidth * 0.425), 100);
        stage.addActor(welcomeLabel);

        ArrayList<TextButton> levelButtons = new ArrayList<>();

        TextButton level11Button = generateButton("1.1", 310 - col_width / 2, 55 - row_height / 2, new GameScreen_Level1(game, 1));
        stage.addActor(level11Button);
        level11Button.setName("1.1");
        levelButtons.add(level11Button);

        TextButton level12Button = generateButton("1.2", 300 - col_width / 2, 225 - row_height / 2, new GameScreen_Level1(game, 2));
        stage.addActor(level12Button);
        level12Button.setName("1.2");
        levelButtons.add(level12Button);

        TextButton level13Button = generateButton("1.3", 327 - col_width / 2, 400 - row_height / 2, new GameScreen_Level1(game, 3));
        stage.addActor(level13Button);
        level13Button.setName("1.3");
        levelButtons.add(level13Button);

        TextButton level21Button = generateButton("2.0", 540 - col_width / 2, 380 - row_height / 2, new GameScreen_Level2(game));
        stage.addActor(level21Button);
        level21Button.setName("2.0");
        levelButtons.add(level21Button);

        TextButton level3Button = generateButton("3.1", 625 - col_width / 2, 210 - row_height / 2, new GameScreen_Level3(game, 1));
        stage.addActor(level3Button);
        level3Button.setName("3.1");
        levelButtons.add(level3Button);

        TextButton level32Button = generateButton("3.2", 850 - col_width / 2, 160 - row_height / 2, new GameScreen_Level3(game, 2));
        stage.addActor(level32Button);
        level32Button.setName("3.2");
        levelButtons.add(level32Button);

        TextButton level33Button = generateButton("3.3", 900 - col_width / 2, 300 - row_height / 2, new GameScreen_Level3(game, 3));
        stage.addActor(level33Button);
        level33Button.setName("3.3");
        levelButtons.add(level33Button);

        TextButton level34Button = generateButton("3.4", 830 - col_width / 2, 440 - row_height / 2, new GameScreen_Level3(game, 4));
        stage.addActor(level34Button);
        level34Button.setName("3.4");
        levelButtons.add(level34Button);

        boatWidth = (float) (camera.viewportWidth * 0.125);
        boatImage = new Image(game.assetManager.get("ship.png", Texture.class));
        boatImage.setSize(boatWidth, boatWidth);
        for (TextButton button : levelButtons) {
            String name = button.getName();
            if (name.equals(String.valueOf(currentLevel))) {
                boatImage.setPosition(button.getX() - boatWidth / 2, button.getY() + row_height);
            }
        }
        stage.addActor(boatImage);
        if(game.firstOpened){
            stage.addActor(infotext);
            game.firstOpened = false;
        }
        boatImage.setName("boatImage");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0.95f, 0.871f, 0.726f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    public TextButton generateButton(String name, int x, int y, final Screen screen) {
        final TextButton button = new TextButton(name, game.mySkin, "default");
        button.setSize(col_width, row_height);
        button.setPosition(x, y);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Create the moveTo action for the boatImage
                float duration = 1f; // Duration of the movement action
                float targetX = button.getX();
                float targetY = button.getY();

                Action moveToAction = Actions.moveTo(targetX, targetY, duration);

                // Add a runnable action to set the new screen after the moveTo action is completed
                Action setScreenAction = Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(screen);
                        dispose();
                    }
                });

                // Chain the actions using ActionSequence
                SequenceAction sequenceAction = Actions.sequence(moveToAction, setScreenAction);

                // Apply the sequence action to the boatImage
                boatImage.addAction(sequenceAction);
            }
        });

        return button;
    }

    @Override
    public void resize(int width, int height) {
        fitViewport.update(width, height, true);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
