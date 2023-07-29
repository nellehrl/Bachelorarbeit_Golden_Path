package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;;
import java.util.List;

import static com.badlogic.gdx.utils.Align.left;

public class GameScreen_Level2 implements Screen {
    final DijkstraAlgorithm game;
    Image boatImage;
    ShapeRenderer shapeRenderer  = new ShapeRenderer();
    Table mangoCounter;
    Label mangoCounterLabel;
    OrthographicCamera camera;
    private final FitViewport fitViewport;
    ArrayList<City> currentConnection = new ArrayList<>();
    private Stage stage;
    Button mainMenuButton, closeButton;
    private List<LineData> linesToDraw;
    DropBoxWindow dropBox;
    Group background;
    Graph connections;
    String text;
    InfoTextGroup infotext;
    DrawLineOrArrow draw;

    public GameScreen_Level2(final DijkstraAlgorithm game) {
        //init game & camera
        this.game = game;
        game.createCities(game.allCities);
        game.getCities();

        //init camera
        camera = game.camera;
        fitViewport = game.fitViewport;
        stage = new Stage(fitViewport);

        //init cities and starting point
        currentConnection.add(game.cities.get(0));
        connections = new Graph(game.vertices, 1);

        //init Background & text
        initializeBackground();
        initializeText();
        dropBoxItems();
        linesToDraw = new ArrayList<>();
        draw = new DrawLineOrArrow();

        //init connections&ports
        stage.addActor(new MapGroup_Level2_Level3(game, 0, connections, boatImage, linesToDraw));

        //init infotext
        infotext = new InfoTextGroup(game, text);
        closeButton = infotext.closeButton;
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                infotext.remove();
                int parrotWidth = (int) (camera.viewportWidth * 0.1);
                game.parrotImage.setSize((float) parrotWidth, (float) (parrotWidth * 1.25));
                game.parrotImage.setPosition((float) (Gdx.graphics.getWidth() - parrotWidth - game.offset), (camera.viewportHeight / 3 - game.space));
                game.infoImage.setSize((float) (camera.viewportWidth*0.025), (float) (camera.viewportWidth*0.025));
                game.infoImage.setPosition(game.parrotImage.getX() - game.space, game.parrotImage.getY() + game.parrotImage.getHeight());
                game.parrotImage.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        stage.addActor(infotext);
                        game.parrotImage.remove();
                        game.infoImage.remove();
                    }
                });
                stage.addActor(game.infoImage);
                stage.addActor(game.parrotImage);
            }
        });

        //add remaining actors
        stage.addActor(boatImage);
        stage.addActor(mainMenuButton);
        stage.addActor(infotext);
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
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        background.draw(game.batch, 1.0f);
        game.batch.end();

        // Render the connections (lines)
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (LineData lineData : linesToDraw) {
            draw.drawArrow(shapeRenderer,3,  lineData.getColor(), lineData.getStart(), lineData.getEnd());
        }
        shapeRenderer.end();

        // Draw the stage
        stage.draw();
    }

    public void initializeBackground() {
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
    }

    public void initializeText() {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("You know who this is, Captain!\n\n");
        textBuilder.append("Fantastic job so far! We updated our system. Now that you have added ");
        textBuilder.append("the right weight to the connections, we have an excellent overview!\n\n");
        textBuilder.append("Let`s check if everything is clear - find the weights for the connections written down below.\n\n");
        textBuilder.append("We must to understand how these graphs work to get ahead of the other crews. Like that, we can generate ");
        textBuilder.append("much more loooooooooooooooot!\n\n");
        textBuilder.append("Remember to ENTER to check if you got the costs correct!");
        text = textBuilder.toString();
    }

    public void dropBoxItems() {
        final int[] numOfEdges = {0};
        for (int i = 0; i < game.vertices; i++) {
            //init j for positioning items correctly
            int j = 0;
            java.util.List<Edge> neighbors = connections.getNeighbors(i);
            City sourceCity = game.cities.get(i);
            //init table to fill
            for (Edge neighbor : neighbors) {
                //define destination City and connection to color
                City destCity = game.cities.get(neighbor.destination);
                final Vector2 start = new Vector2(sourceCity.x, sourceCity.y);
                final Vector2 end = new Vector2(destCity.x, destCity.y);
                //init label
                String labelText = sourceCity.name + " - " + destCity.name;
                String fieldText = "Costs: ";
                //init table
                Table infoTable = createInfoTable(labelText, i, j);
                TextField textField = createTextField(fieldText, start, end, neighbor, numOfEdges);
                //add table to stage
                infoTable.add(textField);
                infoTable.setBackground(game.fontSkin.getDrawable("color"));
                stage.addActor(infoTable);
                j++;
            }
        }
    }

    private Table createInfoTable(String labelText, int i, int j) {
        Table infoTable = new Table(game.fontSkin);
        infoTable.setSize((camera.viewportWidth) / (game.vertices), 50);
        infoTable.setPosition(2 * game.offset + infoTable.getWidth() * i + game.space * (i + 1), (float) ((camera.viewportHeight * 0.28) - game.offset - (infoTable.getHeight() + game.space) * j));

        Label codeLabel = new Label(labelText, game.fontSkin);
        codeLabel.setAlignment(left);
        codeLabel.setFontScale(0.75f);
        infoTable.add(codeLabel).row();

        return infoTable;
    }

    private TextField createTextField(final String fieldText, final Vector2 start, final Vector2 end, final Edge neighbor, final int[] numOfEdges) {
        //init default
        final TextField textField = new TextField(fieldText, game.fontSkin);
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
                boolean isCorrect = userInput.equals(fieldText + neighbor.weight);
                //set connection green if correct
                if (key == '\r' || key == '\n') {
                    if (isCorrect && !neighbor.isEdgeAdded()) {
                        game.dropSound.play();
                        numOfEdges[0]++;
                        neighbor.setEdgeAdded(true);
                        textField.setColor(Color.GREEN);
                        linesToDraw.add(new LineData(start, end, Color.GREEN));
                    } else {
                        levelLost();
                    }
                }
                if (numOfEdges[0] == connections.numOfEdges) {
                    game.setScreen(new LevelWonScreen(game, 2));
                    dispose();
                }
            }
        });
        return textField;
    }

    public void levelLost() {
        GameScreen_Level1.negativeFeedbackLoop(game, camera, stage);
        final int newValue = Integer.parseInt(String.valueOf(mangoCounterLabel.getText())) - 10;
        if (newValue > 0) mangoCounterLabel.setText(newValue);
        else {
            game.parrotImage.remove();

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
