package com.mygdx.dijkstra.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.mygdx.dijkstra.DijkstraAlgorithm;
import com.mygdx.dijkstra.models.*;
import com.mygdx.dijkstra.views.*;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.utils.Align.left;

public class GameScreen_Level2 implements Screen {
    private final DijkstraAlgorithm game;
    private Stage stage;
    private Image boatImage;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private Label mangoCounterLabel;
    private Button mainMenuButton;
    private Group background;
    private Group infoText;
    private final List<LineData> linesToDraw = new ArrayList<>();
    private Graph graph;
    private String text;
    private DrawLineOrArrow draw;
    private CheckCode checkCode;

    public GameScreen_Level2(final DijkstraAlgorithm game) {
        this.game = game;
        initializeVariables();
        initializeUIElements();
        initializeGameComponents();
    }

    @Override
    public void render(float delta) {
        // Update the game.getCamera() and stage
        game.getCamera().update();
        stage.act(delta);

        // Clear the screen
        Gdx.gl.glClearColor(0.95f, 0.871f, 0.726f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render the background
        game.getBatch().setProjectionMatrix(game.getCamera().combined);
        game.getBatch().begin();
        background.draw(game.getBatch(), 1.0f);
        game.getBatch().end();

        // Render the graph (lines)
        shapeRenderer.setProjectionMatrix(game.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (LineData lineData : linesToDraw) {
            draw.drawArrow(shapeRenderer, 3, lineData.getColor(), lineData.getStart(), lineData.getEnd());
        }
        shapeRenderer.end();

        // Draw the stage
        stage.draw();
    }

    private void initializeVariables() {
        stage = new Stage(game.getFitViewport());
        graph = new Graph(game.getVertices(),false);
    }

    private void initializeUIElements() {
        initializeText();
        background = new BackgroundGroup(game, stage, text, 4);
        mainMenuButton = background.findActor("mainMenuButton");
        boatImage = background.findActor("boatImage");
        infoText = background.findActor("infotext");
        Table mangoCounter = background.findActor("mangoCounter");
        mangoCounterLabel = (Label) mangoCounter.getChild(1);
        initializeDropBoxItems();
    }

    private void initializeGameComponents() {
        draw = new DrawLineOrArrow();
        stage.addActor(new MapGroup_Level2_3(game,0, graph, boatImage, linesToDraw));
        checkCode = new CheckCode(mangoCounterLabel,game.getCamera().viewportWidth / 4, game.getCamera().viewportHeight / 2, (float) (game.getCamera().viewportWidth * 0.6), game.getCamera().viewportHeight/5, "code", game, stage, 4);
        
        stage.addActor(boatImage);
        stage.addActor(mainMenuButton);
        stage.addActor(infoText);
    }

    public void initializeText() {
        text = "You know who this is, Captain!\n\nFantastic job so far - we finally updated our system. Now that you have " +
                "added the correct costs to the connections, we have an excellent overview!\nLet`s check if everything is clear - " +
                "find the costs for the connections written on the radar. Can you see them?\nWe must get ahead of the other crews. " +
                "Like that, we can get much more loooooooooooooooot and mangos!\n\nP.S:Click in a text field to start and remember to ENTER to check if you got the costs correct!";
    }

    public void initializeDropBoxItems() {
        final int[] numOfEdges = {0};
        int count = 0;
        for (int i = 0; i < game.getVertices(); i++) {

            int j = 0;
            java.util.List<Edge> neighbors = graph.getNeighbors(i);
            City sourceCity = game.getCities().get(i);

            if(neighbors.size() != 0) {
                for (Edge neighbor : neighbors) {

                    City destCity = game.getCities().get(neighbor.getDestination());
                    final Vector2 start = new Vector2(sourceCity.getX(), sourceCity.getY());
                    final Vector2 end = new Vector2(destCity.getX(), destCity.getY());

                    String labelText = sourceCity.getName() + " - " + destCity.getName();
                    String fieldText = "Costs: ";

                    Table infoTable = createInfoTable(labelText, count, j);
                    TextField textField = createTextField(fieldText, start, end, neighbor, numOfEdges);

                    infoTable.add(textField);
                    infoTable.setBackground(game.getFontSkin().getDrawable("color"));
                    stage.addActor(infoTable);
                    j++;
                }
                count++;
            }
        }
    }

    private Table createInfoTable(String labelText, int i, int j) {
        Table infoTable = new Table(game.getFontSkin());
        infoTable.setSize((game.getCamera().viewportWidth) / (game.getVertices()+1), (game.getCamera().viewportHeight / 3) / (game.getVertices() - 1));
        infoTable.setPosition(2 * game.getOffset() + infoTable.getWidth() * i + game.getSpace() * (i + 1), (float) ((game.getCamera().viewportHeight * 0.25) - game.getOffset() - (infoTable.getHeight() + game.getSpace()) * j));

        Label codeLabel = new Label(labelText, game.getFontSkin());
        codeLabel.setAlignment(left);
        codeLabel.setFontScale(0.75f);
        infoTable.add(codeLabel).row();

        return infoTable;
    }

    private TextField createTextField(final String fieldText, final Vector2 start, final Vector2 end, final Edge neighbor, final int[] numOfEdges) {
        //init default
        final TextField textField = new TextField(fieldText, game.getFontSkin());
        textField.setColor(Color.BLACK);
        textField.setAlignment(left);

        ConnectionHoverActor card = new ConnectionHoverActor(game, (start.x + end.x) / 2, (end.y + start.y) / 2,
                150, 60, game.getCities().get(neighbor.getSource()).getName(), game.getCities().get(neighbor.getDestination()).getName(), neighbor.getWeight());
        final Table cardTableFinal = card.getTable();
        final Image connectionArea = new ConnectionAreaImage(start, end, stage, cardTableFinal, true);

        textField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (!focused) { 
                    connectionArea.remove();
                    textField.setColor(Color.DARK_GRAY);
                    linesToDraw.add(new LineData(start, end, Color.DARK_GRAY));
                }
            }
        });

        textField.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                textField.setColor(Color.RED);
                stage.addActor(connectionArea);
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
                        connectionArea.remove();
                    } else {
                        new WrongUserInput(mangoCounterLabel,game, stage, 4);
                    }
                }
                if (numOfEdges[0] == graph.getNumOfEdges()) {
                    stage.addActor(checkCode);
                }
            }
        });
        return textField;
    }

    @Override
    public void resize(int width, int height) {
        game.getFitViewport().update(width, height, true);
        game.getCamera().position.set((float) 1200 / 2, (float) 720 / 2, 0);
    }

    @Override
    public void show() {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
