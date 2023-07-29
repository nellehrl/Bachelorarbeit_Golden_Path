package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GameScreen_Level1 implements Screen {
    final DijkstraAlgorithm game;
    private float triangleSize = 40;
    Action move;
    final int mode;
    ShapeRenderer shapeRenderer  = new ShapeRenderer();
    Image boatImage, connectionArea;
    Table portTable, mangoCounter;
    boolean correctDrop, isValidConnection = false;
    OrthographicCamera camera;
    private final FitViewport fitViewport;
    Graph connections;
    private Stage stage;
    Button mainMenuButton, closeButton;
    ArrayList<City> currentConnection = new ArrayList<>();
    ArrayList<Integer> validConnection = new ArrayList<>();
    private java.util.List<LineData> linesToDraw;
    Group background;
    DropBoxWindow dropBox;
    boolean[] visited, added;
    private DragAndDrop dragAndDrop;
    InfoTextGroup infotext;
    Label mangoCounterLabel;
    StringBuilder textBuilder;
    DrawLineOrArrow draw;

    public GameScreen_Level1(final DijkstraAlgorithm game, final int mode) {
        //init game and stage
        this.mode = mode;
        this.game = game;

        //init camera
        camera = game.camera;
        fitViewport = game.fitViewport;
        stage = new Stage(fitViewport);

        //define Graph
        switch (mode) {
            case 1: case 2:
                connections = new Graph(game.vertices, 2);
                connections = new Graph(game.vertices, 2);
                break;
            case 3:
                connections = new Graph(game.vertices, 1);
                dragAndDrop = new DragAndDrop();
                break;
        }

        //init arrays
        linesToDraw = new ArrayList<>();
        visited = new boolean[game.cities.size()];
        added = new boolean[connections.numOfEdges];
        draw = new DrawLineOrArrow();

        //init cities and starting point
        background = new BackgroundGroup(game);
        initializeBackground();
        initializeCities();
        currentConnection.add(game.cities.get(0));
        validConnection.add(0);

        //init Text
        String text = createTextForMode(mode);
        textBuilder = new StringBuilder(text);

        //init infoText
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

        //init connectionOverview
        int width = (int) (camera.viewportWidth / game.vertices);
        int height = (int) (camera.viewportHeight / 6 - game.space);
        int x = 3 * game.offset;
        int y = (int) (camera.viewportHeight * 0.28 - game.space);
        if (mode == 3) y = (int) (camera.viewportHeight * 0.225 - 2 * game.space);
        stage.addActor(new ConnectionOverviewGroup(game.vertices, game.cities, game, (int) (width * 0.8), height, x, y, connections, mode));

        //add rest of actors
        stage.addActor(boatImage);
        stage.addActor(mainMenuButton);
        stage.addActor(infotext);
    }

    @Override
    public void render(float delta) {
        // Tell the SpriteBatch to render in the coordinate system specified by the camera.
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0.95f, 0.871f, 0.726f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();

        // Render background
        background.draw(game.batch, 1.0f);

        switch (mode) {
            case 1:
                renderConnections(1);
                break;
            case 2:
                renderConnections(2);
                break;
            case 3:
                renderMode3Connections();
                break;
        }

        game.batch.end();

        stage.act();
        stage.draw();
    }

    private String createTextForMode(int mode) {
        StringBuilder textBuilder = new StringBuilder();

        switch (mode) {
            case 1:
                textBuilder.append("Howdy Captain,\n\n");
                textBuilder.append("Let's see what we got here….We want to visit all cities and then come back to bring");
                textBuilder.append("all our conquests to our treasury.\n\n");
                textBuilder.append("In the box down on the radar you can see all connections.");
                textBuilder.append("They go both ways. So it should be easy, right? Let's get on it.\n\n");
                textBuilder.append("Please stay on the route cause there are");
                textBuilder.append(" other pirates out there with canooons waiting for a fight.");
                break;
            case 2:
                textBuilder.append("What`s kickin`, Captain?\n\n");
                textBuilder.append("That was great. Those mangos are quite delicious, but we will");
                textBuilder.append(" need more of them for me and more gold for you. Let`s keep chartering.\u2028\u2028");
                textBuilder.append("It is windy and stormy around this time of the year. Lets use it to our advantage!\n\n");
                textBuilder.append("You can see all connections below. Keep in mind to check the directions that are marked for ");
                textBuilder.append("the connections.\n\n");
                textBuilder.append("We can`t go in the other direction - the wind will hold us back! ");
                textBuilder.append("Please remember the other pirates. I can`t see blood. I am always getting sick when I see it.");
                break;
            case 3:
                textBuilder.append("Ahoy, Captain! \n\n");
                textBuilder.append("Wow, you are fast! I don`t know how to keep up with your pace! It`s great to have ");
                textBuilder.append("you finally on board - controlling wild crew!\n\n");
                textBuilder.append("Our crew is developing, and we can get more strategic now. In the box below, you can see all connections. Let`s ");
                textBuilder.append("organize the graph and put the weights on the corresponding connection. Like that, we will get a");
                textBuilder.append(" better overview of the current situation.\n\n");
                textBuilder.append("Just grab a weight and drop it at the right space.");
                break;
        }

        return textBuilder.toString();
    }

    public void initializeBackground(){
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
                        game.setScreen(new MainMenuScreen(game, game.currentLevel));
                        dispose();
                    }
                });
            } else if (actor.getName().equals("dropBox")) dropBox = (DropBoxWindow) actor;
            else if (actor.getName().equals("boatImage")) boatImage = (Image) actor;
            else if (actor.getName().equals("mangoCounter")) {
                mangoCounter = (Table) actor;
                mangoCounterLabel = (Label) mangoCounter.getChild(1);
            }
        }
    }

    private void initializeCities() {
        int count = 0;
        for (int i = 0; i < game.vertices; i++) {
            final City value = game.cities.get(i);
            portTable = new Ports(value, game);
            switch (mode) {
                case 1:
                case 2:
                    initializeCityForMode1And2(value);
                    break;
                case 3:
                    count = initializeCityForMode3(i, count);
                    break;
            }

            stage.addActor(portTable);
        }
    }

    private void initializeCityForMode1And2(final City currentCity) {
        portTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentConnection.add(currentCity);
                visited[game.cities.indexOf(currentCity)] = true; // Mark the city as visited
                if (checkAllCitiesVisited()) {
                    if(mode == 2) game.setScreen(new LevelWonScreen(game, 1.2));
                    else game.setScreen(new LevelWonScreen(game, 1.1));
                    dispose();
                }
            }
        });
    }

    private int initializeCityForMode3(int cityIndex, int count) {
        java.util.List<Edge> neighbors = connections.getNeighbors(cityIndex);
        for (int j = 0; j < neighbors.size(); j++) {
            int destination = neighbors.get(j).destination;
            City destCity = game.cities.get(destination);
            City sourceCity = game.cities.get(cityIndex);

            connectionArea = new ConnectionAreaImage(sourceCity, destCity);
            connectionArea.setName(cityIndex + "-" + j);
            Stack stack = createWeights(String.valueOf(neighbors.get(j).weight), 5 * game.offset + game.space + count * (game.space + 40), (float) (camera.viewportHeight * 0.3 - game.space / 2));
            stack.setName(cityIndex + "-" + j);

            stage.addActor(connectionArea);
            stage.addActor(stack);
            addDragAndDrop(stack, count);
            count++;
        }
        return count;
    }

    private void renderConnections(final int mode) {
        if (currentConnection.size() >= 2) {
            for (int i = 0; i < currentConnection.size() - 3; i++) {
                isValidConnection = checkConnections(i, mode, false);
                if (!isValidConnection) break;
            }
            isValidConnection = checkConnections(currentConnection.size() - 2, mode, true);
            if (isValidConnection) {
                shapeRenderer.setProjectionMatrix(camera.combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                for (LineData lineData : linesToDraw) {
                    draw.drawLine(shapeRenderer,3, lineData.getColor(), lineData.getStart(), lineData.getEnd());
                }
                shapeRenderer.end();
            } else{
                currentConnection.remove(currentConnection.size()-1);
                levelLost();
            }
        }
    }

    private void renderMode3Connections() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < game.vertices; i++) {
            java.util.List<Edge> neighbors = connections.getNeighbors(i);
            for (int j = 0; j < neighbors.size(); j++) {
                int destination = neighbors.get(j).destination;
                City destCity = game.cities.get(destination);
                City sourceCity = game.cities.get(i);

                Vector2 point1 = new Vector2(sourceCity.x, sourceCity.y);
                Vector2 point2 = new Vector2(destCity.x, destCity.y);

                draw.drawArrow(shapeRenderer, 3, Color.BLACK, point1, point2);
            }
        }
        shapeRenderer.end();
    }

    private boolean checkConnections(int i, final int mode, boolean lastConnection) {
        // Not enough cities or no more connections to check
        if (currentConnection.size() < 2 || i >= currentConnection.size() - 1) {
            return false;
        }

        //define source and dest city index
        int source = game.cities.indexOf(currentConnection.get(i));
        int dest = game.cities.indexOf(currentConnection.get(i + 1));

        //define all possible destinations
        Set<Integer> neighborDestinations = new HashSet<>();
        for (Edge edge : connections.getNeighbors(source)) neighborDestinations.add(edge.destination);
        isValidConnection = neighborDestinations.contains(dest);

        if (mode == 1 && !isValidConnection) {
            for (Edge edge : connections.getNeighbors(dest)) neighborDestinations.add(edge.destination);
            isValidConnection = neighborDestinations.contains(source);
        }

        if (isValidConnection) {
            Vector2 start = new Vector2(currentConnection.get(i).x, currentConnection.get(i).y);
            Vector2 end = new Vector2(currentConnection.get(i + 1).x, currentConnection.get(i + 1).y);
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
        DragAndDrop.Source source = createSource(stack);
        DragAndDrop.Target target = createTarget(connectionArea, index);
        dragAndDrop.addSource(source);
        dragAndDrop.addTarget(target);
    }

    private DragAndDrop.Source createSource(final Stack stack) {
        return new DragAndDrop.Source(stack) {
            private float initialX;
            private float initialY;

            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                initialX = stack.getX();
                initialY = stack.getY();

                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setDragActor(getActor());
                dragAndDrop.setDragActorPosition(x, y - 30);
                stage.addActor(getActor());

                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                if (target != null && target.getActor().getName().equals(payload.getDragActor().getName())) {
                    correctDrop = true;
                    game.dropSound.play();
                } else {
                    stack.setPosition(initialX, initialY);
                    levelLost();
                }
            }
        };
    }

    private DragAndDrop.Target createTarget(final Image target, final int index) {
        return new DragAndDrop.Target(target) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                return true;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {

                payload.getDragActor();
                payload.getDragActor().setUserObject(connectionArea);
                payload.getDragActor().toFront();

                added[index] = true; // Mark the edge as added

                if (checkAllEdgesAdded()) {
                    game.setScreen(new LevelWonScreen(game, 1.3));
                    dispose();
                }
            }
        };
    }

    public void move(int index) {
        float x = currentConnection.get(index).x - boatImage.getWidth() / 2;
        float y = currentConnection.get(index).y - boatImage.getHeight() / 2;
        float duration = 1f;
        move = Actions.moveTo(x, y, duration);
    }

    private Label createLabel(String text, Color fontColor) {
        Label.LabelStyle labelStyle = game.mySkin.get(Label.LabelStyle.class);
        labelStyle.fontColor = fontColor;
        return new Label(text, labelStyle);
    }

    private Stack createWeights(String weight, float centerX, float centerY) {
        //init Label & Image
        Image triangleImage = new Image(game.assetManager.get("triangle.png", Texture.class));
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
        stack.setPosition(centerX - game.offset, centerY - game.offset);
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

    public void levelLost() {
        negativeFeedbackLoop(game, camera, stage);
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
                    game.setScreen(new GameScreen_Level1(game, mode));
                    dispose();
                }
            });
        }
    }

    static void negativeFeedbackLoop(DijkstraAlgorithm game, OrthographicCamera camera, Stage stage) {
        game.battle.play();
        game.blood.setSize(camera.viewportWidth, camera.viewportHeight);
        game.blood.setPosition(0, 0);
        stage.addActor(game.blood);
        game.blood.addAction(Actions.sequence(
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
