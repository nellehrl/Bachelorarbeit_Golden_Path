package com.mygdx.dijkstra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class DropBoxWindow extends Window {

    private static final WindowStyle windowStyle;

    static {
        Skin mySkin = new Skin(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));

        // Set the window background region
        windowStyle = new Window.WindowStyle();
        windowStyle.background = mySkin.getDrawable("window-c");

        // Set the font for the window title label
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = mySkin.getFont("font");
        windowStyle.titleFont = labelStyle.font;

        mySkin.add("default", windowStyle);
    }

    /**
     * Default constructor.
     */
    public DropBoxWindow() {
        super("", windowStyle);
        setClip(false);
        setTransform(true);
    }
}