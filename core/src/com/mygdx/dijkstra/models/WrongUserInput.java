package com.mygdx.dijkstra.models;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.dijkstra.DijkstraAlgorithm;
import com.mygdx.dijkstra.systems.GameScreen_Level1;
import com.mygdx.dijkstra.systems.MainMenuScreen;
import com.mygdx.dijkstra.views.LevelLostGroup;

public class WrongUserInput {
    private final Label mangoCounterLabel;
    private final DijkstraAlgorithm game;
    public WrongUserInput(Label mangoCounterLabel, DijkstraAlgorithm game, Stage stage, int level) {
        this.mangoCounterLabel = mangoCounterLabel;
        this.game = game;
        provideNegativeFeedback(stage);

        int newValue = Integer.parseInt(String.valueOf(mangoCounterLabel.getText())) - 10;
        if (newValue > 0) {
            mangoCounterLabel.setText(newValue);
        } else {
            handleLevelLost(stage, level);
        }
    }

    private void handleLevelLost(Stage stage, final int level) {
        game.getParrotImage().remove();
        LevelLostGroup lost = new LevelLostGroup(game, game.getCamera());
        stage.addActor(lost);

        Button close = (Button) lost.getChild(2);
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setMangos(30);
                mangoCounterLabel.setText(game.getMangos());
                game.resetGlobalState();
                game.setScreen(new GameScreen_Level1(game, level));
            }
        });
    }

    private void provideNegativeFeedback(Stage stage) {
        System.out.println(game.getVolume());
        game.getBattle().play(game.getVolume());
        stage.addActor(game.getBlood());
        game.getBlood().addAction(Actions.sequence(
                Actions.fadeOut(1f),
                Actions.removeActor()
        ));
    }
}
