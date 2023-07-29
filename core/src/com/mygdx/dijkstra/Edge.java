package com.mygdx.dijkstra;

public class Edge {
    int source;
    int destination;
    int weight;
    private boolean edgeAdded;

    public Edge(int source, int destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        this.edgeAdded = false;
    }

    public boolean isEdgeAdded() {
        return edgeAdded;
    }

    public void setEdgeAdded(boolean edgeAdded) {
        this.edgeAdded = edgeAdded;
    }

    public int getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }
}
