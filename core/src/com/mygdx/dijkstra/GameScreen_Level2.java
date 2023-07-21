package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;;
import java.util.List;

import static com.badlogic.gdx.utils.Align.left;

public class GameScreen_Level2 implements Screen {
    final DijkstraAlgorithm game;
    Image boatImage, connectionArea, cockpit;
    Table portTable;
    OrthographicCamera camera;
    private final FitViewport fitViewport;
    ArrayList<City> currentConnection = new ArrayList<>();
    private Stage stage;
    private final ScreenViewport viewport = new ScreenViewport();
    Button mainMenuButton,closeButton, doneButton;
    Group tableGroup;
    private List<LineData> linesToDraw;
    DropBox dropBox;
    Group background;
    Graph connections;
    String text;
    InfoText infotext;

    public GameScreen_Level2(final DijkstraAlgorithm game) {

        this.game = game;

        stage = new Stage(viewport);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fitViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        //init cities and starting point
        currentConnection.add(game.cities.get(0));

        connections = new Graph(game.vertices, 1);

        background = new Background(game, 2);
        for (Actor actor : background.getChildren()) {
            if(actor.getName().equals("mainMenuButton")) {
                mainMenuButton = (Button) actor;
                mainMenuButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(new MainMenuScreen(game));
                        dispose();
                    }
                });
            }
            else if(actor.getName().equals("dropBox")) dropBox = (DropBox)actor;
            else if(actor.getName().equals("cockpit")) cockpit = (Image)actor;
            else if (actor.getName().equals("doneButton")){
                doneButton = (Button) background.getChild(5);
                doneButton.remove();
            }
            else if(actor.getName().equals("boatImage")) boatImage = (Image)actor;
        }

        linesToDraw = new ArrayList<>();
        tableGroup = new Group();
        for (int i = 0; i < game.vertices; i++) {

            City sourceCity = game.cities.get(i);
            portTable = new Ports(sourceCity, game.fontSkin);
            tableGroup.addActor(portTable);
            java.util.List<Edge> neighbors = connections.getNeighbors(i);

            for (int j = 0; j < neighbors.size(); j++) {
                int destination = neighbors.get(j).destination;
                City destCity = game.cities.get(destination);
                int weight = neighbors.get(j).weight;

                Vector2 start = new Vector2(sourceCity.x, sourceCity.y);
                Vector2 end = new Vector2(destCity.x, destCity.y);

                connectionArea = new ConnectionArea(sourceCity, destCity);
                Image connectionArea2 = new ConnectionArea(destCity, sourceCity);
                tableGroup.addActor(connectionArea);
                tableGroup.addActor(connectionArea);

                LineData lineData = new LineData(start, end, Color.BLACK);
                linesToDraw.add(lineData);

                InfoCard card = new InfoCard(game.fontSkin, (float) (destCity.x + sourceCity.x) / 2,
                        (float) (destCity.y + sourceCity.y) / 2, 150, 60, sourceCity.name, destCity.name, weight, false);
                final Table cardTableFinal = card.getTable(); // Create a final variable

                connectionArea.addListener(new InputListener() {
                    final Table cardTable = cardTableFinal; // Use the final variable
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        stage.addActor(cardTable);
                    }
                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        cardTable.remove();
                    }
                });
                connectionArea2.addListener(new InputListener() {
                    final Table cardTable = cardTableFinal; // Use the final variable
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        stage.addActor(cardTable);
                    }
                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        cardTable.remove();
                    }
                });
            }
        }
        dropBoxItems();

        text = "You know who this is, Captain! \n\nFantastic job so far! We updated our system. Now that you have added " +
                "the right weight to the connections, we have a great overview over everything!\n\n" +
                "Let`s check if everything is clear - find out the weights for the connections written down below.\n\n" +
                "We need to understand how these graphs work to get ahead of the other crews. Like that we can generate " +
                "much more loooooooooooooooot!";

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
        stage.addActor(cockpit);
        tableGroup.addActor(boatImage);
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

        //render background
        game.batch.begin();
        background.draw(game.batch, 1.0f);
        game.batch.end();

        //render lines alias connections
        for (LineData lineData : linesToDraw) {
            new DrawLineOrArrow(5, camera.combined, lineData.getColor(), lineData.getStart(), lineData.getEnd(),1);
        }

        //render stage
        stage.act();
        stage.draw();

    }

    public void dropBoxItems() {
        for (int i = 0; i < game.vertices; i++) {
            java.util.List<Edge> neighbors = connections.getNeighbors(i);
            for (int j = 0; j < neighbors.size(); j++) {
                int destination = neighbors.get(j).destination;
                final int weight = neighbors.get(j).weight;
                City destCity = game.cities.get(destination);
                City sourceCity = game.cities.get(i);
                String destCityName = destCity.name;
                String sourceCityName = game.cities.get(i).name;

                if (sourceCity != destCity) {
                    final Vector2 start = new Vector2(sourceCity.x, sourceCity.y);
                    final Vector2 end = new Vector2(destCity.x, destCity.y);

                    String labelText = sourceCityName + " - " + destCityName;
                    final String fieldText = "Costs: ";

                    Table infoTable = new Table(game.fontSkin);
                    infoTable.setSize((camera.viewportWidth)/(game.vertices), 50);
                    infoTable.setPosition(2*game.offset + infoTable.getWidth() * i + game.space * (i + 1), (float) ((camera.viewportHeight*0.28) - game.offset - (infoTable.getHeight() + game.space) * j));

                    Label codeLabel = new Label(labelText, game.fontSkin);
                    codeLabel.setAlignment(left);
                    codeLabel.setFontScale(0.75f);
                    infoTable.add(codeLabel).row();
                    final TextField textfield = new TextField(fieldText, game.fontSkin);
                    textfield.setAlignment(left);
                    textfield.addListener(new ClickListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            String userInput = textfield.getText();
                            boolean isCorrect = userInput.equals(fieldText + weight);
                            if (!isCorrect) {
                                textfield.setColor(Color.RED);
                                linesToDraw.add(new LineData(start, end, Color.RED));
                            }
                            return true;
                        }
                    });
                    textfield.setTextFieldListener(new TextField.TextFieldListener() {
                        @Override
                        public void keyTyped(TextField textField, char key) {
                            String userInput = textField.getText();
                            boolean isCorrect = userInput.equals(fieldText + weight);
                            if (isCorrect) {
                                textField.setColor(Color.GREEN);
                                linesToDraw.add(new LineData(start, end, Color.GREEN));
                            }
                        }
                    });
                    infoTable.add(textfield);
                    Drawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("gray.png"))));
                    infoTable.setBackground(backgroundDrawable);
                    tableGroup.addActor(infoTable);
                }
            }
        }
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
