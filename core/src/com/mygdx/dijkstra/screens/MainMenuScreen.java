package com.mygdx.dijkstra.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.dijkstra.DijkstraAlgorithm;
import com.mygdx.dijkstra.views.InfoTextGroup;
import com.mygdx.dijkstra.views.LevelDescriptionHoverActor;

import java.util.ArrayList;

public class MainMenuScreen implements Screen {
    private final DijkstraAlgorithm game;
    private static final float ANIMATION_DURATION = 1f;
    private InfoTextGroup infotext;
    private Image boatImage;
    private Stage stage;
    private OrthographicCamera camera;
    private FitViewport fitViewport;
    private final int row_height, col_width;

    public MainMenuScreen(final DijkstraAlgorithm game, int currentLevel) {

        this.game = game;
        int offset = game.getOffset();
        int space = game.getSpace();
        Skin mySkin = game.getMySkin();
        row_height = offset;
        col_width = offset * 3;

        setupCamera();
        setupInfoText();
        setupTextures();
        setupLabels();
        setupButtons(currentLevel);
        if (game.isFirstOpened()) {
            stage.addActor(infotext);
            game.setFirstOpened(false);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0.95f, 0.871f, 0.726f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    private void setupCamera() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fitViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        camera.setToOrtho(false, camera.viewportWidth, camera.viewportHeight);
        stage = new Stage(fitViewport);
    }

    private void setupInfoText() {
        String text = "Hey Captain,\n\nWelcome on board - I am papou. Your help is much needed!\n\nThis crew gets lost " +
                "on the routes and cant read cards let alone finding fast paths. Let`s get on it and find the hidden treasures." +
                "\n\nIf you are new here start with level 1.1. If you are already familiar with Graphs you can start with " +
                "level 1.3. If you are pro go to level 3 than we can get to the treasures even faster and I will finally get my mangooooooos.";
        infotext = new InfoTextGroup(game, text, camera);
        Button closeButton = infotext.getCloseButton();
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                infotext.remove();
            }
        });
    }

    private void setupLabels() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.getMySkin().getFont("title"), game.getMySkin().getColor("color"));
        Label welcomeLabel = new Label("Welcome to\n     Golden Path ", game.getMySkin());
        welcomeLabel.setStyle(titleStyle);
        welcomeLabel.setPosition((float) (camera.viewportWidth * 0.425), 100);
        stage.addActor(welcomeLabel);
    }

    private void setupButtons(int currentLevel) {
        ArrayList<TextButton> levelButtons = new ArrayList<>();

        TextButton level11Button = generateButton("1.1", 310 - col_width / 2, 55 - row_height / 2, new GameScreen_Level1(game, 1), 1);
        stage.addActor(level11Button);
        level11Button.setName("1");
        levelButtons.add(level11Button);

        TextButton level12Button = generateButton("1.2", 300 - col_width / 2, 225 - row_height / 2, new GameScreen_Level1(game, 2), 2);
        stage.addActor(level12Button);
        level12Button.setName("2");
        levelButtons.add(level12Button);

        TextButton level13Button = generateButton("1.3", 327 - col_width / 2, 400 - row_height / 2, new GameScreen_Level1(game, 3), 3);
        stage.addActor(level13Button);
        level13Button.setName("3");
        levelButtons.add(level13Button);

        TextButton level21Button = generateButton("2.0", 540 - col_width / 2, 380 - row_height / 2, new GameScreen_Level2(game), 4);
        stage.addActor(level21Button);
        level21Button.setName("4");
        levelButtons.add(level21Button);

        TextButton level3Button = generateButton("3.1", 625 - col_width / 2, 210 - row_height / 2, new GameScreen_Level3(game, 5), 5);
        stage.addActor(level3Button);
        level3Button.setName("5");
        levelButtons.add(level3Button);

        TextButton level32Button = generateButton("3.2", 850 - col_width / 2, 160 - row_height / 2, new GameScreen_Level3(game, 6), 6);
        stage.addActor(level32Button);
        level32Button.setName("6");
        levelButtons.add(level32Button);

        TextButton level33Button = generateButton("3.3", 900 - col_width / 2, 300 - row_height / 2, new GameScreen_Level3(game, 7), 7);
        stage.addActor(level33Button);
        level33Button.setName("7");
        levelButtons.add(level33Button);

        TextButton level34Button = generateButton("3.4", 800 - col_width / 2, 440 - row_height / 2, new GameScreen_Level3(game, 8), 8);
        stage.addActor(level34Button);
        level34Button.setName("8");
        levelButtons.add(level34Button);

        float boatWidth = (float) (camera.viewportWidth * 0.125);
        boatImage = new Image(game.getAssetManager().get("ship.png", Texture.class));
        boatImage.setSize(boatWidth, boatWidth);
        boatImage.setName("boatImage");
        stage.addActor(boatImage);

        for (TextButton button : levelButtons) {
            String name = button.getName();
            if (name.equals(String.valueOf(currentLevel)))
                boatImage.setPosition(button.getX() - boatWidth / 2, button.getY() + row_height);
        }
    }

    private void setupTextures() {
        Texture backGroundTexture = game.getAssetManager().get("background.png", Texture.class);
        Image background = new Image(backGroundTexture);
        background.setSize((float) (camera.viewportWidth * 1.1), (float) (camera.viewportHeight * 1.1));
        background.setPosition(-52, -45);
        stage.addActor(background);

        Texture mainMenuTexture = game.getAssetManager().get("mainMenuScreen.png", Texture.class);
        Image mainMenuScreen = new Image(mainMenuTexture);
        mainMenuScreen.setSize(camera.viewportWidth, camera.viewportHeight);
        mainMenuScreen.setPosition(0, 0);
        stage.addActor(mainMenuScreen);
    }

    private TextButton generateButton(String name, int x, int y, final Screen screen, int level) {
        final TextButton button = new TextButton(name, game.getMySkin(), "default");
        button.setSize(col_width, row_height);
        button.setPosition(x, y);
        LevelDescriptionHoverActor card = new LevelDescriptionHoverActor(game, button.getX(), button.getY() + button.getHeight(), 200, level);
        final Table cardTableFinal = card.getTable();
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                moveBoatAndSetScreen(button, screen);
            }
        });
        button.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                stage.addActor(cardTableFinal);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                cardTableFinal.remove();
            }
        });

        return button;
    }

    private void moveBoatAndSetScreen(TextButton button, final Screen screen) {
        float targetX = button.getX();
        float targetY = button.getY();
        Action moveToAction = Actions.moveTo(targetX, targetY, ANIMATION_DURATION);
        Action setScreenAction = Actions.run(new Runnable() {
            @Override
            public void run() {
                game.setScreen(screen);
                MainMenuScreen.this.dispose();
            }
        });
        SequenceAction sequenceAction = Actions.sequence(moveToAction, setScreenAction);
        boatImage.addAction(sequenceAction);
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
