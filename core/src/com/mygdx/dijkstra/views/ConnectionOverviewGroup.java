package com.mygdx.dijkstra.views;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.dijkstra.DijkstraAlgorithm;
import com.mygdx.dijkstra.models.Edge;
import com.mygdx.dijkstra.models.City;
import com.mygdx.dijkstra.models.Graph;

import java.util.ArrayList;

import static com.badlogic.gdx.utils.Align.left;

public class ConnectionOverviewGroup extends Group {

    public ConnectionOverviewGroup(int vertices, ArrayList<City> cities, final DijkstraAlgorithm game, int width, int height, int x, int y, Graph connections, int level) {
        for (int i = 0; i < vertices; i++) {
            java.util.List<Edge> neighbors = connections.getNeighbors(i);
            for (int j = 0; j < neighbors.size(); j++) {
                int destination = neighbors.get(j).getDestination();
                String destCity = cities.get(destination).getName();
                String sourceCity = cities.get(i).getName();
                int weight = neighbors.get(j).getWeight();
                String boxText = "";

                switch (level) {
                    case 1:
                        boxText = sourceCity + " - " + destCity;
                        break;
                    case 2:
                        boxText = sourceCity + " > " + destCity;
                        break;
                    case 3:
                        boxText = sourceCity + " > " + destCity + ": " + weight;
                }

                int space = 10;
                Table infoTable = new Table(game.getFontSkin());
                infoTable.setSize(width, (float) height / 3);
                infoTable.setPosition(x + infoTable.getWidth() * i + space * (i + 1), y - infoTable.getHeight() * j - space * j);

                createCodeLabel(boxText, game.getFontSkin(), infoTable, game);
                Drawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(game.getAssetManager().get("white 1.png", Texture.class)));
                infoTable.setBackground(backgroundDrawable);
                addActor(infoTable);
            }
        }
    }

    private void createCodeLabel(String boxText, Skin skin, Table infoTable, DijkstraAlgorithm game) {
        Label codeLabel = new Label(boxText, skin);
        codeLabel.setAlignment(left);
        codeLabel.setFontScale(0.7f);
        infoTable.add(codeLabel);
    }
}
