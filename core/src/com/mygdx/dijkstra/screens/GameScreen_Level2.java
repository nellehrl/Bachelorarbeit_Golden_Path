package com.mygdx.dijkstra.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.dijkstra.DijkstraAlgorithm;
import com.mygdx.dijkstra.models.City;
import com.mygdx.dijkstra.models.Edge;
import com.mygdx.dijkstra.models.Graph;
import com.mygdx.dijkstra.models.LineData;
import com.mygdx.dijkstra.views.*;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.utils.Align.left;

public class GameScreen_Level2 implements Screen {
    private final DijkstraAlgorithm game;
    private OrthographicCamera camera;
    private FitViewport fitViewport;
    private Stage stage;
    private Batch batch;
    private Skin fontSkin;
    private Image boatImage;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private Label mangoCounterLabel;
    private Button mainMenuButton;
    private Group background;
    private Group infotext;
    private final ArrayList<City> currentConnection = new ArrayList<>();
    private final List<LineData> linesToDraw = new ArrayList<>();
    private Graph graph;
    private String text;
    private DrawLineOrArrow draw;
    private int vertices, space, offset;
    private ArrayList<City> cities;
    private CheckCode checkCode;

    public GameScreen_Level2(final DijkstraAlgorithm game) {
        this.game = game;
        initializeVariables();
        initializeUIElements();
        initializeGameComponents();
    }

    @Override
    public void render(float delta) {
        // Update the camera and stage
        camera.update();
        stage.act(delta);

        // Clear the screen
        Gdx.gl.glClearColor(0.95f, 0.871f, 0.726f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render the background
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        background.draw(batch, 1.0f);
        batch.end();

        // Render the graph (lines)
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (LineData lineData : linesToDraw) {
            draw.drawArrow(shapeRenderer, 3, lineData.getColor(), lineData.getStart(), lineData.getEnd());
        }
        shapeRenderer.end();

        // Draw the stage
        stage.draw();
    }

    private void initializeVariables() {
        camera = game.getCamera();
        fitViewport = game.getFitViewport();
        stage = new Stage(fitViewport);
        cities = game.getCities();
        vertices = game.getVertices();
        space = game.getSpace();
        offset = game.getOffset();
        batch = game.getBatch();
        fontSkin = game.getFontSkin();
        currentConnection.add(cities.get(0));
        graph = new Graph(vertices, 1);
    }

    private void initializeUIElements() {
        initializeText();
        background = new BackgroundGroup(game, stage, text, 4);
        mainMenuButton = background.findActor("mainMenuButton");
        boatImage = background.findActor("boatImage");
        infotext = background.findActor("infotext");
        Table mangoCounter = background.findActor("mangoCounter");
        mangoCounterLabel = (Label) mangoCounter.getChild(1);
        initializeDropBoxItems();
    }

    private void initializeGameComponents() {
        draw = new DrawLineOrArrow();
        stage.addActor(new MapGroup_Level2_3(game, 0, graph, boatImage, linesToDraw));
        checkCode = new CheckCode(camera.viewportWidth / 4, camera.viewportHeight / 2, (float) (camera.viewportWidth * 0.6), 150, "code", game, stage, camera, 4);

        // Add remaining actors
        stage.addActor(boatImage);
        stage.addActor(mainMenuButton);
        stage.addActor(infotext);
    }

    public void initializeText() {
        text = "You know who this is, Captain!\n\n" +
                "Fantastic job so far! We updated our system. Now that you have added " +
                "the right weight to the graph, we have an excellent overview!\n\n" +
                "Let`s check if everything is clear - find the weights for the graph written down below.\n\n" +
                "We must to understand how these graphs work to get ahead of the other crews. Like that, we can generate " +
                "much more loooooooooooooooot!\n\n" +
                "Remember to ENTER to check if you got the costs correct!";
    }

    public void initializeDropBoxItems() {
        final int[] numOfEdges = {0};
        for (int i = 0; i < vertices; i++) {
            //init j for positioning items correctly
            int j = 0;
            java.util.List<Edge> neighbors = graph.getNeighbors(i);
            City sourceCity = cities.get(i);
            //init table to fill
            for (Edge neighbor : neighbors) {
                //define destination City and connection to color
                City destCity = cities.get(neighbor.getDestination());
                final Vector2 start = new Vector2(sourceCity.getX(), sourceCity.getY());
                final Vector2 end = new Vector2(destCity.getX(), destCity.getY());
                //init label
                String labelText = sourceCity.getName() + " - " + destCity.getName();
                String fieldText = "Costs: ";
                //init table
                Table infoTable = createInfoTable(labelText, i, j);
                TextField textField = createTextField(fieldText, start, end, neighbor, numOfEdges);
                //add table to stage
                infoTable.add(textField);
                infoTable.setBackground(fontSkin.getDrawable("color"));
                stage.addActor(infoTable);
                j++;
            }
        }
    }

    private Table createInfoTable(String labelText, int i, int j) {
        Table infoTable = new Table(fontSkin);
        infoTable.setSize((camera.viewportWidth) / (vertices), (camera.viewportHeight / 3) / (vertices - 1));
        infoTable.setPosition(2 * offset + infoTable.getWidth() * i + space * (i + 1), (float) ((camera.viewportHeight * 0.28) - offset - (infoTable.getHeight() + space) * j));

        Label codeLabel = new Label(labelText, fontSkin);
        codeLabel.setAlignment(left);
        codeLabel.setFontScale(0.75f);
        infoTable.add(codeLabel).row();

        return infoTable;
    }

    private TextField createTextField(final String fieldText, final Vector2 start, final Vector2 end, final Edge neighbor, final int[] numOfEdges) {
        //init default
        final TextField textField = new TextField(fieldText, fontSkin);
        textField.setColor(Color.BLACK);
        textField.setAlignment(left);
        textField.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //set connection red as visualization if clicked
                textField.setColor(Color.RED);
                linesToDraw.add(new LineData(start, end, Color.RED));
                return true;
            }
        });
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                String userInput = textField.getText();
                String userInputSubstring = userInput.substring(userInput.length() - 2).trim();
                boolean isCorrect = userInputSubstring.equals(String.valueOf(neighbor.getWeight()));
                //set connection green if correct
                if (key == '\r' || key == '\n') {
                    if (isCorrect && !neighbor.isEdgeAdded()) {
                        game.getDropSound().play();
                        numOfEdges[0]++;
                        neighbor.setEdgeAdded(true);
                        textField.setColor(Color.GREEN);
                        linesToDraw.add(new LineData(start, end, Color.GREEN));
                        textField.setDisabled(true);
                        for (EventListener listener : textField.getListeners()) {
                            textField.removeListener(listener);
                        }
                    } else {
                        handleLevelLost();
                    }
                }
                if (numOfEdges[0] == graph.getNumOfEdges()) {
                    stage.addActor(checkCode);
                }
            }
        });
        return textField;
    }

    public void handleLevelLost() {
        provideNegativeFeedback();
        final int newValue = Integer.parseInt(String.valueOf(mangoCounterLabel.getText())) - 10;
        if (newValue > 0) mangoCounterLabel.setText(newValue);
        else {
            game.getParrotImage().remove();

            final LevelLostGroup lost = new LevelLostGroup(game, camera);
            stage.addActor(lost);

            Button close = (Button) lost.getChild(2);
            close.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setMangos(30);
                    mangoCounterLabel.setText(game.getMangos());
                    game.setScreen(new GameScreen_Level2(game));
                    dispose();
                }
            });
        }
    }

    private void provideNegativeFeedback() {
        game.getBattle().play();
        game.getBlood().addAction(Actions.sequence(
                Actions.fadeOut(1f),
                Actions.removeActor()
        ));
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
        shapeRenderer.dispose();
        stage.dispose();
    }
}
