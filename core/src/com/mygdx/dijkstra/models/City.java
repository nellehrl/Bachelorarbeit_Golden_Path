package com.mygdx.dijkstra.models;

public class City {

    String name;
    int x;
    int y;
    String shortName;

    public City(String name, int x, int y, String shortName) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.shortName = shortName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setShortName(String name) {
        this.shortName = name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return this.x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return this.y;
    }

}
