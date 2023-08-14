package com.mygdx.dijkstra.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.dijkstra.DijkstraAlgorithm;
import com.mygdx.dijkstra.models.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.dijkstra.views.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GameScreen_Level1 implements Screen {
    private final DijkstraAlgorithm game;
    private int countTotalWeights;
    private CheckCode checkCode;
    private Action move;
    private final int level;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private Image boatImage;
    private Image[] conenctionAreas;
    private Table portTable;
    private Group infotext;
    private OrthographicCamera camera;
    private FitViewport fitViewport;
    private Graph graph;
    private Stage stage;
    private final ArrayList<City> currentConnection = new ArrayList<>();
    private final ArrayList<Integer> validConnection = new ArrayList<>();
    private java.util.List<LineData> linesToDraw;
    private Group background;
    private ArrayList<City> cities;
    boolean[] visited, added;
    private DragAndDrop dragAndDrop;
    private Label mangoCounterLabel;
    private DrawLineOrArrow draw;
    private int vertices, space, offset, triangleSize = 40;
    private Batch batch;
    private Button mainMenuButton;

    public GameScreen_Level1(final DijkstraAlgorithm game, final int level) {
        this.level = level;
        this.game = game;

        initializeCameraAndViewport();
        defineGraphBasedOnLevel();
        initializeUIElements();
        initializeCityAndConnectionData();
        initializeActorsForStage();
    }

    @Override
    public void render(float delta) {
        // Tell the SpriteBatch to render in the coordinate system specified by the camera.
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0.95f, 0.871f, 0.726f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Render background
        background.draw(batch, 1.0f);

        switch (level) {
            case 1:
            case 2:
                renderCurrentConnections();
                break;
            case 3:
                renderCurrentConnectionsLevel3();
                break;
        }

        batch.end();

        stage.act();
        stage.draw();
    }

    private void initializeCameraAndViewport() {
        camera = game.getCamera();
        fitViewport = game.getFitViewport();
        stage = new Stage(fitViewport);
        cities = game.getCities();
        vertices = game.getVertices();
        space = game.getSpace();
        offset = game.getOffset();
        batch = game.getBatch();
    }

    private void defineGraphBasedOnLevel() {
        switch (level) {
            case 1:
            case 2:
                graph = new Graph(game, vertices, 2);
                break;
            case 3:
                graph = new Graph(game, vertices, 1);
                dragAndDrop = new DragAndDrop();
                break;
        }
    }

    private void initializeUIElements() {
        linesToDraw = new ArrayList<>();
        visited = new boolean[cities.size()];
        added = new boolean[graph.getNumOfEdges()];
        draw = new DrawLineOrArrow();

        String text = createTextForLevel();

        background = new BackgroundGroup(game, stage, text, level);
        mainMenuButton = background.findActor("mainMenuButton");
        boatImage = background.findActor("boatImage");
        infotext = background.findActor("infotext");
        Table mangoCounter = background.findActor("mangoCounter");
        mangoCounterLabel = (Label) mangoCounter.getChild(1);
    }

    private void initializeCityAndConnectionData() {
        initializeCities();
        currentConnection.add(cities.get(0));
        validConnection.add(0);
        checkCode = new CheckCode(mangoCounterLabel, camera.viewportWidth / 4, camera.viewportHeight / 2, camera.viewportWidth / 2, camera.viewportHeight / 5, "code", game, stage, camera, level);
    }

    private void initializeActorsForStage() {
        int x = 3 * offset;
        int y = (int) (camera.viewportHeight * 0.25);
        int width = (int) ((camera.viewportWidth - 3 * x) / vertices);
        int height = (int) (camera.viewportHeight / 6 - space);
        if (level == 3) y = y - triangleSize - space;

        stage.addActor(new ConnectionOverviewGroup(vertices, cities, game, width, height, x, y, graph, level));
        stage.addActor(boatImage);
        stage.addActor(mainMenuButton);
        stage.addActor(infotext);
    }

    private String createTextForLevel() {
        StringBuilder textBuilder = new StringBuilder();

        switch (level) {
            case 1:
                textBuilder.append("Howdy Captain,\n\nLet's see what we got here. On the map, you can see our target cities. " +
                        "We need to visit all of them, then return to bring all our conquests to our treasury.\n" +
                        "Can you see the box with all the connections down on the radar?\n" +
                        "The connections go both ways - they are undirected. Let's visit all cities but remember to stay +" +
                        "on the route cause there are other pirates out there with canooons waiting for a fight.");
                break;
            case 2:
                textBuilder.append("What's kickin', Captain?\n\nThat was great. Those mangos are pretty delicious. " +
                        "We will need more of them for me and more gold for you. Let's keep chartering!\n" +
                        "It is windy and stormy around this time of the year. Let's use it to our advantage!\n" +
                        "Can you see the connections on the radar again? Keep in mind to check the directions that are marked for each connection. " +
                        "We can't go in the other direction - the wind will hold us back, and we will cross other pirates. " +
                        "I really can't see blood!\n I am always getting sick when I see it.");
                break;
            case 3:
                textBuilder.append("Ahoy, Captain! \n\n Wow, we are fast, and our mango stock is growing! It's great " +
                        "to have you finally on board - controlling this wild crew!\nOur crew is developing with you, " +
                        "and we can get more strategic now. In the box below, you can see all the connections again. Can you see the costs too?\n" +
                        "We need to match the costs to the corresponding connections. Through that, we will better " +
                        "understand the current situation and how long we need for each city. Just grab a cost and drop " +
                        "it on the proper connection!");
                break;
        }

        return textBuilder.toString();
    }

    private void initializeCities() {
        int index = 0;
        conenctionAreas = new Image[graph.getNumOfEdges()];
        for (int i = 0; i < vertices; i++) {
            final City currentCity = cities.get(i);
            portTable = new Ports(currentCity, game);

            if (level <= 2) {
                portTable.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        currentConnection.add(currentCity);
                    }
                });
            } else if (level == 3) {
                java.util.List<Edge> neighbors = graph.getNeighbors(i);
                for (int j = 0; j < neighbors.size(); j++) {
                    City sourceCity = cities.get(i);
                    City destCity = cities.get(neighbors.get(j).getDestination());
                    initializeConnectionArea(sourceCity, destCity, neighbors.get(j).getWeight(), index);
                    initializDraggableWeightStack(neighbors, j, index);
                    index++;
                }
            }

            stage.addActor(portTable);
        }
    }

    private void initializeConnectionArea(City sourceCity, City destCity, int weight, int i) {
        Image connectionArea = new ConnectionAreaImage(sourceCity, destCity);
        connectionArea.setName(" " + weight);
        conenctionAreas[i] = connectionArea;
    }

    private void initializDraggableWeightStack(java.util.List<Edge> neighbors, int j, int index) {
        int start = 5 * offset + space;
        Stack stack = createWeights(String.valueOf(neighbors.get(j).getWeight()),
                start + countTotalWeights * (space + triangleSize), (float) (camera.viewportHeight * 0.3 - space / 2));
        stack.setName(" " + neighbors.get(j).getWeight());
        addDragAndDrop(stack, index);
        stage.addActor(stack);
        countTotalWeights++;
    }

    private void renderCurrentConnections() {
        if (currentConnection.size() < 2) return;
        boolean isValidPath = validatePathConnections();
        if (isValidPath) drawConnections();
        else {
            currentConnection.remove(currentConnection.size() - 1);
            new WrongUserInput(mangoCounterLabel, game, stage, level);
        }
    }

    private void drawConnections() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (LineData lineData : linesToDraw) {
            if (level == 1) {
                draw.drawLine(shapeRenderer, 3, lineData.getColor(), lineData.getStart(), lineData.getEnd());
            } else {
                draw.drawArrow(shapeRenderer, 3, lineData.getColor(), lineData.getStart(), lineData.getEnd());
            }
        }

        shapeRenderer.end();
    }

    private boolean validatePathConnections() {
        for (int i = 0; i < currentConnection.size() - 3; i++) {
            if (!checkConnectionValidity(i, false)) return false;
        }
        return checkConnectionValidity(currentConnection.size() - 2, true);
    }

    private void renderCurrentConnectionsLevel3() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < vertices; i++) {
            java.util.List<Edge> neighbors = graph.getNeighbors(i);
            for (Edge neighbor : neighbors) {
                int destination = neighbor.getDestination();
                City destCity = cities.get(destination);
                City sourceCity = cities.get(i);
                Vector2 point1 = new Vector2(sourceCity.getX(), sourceCity.getY());
                Vector2 point2 = new Vector2(destCity.getX(), destCity.getY());
                draw.drawArrow(shapeRenderer, 3, Color.BLACK, point1, point2);
            }
        }
        shapeRenderer.end();
    }

    private boolean checkConnectionValidity(int i, boolean lastConnection) {
        // Not enough cities or no more graph to check
        if (currentConnection.size() < 2 || i >= currentConnection.size() - 1) {
            return false;
        }

        //define source and dest city index
        int source = cities.indexOf(currentConnection.get(i));
        int dest = cities.indexOf(currentConnection.get(i + 1));

        //define all possible destinations
        Set<Integer> neighborDestinations = new HashSet<>();
        for (Edge edge : graph.getNeighbors(source)) neighborDestinations.add(edge.getDestination());
        boolean isValidConnection = neighborDestinations.contains(dest);

        if (level == 1 && !isValidConnection) {
            for (Edge edge : graph.getNeighbors(dest)) neighborDestinations.add(edge.getDestination());
            isValidConnection = neighborDestinations.contains(source);
        }

        if (isValidConnection) {
            visited[cities.indexOf(currentConnection.get(i + 1))] = true; // Mark the city as visited
            if (checkAllCitiesVisited()) {
                stage.addActor(checkCode);
            }
            Vector2 start = new Vector2(currentConnection.get(i).getX(), currentConnection.get(i).getY());
            Vector2 end = new Vector2(currentConnection.get(i + 1).getX(), currentConnection.get(i + 1).getY());
            LineData lineData = new LineData(start, end, Color.DARK_GRAY);
            linesToDraw.add(lineData);
            if (lastConnection) {
                move(currentConnection.size() - 1);
                boatImage.addAction(move);
            }
        }
        return isValidConnection;
    }

    public void addDragAndDrop(Stack stack, int index) {
        DragAndDrop.Source source = createSource(stack, index);
        dragAndDrop.addSource(source);
    }

    private DragAndDrop.Source createSource(final Stack stack, final int index) {
        return new DragAndDrop.Source(stack) {
            private float initialX;
            private float initialY;

            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Target target = createTarget(conenctionAreas[index], index);
                dragAndDrop.addTarget(target);
                stage.addActor(conenctionAreas[index]);
                initialX = stack.getX();
                initialY = stack.getY();

                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setDragActor(getActor());
                dragAndDrop.setDragActorPosition(x, y - triangleSize);
                stage.addActor(getActor());

                payload.setObject(stack.getName());  // Setting the name of the source actor in the payload
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                if (target != null && target.getActor().getName().equals(payload.getDragActor().getName())) {
                    game.getDropSound().play();
                } else {
                    stack.setPosition(initialX, initialY);
                    new WrongUserInput(mangoCounterLabel, game, stage, level);
                }
            }
        };
    }

    private DragAndDrop.Target createTarget(final Image target, final int index) {
        return new DragAndDrop.Target(target) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                String sourceName = (String) payload.getObject();

                return getActor().getName().equals(sourceName);
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {

                payload.getDragActor().setUserObject(conenctionAreas[index]);
                payload.getDragActor().toFront();

                added[index] = true; // Mark the edge as added

                if (checkAllEdgesAdded()) {
                    stage.addActor(checkCode);
                }
            }
        };
    }

    public void move(int index) {
        float x = currentConnection.get(index).getX() - boatImage.getWidth() / 2;
        float y = currentConnection.get(index).getY() - boatImage.getHeight() / 2;
        float duration = 1f;
        move = Actions.moveTo(x, y, duration);
    }

    private Label createLabel(String text, Color fontColor) {
        Label.LabelStyle labelStyle = game.getMySkin().get(Label.LabelStyle.class);
        labelStyle.fontColor = fontColor;
        return new Label(text, labelStyle);
    }

    private Stack createWeights(String weight, float centerX, float centerY) {
        //init Label & Image
        Image triangleImage = new Image(game.getAssetManager().get("triangle.png", Texture.class));
        Label numberLabel = createLabel(weight, Color.WHITE);

        // Create a table to hold the label at the center of the triangle
        Table labelTable = new Table();
        labelTable.add(numberLabel).center();
        Table triangleTable = new Table();
        triangleTable.add(triangleImage);

        // Stack to hold triangle and centered label
        Stack stack = new Stack();
        stack.add(triangleTable);
        stack.add(labelTable);
        stack.setPosition(centerX - offset, centerY - offset);
        stack.setSize(triangleSize, triangleSize);

        return stack;
    }

    private boolean checkAllCitiesVisited() {
        for (boolean cityVisited : visited) {
            if (!cityVisited) {
                return false;
            }
        }
        return true;
    }

    private boolean checkAllEdgesAdded() {
        for (boolean edgeAdded : added) {
            if (!edgeAdded) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void resize(int width, int height) {
        fitViewport.update(width, height, true);
        camera.update(true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
