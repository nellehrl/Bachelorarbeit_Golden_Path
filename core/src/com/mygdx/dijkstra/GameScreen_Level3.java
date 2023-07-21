package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.*;
import java.util.List;

public class GameScreen_Level3 implements Screen {
    final DijkstraAlgorithm game;
    Image boatImage, connectionArea, cockpit;
    OrthographicCamera camera;
    private final FitViewport fitViewport;
    private Map<String, TextField> cellMap;
    ScrollPane.ScrollPaneStyle scrollPaneStyle;
    private Stage stage;
    String code = "";
    private final ScreenViewport viewport = new ScreenViewport();
    Button mainMenuButton, doneButton;
    ScrollPane dropBox;
    Table algorithmTable = new Table(), portTable;
    Graph connections;
    int[] distances;
    int[][] iterations;
    ArrayList<Integer> dijkstraConnections;
    private List<LineData> linesToDraw;
    Group background;
    checkCode checkCode;
    InfoText infotext;
    Button closeButton;

    public GameScreen_Level3(final DijkstraAlgorithm game, int mode) {

        //init variables
        this.game = game;
        stage = new Stage(viewport);
        connections = new Graph(game.vertices, 1);

        //init camera
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fitViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        //init Arrays
        distances = new int[game.vertices];
        iterations = new int[game.vertices][game.vertices];
        dijkstraConnections = new ArrayList<>();

        // Create the ScrollPane
        scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.background = game.fontSkin.getDrawable("window-c");

        //init background
        background = new Background(game, 2);

        for (Actor actor : background.getChildren()) {
            if (actor.getName().equals("mainMenuButton")) {
                mainMenuButton = (Button) actor;
                mainMenuButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(new MainMenuScreen(game));
                        dispose();
                    }
                });
            }
            else if (actor.getName().equals("boatImage")) boatImage = (Image) actor;
            else if (actor.getName().equals("doneButton")) doneButton = (Button) actor;
            else if (actor.getName().equals("cockpit")) cockpit = (Image) actor;
        }

        Group tableGroup = new Group();

        //init map
        linesToDraw = new ArrayList<>();
        for (int i = 0; i < game.vertices; i++) {
            City sourceCity = game.cities.get(i);
            portTable = new Ports(sourceCity, game.fontSkin);
            stage.addActor(portTable);
            java.util.List<Edge> neighbors = connections.getNeighbors(i);
            for (int j = 0; j < neighbors.size(); j++) {

                int destination = neighbors.get(j).destination;
                int weight = neighbors.get(j).weight;
                City destCity = game.cities.get(destination);

                Vector2 start = new Vector2(sourceCity.x, sourceCity.y);
                Vector2 end = new Vector2(destCity.x, destCity.y);

                connectionArea = new ConnectionArea(sourceCity, destCity);
                Image connectionArea2 = new ConnectionArea(destCity, sourceCity);

                InfoCard card = new InfoCard(game.fontSkin, (float) (destCity.x + sourceCity.x) / 2,
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
                connectionArea2.addListener(new InputListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        stage.addActor(cardTableFinal);
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        cardTableFinal.remove();
                    }
                });

                LineData lineData = new LineData(start, end, Color.DARK_GRAY);
                linesToDraw.add(lineData);
                tableGroup.addActor(connectionArea);
                tableGroup.addActor(connectionArea2);
            }
        }

        //dijkstra
        dijkstra();
        createTable();

        dropBox = new ScrollPane(algorithmTable, scrollPaneStyle);
        dropBox.setWidth((float) (camera.viewportWidth * 0.975) + 3);
        dropBox.setHeight((float) (camera.viewportHeight * 0.34) + 2);
        dropBox.setPosition(11, 4);

        iterations = transposeArray(iterations);

        if (mode != 3) fillTable(mode);

        for (int distance : distances) {
            if (distance == Integer.MAX_VALUE) distance = 0;
            code += String.valueOf(distance);
        }

        //check if dijkstra is correct
        checkCode = new checkCode((float) Gdx.graphics.getWidth() / 2, (float) Gdx.graphics.getHeight() / 2, 300, 150, code, game, mode);
        doneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addActor(checkCode);
            }
        });

        String text = "";
        switch(mode){
            case 1:
                text = "Servus Captain, \n\nWe need to deliver now! Cause these damn mangos are hard to get let`s see if we are able to find " +
                        "the shortest paths to each city from our home town so that we can get those mangos fast when they are available! :D \n" +
                        "\n" +
                        "Look at the table down below and try to fill it out! In the end we will know how long it takes us to " +
                        "travel to each city and what the fastest path is! I ams ure you will find our how it works!\n" +
                        "\n" +
                        "Oh and you will need to find out the code to unlock this treasure I have found!";
                break;
            case 2:
                text = "Hola Captain, \n\nYou are getting on it! We are surely becoming the pirates of the golden paths!  Everyone will pay" +
                        " millions to know our secret - but first get back to work\n" +
                        "\n" +
                        "I guess you know what to do? If not I am here to help cause you know - I am the endless source " +
                        "of wisdom.\n" +
                        "\n" +
                        "And don`t forget about the code so we can get an endless amount of mangos!";
                break;
            case 3:
                text = "Ay Ay Captain, \n\nFinally! I think all our hard wokr paid off if we can open this last treasure we can finally retire. \n" +
                        "\n" +
                        "Find the last code to unlock the last treasure.";
                break;
        }

        infotext = new InfoText(game,text);

        for (Actor actor : infotext.getChildren()) {
            if (actor.getName().equals("closeButton")) {
                closeButton = (Button) actor;
                closeButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        infotext.remove();
                        int parrottWidth = (int) (Gdx.graphics.getWidth() * 0.1);
                        final Image image = new Image(new Texture(Gdx.files.internal("parrott.png")));
                        image.setSize((float) parrottWidth, (float) (parrottWidth * 1.25));
                        image.setPosition((float) (Gdx.graphics.getWidth() * 0.85), (float) (Gdx.graphics.getHeight() * 0.31));
                        image.addListener(new ClickListener(){
                            @Override
                            public void clicked(InputEvent event, float x, float y){
                                stage.addActor(infotext);
                                image.remove();
                            }
                        });
                        stage.addActor(image);
                    }
                });
            }
        }

        // Add the tableGroup to the stage after the cockpit, ensuring that the cockpit is rendered on top
        tableGroup.addActor(dropBox);
        stage.addActor(cockpit);
        stage.addActor(tableGroup);
        stage.addActor(mainMenuButton);
        stage.addActor(boatImage);
        stage.addActor(doneButton);
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

    private void fillTable(int mode) {
        Vector2 start;
        Vector2 end;

        for (int i = 0; i < dijkstraConnections.size(); i++) {
            for (int j = 0; j < game.vertices + 1; j++) {
                if (mode == 1) {
                    final TextField textField = cellMap.get("cell_" + i + "_" + j);
                    if (j > 0) {
                        final String correctValue = String.valueOf(iterations[(j - 1)][i]);

                        if (correctValue.equals(String.valueOf(Integer.MAX_VALUE))) {
                            textField.setText("INFINITY");
                            textField.setColor(Color.GREEN);
                        } else if (correctValue.equals(String.valueOf(0))) {
                            textField.setText("0");
                            textField.setColor(Color.GREEN);
                        }
                    }
                }

                if (j == 0) {
                    final TextField textFieldName = cellMap.get("cell_" + i + "_" + j);
                    if (i < dijkstraConnections.size()) {
                        final String value = game.cities.get(dijkstraConnections.get(i)).name;
                        textFieldName.setText(value);
                        textFieldName.setColor(Color.GREEN);
                    }
                } else {
                    final TextField textField = cellMap.get("cell_" + i + "_" + j);
                    String correctValue = String.valueOf(iterations[(j - 1)][i]);
                    if (correctValue.equals(String.valueOf(Integer.MAX_VALUE))) correctValue = "INFINITY";
                    if (correctValue.equals(String.valueOf(0))) correctValue = String.valueOf(0);
                    final String finalCorrectValue = correctValue;
                    if (i == 0 && j == 1) {
                        textField.setText("0");
                        textField.setColor(Color.GREEN);
                    }
                    textField.addListener(new ClickListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            if (textField.getText().equals(finalCorrectValue)) textField.setColor(Color.GREEN);
                            return true;
                        }
                    });
                    textField.setTextFieldListener(new TextField.TextFieldListener() {
                        @Override
                        public void keyTyped(TextField textField, char key) {
                            if (textField.getText().equals(" " + finalCorrectValue))
                                textField.setColor(Color.GREEN);
                        }
                    });

                    if (dijkstraConnections.size() > i) {
                        java.util.List<Edge> neighbors = connections.getNeighbors(dijkstraConnections.get(i));

                        for (Edge edge : neighbors) {
                            if (edge.getDestination() == (j - 1)) {
                                City destCity = game.cities.get(j - 1);
                                City sourceCity = game.cities.get(dijkstraConnections.get(i));
                                start = new Vector2(sourceCity.x, sourceCity.y);
                                end = new Vector2(destCity.x, destCity.y);
                                final Vector2 finalStart = start;
                                final Vector2 finalEnd = end;
                                textField.addListener(new ClickListener() {
                                    @Override
                                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                        if (!textField.getText().equals(String.valueOf(0)) && !textField.getText().equals(String.valueOf(Integer.MAX_VALUE)))
                                            linesToDraw.add(new LineData(finalStart, finalEnd, Color.RED));
                                        else linesToDraw.add(new LineData(finalStart, finalEnd, Color.GREEN));
                                        return true;
                                    }
                                });
                                textField.setTextFieldListener(new TextField.TextFieldListener() {
                                    @Override
                                    public void keyTyped(TextField textField, char key) {
                                        String userInput = textField.getText();
                                        boolean isCorrect = userInput.equals(" " + finalCorrectValue);
                                        if (isCorrect) {
                                            textField.setColor(Color.GREEN);
                                            linesToDraw.add(new LineData(finalStart, finalEnd, Color.GREEN));
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
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
        minHeap.add(new Node(source, 0));

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
                        distances[neighbor] = newDistance;
                        minHeap.add(new Node(neighbor, newDistance));
                    }
                }
            }
            System.arraycopy(distances, 0, iterations[count], 0, game.vertices);
            count++;
        }
    }

    public static int[][] transposeArray(int[][] array) {
        int numRows = array.length;
        int numCols = array[0].length;

        int[][] transposedArray = new int[numCols][numRows];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                transposedArray[j][i] = array[i][j];
            }
        }

        return transposedArray;
    }

    private void createTable() {
        cellMap = new LinkedHashMap<>(); //LinkedHashMap from ChatGPT on July 14th at 10.09am

        float cellWidth = 120f;
        float cellHeight = 20f;

        // Create a nested table for the top row labels
        Table topLabelsTable = new Table();
        topLabelsTable.defaults().center().expandX().fillX();

        for (int col = 0; col < game.vertices + 1; col++) {
            // Create a nested table for each cell
            Table cellTable = new Table();
            cellTable.defaults().width(cellWidth).height(cellHeight).pad(0).space(0);

            // Create and add the top label to the cell table
            Label topLabel;
            if (col == 0) topLabel = new Label("Start Point", game.fontSkin);
            else topLabel = new Label(game.cities.get(col - 1).name, game.fontSkin);
            cellTable.add(topLabel).top().padBottom(10);
            cellTable.row().padBottom(5);

            for (int row = 0; row < dijkstraConnections.size(); row++) {
                // Create the text field and add it to the cell table
                TextField textField = new TextField(" ", game.fontSkin);
                textField.setColor(Color.DARK_GRAY);
                String cellKey = "cell_" + row + "_" + col;
                cellMap.put(cellKey, textField);
                cellTable.add(textField).expandX().fillX();
                cellTable.row().padBottom(5);
                cellTable.row().padBottom(5);
            }
            // Add the cell table to the main table
            algorithmTable.add(cellTable).padRight(5);
        }
        // Add labels above cells
        topLabelsTable.add(new Label("", game.mySkin)).top().center();

        algorithmTable.row();
        algorithmTable.add(topLabelsTable).colspan(game.vertices);
        algorithmTable.setPosition((float) 800 / 2, camera.viewportHeight / 6);

        stage.addActor(algorithmTable);
    }

    @Override
    public void resize(int width, int height) {
        fitViewport.update(width, height, true);
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
