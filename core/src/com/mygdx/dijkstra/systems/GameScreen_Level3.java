package com.mygdx.dijkstra.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.dijkstra.DijkstraAlgorithm;
import com.mygdx.dijkstra.models.*;
import com.mygdx.dijkstra.views.*;

import java.util.ArrayList;
import java.util.List;

public class GameScreen_Level3 implements Screen {
    private final DijkstraAlgorithm game;
    private static final Color LABEL_COLOR = new Color(0.95f, 0.871f, 0.726f, 1);
    private static final float CELL_WIDTH = 140f;
    private static final float CELL_HEIGHT = 20f;
    private static final float PAD_BOTTOM = 10;
    private String hint;
    private static final String infinity = "INFINITY";
    private Stage stage;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private List<LineData> linesToDraw;
    private FitViewport fitViewport;
    private Image boatImage, parrotImage, infoImage;
    private OrthographicCamera camera;
    private String code = "";
    private Button mainMenuButton, doneButton;
    private ScrollPane dropBox;
    private Table algorithmTable;
    private Graph graph;
    private int[] distances;
    private final ArrayList<Integer> correctNodes = new ArrayList<>();
    private int[][] iterations, precursor;
    private Label mangoCounterLabel;
    private DrawLineOrArrow draw;
    private ArrayList<Integer> dijkstraConnections;
    private Group background;
    private CheckCode checkCode;
    private Group infotext;
    private int correctlyFilledTextFieldsInCurrentRow = 0;
    private final int level;
    private String text;
    private int vertices, space, offset;
    private Batch batch;
    private ArrayList<City> cities;
    private Skin fontSkin;

    public GameScreen_Level3(final DijkstraAlgorithm game, final int level) {

        this.game = game;
        this.level = level;


        initializeEnvironment();
        initializeBackgroundAndMap();
        processDijkstraAndPrepareTable();
        initializeCodeCheck();

        stage.addActor(dropBox);
        stage.addActor(mainMenuButton);
        stage.addActor(boatImage);
        stage.addActor(infotext);
        stage.addActor(doneButton);
    }

    @Override
    public void render(float delta) {
        // tell the SpriteBatch to render in the coordinate system specified by the camera.
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        //clear with background color
        Gdx.gl.glClearColor(0.95f, 0.871f, 0.726f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render background
        batch.begin();
        background.draw(batch, 1.0f); // Render the actors from the selected group
        batch.end();

        //draw lines
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (LineData lineData : linesToDraw) {
            draw.drawArrow(shapeRenderer, 3, lineData.getColor(), lineData.getStart(), lineData.getEnd());
        }
        shapeRenderer.end();

        //render rest of stage
        stage.act();
        stage.draw();
    }

    private void initializeEnvironment() {
        camera = game.getCamera();
        fitViewport = game.getFitViewport();
        stage = new Stage(fitViewport);

        cities = game.getCities();
        vertices = game.getVertices();
        space = game.getSpace();
        offset = game.getOffset();
        batch = game.getBatch();
        fontSkin = game.getFontSkin();

        graph = new Graph(game, vertices, 1);
        GraphAlgorithms dijkstra = new GraphAlgorithms(graph);
        distances = dijkstra.getDistances();
        iterations = dijkstra.getIterations();
        dijkstraConnections = dijkstra.getDijkstraConnections();
        precursor = dijkstra.getPrecursor();

        linesToDraw = new ArrayList<>();
        draw = new DrawLineOrArrow();
    }

    private void initializeBackgroundAndMap() {
        createTextForMode(level);

        background = new BackgroundGroup(game, stage, text, level);
        mainMenuButton = background.findActor("mainMenuButton");
        boatImage = background.findActor("boatImage");
        infotext = background.findActor("infotext");
        parrotImage = background.findActor("parrotImage");
        infoImage = background.findActor("infoImage");
        Table mangoCounter = background.findActor("mangoCounter");
        mangoCounterLabel = (Label) mangoCounter.getChild(1);

        stage.addActor(new MapGroup_Level2_3(game, level, graph, boatImage, linesToDraw));
    }

    private void processDijkstraAndPrepareTable() {

        algorithmTable = new Table(fontSkin);
        algorithmTable.setBackground(fontSkin.getDrawable("color"));
        createTable();

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.background = fontSkin.getDrawable("window-c");
        dropBox = new ScrollPane(algorithmTable, scrollPaneStyle);
        dropBox.setSize(camera.viewportWidth + 20, (float) (camera.viewportHeight * 0.33) + offset);
        dropBox.setPosition(-10, -10);
    }

    private void initializeCodeCheck() {
        buildCode();
        checkCode = new CheckCode(mangoCounterLabel, camera.viewportWidth / 4, camera.viewportHeight / 2, camera.viewportWidth / 2, camera.viewportHeight / 5, code, game, stage, camera, level);
        doneButton = new TextButton("Done", game.getMySkin(), "default");
        doneButton.setSize(4 * offset, (float) (1.5 * offset));
        doneButton.setPosition(offset, camera.viewportHeight - (3 * offset) - mainMenuButton.getHeight());
        doneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addActor(checkCode);
            }
        });
    }

    private void createTextForMode(int level) {
        StringBuilder textBuilder = new StringBuilder();
        switch (level) {
            case 5:
                textBuilder.append("Servus Captain,\n\nThese damn mangooos are hard to get. We need to find the shortest" +
                        " paths to each city from our hometown to get them before the other crews! \nFill out the table below, " +
                        "I will help you! Ultimately, we will know how long it takes us to travel to each city from our treasury" +
                        " and the fastest path!\nI am sure you will find out how it works!\n\nWe will need to find the " +
                        "code to unlock this treasure I have found!\nCode: The code is built by the costs of the connection " +
                        "to the city. For INFINITY, it was a ...... 0");
                break;
            case 6:
                textBuilder.append("Hola, Captain,\n\nYou are getting on it! We are indeed becoming the pirates of the " +
                        "golden paths! Everyone will pay millions to know our secret - but first: Let`s get back to work.\n " +
                        "Do you know what to do? If not, I am here to help. Cause you know - I am the endless source of wisdom.\n" +
                        "And remember the code so we can get endless mangooos!\nIt would be good to" +
                        " note where we are currently at - the precursor, you know? Like that, it is easier for us to find " +
                        "the correct route afterward. Our ship will show you where we currently are.\nCode: The code is " +
                        "now built by the shortages of the cities we passed and the total costs of the connection. ");
                break;
            case 7:
                textBuilder.append("Hello and welcome on board again, Captain, \nThere are still some routes to calculate, " +
                        "but we are getting better. So we will likely find the solution this time too!\nTIP: Remember that we " +
                        "seek the shortest connection from our start city. We are always moving to the fastest available " +
                        "connection to discover new ones. Like that, we will always find the shortest path.\nCode: Remember," +
                        " the code is built by the shortages of the cities we passed and the total costs of the connection.");
                break;
            case 8:
                textBuilder.append("Ay Ay Captain,\n" +
                        "Finally! All our hard work pays off if we can open this last treasure. We can finally retire. " +
                        "Find the last code to unlock the treasure.\nTIP: Remember that we seek the shortest connection " +
                        "from our start city. You can click on the ports, and our ship will move there. Like that, you " +
                        "always know where we currently are!\nCode: Remember, the code is built by the shortages of the" +
                        " cities we passed and the total costs of the connection.");
                break;
        }
        text = textBuilder.toString();
    }

    private String getTopLabelText(int col) {
        return (col == 0) ? "Input" : String.format("%s (%s)", cities.get(col - 1).getName(), cities.get(col - 1).getShortName());
    }

    private void createTable() {
        Table topLabelsTable = createTopLabelsTable();
        algorithmTable.add(topLabelsTable).colspan(vertices + 1);
        algorithmTable.setColor(LABEL_COLOR);
        stage.addActor(algorithmTable);
        addNewRow(0);
    }

    private Table createTopLabelsTable() {
        Table topLabelsTable = new Table(fontSkin);
        topLabelsTable.defaults().width(CELL_WIDTH).height(CELL_HEIGHT).pad(0).space(0);

        for (int col = 0; col < vertices + 1; col++) {
            Label topLabel = new Label(getTopLabelText(col), fontSkin);
            topLabelsTable.add(topLabel).top().padBottom(PAD_BOTTOM);
        }

        topLabelsTable.setColor(LABEL_COLOR);
        return topLabelsTable;
    }

    private void buildCode() {
        if (level > 5) {
            int end = 5;
            int count = 2;
            int precursorLengthAtIndex = precursor[end].length - 1;
            int precursorCode = precursor[precursorLengthAtIndex][end];
            int[] codeArray = new int[end];
            codeArray[codeArray.length - 1] = precursorCode;
            code = cities.get(0).getShortName();
            while (precursorCode != 0) {
                precursorCode = precursor[precursorLengthAtIndex][precursorCode];
                codeArray[codeArray.length - count] = precursorCode;
                count++;
            }
            for (int i : codeArray) if (i != 0) code += " - " + cities.get(i).getShortName();
            code += " - " + distances[end];
        } else {
            for (int i = 0; i < distances.length; i++) {
                if (distances[i] == Integer.MAX_VALUE) distances[i] = 0;
                code += distances[i];
            }
        }
    }

    private void addNewRow(final int iteration) {
        correctlyFilledTextFieldsInCurrentRow = 0;

        Table newRow = new Table();
        newRow.defaults().width(CELL_WIDTH).height(CELL_HEIGHT).pad(0).space(0);
        java.util.List<Edge> neighbors = graph.getNeighbors(dijkstraConnections.get(iteration));

        initializeFirstColumn(newRow);
        initializeColumns(newRow, neighbors, iteration);

        algorithmTable.row();
        algorithmTable.add(newRow).colspan(vertices + 1).padRight(5);
    }

    private void initializeFirstColumn(Table newRow) {
        final TextField textField0 = new TextField(" ", fontSkin);
        textField0.setColor(0, 0, 0, 1);
        textField0.setText("Precursor - Costs");
        newRow.add(textField0).expandX().fillX();

        if (level == 5) {
            textField0.setText("Lowest costs");
            textField0.setDisabled(true);
        }
    }

    private void initializeColumns(Table newRow, List<Edge> neighbors, int iteration) {
        for (int col = 0; col < vertices; col++) {
            TextField textField = new TextField(" ", fontSkin);
            textField.setColor(0, 0, 0, 1);

            int costs = iterations[iteration][col];
            String precursorString = cities.get(precursor[iteration][col]).getShortName();
            String correctValue = buildCorrectValue(costs, precursorString);

            switch (level) {
                case 5:
                case 6:
                    if (costs == Integer.MAX_VALUE || costs == 0 || correctNodes.contains(col))
                        disableTextField(textField, correctValue);
                    break;
                case 7:
                    if (correctNodes.contains(col) || costs == 0) disableTextField(textField, correctValue);
                    break;
                case 8:
                    if (costs == 0) disableTextField(textField, correctValue);
                    break;
            }

            // Modify and add listeners based on the neighboring cities
            textField = addListenersToTextField(textField, correctValue, neighbors, iteration, col);
            newRow.add(textField).expandX().fillX();
        }
    }

    private TextField addListenersToTextField(TextField textField, String correctValue, List<Edge> neighbors, int iteration, int col) {
        City sourceCity = cities.get(dijkstraConnections.get(iteration));
        City destCity = cities.get(col);

        boolean isNeighbor = false;
        for (Edge edge : neighbors) {
            if (edge.getDestination() == col) {
                isNeighbor = true;
                break;
            }
        }

        if (isNeighbor) return addNeighborListener(textField, sourceCity, destCity, correctValue, iteration, true);
        else return addNeighborListener(textField, sourceCity, destCity, correctValue, iteration, false);
    }

    private void disableTextField(TextField textField, String correctValue) {
        correctlyFilledTextFieldsInCurrentRow++;
        textField.setText(correctValue);
        textField.setColor(Color.GREEN);
        textField.setDisabled(true);
        for (EventListener listener : textField.getListeners()) {
            textField.removeListener(listener);
        }
    }

    private TextField addNeighborListener(final TextField textField, final City sourceCity, City destCity, final String finalCorrectValue, final int i, final boolean neighbor) {
        final Vector2 start = new Vector2(sourceCity.getX(), sourceCity.getY());
        final Vector2 end = new Vector2(destCity.getX(), destCity.getY());

        if (level < 8) {
            textField.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    handleTouchDown(textField, sourceCity, neighbor, start, end, finalCorrectValue, i);
                    return true;
                }
            });

            textField.setTextFieldListener(new TextField.TextFieldListener() {
                @Override
                public void keyTyped(TextField textField, char key) {
                    handleKeyTyped(textField, sourceCity, key, neighbor, start, end, finalCorrectValue, i);
                }
            });
        }
        return textField;
    }

    private void handleTouchDown(TextField textField, City sourceCity, final boolean neighbor, final Vector2 start, final Vector2 end, final String finalCorrectValue, final int i) {
        if (neighbor) {
            linesToDraw.add(new LineData(start, end, Color.RED));
            lookForPrecursors(i, start, sourceCity, Color.RED);
            textField.setColor(Color.RED);
        } else {
            if (finalCorrectValue.equals("INFINITY")){
                hint = "I can`t find a route to this city yet, you? Maybe we will find one later";
                stage.addActor(new HintGroup(hint, game));
            }
            else{
                hint = "I can`t find a new connection to this city from " + sourceCity.getName() + ". We may find another one later but lets stick to the one we know.";
                stage.addActor(new HintGroup(hint, game));
            }
        }
    }

    private void handleKeyTyped(TextField textField, City sourceCity, char key, boolean neighbor, Vector2 start, Vector2 end, String finalCorrectValue, int i) {
        if (key == '\r' || key == '\n') {
            String userInput = textField.getText().replaceAll("\\s", "").toLowerCase();
            boolean isCorrect = userInput.equals(finalCorrectValue.trim().replaceAll("\\s", "").toLowerCase());

            if (isCorrect) handleCorrectInput(textField, sourceCity, neighbor, start, end, i);
            else {
                userInput = userInput.replaceAll("[^0-9]", "");
                finalCorrectValue = finalCorrectValue.replaceAll("[^0-9]", "");
                boolean isShortestPath = false;
                if(!userInput.equals(""))isShortestPath = Integer.valueOf(userInput) > Integer.valueOf(finalCorrectValue);
                switch (level) {
                    case 5:
                        if (isShortestPath) hint = "Are you sure you chose the shortest path?";
                        else hint = "Are you sure that you added the costs of the routes correctly";
                        break;
                    case 6:
                        if (isShortestPath) hint = "Are you sure you chose the shortest path?";
                        else hint = "Did you build the value correctly?\n(Shortage of precursor - Added costs)";
                        break;
                    case 7:
                        if (isShortestPath) hint = "Are you sure you chose the shortest path?";
                        else if(userInput.length() >= 2 && userInput.substring(0,2) != finalCorrectValue.substring(0,2)) {
                            hint = "Did you build the value correctly?\n(Shortage of precursor - Added costs)";
                        }
                        else hint = "Is there a route to this city?\nIf there is no route the correct Value is Infinity.";
                        break;
                    default:
                        hint = "To go to our treasury (starting point) we need 0 costs.\nDid you build the value correctly?\n(Shortage of precursor - Added costs)";
                        stage.addActor(new HintGroup(hint, game));
                        break;
                }
                stage.addActor(new HintGroup(hint, game));
                addHintListener(parrotImage);
                addHintListener(infoImage);
            }
        }
    }

    private void addHintListener(Image image){
        image.clearListeners();
        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addActor(new HintGroup(hint, game));
            }
        });
    }

    private void handleCorrectInput(TextField textField, City sourceCity, boolean neighbor, Vector2 start, Vector2 end, int i) {
        game.getDropSound().play();
        textField.setColor(Color.GREEN);
        if (level < 8) {
            if (neighbor) {
                linesToDraw.add(new LineData(start, end, Color.GREEN));
                lookForPrecursors(i, start, sourceCity, Color.GREEN);
            }
            textField.setDisabled(true);
            for (EventListener listener : textField.getListeners()) textField.removeListener(listener);
            checkIfNewRow(i);
        }
    }

    private void lookForPrecursors(int i, Vector2 start, City sourceCity, Color color) {
        if (i <= 0) {
            return; // No precursors to look for
        }

        int sourceCityIndex = cities.indexOf(sourceCity);

        while (i > 0) {
            int precursorIndex = precursor[i - 1][sourceCityIndex];
            City precursorCity = cities.get(precursorIndex);

            Vector2 precursorPosition = new Vector2(precursorCity.getX(), precursorCity.getY());
            linesToDraw.add(new LineData(precursorPosition, start, color));

            start = precursorPosition;
            sourceCityIndex = precursorIndex;
            i--;
        }
    }

    private void checkIfNewRow(int iteration) {

        // Check if we have filled all the fields in the current row
        correctlyFilledTextFieldsInCurrentRow++;
        if (correctlyFilledTextFieldsInCurrentRow != vertices) return;

        if (level == 8)
            stage.addActor(new HintGroup("Great job!\nLet's move (click) to the city with the current lowest costs! Like that we will always find the shortest Path", game));
        else if (iteration + 1 < dijkstraConnections.size() - 1) moveBoatToNextCity(iteration);
        else {
            for (LineData line : linesToDraw) line.setColor(Color.GREEN);
            stage.addActor(checkCode);
        }
    }

    private void moveBoatToNextCity(int iteration) {
        City nextCity = cities.get(dijkstraConnections.get(iteration + 1));
        if (level < 8) {
            String hintText = "Let's move to " + nextCity.getName() + ", it has the current lowest costs! Like that we will always find the shortest Path";
            stage.addActor(new HintGroup(hintText, game));
            boatImage.addAction(Actions.moveTo(nextCity.getX() - boatImage.getHeight() / 2,
                    nextCity.getY() - boatImage.getHeight() / 2 + offset,
                    1.5f));
        }
        correctNodes.add(dijkstraConnections.get(iteration + 1));
        addNewRow(iteration + 1);
    }

    public String buildCorrectValue(int costs, String precursorString) {
        if (level == 5) {
            if (costs == Integer.MAX_VALUE) return infinity;
            else if (costs == 0) return " 0";
            else return String.valueOf(costs);
        } else {
            if (costs == Integer.MAX_VALUE) return infinity;
            else if (costs == 0) return precursorString + " - 0";
            else return precursorString + " - " + costs;
        }
    }

    @Override
    public void resize(int width, int height) {
        fitViewport.update(width, height, true);
        camera.position.set((float) 1200 / 2, (float) 720 / 2, 0);
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