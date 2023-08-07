package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.*;
import java.util.List;

import static com.badlogic.gdx.utils.Align.left;

public class GameScreen_Level3 implements Screen {
    final DijkstraAlgorithm game;
    private static final String infinity = "INFINITY";
    private final Stage stage;
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final List<LineData> linesToDraw;
    private final FitViewport fitViewport;
    Image boatImage;
    OrthographicCamera camera;
    ScrollPane.ScrollPaneStyle scrollPaneStyle;
    String code = "";
    Button mainMenuButton, doneButton;
    ScrollPane dropBox;
    Table algorithmTable;
    Graph connections;
    int[] distances;
    ArrayList<Integer> correctNodes = new ArrayList<>();
    int[][] iterations, precursor;
    DrawLineOrArrow draw;
    ArrayList<Integer> dijkstraConnections;
    Group background;
    CheckCode checkCode;
    InfoTextGroup infotext;
    private int correctlyFilledTextFieldsInCurrentRow = 0;
    int level;
    float cellWidth = 140f, cellHeight = 20f;
    String text;

    public GameScreen_Level3(final DijkstraAlgorithm game, final int level) {

        //init variables
        this.game = game;
        this.level = level;
        connections = new Graph(game.vertices, 1);
        linesToDraw = new ArrayList<>();
        int row_height = game.offset;
        int col_width = 2 * game.offset;

        //init camera
        camera = game.camera;
        fitViewport = game.fitViewport;
        stage = new Stage(fitViewport);

        //init Arrays
        distances = new int[game.vertices];
        iterations = new int[game.vertices][game.vertices];
        dijkstraConnections = new ArrayList<>();
        draw = new DrawLineOrArrow();

        //init background
        createTextForMode(level);
        initializeBackground();
        stage.addActor(new MapGroup_Level2_3(game, level, connections, boatImage, linesToDraw));

        //init table
        algorithmTable = new Table(game.fontSkin);
        algorithmTable.setBackground(game.fontSkin.getDrawable("color"));

        //dijkstra Algorithm
        dijkstra();
        createTable();

        //init ScrollPane
        scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.background = game.fontSkin.getDrawable("window-c");
        dropBox = new ScrollPane(algorithmTable, scrollPaneStyle);
        dropBox.setWidth(camera.viewportWidth + 20);
        dropBox.setHeight((float) (camera.viewportHeight * 0.33) + 25);
        dropBox.setPosition(-10, -10);

        //init code
        buildCode();

        //check if dijkstra is correct
        checkCode = new CheckCode(camera.viewportWidth / 4, camera.viewportHeight / 2, camera.viewportWidth / 2, 150, code, game, stage, camera, level);
        doneButton = new TextButton("Done", game.mySkin, "default");
        doneButton.setSize((float) (2 * col_width), (float) (1.5 * row_height));
        doneButton.setPosition(3 * game.space, (float) (camera.viewportHeight - (1.5 * row_height) - mainMenuButton.getHeight() - 3 * game.space));
        doneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addActor(checkCode);
            }
        });
        stage.addActor(doneButton);

        // Add remaining actors to stage
        stage.addActor(dropBox);
        stage.addActor(mainMenuButton);
        stage.addActor(boatImage);
        stage.addActor(infotext);
    }

    @Override
    public void render(float delta) {
        // tell the SpriteBatch to render in the coordinate system specified by the camera.
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        //clear with background color
        Gdx.gl.glClearColor(0.95f, 0.871f, 0.726f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render background
        game.batch.begin();
        background.draw(game.batch, 1.0f); // Render the actors from the selected group
        game.batch.end();

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

    private void createTextForMode(int level) {
        StringBuilder textBuilder = new StringBuilder();
        switch (level) {
            case 5:
                textBuilder = new StringBuilder();
                textBuilder.append("Servus Captain,\n\nThese damn mangooos are hard to get. :D To get them before the other crews we need to find ");
                textBuilder.append("the shortest paths to each city from our hometown! \n\nFill out the table below, I will help you! ");
                textBuilder.append("In the end, we will know how long it takes us to travel to each city from our treasury and the fastest path!\n");
                textBuilder.append("I am sure you will find out how it works!\n\nWe will need to find the code to unlock this treasure I have found!\n\n");
                textBuilder.append("\n\nCode: The code is built by the distance of the connection to the city. For INFINITY, it was a ...... 0");
                text = textBuilder.toString();
                break;
            case 6:
                textBuilder.append("Hola Captain, \n\nYou are getting on it! We are surely becoming the pirates of the golden paths! Everyone will pay");
                textBuilder.append(" millions to know our secret - but first: Let`s get back to work\n\n");
                textBuilder.append("I guess you know what to do? If not, I am here to help. Cause you know - I am the endless source ");
                textBuilder.append("of wisdom.\n And don`t forget about the code so we can get an endless amount of mangooos!\n\n");
                textBuilder.append("Oh, and I have realized that it would be good to note down where we are always coming from...the precursors you know?");
                textBuilder.append(" Like that, it is easier for us to find the correct route afterward. Our ship will show you where we currently are.");
                textBuilder.append("\n\nCode: Remember the code is built by the distance of the connection to the city. For INFINITY, ");
                textBuilder.append("it was a ...... 0");
                text = textBuilder.toString();
                break;
            case 7:
                textBuilder.append("Hello and welcome on board again Captain, \n\nThere are still some routes to calculate but we ");
                textBuilder.append("are getting better. So I am sure we will find the solution this time too!\n\n");
                textBuilder.append("TIP: Remember that we seek for the shortest connection from our start city. We are always moving to the shortest available connection to discover new ones.");
                textBuilder.append(" Like that, we will always find the shortest path.\n\n");
                textBuilder.append("Code: Remember the code is built by distance of the connection to the city. For INFINITY, it was a ...... 0");
                text = textBuilder.toString();
                break;
            case 8:
                textBuilder.append("Ay Ay Captain, \n\nFinally! I think all our hard work pays off if we can open this last treasure. We can finally retire.\n");
                textBuilder.append("\nFind the last code to unlock the treasure.\n\n");
                textBuilder.append("TIP: Remember that we seek for the shortest connection from our start city. You can click on the ports and our ship will move there.");
                textBuilder.append(" Like that, you always know where we currently are!\n\n");
                textBuilder.append("Code: Remember the code is built by the distance of the connection to the city. For INFINITY, it was a ...... 0");
                text = textBuilder.toString();
                break;
        }

    }

    private void initializeBackground() {
        background = new BackgroundGroup(game, stage, text);
        for (Actor actor : background.getChildren()) {
            if (actor.getName().equals("mainMenuButton")) {
                mainMenuButton = (Button) actor;
                mainMenuButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        switch (level) {
                            case 5:
                                game.currentLevel = 5;
                                break;
                            case 6:
                                game.currentLevel = 6;
                                break;
                            case 7:
                                game.currentLevel = 7;
                                break;
                            case 8:
                                game.currentLevel = 8;
                                break;
                        }
                        game.setScreen(new MainMenuScreen(game, game.currentLevel));
                        dispose();
                    }
                });
            } else if (actor.getName().equals("boatImage")) boatImage = (Image) actor;
            else if (actor.getName().equals("infotext")) infotext = (InfoTextGroup) actor;
        }
    }

    public void dijkstra() {
        int count = 0, source = 0;
        boolean[] visited = new boolean[game.vertices];

        Arrays.fill(distances, Integer.MAX_VALUE); //fill distances with infinity to initialize it
        distances[source] = 0; //set distance to zero at source node

        //got the priorityQueue idea from chatgpt conversation on the 13th of July 3.32
        Comparator<Node> nodeComparator = new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                return Integer.compare(node1.distance, node2.distance);
            }
        };
        PriorityQueue<Node> minHeap = new PriorityQueue<>(nodeComparator);
        precursor = new int[game.vertices][game.vertices];
        minHeap.add(new Node(source, 0));

        //loop until all connections are found
        while (!minHeap.isEmpty()) {
            //get the node with the lowest distances + current node
            Node currentNode = minHeap.poll();
            int vertex = currentNode.vertex;
            if (!dijkstraConnections.contains(vertex)) dijkstraConnections.add(vertex);

            // if node has already been visited go to next one otherwise set visited
            if (visited[vertex]) continue;
            visited[vertex] = true;

            //get all neighbors (possible conections) for vertex --> class see below
            List<Edge> neighbors = connections.getNeighbors(vertex);

            //Calculate distance to each node and add to queue (which orders them then by distance)
            for (Edge edge : neighbors) {
                int neighbor = edge.getDestination();
                int weight = edge.getWeight();

                if (!visited[neighbor]) {
                    int newDistance = distances[vertex] + weight;

                    //check if there has already been a shorter connection via a different node
                    if (newDistance < distances[neighbor]) {
                        for (int i = count; i < game.vertices; i++) {
                            precursor[i][neighbor] = vertex;
                        }
                        distances[neighbor] = newDistance;
                        minHeap.add(new Node(neighbor, newDistance));
                    }
                }
            }
            System.arraycopy(distances, 0, iterations[count], 0, game.vertices);
            count++;
        }
    }

    private String getTopLabelText(int col) {
        if (col == 0) {
            return "Input";
        } else {
            City city = game.cities.get(col - 1);
            return city.name + "(" + city.shortName + ")";
        }
    }

    private void createTable() {
        // Create a nested table for the top row labels
        Table topLabelsTable = new Table(game.fontSkin);
        topLabelsTable.defaults().width(cellWidth).height(cellHeight).pad(0).space(0);

        // Create and add the top label to the cell table
        for (int col = 0; col < game.vertices + 1; col++) {
            Label topLabel = new Label(getTopLabelText(col), game.fontSkin);
            topLabelsTable.add(topLabel).top().padBottom(10);
            topLabelsTable.setColor(0.95f, 0.871f, 0.726f, 1);
        }

        //create algorithmTable default
        algorithmTable.add(topLabelsTable).colspan(game.vertices + 1);
        algorithmTable.setColor(0.95f, 0.871f, 0.726f, 1);
        algorithmTable.setPosition((float) 800 / 2, camera.viewportHeight / 6);
        stage.addActor(algorithmTable);
        addNewRow(0);
    }

    private void buildCode() {
        if (level > 5) {
            int end = 5;
            int precursorCode = precursor[precursor[end].length - 1][end];
            int[] codeArray = new int[end];
            codeArray[codeArray.length - 1] = precursorCode;
            code = game.cities.get(0).shortName;
            int count = 2;
            while (precursorCode != 0) {
                precursorCode = precursor[precursor[precursorCode].length - 1][precursorCode];
                codeArray[codeArray.length - count] = precursorCode;
                count++;
            }
            for (int i : codeArray) {
                if (i != 0) code += " - " + game.cities.get(i).shortName;
            }
            code += " - " + distances[end];
        } else {
            for (int i = 0; i < distances.length; i++) {
                if (distances[i] == Integer.MAX_VALUE) distances[i] = 0;
                code += distances[i];
            }
        }
    }

    private void addNewRow(final int iteration) {
        //helpVariable to know when to generate a new row
        correctlyFilledTextFieldsInCurrentRow = 0;

        // Create a new table for the row
        Table newRow = new Table();
        newRow.defaults().width(cellWidth).height(cellHeight).pad(0).space(0);

        java.util.List<Edge> neighbors = connections.getNeighbors(dijkstraConnections.get(iteration));

        //init first col
        final TextField textField0 = new TextField(" ", game.fontSkin);
        textField0.setColor(0, 0, 0, 1);
        textField0.setText("Precursor - Costs");
        newRow.add(textField0).expandX().fillX();

        //fill rest of cols
        for (int col = 0; col < game.vertices; col++) {
            TextField textField = new TextField(" ", game.fontSkin);
            textField.setColor(0, 0, 0, 1);
            int costs = iterations[iteration][col];
            String precursorString = game.cities.get(precursor[iteration][col]).shortName;
            String correctValue = buildCorrectValue(costs, precursorString);
            final int sourceCityIndex = dijkstraConnections.get(iteration);
            final City sourceCity = game.cities.get(sourceCityIndex);
            final City destCity = game.cities.get(col);
            boolean filled = false;

            //define level of help
            switch (level) {
                case 5:
                    textField0.setText("Lowest costs");
                    correctValue = String.valueOf(costs);
                    if (costs == Integer.MAX_VALUE) correctValue = "INFINITY";
                    textField0.setDisabled(true);
                case 6:
                    if (costs == Integer.MAX_VALUE || costs == 0 || correctNodes.contains(col)) {
                        correctlyFilledTextFieldsInCurrentRow++;
                        textField.setText(correctValue);
                        textField.setColor(Color.GREEN);
                        textField.setDisabled(true);
                        for (EventListener listener : textField.getListeners()) {
                            textField.removeListener(listener);
                        }
                    }
                    break;
                case 7:
                    if (correctNodes.contains(col) || costs == 0) {
                        correctlyFilledTextFieldsInCurrentRow++;
                        textField.setText(correctValue);
                        textField.setColor(Color.GREEN);
                        textField.setDisabled(true);
                        for (EventListener listener : textField.getListeners()) {
                            textField.removeListener(listener);
                        }
                    }
                    break;
                case 8:
                    if (costs == 0) {
                        correctlyFilledTextFieldsInCurrentRow++;
                        textField.setText(correctValue);
                        textField.setDisabled(true);
                        for (EventListener listener : textField.getListeners()) {
                            textField.removeListener(listener);
                        }
                    }
                    break;
            }

            //add listeners if city is a neighbor
            for (Edge edge : neighbors) {
                if (edge.getDestination() == col) {
                    filled = true;
                    textField = addNeighborListener(textField, sourceCity, destCity, correctValue, iteration, true);
                }
            }
            //add Listener if City isn`t a neighbor
            if (!filled)
                textField = addNeighborListener(textField, sourceCity, destCity, correctValue, iteration, false);
            newRow.add(textField).expandX().fillX();
        }
        algorithmTable.row();
        algorithmTable.add(newRow).colspan(game.vertices + 1).padRight(5);
    }

    private TextField addNeighborListener(final TextField textField, final City sourceCity, City destCity,
                                          final String finalCorrectValue, final int i, final boolean neighbor) {

        final Vector2 start = new Vector2(sourceCity.x, sourceCity.y);
        final Vector2 end = new Vector2(destCity.x, destCity.y);

        // ClickListener for the text field
        if (level < 8) {
            textField.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (neighbor) {
                        linesToDraw.add(new LineData(start, end, Color.RED));
                        lookForPrecursors(i, start, sourceCity, Color.RED);
                        textField.setColor(Color.RED);
                    } else {
                        String hintText = "";
                        if(finalCorrectValue == "INFINITY") hintText = "There is no route to this city yet. Maybe we will find one later";
                        else hintText = "There is no new connection available to this city from " + sourceCity.name + ". We" +
                                " may find another one but lets stick to the one we know.";
                        displayHint(hintText);
                    }
                    return true;
                }
            });
        }
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                String userInput = textField.getText();
                userInput = userInput.replaceAll("\\s", "").toLowerCase();
                boolean isCorrect = userInput.trim().equals(finalCorrectValue.trim().replaceAll("\\s", "").toLowerCase());
                if (key == '\r' || key == '\n') {
                    // Handle correct input
                    if (level < 8) {
                        if (isCorrect) {
                            game.dropSound.play();
                            if (neighbor) {
                                linesToDraw.add(new LineData(start, end, Color.GREEN));
                                lookForPrecursors(i, start, sourceCity, Color.GREEN);
                            }
                            textField.setColor(Color.GREEN);
                            for (EventListener listener : textField.getListeners()) textField.removeListener(listener);
                            textField.setDisabled(true);
                            checkIfNewRow(i);
                        }
                        //handle incorrect input && display hint box
                        else {
                            String hintText = "";
                            switch (level) {
                                case 5:
                                    hintText = "Hint: We are always looking for the shortest path from our treasury!";
                                    break;
                                case 6:
                                    hintText = "Hint: We are always looking for the shortest path from our treasury!\n" +
                                            "This is how you build the value:\nShortage of precursor - Added costs";
                                    break;
                                case 7:
                                    hintText = "This is how you build the value:\nShortage of precursor - Added costs";
                                    break;
                                case 8:
                                    hintText = "To our treasury(starting point) we need 0 costs.\n" +
                                            "This is how you build the value:\nShortage of precursor - Added costs";
                                    break;
                            }
                            displayHint(hintText);
                        }
                    }
                    else{
                        game.dropSound.play();
                        checkIfNewRow(i);
                    }
                }
            }
        });
        return textField;
    }

    private void displayHint(String hintText) {
        //create Table for organization
        Table textBoxTable = new Table(game.fontSkin);
        textBoxTable.setSize(camera.viewportWidth / 4 + 2 * game.space, camera.viewportHeight / 10);
        textBoxTable.setPosition(game.parrotImage.getX() - textBoxTable.getWidth() - game.space,
                game.parrotImage.getY() + game.parrotImage.getHeight(), left);
        Drawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(game.assetManager.get("white 1.png", Texture.class)));
        textBoxTable.setBackground(backgroundDrawable);

        //create label
        Label textBox = new Label(hintText, game.fontSkin);
        textBox.setPosition(game.parrotImage.getX() - textBoxTable.getWidth(),
                game.parrotImage.getY() + game.parrotImage.getHeight(), left);
        textBox.setFontScale(0.7f);
        textBox.setAlignment(left);
        textBox.setWrap(true);
        textBox.setWidth(camera.viewportWidth / 4);
        stage.addActor(textBoxTable);
        stage.addActor(textBox);

        //Let textbox disappear after 5 seconds
        textBoxTable.addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.fadeOut(1f),
                Actions.removeActor()
        ));
        textBox.addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.fadeOut(1f),
                Actions.removeActor()
        ));
    }

    private void lookForPrecursors(int i, Vector2 start, City sourceCity, Color color) {
        if (i - 1 >= 0) {
            int precursorIndex = i;
            while (precursorIndex != 0) {
                precursorIndex = precursor[i - 1][game.cities.indexOf(sourceCity)];
                City precursorCity = game.cities.get(precursorIndex);
                Vector2 precursor = new Vector2(precursorCity.x, precursorCity.y);
                linesToDraw.add(new LineData(precursor, start, color));
                start = precursor;
                sourceCity = precursorCity;
                i--;
            }
        }
    }

    private void checkIfNewRow(int iteration) {
        correctlyFilledTextFieldsInCurrentRow++;
        if(correctlyFilledTextFieldsInCurrentRow == game.vertices && level == 8){
            String hintText = "Let`s move (click) to the city with the current lowest costs! Like that we will always find the shortest Path";
            displayHint(hintText);
        }
        if (correctlyFilledTextFieldsInCurrentRow == game.vertices) {
            if (iteration + 1 < dijkstraConnections.size() - 1) {
                City nextCity = game.cities.get(dijkstraConnections.get(iteration + 1));
                if (level < 8){
                    String hintText = "Let`s move to " + nextCity.name + ", it has the current lowest costs! Like that we will always find the shortest Path";
                    displayHint(hintText);
                    boatImage.addAction(Actions.moveTo(nextCity.x - boatImage.getHeight() / 2, nextCity.y -
                            boatImage.getHeight() / 2 + game.offset, 1.5f));
                }
                correctNodes.add(dijkstraConnections.get(iteration + 1));
                addNewRow(iteration + 1);
            } else {
                for (LineData line : linesToDraw) {
                    line.setColor(Color.GREEN);
                }
                stage.addActor(checkCode);
            }
        }
    }

    public String buildCorrectValue(int costs, String precursorString) {
        if (costs == Integer.MAX_VALUE) {
            return infinity;
        } else if (costs == 0) {
            return precursorString + " - 0";
        } else {
            return precursorString + " - " + costs;
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
