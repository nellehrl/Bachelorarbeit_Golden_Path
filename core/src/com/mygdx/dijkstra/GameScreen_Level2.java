package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;;
import java.util.List;

import static com.badlogic.gdx.utils.Align.left;

public class GameScreen_Level2 implements Screen {
    final DijkstraAlgorithm game;
    Image boatImage, connectionArea, parrotImage;
    Table portTable, mangoCounter;
    Label mangoCounterLabel;
    OrthographicCamera camera;
    private final FitViewport fitViewport;
    ArrayList<City> currentConnection = new ArrayList<>();
    private Stage stage;
    Button mainMenuButton, closeButton;
    Group tableGroup;
    private List<LineData> linesToDraw;
    DropBoxWindow dropBox;
    Group background;
    Graph connections;
    String text;
    InfoTextGroup infotext;

    public GameScreen_Level2(final DijkstraAlgorithm game) {

        this.game = game;

        camera = game.camera;
        fitViewport = game.fitViewport;
        stage = new Stage(fitViewport);
        //init cities and starting point
        currentConnection.add(game.cities.get(0));
        connections = new Graph(game.vertices, 1);

        background = new BackgroundGroup(game);
        for (Actor actor : background.getChildren()) {
            if (actor.getName().equals("mainMenuButton")) {
                mainMenuButton = (Button) actor;
                mainMenuButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.currentLevel = 2;
                        game.setScreen(new MainMenuScreen(game, 2));
                        dispose();
                    }
                });
            } else if (actor.getName().equals("dropBox")) dropBox = (DropBoxWindow) actor;
            else if (actor.getName().equals("boatImage")) boatImage = (Image) actor;
            else if (actor.getName().equals("mangoCounter")) mangoCounter = (Table) actor;
        }

        mangoCounterLabel = (Label) mangoCounter.getChild(1);

        linesToDraw = new ArrayList<>();
        tableGroup = new Group();
        for (int i = 0; i < game.vertices; i++) {

            City sourceCity = game.cities.get(i);
            portTable = new Ports(sourceCity, game);
            tableGroup.addActor(portTable);
            java.util.List<Edge> neighbors = connections.getNeighbors(i);

            for (int j = 0; j < neighbors.size(); j++) {
                int destination = neighbors.get(j).destination;
                City destCity = game.cities.get(destination);
                int weight = neighbors.get(j).weight;

                Vector2 start = new Vector2(sourceCity.x, sourceCity.y);
                Vector2 end = new Vector2(destCity.x, destCity.y);

                connectionArea = new ConnectionAreaImage(sourceCity, destCity);
                Image connectionArea2 = new ConnectionAreaImage(destCity, sourceCity);
                tableGroup.addActor(connectionArea);
                tableGroup.addActor(connectionArea);

                LineData lineData = new LineData(start, end, Color.BLACK);
                linesToDraw.add(lineData);

                InfoCardActor card = new InfoCardActor(game, (float) (destCity.x + sourceCity.x) / 2,
                        (float) (destCity.y + sourceCity.y) / 2, 150, 60, sourceCity.name, destCity.name, weight, false);
                final Table cardTableFinal = card.getTable(); // Create a final variable

                connectionArea.addListener(new InputListener() {
                    final Table cardTable = cardTableFinal; // Use the final variable

                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        stage.addActor(cardTable);
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        cardTable.remove();
                    }
                });
                connectionArea2.addListener(new InputListener() {
                    final Table cardTable = cardTableFinal; // Use the final variable

                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        stage.addActor(cardTable);
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        cardTable.remove();
                    }
                });
            }
        }
        dropBoxItems();

        text = "You know who this is, Captain!\n\nFantastic job so far! We updated our system. Now that you have added " +
                "the right weight to the connections, we have an excellent overview!\n\n" +
                "Let`s check if everything is clear - find the weights for the connections written down below.\n\n" +
                "We must to understand how these graphs work to get ahead of the other crews. Like that, we can generate " +
                "much more loooooooooooooooot!\n\nRemember to ENTER to check if you got the costs correct!";

        infotext = new InfoTextGroup(game, text);
        closeButton = infotext.closeButton;
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                infotext.remove();
                int parrottWidth = (int) (Gdx.graphics.getWidth() * 0.1);
                parrotImage = new Image(game.assetManager.get("parrott.png", Texture.class));
                parrotImage.setSize((float) parrottWidth, (float) (parrottWidth * 1.25));
                parrotImage.setPosition((float) (Gdx.graphics.getWidth() * 0.85), (float) (camera.viewportHeight * 0.31));
                parrotImage.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        stage.addActor(infotext);
                        parrotImage.remove();
                    }
                });
                stage.addActor(parrotImage);
            }
        });

        Image box = new Image(game.assetManager.get("box.png", Texture.class));
        box.setSize(camera.viewportWidth-2, camera.viewportHeight/3 - 2);
        box.setPosition(1,1);
        stage.addActor(box);
        tableGroup.addActor(boatImage);
        stage.addActor(tableGroup);
        stage.addActor(mainMenuButton);
        stage.addActor(infotext);
    }

    @Override
    public void render(float delta) {
        // tell the SpriteBatch to render in the coordinate system specified by the camera.
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0.95f, 0.871f, 0.726f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render background
        game.batch.begin();
        background.draw(game.batch, 1.0f);
        game.batch.end();

        //render lines alias connections
        for (LineData lineData : linesToDraw) {
            new DrawLineOrArrow(5, camera.combined, lineData.getColor(), lineData.getStart(), lineData.getEnd(), 1);
        }

        //render stage
        stage.act();
        stage.draw();

    }

    public void dropBoxItems() {
        final int[] numOfEdges = {0};
        for (int i = 0; i < game.vertices; i++) {
            java.util.List<Edge> neighbors = connections.getNeighbors(i);
            for (int j = 0; j < neighbors.size(); j++) {
                int destination = neighbors.get(j).destination;
                final int weight = neighbors.get(j).weight;
                City destCity = game.cities.get(destination);
                City sourceCity = game.cities.get(i);
                String destCityName = destCity.name;
                String sourceCityName = game.cities.get(i).name;

                if (sourceCity != destCity) {
                    final Vector2 start = new Vector2(sourceCity.x, sourceCity.y);
                    final Vector2 end = new Vector2(destCity.x, destCity.y);

                    String labelText = sourceCityName + " - " + destCityName;
                    final String fieldText = "Costs: ";

                    Table infoTable = new Table(game.fontSkin);
                    infoTable.setSize((camera.viewportWidth) / (game.vertices), 50);
                    infoTable.setPosition(2 * game.offset + infoTable.getWidth() * i + game.space * (i + 1), (float) ((camera.viewportHeight * 0.28) - game.offset - (infoTable.getHeight() + game.space) * j));

                    Label codeLabel = new Label(labelText, game.fontSkin);
                    codeLabel.setAlignment(left);
                    codeLabel.setFontScale(0.75f);
                    infoTable.add(codeLabel).row();
                    final TextField textfield = new TextField(fieldText, game.fontSkin);
                    textfield.setColor(0, 0, 0, 1);
                    textfield.setAlignment(left);
                    textfield.addListener(new ClickListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            textfield.setColor(Color.RED);
                            linesToDraw.add(new LineData(start, end, Color.RED));
                            return true;
                        }
                    });
                    final boolean[] edgeAdded = {false};
                    textfield.setTextFieldListener(new TextField.TextFieldListener() {
                        @Override
                        public void keyTyped(TextField textField, char key) {
                            String userInput = textField.getText();
                            boolean isCorrect = userInput.equals(fieldText + weight);
                            if (key == '\r' || key == '\n') {
                                if (isCorrect && !edgeAdded[0]) {
                                    game.dropSound.play();
                                    numOfEdges[0]++;
                                    edgeAdded[0] = true;
                                    textField.setColor(Color.GREEN);
                                    linesToDraw.add(new LineData(start, end, Color.GREEN));
                                }
                                else {
                                    int newValue = calcNewValue(game, camera, stage, mangoCounterLabel);
                                    if (newValue > 0) mangoCounterLabel.setText(newValue);
                                    else {
                                        parrotImage.remove();
                                        final LevelLostGroup lost = new LevelLostGroup(game, camera);
                                        stage.addActor(lost);
                                        Button close = (Button) lost.getChild(2);
                                        close.addListener(new ClickListener() {
                                            @Override
                                            public void clicked(InputEvent event, float x, float y) {
                                                game.mangos = 30;
                                                mangoCounterLabel.setText(game.mangos);
                                                game.setScreen(new GameScreen_Level2(game));
                                                dispose();
                                            }
                                        });
                                    }

                                }
                            }
                            if (numOfEdges[0] == connections.numOfEdges) {
                                game.setScreen(new LevelWonScreen(game, 2));
                                dispose();
                            }
                        }
                    });
                    infoTable.add(textfield);
                    infoTable.setBackground(game.fontSkin.getDrawable("color"));
                    tableGroup.addActor(infoTable);
                }
            }
        }
    }

    static int calcNewValue(DijkstraAlgorithm game, OrthographicCamera camera, Stage stage, Label mangoCounterLabel) {
        GameScreen_Level1.negativeFeedbackLoop(game, camera, stage);
        return Integer.parseInt(String.valueOf(mangoCounterLabel.getText())) - 10;
    }

    @Override
    public void resize(int width, int height) {
        fitViewport.update(width, height, true);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
