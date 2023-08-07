package com.mygdx.dijkstra;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.List;

public class MapGroup_Level2_3 extends Group {
    final DijkstraAlgorithm game;
    public MapGroup_Level2_3(final DijkstraAlgorithm game, int mode, Graph connections, final Image boatImage, List<LineData> linesToDraw){
        this.game = game;
        for (int i = 0; i < game.vertices; i++) {
            final City sourceCity = game.cities.get(i);
            Table portTable = new Ports(sourceCity, game);
            if (mode == 4 || mode == 8) {
                portTable.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        boatImage.addAction(Actions.moveTo(sourceCity.getX() - boatImage.getHeight() / 2, sourceCity.getY() - boatImage.getHeight() / 2, 1.5f));
                    }
                });
            }
            addActor(portTable);
            java.util.List<Edge> neighbors = connections.getNeighbors(i);
            for (int j = 0; j < neighbors.size(); j++) {

                int destination = neighbors.get(j).destination;
                int weight = neighbors.get(j).weight;
                City destCity = game.cities.get(destination);

                Vector2 start = new Vector2(sourceCity.x, sourceCity.y);
                Vector2 end = new Vector2(destCity.x, destCity.y);
                Image connectionArea = new ConnectionAreaImage(sourceCity, destCity);

                ConnectionHoverActor card = new ConnectionHoverActor(game, (float) (destCity.x + sourceCity.x) / 2,
                        (float) (destCity.y + sourceCity.y) / 2, 150, 60, sourceCity.name, destCity.name, weight, false);
                final Table cardTableFinal = card.getTable();

                connectionArea.addListener(new InputListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        addActor(cardTableFinal);
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        cardTableFinal.remove();
                    }
                });
                linesToDraw.add(new LineData(start, end, Color.DARK_GRAY));
                addActor(connectionArea);
            }
        }
    }
}
