package com.mygdx.dijkstra.models;

import com.mygdx.dijkstra.DijkstraAlgorithm;
import com.mygdx.dijkstra.systems.LevelWonScreen;

public class CheckCodeModel {
    private String input = "";
    private String text, option1, option2, option3;
    private final DijkstraAlgorithm game;
    private final int level;

    public CheckCodeModel(DijkstraAlgorithm game, int level){
        this.game = game;
        this.level = level;

        setTextByLevel(level);
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getText() {
        return text;
    }

    public void setTextByLevel(int level) {
        switch (level) {
            case 1:
                this.text = "What is an undirected graph:";
                break;
            case 2:
                this.text = "What is a directed graph:";
                break;
            case 3:
                this.text = "What is a weighted graph:";
                break;
            case 4:
                this.text = "What kind of graph was represented:";
                break;
            case 5:
                this.text = "Enter the Code (final costs to each city from " + game.getCities().get(0).getShortName() +"):";
                break;
            case 6:
            case 7:
            case 8:
                this.text = "Enter the exact route and the final costs: " + game.getCities().get(0).getShortName() + " -> " + game.getCities().get(5).getShortName() + "?\n";
                break;
            default:
                this.text = "";
        }
    }

    public String getOption1() {
        return option1;
    }
    public String getOption2() {
        return option2;
    }
    public String getOption3() {
        return option3;
    }

    public void setOptionsByLevel(int level) {
        switch (level) {
            case 1:
                this.option1 = "Goes in both directions";
                this.option2 = "Has no destination";
                this.option3 = "Has no source";
                break;
            case 2:
                this.option1 = "Has a destination";
                this.option2 = "Has a source and a destination";
                this.option3 = "Has a source";
                break;
            case 3:
                this.option1 = "A directed graph";
                this.option2 = "Each edge has a cost";
                this.option3 = "The graph has a cost";
                break;
            case 4:
                this.option1 = "A weighted graph";
                this.option2 = "A directed graph";
                this.option3 = "A weighted and directed graph";
                break;
            default:
                this.option1 = "";
                this.option2 = "";
                this.option3 = "";
        }
    }

    public void correctInput() {
        if (game.getMangos() < 30) {
            game.setMangos(30);
        }
        game.getDropSound().play();
        game.resetGlobalState();
        game.setScreen(new LevelWonScreen(game, level));
    }

    public boolean checkInputAgainstCode(String code) {
        return input.trim().replaceAll("\\s", "").equalsIgnoreCase(code.trim().replaceAll("\\s", ""));
    }
}
