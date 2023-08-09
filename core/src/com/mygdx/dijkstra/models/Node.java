package com.mygdx.dijkstra.models;

public class Node {
    int vertex;
    int distance;

    public Node(int vertex, int distance) {
        this.vertex = vertex;
        this.distance = distance;
    }

    public int getDistance() {
        return this.distance;
    }

    public int getVertex() {
        return this.vertex;
    }
}
