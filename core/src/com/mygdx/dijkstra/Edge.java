package com.mygdx.dijkstra;

public class Edge {
    int source;
    int destination;
    int weight;

    public Edge(int source, int destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public int getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }
}
