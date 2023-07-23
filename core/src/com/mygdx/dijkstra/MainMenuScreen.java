package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    final DijkstraAlgorithm game;
    Image boatImage;
    private Stage stage;
    OrthographicCamera camera;
    private final FitViewport fitViewport;
    private final ScreenViewport viewport = new ScreenViewport();
    int row_height, col_width;
    float boatWidth;

    public MainMenuScreen(final DijkstraAlgorithm game) {
        this.game = game;

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fitViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        stage = new Stage(new ScreenViewport());
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        row_height = 25;
        col_width = 75;

        final Image image = new Image(new Texture(Gdx.files.internal("mainMenuScreen.png")));
        image.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        image.setPosition(0, 0);
        stage.addActor(image);

        boatWidth = (float) (Gdx.graphics.getWidth() * 0.125);
        boatImage = new Image(new Texture(Gdx.files.internal("ship.png")));
        boatImage.setSize(boatWidth, boatWidth);
        boatImage.setPosition(175, 50);
        stage.addActor(boatImage);
        boatImage.setName("boatImage");

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.mySkin.getFont("title"), game.mySkin.getColor("color"));  // Replace 'otherStyle' with the desired style

        Label welcomeLabel = new Label("Welcome to\n     Golden Path ", game.mySkin);
        welcomeLabel.setStyle(titleStyle);
        welcomeLabel.setPosition((float) (Gdx.graphics.getWidth() * 0.425), 100);
        stage.addActor(welcomeLabel);

        TextButton level1Button = generateButton("1.1", 310 - col_width/2, 55 - row_height/2, new GameScreen_Level1(game, 1));
        stage.addActor(level1Button);

        TextButton level12Button = generateButton("1.2", 300 - col_width/2, 225 - row_height/2, new GameScreen_Level1(game, 2));
        stage.addActor(level12Button);

        TextButton level13Button = generateButton("1.3", 327 - col_width/2, 550 - row_height/2, new GameScreen_Level1(game, 3));
        stage.addActor(level13Button);

        TextButton level21Button = generateButton("2", 560 - col_width/2, 465 - row_height/2, new GameScreen_Level2(game));
        stage.addActor(level21Button);

        TextButton level3Button = generateButton("3.1", 625 - col_width/2, 190 - row_height/2, new GameScreen_Level3(game, 1));
        stage.addActor(level3Button);

        TextButton level32Button = generateButton("3.2", 885 - col_width/2, 230 - row_height/2, new GameScreen_Level3(game, 2));
        stage.addActor(level32Button);

        TextButton level33Button = generateButton("3.3", 850 - col_width/2, 340 - row_height/2, new GameScreen_Level3(game, 3));
        stage.addActor(level33Button);

        TextButton level34Button = generateButton("3.4", 830 - col_width/2, 440 - row_height/2, new GameScreen_Level3(game, 4));
        stage.addActor(level34Button);
    }

    @Override
    public void show() {

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
    public TextButton generateButton(String name, int x, int y, final Screen screen){
        final TextButton button = new TextButton(name, game.mySkin, "default");
        button.setSize(col_width, row_height);
        button.setPosition(x,y);
        button.addListener(new ClickListener(){
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

    }
}
