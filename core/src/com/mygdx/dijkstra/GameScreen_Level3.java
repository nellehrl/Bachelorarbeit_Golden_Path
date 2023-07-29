package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.*;
import java.util.List;

import static com.badlogic.gdx.utils.Align.left;

public class GameScreen_Level3 implements Screen {
    final DijkstraAlgorithm game;
    private final Stage stage;
    private final List<LineData> linesToDraw;
    private final FitViewport fitViewport;
    Image boatImage, connectionArea, parrottimage;
    OrthographicCamera camera;
    ScrollPane.ScrollPaneStyle scrollPaneStyle;
    String code = "";
    Button mainMenuButton, doneButton;
    ScrollPane dropBox;
    Table algorithmTable, portTable;
    Graph connections;
    int[] distances;
    ArrayList<Integer> correctNodes = new ArrayList<>();
    int[][] iterations, precursor;
    ArrayList<Integer> dijkstraConnections;
    Group background;
    checkCode checkCode;
    InfoTextGroup infotext;
    Button closeButton;
    private int correctlyFilledTextFieldsInCurrentRow = 0;
    int mode;
    float cellWidth = 140f, cellHeight = 20f;

    public GameScreen_Level3(final DijkstraAlgorithm game, final int mode) {

        //init variables
        this.game = game;
        this.mode = mode;
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

        //init background
        background = new BackgroundGroup(game);
        for (Actor actor : background.getChildren()) {
            if (actor.getName().equals("mainMenuButton")) {
                mainMenuButton = (Button) actor;
                mainMenuButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        switch (mode) {
                            case 1:
                                game.currentLevel = 1.1;
                                break;
                            case 2:
                                game.currentLevel = 1.2;
                                break;
                            case 3:
                                game.currentLevel = 1.3;
                                break;
                        }
                        game.setScreen(new MainMenuScreen(game, mode));
                        dispose();
                    }
                });
            }
            else if (actor.getName().equals("boatImage")) boatImage = (Image) actor;
        }

        //init map
        for (int i = 0; i < game.vertices; i++) {
            City sourceCity = game.cities.get(i);
            portTable = new Ports(sourceCity, game);
            if (mode == 4) {
                portTable.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        boatImage.addAction(Actions.moveTo(x - boatImage.getHeight() / 2, y - boatImage.getHeight() / 2));
                    }
                });
            }
            stage.addActor(portTable);
            java.util.List<Edge> neighbors = connections.getNeighbors(i);
            for (int j = 0; j < neighbors.size(); j++) {

                int destination = neighbors.get(j).destination;
                int weight = neighbors.get(j).weight;
                City destCity = game.cities.get(destination);

                Vector2 start = new Vector2(sourceCity.x, sourceCity.y);
                Vector2 end = new Vector2(destCity.x, destCity.y);
                connectionArea = new ConnectionAreaImage(sourceCity, destCity);

                InfoCardActor card = new InfoCardActor(game, (float) (destCity.x + sourceCity.x) / 2,
                        (float) (destCity.y + sourceCity.y) / 2, 150, 60, sourceCity.name, destCity.name, weight, false);
                final Table cardTableFinal = card.getTable();

                connectionArea.addListener(new InputListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        stage.addActor(cardTableFinal);
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        cardTableFinal.remove();
                    }
                });

                linesToDraw.add(new LineData(start, end, Color.DARK_GRAY));
                stage.addActor(connectionArea);
            }
        }

        //init table
        algorithmTable = new Table(game.fontSkin);
        algorithmTable.setBackground(game.fontSkin.getDrawable("color"));

        //dijkstra
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
        for (int i = 0; i < distances.length; i++) {
            if (distances[i] == Integer.MAX_VALUE) distances[i] = 0;
            code += distances[i];
        }

        //check if dijkstra is correct
        checkCode = new checkCode(camera.viewportWidth / 4, camera.viewportHeight / 2, camera.viewportWidth / 2, 150, code, game, mode, stage, camera);
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

        String text = "";
        switch (mode) {
            case 1:
                text = "Servus Captain,\n\nThese damn mangooos are hard to get. :D To get them before the other crews we need to find " +
                        "the shortest paths to each city from our hometown! \n\nFill out the table below, I will help you! " +
                        "In the end we will know how long it takes us to travel to each city from our treasury and the fastest path!\nI am " +
                        "sure you will find out how it works!\n\nWe will need to find the code to unlock this treasure I have found!\n\n" +
                        "\n\nCode: The code is built by the distance of the connection to the city. For INFINITY, it was a ...... 0";
                break;
            case 2:
                text = "Hola Captain, \n\nYou are getting on it! We are surely becoming the pirates of the golden paths! Everyone will pay" +
                        " millions to know our secret - but first: Let`s get back to work\n\n" +
                        "I guess you know what to do? If not I am here to help. Cause you know - I am the endless source " +
                        "of wisdom.\n And don`t forget about the code so we can get an endless amount of mangooos!\n\n" +
                        "Oh and I have realized that it would be good to note down where we are always coming from...the precursors you know?" +
                        " Like that it is easier for us to find the correct route afterwards. Our ship will show you where we currently are." +
                        "\n\nCode: Remember the code is built by the distance of the connection to the city. For INFINITY, " +
                        "it was a ...... 0";
                break;
            case 3:
                text = "Hello and welcome on board again Captain, \n\nThere are still some routes to calculate but we " +
                        "are getting better. So I am sure we will find the solution this time to!\n\nTIP: Remember that we seek " +
                        "for the shortest connection from our start city. We are always moving to the shortest available connection to discover new ones." +
                        " Like that we will always find the shortest path.\n\nCode: Remember the code is built by distance of the connection " +
                        "to the city. For INFINITY, it was a ...... 0";
                break;
            case 4:
                text = "Ay Ay Captain, \n\nFinally! I think all our hard work pais off if we can open this last treasure we can finally retire.\n" +
                        "\nFind the last code to unlock the treasure.\n\nTIP: Remember that we seek " +
                        "for the shortest connection from our start city. You can click on the ports and our ship will move there." +
                        " Like that you always know where we currently are!\n\nCode: Remember the code is built by the distance of the connection " +
                        "to the city. For INFINITY it was a ...... 0";
                break;
        }
        infotext = new InfoTextGroup(game, text);

        //listener to close infotext
        closeButton = infotext.closeButton;
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                infotext.remove();
                int parrottWidth = (int) (camera.viewportWidth * 0.1);
                parrottimage = new Image(game.assetManager.get("parrott.png", Texture.class));
                parrottimage.setSize((float) parrottWidth, (float) (parrottWidth * 1.25));
                parrottimage.setPosition((float) (camera.viewportWidth * 0.85), (float) (camera.viewportHeight * 0.32));
                parrottimage.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        stage.addActor(infotext);
                        parrottimage.remove();
                    }
                });
                stage.addActor(parrottimage);
            }
        });

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
        for (LineData lineData : linesToDraw) {
            new DrawLineOrArrow(1, camera.combined, lineData.getColor(), lineData.getStart(), lineData.getEnd(), 2);
        }

        //render rest of stage
        stage.act();
        stage.draw();
    }

    public void dijkstra() {
        int count = 0, source = 0;
        boolean[] visited = new boolean[game.vertices];

        Arrays.fill(distances, Integer.MAX_VALUE); //fill distances with infinity to initialize it
        distances[source] = 0; //set distance to zero at source node

        //got the priorityQueue idea from chatgpt conversation on the 13th of July 3.32
        Comparator<Node> nodeComparator = new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {return Integer.compare(node1.distance, node2.distance);
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

    private void createTable() {
        Map<String, TextField> cellMap = new LinkedHashMap<>(); // LinkedHashMap from ChatGPT on July 14th at 10.09am

        // Create a nested table for the top row labels
        Table topLabelsTable = new Table(game.fontSkin);
        topLabelsTable.defaults().width(cellWidth).height(cellHeight).pad(0).space(0);

        for (int col = 0; col < game.vertices + 1; col++) {
            // Create and add the top label to the cell table
            Label topLabel;
            if (col == 0) topLabel = new Label("Input", game.fontSkin);
            else {
                City city = game.cities.get(col - 1);
                String topLabelString = city.name + "(" + city.shortName + ")";
                topLabel = new Label(topLabelString, game.fontSkin);
            }
            topLabelsTable.add(topLabel).top().padBottom(10);
            topLabelsTable.setColor(0.95f, 0.871f, 0.726f, 1);
        }

        algorithmTable.add(topLabelsTable).colspan(game.vertices + 1);
        algorithmTable.setColor(0.95f, 0.871f, 0.726f, 1);
        algorithmTable.setPosition((float) 800 / 2, camera.viewportHeight / 6);
        stage.addActor(algorithmTable);
        addNewRow(0);
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

            //define level of help per mode
            switch (mode) {
                case 1:
                    textField0.setText("Costs");
                    correctValue = String.valueOf(costs);
                    if (costs == Integer.MAX_VALUE) correctValue = "INFINITY";
                case 2:
                    if (costs == Integer.MAX_VALUE || costs == 0 || correctNodes.contains(col)) {
                        correctlyFilledTextFieldsInCurrentRow++;
                        textField.setText(correctValue);
                        textField.setColor(Color.GREEN);
                    }
                    break;
                case 3:
                    if (costs == Integer.MAX_VALUE || costs == 0) {
                        correctlyFilledTextFieldsInCurrentRow++;
                        textField.setText(correctValue);
                        textField.setColor(Color.GREEN);
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
            if (!filled) textField = addNeighborListener(textField, sourceCity, destCity, correctValue, iteration, false);
            newRow.add(textField).expandX().fillX();
        }
        algorithmTable.row();
        algorithmTable.add(newRow).colspan(game.vertices + 1).padRight(5);
    }

    private TextField addNeighborListener(final TextField textField, final City sourceCity, City destCity,
                                          final String finalCorrectValue, final int i, final boolean neighbor) {

        final Vector2 start = new Vector2(sourceCity.x, sourceCity.y);
        final Vector2 end = new Vector2(destCity.x, destCity.y);

        textField.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (neighbor) {
                    if (finalCorrectValue.contains(game.cities.get(0).shortName))
                        linesToDraw.add(new LineData(start, end, Color.RED));
                    else {
                        linesToDraw.add(new LineData(start, end, Color.RED));
                        lookForPrecursors(i, start, sourceCity, Color.RED);
                    }
                    textField.setColor(Color.RED);
                } else {
                    lookForPrecursors(i, end, sourceCity, Color.RED);
                }
                return true;
            }
        });
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                String userInput = textField.getText();
                boolean isCorrect = userInput.trim().equals(finalCorrectValue);
                if (key == '\r' || key == '\n') {
                    if (isCorrect) {
                        game.dropSound.play();
                        textField.setColor(Color.GREEN);
                        if (neighbor) {
                            if (finalCorrectValue.contains(game.cities.get(0).shortName))
                                linesToDraw.add(new LineData(start, end, Color.GREEN));
                            else {
                                linesToDraw.add(new LineData(start, end, Color.GREEN));
                                lookForPrecursors(i, start, sourceCity, Color.GREEN);
                            }
                        } else {
                            lookForPrecursors(i, end, sourceCity, Color.GREEN);
                        }
                        checkIfNewRow(i);
                    } else {
                        String text = "";
                        switch (mode) {
                            case 1:
                                text = "Did you get the correct cost for the connection?" +
                                        "\n\nHint: We are always looking for the shortest\npath from our treasury!";
                                break;
                            case 3:
                            case 4:
                            case 2:
                                text = "Hint: We are always looking for the shortest\npath from our treasury!\n\n" +
                                        "Correct Value: Shortage of precursor - Added costs";
                                break;
                        }
                        Table textBoxTable = new Table(game.fontSkin);
                        textBoxTable.setSize(camera.viewportWidth / 4, camera.viewportHeight / 10);
                        textBoxTable.setPosition(parrottimage.getX() - textBoxTable.getWidth(),
                                parrottimage.getY() + parrottimage.getHeight(), left);
                        Label textBox = new Label(text, game.fontSkin);
                        textBox.setFontScale(0.7f);
                        textBoxTable.add(textBox);
                        Drawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(game.assetManager.get("white 1.png", Texture.class)));
                        textBoxTable.setBackground(backgroundDrawable);
                        stage.addActor(textBoxTable);
                        // Make the textbox disappear after 5 seconds
                        textBoxTable.addAction(Actions.sequence(
                                Actions.delay(5f),
                                Actions.fadeOut(1f),
                                Actions.removeActor()
                        ));
                    }
                }
            }
        });
        return textField;
    }

    private void lookForPrecursors(int i, Vector2 start, City sourceCity, Color color) {
        if (i - 1 >= 0) {
            int precursorIndex = 1000;
            int i1 = i;
            Vector2 start1 = start;
            while (precursorIndex != 0) {
                precursorIndex = precursor[i1 - 1][game.cities.indexOf(sourceCity)];
                City precursorCity = game.cities.get(precursorIndex);
                Vector2 precursor = new Vector2(precursorCity.x, precursorCity.y);
                linesToDraw.add(new LineData(precursor, start1, color));
                start1 = precursor;
                i1--;
            }
        }
    }

    private void checkIfNewRow(int iteration) {
        correctlyFilledTextFieldsInCurrentRow++;
        if (correctlyFilledTextFieldsInCurrentRow == game.vertices) {
            if (iteration + 1 < dijkstraConnections.size() - 1) {
                City nextCity = game.cities.get(dijkstraConnections.get(iteration + 1));
                if (mode < 4) boatImage.addAction(Actions.moveTo(nextCity.x - boatImage.getHeight() / 2,
                        nextCity.y - boatImage.getHeight() / 2 + game.offset, 1f));
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
        String correctValue = "";
        if (costs == Integer.MAX_VALUE) correctValue += "INFINITY";
        else if (costs == 0) correctValue += precursorString + " - " + 0;
        else correctValue += precursorString + " - " + costs;
        return correctValue;
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
