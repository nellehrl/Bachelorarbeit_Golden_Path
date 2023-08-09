package com.mygdx.dijkstra.views;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.dijkstra.DijkstraAlgorithm;

import static com.badlogic.gdx.utils.Align.left;

public class HintGroup extends Group {
    private final DijkstraAlgorithm game;
    public HintGroup(String hintText, DijkstraAlgorithm game){
        this.game = game;
        Table textBoxTable = createHintTable();
        Label textBox = createHintLabel(hintText, textBoxTable);

        addFadeOutAndRemoveAction(textBoxTable);
        addFadeOutAndRemoveAction(textBox);

        addActor(textBoxTable);
        addActor(textBox);
    }

    private Table createHintTable() {
        Table textBoxTable = new Table(game.getFontSkin());
        textBoxTable.setSize(game.getCamera().viewportWidth / 4 + 2 * game.getSpace(), game.getCamera().viewportHeight / 10);
        textBoxTable.setPosition(game.getParrotImage().getX() - textBoxTable.getWidth() - game.getSpace(),
                game.getParrotImage().getY() + game.getParrotImage().getHeight(), left);

        Drawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(game.getAssetManager().get("white 1.png", Texture.class)));
        textBoxTable.setBackground(backgroundDrawable);

        return textBoxTable;
    }

    private Label createHintLabel(String hintText, Table textBoxTable) {
        Label textBox = new Label(hintText, game.getFontSkin());
        textBox.setPosition(game.getParrotImage().getX() - textBoxTable.getWidth(),
                game.getParrotImage().getY() + game.getParrotImage().getHeight(), left);
        textBox.setFontScale(0.7f);
        textBox.setAlignment(left);
        textBox.setWrap(true);
        textBox.setWidth(game.getCamera().viewportWidth / 4);

        return textBox;
    }

    private void addFadeOutAndRemoveAction(Actor actor) {
        actor.addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.fadeOut(1f),
                Actions.removeActor()
        ));
    }
}
