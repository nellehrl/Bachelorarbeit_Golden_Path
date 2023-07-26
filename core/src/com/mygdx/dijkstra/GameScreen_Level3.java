package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.*;
import java.util.List;

public class GameScreen_Level3 implements Screen {
    final DijkstraAlgorithm game;
    Image boatImage, connectionArea;
    OrthographicCamera camera;
    private final FitViewport fitViewport;
    private Map<String, TextField> cellMap;
    ScrollPane.ScrollPaneStyle scrollPaneStyle;
    private Stage stage;
    String code = "";
    private final ScreenViewport viewport = new ScreenViewport();
    Button mainMenuButton, doneButton;
    ScrollPane dropBox;
    Table algorithmTable, portTable;
    Graph connections;
    int[] distances;
    ArrayList<Integer> correctNodes = new ArrayList<>();
    int[][] iterations,precursor;
    ArrayList<Integer> dijkstraConnections;
    private List<LineData> linesToDraw;
    Group background;
    checkCode checkCode;
    InfoText infotext;
    Button closeButton;
    private int correctlyFilledTextFieldsInCurrentRow = 0;
    int mode;
    float cellWidth = 140f, cellHeight = 20f;

    public GameScreen_Level3(final DijkstraAlgorithm game, final int mode) {

        //init variables
        this.game = game;
        this.mode = mode;
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
                        switch (mode) {
                            case 1:
                                game.currentLevel = 3.1;
                                break;
                            case 2:
                                game.currentLevel = 3.2;
                                break;
                            case 3:
                                game.currentLevel = 3.3;
                                break;
                            case 4:
                                game.currentLevel = 3.4;
                                break;
                        }
                        game.setScreen(new MainMenuScreen(game, mode));
                        dispose();
                    }
                });
            } else if (actor.getName().equals("boatImage")) boatImage = (Image) actor;
        }

        Group tableGroup = new Group();

        //init map
        linesToDraw = new ArrayList<>();
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

                connectionArea = new ConnectionArea(sourceCity, destCity);
                Image connectionArea2 = new ConnectionArea(destCity, sourceCity);

                InfoCard card = new InfoCard(game, (float) (destCity.x + sourceCity.x) / 2,
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

        algorithmTable = new Table(game.fontSkin);
        algorithmTable.setBackground(game.fontSkin.getDrawable("color"));

        //dijkstra
        dijkstra();
        createTable();

        dropBox = new ScrollPane(algorithmTable, scrollPaneStyle);
        dropBox.setWidth(camera.viewportWidth + 20);
        dropBox.setHeight((float) (camera.viewportHeight * 0.33) + 25);
        dropBox.setPosition(-10, -10);

        for (int i = 0; i < distances.length; i++) {
            if (distances[i] == Integer.MAX_VALUE) distances[i] = 0;
            code += distances[i];
        }
        System.out.println(code);

        int row_height = game.offset;
        int col_width = 2 * game.offset;

        //check if dijkstra is correct
        checkCode = new checkCode((float) Gdx.graphics.getWidth() / 4, (float) Gdx.graphics.getHeight() / 2,
                (float) Gdx.graphics.getWidth() / 2, 150, code, game, mode);
        doneButton = new TextButton("Done", game.mySkin, "default");
        doneButton.setSize((float) (2 * col_width), (float) (1.5 * row_height));
        doneButton.setPosition(3 * game.space, (float) (Gdx.graphics.getHeight() - (1.5 * row_height) - mainMenuButton.getHeight() - 3 * game.space));
        stage.addActor(doneButton);
        doneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addActor(checkCode);
            }
        });

        String text = "";
        switch (mode) {
            case 1:
                text = "Servus Captain, \n\nWe need to deliver now! Cause these damn mangos are hard to get. :D Let`s see if we are able to find " +
                        "the shortest paths to each city from our home town! Fill out the table below, I will help you! " +
                        "In the end we will know how long it takes us to " +
                        "travel to each city and the fastest path to it! I ams ure you will find out how it works!\n" +
                        "\nOh and you will need to find out the code to unlock this treasure I have found!\n\n" +
                        "TIP: Remember that we are always looking for the shortest connection from our start city" +
                        " our ship shows you were we are currently at and the available connections." +
                        "\n\nCode: The code is build by the distances of the" +
                        " connection to the city. For INFINITY it was a ...... 0";
                break;
            case 2:
                text = "Hola Captain, \n\nYou are getting on it! We are surely becoming the pirates of the golden paths! Everyone will pay" +
                        " millions to know our secret - but first: let`s get back to work\n\n" +
                        "I guess you know what to do? If not I am here to help cause you know - I am the endless source " +
                        "of wisdom.\n And don`t forget about the code so we can get an endless amount of mangoooooooooos!\n\n" +
                        "TIP: Remember that we are always looking for the shortest connection from our start city" +
                        " our ship shows you were we are currently at and the available connections." +
                        "\n\nCode: Remember the code is build by the distances of the" +
                        " connection to the city. For INFINITY it was a ...... 0";
                break;
            case 3:
                text = "Hello and welcome on board again Captain, \n\nThere are still some routes to calculate but we " +
                        "are getting better. SO I ams ure we will find the solution this time tooooooo!\n\nTIP: Remember that we are always looking " +
                        "for the shortest connection from our start city our ship shows you were we are currently at and" +
                        " the available connections.\n\nCode: Remember the code is build by distances of the connection " +
                        "to the city. For INFINITY it was a ...... 0";
                break;
            case 4:
                text = "Ay Ay Captain, \n\nFinally! I think all our hard work pais off if we can open this last treasure we can finally retire. \n" +
                        "\n Find the last code to unlock the last treasure.\n\nTIP: Remember that we are always looking " +
                        "for the shortest connection from our start city our ship shows you were we are currently at and" +
                        " the available connections.\n\nCode: Remember the code is build by the distances of the connection " +
                        "to the city. For INFINITY it was a ...... 0";
                break;
        }

        infotext = new InfoText(game, text);
        closeButton = infotext.closeButton;
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                infotext.remove();
                int parrottWidth = (int) (Gdx.graphics.getWidth() * 0.1);
                final Image image = new Image(game.assetManager.get("parrott.png", Texture.class));
                image.setSize((float) parrottWidth, (float) (parrottWidth * 1.25));
                image.setPosition((float) (Gdx.graphics.getWidth() * 0.85), (float) (Gdx.graphics.getHeight() * 0.32));
                image.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        stage.addActor(infotext);
                        image.remove();
                    }
                });
                stage.addActor(image);
            }
        });

        // Add the tableGroup to the stage after the cockpit, ensuring that the cockpit is rendered on top
        tableGroup.addActor(dropBox);
        stage.addActor(tableGroup);
        stage.addActor(mainMenuButton);
        stage.addActor(boatImage);
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
        cellMap = new LinkedHashMap<>(); // LinkedHashMap from ChatGPT on July 14th at 10.09am

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
        textField0.setText("Costs - Precursor");
        newRow.add(textField0).expandX().fillX();

        //fill rest of cols
        for (int col = 0; col < game.vertices; col++) {
            TextField textField = new TextField(" ", game.fontSkin);
            textField.setColor(0, 0, 0, 1);
            //fill table for help if level is below 3.4
            if (mode < 4) {
                int costs = iterations[iteration][col];
                String precursorString = game.cities.get(precursor[iteration][col]).shortName;
                String correctValue = buildCorrectValue(costs, precursorString);
                final int sourceCityIndex = dijkstraConnections.get(iteration);
                final City sourceCity = game.cities.get(sourceCityIndex);
                final City destCity = game.cities.get(col);
                final Vector2 start = new Vector2(sourceCity.x, sourceCity.y);
                final Vector2 end = new Vector2(destCity.x, destCity.y);
                boolean filled = false;
                //define level of help per mode
                switch (mode) {
                    case 1:
                        if (costs == Integer.MAX_VALUE || costs == 0 || correctNodes.contains(col)) {
                            correctlyFilledTextFieldsInCurrentRow++;
                            textField.setText(correctValue);
                            textField.setColor(Color.GREEN);
                        }
                        break;
                    case 2:
                        if (costs == Integer.MAX_VALUE || costs == 0) {
                            correctlyFilledTextFieldsInCurrentRow++;
                            textField.setText(correctValue);
                            textField.setColor(Color.GREEN);
                        }
                        break;
                }
                //add listeners
                for (Edge edge : neighbors) {
                    if (edge.getDestination() == col) {
                        filled = true;
                        textField = addNeighborListener(textField, start, end, correctValue, iteration, true);
                    }
                }
                if (!filled) textField = addNeighborListener(textField, start, end, correctValue, iteration, false);
            }
            //if mode is 4 only count filled textFields and generate new row if all are filled
            else textField.setTextFieldListener(new TextField.TextFieldListener() {
                @Override
                public void keyTyped(TextField textField, char key) {
                    correctlyFilledTextFieldsInCurrentRow++;
                    if (correctlyFilledTextFieldsInCurrentRow == game.vertices) {
                        if (iteration + 1 < dijkstraConnections.size()) {
                            addNewRow(iteration + 1);
                        } else {
                            stage.addActor(checkCode);
                        }
                    }
                }
            });
            newRow.add(textField).expandX().fillX();
        }
        algorithmTable.row();
        algorithmTable.add(newRow).colspan(game.vertices + 1).padRight(5);
    }

    private TextField addNeighborListener(final TextField textField, final Vector2 start, final Vector2 end, final String finalCorrectValue, final int iteration, final boolean neighbor) {
        textField.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (neighbor) linesToDraw.add(new LineData(start, end, Color.RED));
                textField.setColor(Color.RED);
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
                    if (neighbor) linesToDraw.add(new LineData(start, end, Color.GREEN));
                    checkIfNewRow(iteration);
                }
            }
        });
        return textField;
    }

    private void checkIfNewRow(int iteration) {
        correctlyFilledTextFieldsInCurrentRow++;
        if (correctlyFilledTextFieldsInCurrentRow == game.vertices) {
            if (iteration + 1 < dijkstraConnections.size() - 1) {
                City nextCity = game.cities.get(dijkstraConnections.get(iteration + 1));
                if (mode < 4)
                    boatImage.addAction(Actions.moveTo(nextCity.x - boatImage.getHeight() / 2, nextCity.y + 10, 1f));
                correctNodes.add(dijkstraConnections.get(iteration + 1));
                addNewRow(iteration + 1);
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
