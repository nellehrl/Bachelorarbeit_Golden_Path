package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;

public class GameScreen_Level1 implements Screen {
    final DijkstraAlgorithm game;
    final int mode;
    Sound battle;
    Image boatImage, connectionArea, parrotImage;
    Table portTable, mangoCounter;
    boolean correctDrop;
    OrthographicCamera camera;
    private final FitViewport fitViewport;
    Graph connections;
    private Stage stage;
    private final ScreenViewport viewport = new ScreenViewport();
    Button mainMenuButton, closeButton;
    ArrayList<City> currentConnection = new ArrayList<>();
    ArrayList<Integer> validConnection = new ArrayList<>();
    private java.util.List<LineData> linesToDraw;
    Group background;
    String text;
    DropBox dropBox;
    ArrayList<City> visited = new ArrayList<>();
    private DragAndDrop dragAndDrop;
    ArrayList<Actor> added = new ArrayList<com.badlogic.gdx.scenes.scene2d.Actor>();
    InfoText infotext;
    Label mangoCounterLabel;

    public GameScreen_Level1(final DijkstraAlgorithm game, final int mode) {

        //init game and stage
        this.mode = mode;
        this.game = game;
        stage = new Stage(viewport);
        linesToDraw = new ArrayList<>();
        battle = game.assetManager.get("battle.wav", Sound.class);

        //init camera
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fitViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        //init cities and starting point
        currentConnection.add(game.cities.get(0));
        validConnection.add(0);
        switch (mode) {
            case 1:
                connections = new Graph(game.vertices, 1);
                background = new Background(game, 1);

                break;
            case 2:
                connections = new Graph(game.vertices, 2);
                background = new Background(game, 3);
                break;
            case 3:
                connections = new Graph(game.vertices, 1);
                background = new Background(game, 4);
                break;
        }

        Group tableGroup = new Group();
        //init background
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
                        game.setScreen(new MainMenuScreen(game, 1.1));
                        dispose();
                    }
                });
            } else if (actor.getName().equals("dropBox")) dropBox = (DropBox) actor;
            else if (actor.getName().equals("boatImage")) boatImage = (Image) actor;
            else if (actor.getName().equals("mangoCounter")) mangoCounter = (Table) actor;
        }

        mangoCounterLabel = (Label) mangoCounter.getChild(1);
        battle = Gdx.audio.newSound(Gdx.files.internal("battle.wav"));

        //init cities
        if (mode == 3) dragAndDrop = new DragAndDrop();

        int count = 0;
        for (int i = 0; i < game.vertices; i++) {
            final City value = game.cities.get(i);
            portTable = new Ports(value, game);
            if (mode == 1 || mode == 2) {
                portTable.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        currentConnection.add(value);
                        if (!visited.contains(value)) visited.add(value);
                        if (visited.size() == game.cities.size()) {
                            switch (mode) {
                                case 1:
                                    game.currentLevel = 1.2;
                                    break;
                                case 2:
                                    game.currentLevel = 1.3;
                                    break;
                                case 3:
                                    game.currentLevel = 2;
                                    break;
                            }
                            game.setScreen(new LevelWon(game, game.currentLevel));
                            dispose();
                        }
                    }
                });
            }
            tableGroup.addActor(portTable);
            if (mode == 3) {
                java.util.List<Edge> neighbors = connections.getNeighbors(i);
                for (int j = 0; j < neighbors.size(); j++) {

                    int destination = neighbors.get(j).destination;
                    City destCity = game.cities.get(destination);
                    City sourceCity = game.cities.get(i);

                    connectionArea = new ConnectionArea(sourceCity, destCity);
                    connectionArea.setName(i+ "-" + j);
                    Stack stack = createWeights(String.valueOf(neighbors.get(j).weight), 40, 5 * game.offset + game.space + count * (game.space + 40), (float) (Gdx.graphics.getHeight() * 0.3 - game.space / 2));
                    stack.setName(i+ "-" + j);
                    tableGroup.addActor(connectionArea);
                    tableGroup.addActor(stack);
                    addDragAndDrop(stack);
                    count++;
                }
            }
        }

        switch (mode) {
            case 1:
                text = "Howdy Captain, \n\nLet`s see what we got here….We want to visit all cities and then come back to bring " +
                        "all our conquests to our treasury.\n" + "\n" + "In the box down on the radar you can see all connections." +
                        " They go both ways. So should be easy," + " right? Let`s get on it.\n\nPlease stay on the route cause there are " +
                        "other pirates out there with canooons waiting for a fight.";
                break;
            case 2:
                text = "What`s kickin`, Captain?\n\nThat was great. Those mangos are quite delicious but I think we will" +
                        " need more of them for me " + "and more gold for you. Let`s keep chartering.\u2028\u2028It is " +
                        "quite windy and stormy around " + "this time of the year lets use it to our advantage!\n\nYou cann " +
                        "see all connections below. Keep in mind " + "to up check on the directions that are marked for " +
                        "the connections.\n\nWe can`t go into the " + "other direction - the wind will hold us back! " +
                        "Please remember the other pirates. I can`t see blood I am always getting sick when I see it.";
                break;
            case 3:
                text = "Ahoy Captain! \n\nWow, you are fast! I don`t know how to keep up with your pace! It`s great to have " +
                        "you " + "finally on board - keeping this wild crew under control!\n" + "\n" + "Our crew is developing" +
                        " and I think we can get more strategic now. Down there you can see all " + "connections. Let`s try to " +
                        "organize the graph and put the  weights on the corresponding connection." + " Like that we will get a" +
                        " better overview of the current situation.\n" + "\n" + "Just grap a weight and drop it at the right space. ";
                break;
        }

        infotext = new InfoText(game, text);
        closeButton = infotext.closeButton;
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                infotext.remove();
                int parrottWidth = (int) (Gdx.graphics.getWidth() * 0.1);
                parrotImage = new Image(game.assetManager.get("parrott.png", Texture.class));
                parrotImage.setSize((float) parrottWidth, (float) (parrottWidth * 1.25));
                parrotImage.setPosition((float) (Gdx.graphics.getWidth() * 0.85), (float) (Gdx.graphics.getHeight() * 0.31));
                parrotImage.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        stage.addActor(infotext);
                        parrotImage.remove();
                    }
                });
                stage.addActor(parrotImage);
            }
        });


        tableGroup.addActor(boatImage);
        int width = (int) (camera.viewportWidth / (game.vertices - 1));
        if (mode == 2) width = (int) (camera.viewportWidth / game.vertices);
        int height = (int) (camera.viewportHeight / 6 - game.space);
        int x = 3 * game.offset;
        int y = (int) (camera.viewportHeight * 0.28 - game.space);
        if (mode == 3) y = (int) (camera.viewportHeight * 0.225 - 2 * game.space);

        Image box = new Image(game.assetManager.get("box.png", Texture.class));
        box.setSize(Gdx.graphics.getWidth()-2, Gdx.graphics.getHeight()/3 - 2);
        box.setPosition(1,1);
        stage.addActor(box);
        stage.addActor(new ConnectionOverview(game.vertices, game.cities, game, (int) (width * 0.8), height, x, y, connections, mode));
        stage.addActor(tableGroup);
        stage.addActor(mainMenuButton);
        stage.addActor(infotext);
    }

    @Override
    public void render(float delta) {
        // tell the SpriteBatch to render in the coordinate system specified by the camera.
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0.95f, 0.871f, 0.726f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render stage
        game.batch.begin();
        background.draw(game.batch, 1.0f); // Render the actors from the selected group
        game.batch.end();

        switch (mode) {
            case 1:
                if (currentConnection.size() >= 2) {
                    for (int i = 0; i < currentConnection.size() - 3; i++) {
                        checkConnections(i, 1, false);
                    }
                    for (int i = currentConnection.size() - 2; i < currentConnection.size() - 1; i++) {
                        checkConnections(i, 1, true);
                    }
                    for (LineData lineData : linesToDraw) {
                        new DrawLineOrArrow(5, camera.combined, lineData.getColor(), lineData.getStart(), lineData.getEnd(), 1);
                    }
                }
                break;
            case 2:
                if (currentConnection.size() >= 2) {
                    for (int i = 0; i < currentConnection.size() - 3; i++) {
                        checkConnections(i, 2, false);
                    }
                    for (int i = currentConnection.size() - 2; i < currentConnection.size() - 1; i++) {
                        checkConnections(i, 2, true);
                    }
                    for (LineData lineData : linesToDraw) {
                        new DrawLineOrArrow(5, camera.combined, lineData.getColor(), lineData.getStart(), lineData.getEnd(), 2);
                    }
                }
                break;
            case 3:
                for (int i = 0; i < game.vertices; i++) {
                    java.util.List<Edge> neighbors = connections.getNeighbors(i);
                    for (int j = 0; j < neighbors.size(); j++) {
                        int destination = neighbors.get(j).destination;
                        City destCity = game.cities.get(destination);
                        City sourceCity = game.cities.get(i);

                        Vector2 point1 = new Vector2(sourceCity.x, sourceCity.y);
                        Vector2 point2 = new Vector2(destCity.x, destCity.y);

                        drawGraph(point1, point2);
                    }
                }
                break;
        }
        stage.act();
        stage.draw();
    }

    private void checkConnections(int i, final int mode, boolean lastConnection) {
        if (currentConnection.size() < 2 || i >= currentConnection.size() - 1) {
            return; // Not enough cities or no more connections to check
        }

        int source = game.cities.indexOf(currentConnection.get(i));
        int dest = game.cities.indexOf(currentConnection.get(i + 1));

        boolean isValidConnection = false;
        java.util.List<Edge> neighbors = connections.getNeighbors(source);
        java.util.List<Edge> neighbors2 = connections.getNeighbors(dest);
        for (Edge edge : neighbors) {
            if (source == edge.source && edge.destination == dest) {
                isValidConnection = true;
                Vector2 start = new Vector2(currentConnection.get(i).x, currentConnection.get(i).y);
                Vector2 end = new Vector2(currentConnection.get(i + 1).x, currentConnection.get(i + 1).y);
                LineData lineData = new LineData(start, end, Color.DARK_GRAY);
                linesToDraw.add(lineData);
                if (lastConnection) {
                    boatImage.addAction(Actions.moveTo(currentConnection.get(i + 1).x - boatImage.getWidth() / 2,
                            currentConnection.get(i + 1).y - boatImage.getHeight() / 8, 1f));

                }
            }
        }
        if (mode == 1 && !isValidConnection) {
            for (Edge edge : neighbors2) {
                if ((source == edge.destination && dest == edge.source) || (source == edge.source && edge.destination == dest)) {
                    isValidConnection = true;
                    Vector2 start = new Vector2(currentConnection.get(i).x, currentConnection.get(i).y);
                    Vector2 end = new Vector2(currentConnection.get(i + 1).x, currentConnection.get(i + 1).y);
                    LineData lineData = new LineData(start, end, Color.DARK_GRAY);
                    linesToDraw.add(lineData);
                    if (lastConnection) {
                        boatImage.addAction(Actions.moveTo(currentConnection.get(i + 1).x - boatImage.getWidth() / 2,
                                currentConnection.get(i + 1).y - boatImage.getHeight() / 8, 0.5f));
                    }
                }
            }
        }
        if (!isValidConnection) {
            battle.play();
            final int newValue = Integer.parseInt(String.valueOf(mangoCounterLabel.getText())) - 10;
            if (newValue > 0) mangoCounterLabel.setText(newValue);
            else {
                parrotImage.remove();
                final LevelLost lost = new LevelLost(this.game);
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
            if (currentConnection.size() > 0)
                currentConnection.remove(i + 1); // Remove the last index if the connection is not valid
        }
    }

    private void drawGraph(Vector2 point1, Vector2 point2) {
        new DrawLineOrArrow(5, camera.combined, Color.BLACK, point1, point2, 1);
        boatImage.addAction(Actions.moveTo(currentConnection.get(currentConnection.size() - 1).x - boatImage.getWidth() / 2, currentConnection.get(currentConnection.size() - 1).y - boatImage.getHeight() / 2, 1f));
    }

    public void addDragAndDrop(Stack stack) {
        DragAndDrop.Source source = createSource(stack);
        DragAndDrop.Target target = createTarget(connectionArea);
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
                if (target != null && target.getActor().getName().equals(payload.getDragActor().getName())){
                    correctDrop = true;
                }
                else {
                    stack.setPosition(initialX, initialY);
                    battle.play();

                    final int newValue = Integer.parseInt(String.valueOf(mangoCounterLabel.getText())) - 10;
                    if (newValue > 0) mangoCounterLabel.setText(newValue);
                    else {
                        parrotImage.remove();
                        final LevelLost lost = new LevelLost(game);
                        stage.addActor(lost);
                        Button close = (Button) lost.getChild(2);
                        close.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                game.mangos = 30;
                                mangoCounterLabel.setText(game.mangos);
                                game.setScreen(new GameScreen_Level1(game, 3));
                                dispose();
                            }
                        });
                    }
                }
            }
        };
    }

    private DragAndDrop.Target createTarget(final Image target) {
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

                if (!added.contains(payload.getDragActor())){
                    added.add(payload.getDragActor());
                }
                if (added.size() == connections.numOfEdges) {
                    game.setScreen(new LevelWon(game, 2.0));
                    dispose();
                }
            }
        };
    }

    public Stack createWeights(String weight, float triangleSize, float centerX, float centerY) {
        // Create the triangle texture
        Image triangleImage = new Image(game.assetManager.get("triangle.png", Texture.class));

        Label.LabelStyle labelStyle = game.mySkin.get(Label.LabelStyle.class);
        Label.LabelStyle newLabelStyle = new Label.LabelStyle(labelStyle);
        newLabelStyle.fontColor = Color.WHITE;
        Label numberLabel = new Label(weight, newLabelStyle);

        // Create a table to hold the triangle and number
        triangleImage.setSize(triangleSize, triangleSize);
        triangleImage.setPosition(centerX, centerY);
        Stack stack = new Stack();

        //create table for label to position it correctly
        Table table1 = new Table();
        table1.add(numberLabel);
        table1.setPosition(centerX, centerY);

        //create table for image to position it correctly
        Table table2 = new Table();
        table2.add(triangleImage);
        table2.setPosition(centerX, centerY);

        //add elements to stack
        stack.add(table2);
        stack.add(table1);
        stack.setPosition(centerX - game.offset, centerY - game.offset);
        stack.setSize(triangleSize, triangleSize);
        return stack;
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
        battle.dispose();
    }
}
