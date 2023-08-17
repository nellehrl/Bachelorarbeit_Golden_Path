package com.mygdx.dijkstra.views;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygdx.dijkstra.DijkstraAlgorithm;
import com.mygdx.dijkstra.models.WrongUserInput;
import com.mygdx.dijkstra.systems.GameScreen_Level3;

import static com.badlogic.gdx.utils.Align.center;

public class CheckMoveBoatGroup extends Group {
    private Table codeTable;
    private String input = "";
    private final DijkstraAlgorithm game;
    private final int iteration;
    private final String correctAnswer;
    private final Label mangoCounterLabel;
    private final GameScreen_Level3 level3;

    public CheckMoveBoatGroup(GameScreen_Level3 level3,Label mangoCounterLabel, final float x, final float y, final float width, final float height, final DijkstraAlgorithm game, final Stage stage, String correctAnswer, int iteration) {
        this.game = game;
        this.correctAnswer = correctAnswer;
        this.iteration = iteration;
        this.mangoCounterLabel = mangoCounterLabel;
        this.level3 = level3;

        initializeCodeTable(x, y, width, height, stage);
    }

    private void initializeCodeTable(final float x, final float y, final float width, final float height, final Stage stage) {

        String text = "Which is the city with the current lowest costs?";

        codeTable = new Table(game.getFontSkin());
        codeTable.setSize(width, height);
        codeTable.setPosition(x, y);

        Label codeLabel = new Label(text, game.getFontSkin());
        codeLabel.setWrap(true);
        codeLabel.setAlignment(center);
        codeLabel.setFontScale(1.0f);

        // Set the background color of the codeTable
        codeTable.setBackground(game.getFontSkin().getDrawable("color"));

        // Handle input events or validation for the codeInput field as needed
        createCard(codeLabel, width, height, stage);
        addActor(codeTable);
    }

    private void createCard(Label codeLabel, float width, float height, final Stage stage) {
        // Add an input field to the codeTable
        codeTable.add(codeLabel).expand().fill().center().padLeft(game.getSpace()).padRight(game.getSpace());
        codeTable.row().colspan(3);
        final TextField codeInput = new TextField("", game.getFontSkin());
        codeInput.setMessageText("Shortage of city");
        codeTable.add(codeInput).width(width/2).height(height/6).center().padBottom(game.getSpace());
        codeInput.addListener(new InputListener() {
            @Override
            public boolean keyTyped(InputEvent event, char key) {
                input += key;
                if (key == '\r' || key == '\n') {
                    checkIfLevelLost(input, stage, correctAnswer,codeInput);
                    input = "";
                }
                return super.keyTyped(event, key);
            }
        });
    }

    private void checkIfLevelLost(String input, Stage stage, String correctAnswer,TextField textField) {
        if (correctAnswer.trim().equalsIgnoreCase(input.trim())) level3.moveBoatToNextCity(iteration);
        else{
            textField.setText((""));
            new WrongUserInput(mangoCounterLabel,game, stage, level3.level);
        }
    }
}
