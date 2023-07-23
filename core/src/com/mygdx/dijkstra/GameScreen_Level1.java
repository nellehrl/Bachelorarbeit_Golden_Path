package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
    Image boatImage, cockpit, connectionArea, connectionArea2;
    Table portTable;
    OrthographicCamera camera;
    private final FitViewport fitViewport;
    Graph connections;
    private Stage stage;
    private final ScreenViewport viewport = new ScreenViewport();
    Button mainMenuButton, closeButton, doneButton;
    ArrayList<City> currentConnection = new ArrayList<>();
    Group background;
    String text = "Visit all Cities! \n\nYou can find all connections in the white box below.\n\nYou can use the connections in both directions";
    DropBox dropBox;
    ArrayList<City> visited = new ArrayList<>();
    private DragAndDrop dragAndDrop;
    Stack stack;
    ArrayList<Actor> added = new ArrayList<com.badlogic.gdx.scenes.scene2d.Actor>();
    InfoText infotext;

    public GameScreen_Level1(final DijkstraAlgorithm game, int mode) {

        //init game and stage
        this.mode = mode;
        this.game = game;
        stage = new Stage(viewport);

        //init camera
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fitViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        //init cities and starting point
        currentConnection.add(game.cities.get(0));
        switch(mode){
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
            if(actor.getName().equals("mainMenuButton")) {
                mainMenuButton = (Button)actor;
                mainMenuButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(new MainMenuScreen(game));
                        dispose();
                    }
                });
            }
            else if(actor.getName().equals("dropBox")) dropBox = (DropBox)actor;
            //else if(actor.getName().equals("cockpit")) cockpit = (Image)actor;
            else if (actor.getName().equals("doneButton")){
                doneButton = (Button) background.getChild(4);
                doneButton.remove();
            }
            else if(actor.getName().equals("boatImage")) boatImage = (Image)actor;
        }

        //init cities
        if(mode == 3)dragAndDrop = new DragAndDrop();

        int count = 0;
        for (int i = 0; i < game.vertices; i++) {
            final City value = game.cities.get(i);
            portTable = new Ports(value, game.fontSkin);
            if(mode == 1 || mode == 2) {
                portTable.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        currentConnection.add(value);
                        if (!visited.contains(value)) visited.add(value);
                        if (visited.size() == game.cities.size()) {
                            game.setScreen(new MainMenuScreen(game));
                            dispose();
                        }
                    }
                });
            }
            tableGroup.addActor(portTable);
            if(mode == 3) {
                java.util.List<Edge> neighbors = connections.getNeighbors(i);
                for (int j = 0; j < neighbors.size(); j++) {

                    int destination = neighbors.get(j).destination;
                    City destCity = game.cities.get(destination);
                    City sourceCity = game.cities.get(i);

                    connectionArea = new ConnectionArea(sourceCity, destCity);
                    connectionArea2 = new ConnectionArea(destCity, sourceCity);
                    createWeights(String.valueOf(neighbors.get(j).weight), 40, 5 * game.offset + game.space + count * (game.space + 40),
                            (float) (Gdx.graphics.getHeight()*0.3 - game.space/2));
                    tableGroup.addActor(connectionArea);
                    tableGroup.addActor(connectionArea2);
                    tableGroup.addActor(stack);
                    addDragAndDrop();
                    count++;
                }
            }
        }

        switch(mode){
            case 1:
                text = "Howdy Captain, \n\nLet`s see what we got here….we want to visit all cities and then come back to bring " +
                        "all our conquests to our treasury.\n" +
                        "\n" +
                        "In the box down on the radar you can see all connections. They go both ways. So should be easy," +
                        " right? Let`s get on it.";
                break;
            case 2:
                text = "What`s kickin`, Captain?\n\nThat was great. Those mangos are quite delicious but I think we will need more of them for me " +
                        "and more gold for you. Let`s keep chartering.\u2028\u2028It is quite windy and stormy around " +
                        "this time of the year lets use it to our advantage!\n\nYou cann see all connections below. Keep in mind " +
                        "to up check on the directions that are marked for the connections.\n\nWe can`t go into the " +
                        "other direction - the wind will hold us back! ";
                break;
            case 3:
                text = "Ahoy Captain! \n\nWow, you are fast! I don`t know how to keep up with your pace! It`s great to have you " +
                        "finally on board - keeping this wild crew under control!\n" +
                        "\n" +
                        "Our crew is developing and I think we can get more strategic now. Down there you can see all " +
                        "connections. Let`s try to organize the graph and put the  weights on the corresponding connection." +
                        " Like that we will get a better overview of the current situation.\n" +
                        "\n" +
                        "Just grap a weight and drop it at the right space. ";
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

        tableGroup.addActor(boatImage);
        int width = (int) (camera.viewportWidth/(game.vertices-1));
        if(mode == 2) width = (int) (camera.viewportWidth/game.vertices);
        int height = (int) (camera.viewportHeight/6 - game.space);
        int x = 3*game.offset;
        int y = (int) (camera.viewportHeight*0.28 - game.space);
        if(mode == 3) y = (int) (camera.viewportHeight*0.225 - 2*game.space);
        stage.addActor(new ConnectionOverview(game.vertices, game.cities, game.fontSkin,
                (int) (width* 0.8), height, x, y, connections, mode));
        //stage.addActor(cockpit);
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

        switch(mode) {
            case 1:
            if (currentConnection.size() >= 2) {
                for (int i = 0; i < currentConnection.size(); i++) {
                    if (i + 1 < currentConnection.size()) {
                        int source = game.cities.indexOf(currentConnection.get(i));
                        int nextIndex = game.cities.indexOf(currentConnection.get(i + 1));
                        for (int j = 0; j < game.cities.size(); j++) {
                            java.util.List<Edge> neighbors = connections.getNeighbors(j);
                            for (Edge edge : neighbors) {
                                int dest = edge.destination;
                                if (source == j && dest == nextIndex) draw(i);
                                else if (source == dest && nextIndex == j) draw(i);
                            }
                        }
                    }
                }
            }
            break;
            case 2:
                if (currentConnection.size() >= 2) {
                    for (int i = 0; i < currentConnection.size(); i++) {
                        if (i + 1 < currentConnection.size()) {
                            int source = game.cities.indexOf(currentConnection.get(i));
                            int nextIndex = game.cities.indexOf(currentConnection.get(i + 1));
                            java.util.List<Edge> neighbors = connections.getNeighbors(source);
                            for (Edge edge : neighbors) {
                                int dest = edge.destination;
                                if (dest == nextIndex) draw(i);
                            }
                        }
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
    private void draw(int i) {
        Vector2 start = new Vector2(currentConnection.get(i).getX(), currentConnection.get(i).getY());
        Vector2 end = new Vector2(currentConnection.get(i + 1).getX(), currentConnection.get(i + 1).getY());
        new DrawLineOrArrow( 5, camera.combined, Color.BLACK,start, end, 1);
        boatImage.addAction(Actions.moveTo(currentConnection.get(currentConnection.size() - 1).x - boatImage.getWidth() / 2,
                currentConnection.get(currentConnection.size() - 1).y - boatImage.getHeight()/8, 1f));
    }

    private void drawGraph(Vector2 point1, Vector2 point2) {
        new DrawLineOrArrow( 5, camera.combined, Color.BLACK, point1, point2, 1);
        boatImage.addAction(Actions.moveTo(currentConnection.get(currentConnection.size() - 1).x - boatImage.getWidth() / 2,
                currentConnection.get(currentConnection.size() - 1).y - boatImage.getHeight() / 2, 1f));
    }

    public void addDragAndDrop() {
        DragAndDrop.Source source = createSource(stack);
        DragAndDrop.Target target = createTarget(connectionArea);
        DragAndDrop.Target target2 = createTarget(connectionArea2);
        dragAndDrop.addSource(source);
        dragAndDrop.addTarget(target);
        dragAndDrop.addTarget(target2);
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
                stage.addActor(getActor());
                dragAndDrop.setDragActorPosition(x, y);

                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                if (target == null) {
                    stack.setPosition(initialX, initialY);
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
                stack.setUserObject(connectionArea);
                stack.toFront();
                if (!added.contains(payload.getDragActor())) added.add(payload.getDragActor());
                if (added.size() == connections.numOfEdges) {
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                }
            }
        };
    }

    public void createWeights(String weight, float triangleSize, float centerX, float centerY) {
        // Create the triangle texture
        Image triangleImage = new Image(new Texture(Gdx.files.internal("triangle.png")));

        Label.LabelStyle labelStyle = game.mySkin.get(Label.LabelStyle.class);
        Label.LabelStyle newLabelStyle = new Label.LabelStyle(labelStyle);
        newLabelStyle.fontColor = Color.WHITE;
        Label numberLabel = new Label(weight, newLabelStyle);

        // Create a table to hold the triangle and number
        triangleImage.setSize(triangleSize, triangleSize);
        triangleImage.setPosition(centerX, centerY);
        stack = new Stack();

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
