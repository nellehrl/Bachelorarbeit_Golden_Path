package com.mygdx.dijkstra.views;

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
import com.mygdx.dijkstra.DijkstraAlgorithm;
import com.mygdx.dijkstra.models.LineData;
import com.mygdx.dijkstra.models.City;
import com.mygdx.dijkstra.models.Edge;
import com.mygdx.dijkstra.models.Graph;

import java.util.List;

public class MapGroup_Level2_3 extends Group {
    final DijkstraAlgorithm game;
    private final int level;
    private final Image boatImage;

    public MapGroup_Level2_3(final DijkstraAlgorithm game, int level, Graph graph, final Image boatImage, List<LineData> linesToDraw) {
        this.game = game;
        this.level = level;
        this.boatImage = boatImage;

        initializePorts(graph, linesToDraw);
    }

    private void initializePorts(Graph graph, List<LineData> linesToDraw) {
        for (int i = 0; i < game.getVertices(); i++) {
            final City sourceCity = game.getCities().get(i);
            Table portTable = new Ports(sourceCity, game);
            if (level == 4 || level == 8) portTable.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    boatImage.addAction(Actions.moveTo(sourceCity.getX() - boatImage.getHeight() / 2, sourceCity.getY() - boatImage.getHeight() / 2, 1.5f));
                }
            });
            addActor(portTable);
            java.util.List<Edge> neighbors = graph.getNeighbors(i);
            for (Edge neighbor : neighbors) {
                int destination = neighbor.getDestination();
                int weight = neighbor.getWeight();
                City destCity = game.getCities().get(destination);

                Vector2 start = new Vector2(sourceCity.getX(), sourceCity.getY());
                Vector2 end = new Vector2(destCity.getX(), destCity.getY());
                Image connectionArea = new ConnectionAreaImage(sourceCity, destCity);

                ConnectionHoverActor card = new ConnectionHoverActor(game, (float) (destCity.getX() + sourceCity.getX()) / 2,
                        (float) (destCity.getY() + sourceCity.getY()) / 2, 150, 60, sourceCity.getName(), destCity.getName(), weight);
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
