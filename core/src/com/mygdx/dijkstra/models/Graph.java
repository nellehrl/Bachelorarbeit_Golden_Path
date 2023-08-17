package com.mygdx.dijkstra.models;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final int numVertices;
    private final List<Edge>[] adjacencyList;
    private int numOfEdges;

    public Graph(int numVertices) {
        this.numVertices = numVertices;
        adjacencyList = new List[numVertices];

        for (int i = 0; i < numVertices; i++) adjacencyList[i] = new ArrayList<>();
        createGraph(numVertices);
    }

    public void createGraph(int vertices) {
        double probability;
        probability = 0.3;
        for (int i = 0; i < vertices; i++) {
            ArrayList<Integer> connected = new ArrayList<>();

            if (i != 0) {
                int randomNode = (int) (Math.random() * i);
                double weight = Math.random() * 10 + 1;
                this.addEdge(i, randomNode, (int) weight);
                connected.add(randomNode);
            }

            for (int j = i + 1; j < vertices; j++) {
                if (probability < Math.random() && connected.size() < numVertices / 2) {
                    double weight = Math.random() * 10 + 1;
                    this.addEdge(i, j, (int) weight);
                    connected.add(j);
                }
            }
        }
        checkIfStartIsConnected();
        ensureEachCityIsDestination();
    }

    public void checkIfStartIsConnected(){
        if (0 == this.getNeighbors(0).size()) {
            int randomNode = (int) (Math.random() * numVertices);
            this.addEdge(0, randomNode, (int) (Math.random() * 10 + 1));
        }
    }
    public void ensureEachCityIsDestination() {
        for (int i = 0; i < numVertices; i++) {
            boolean isDestination = false;

            for (int j = 0; j < numVertices; j++) {
                if (i != j) {
                    if (hasEdge(j, i)) {
                        isDestination = true;
                        break;
                    }
                }
            }

            if (!isDestination) {
                int randomSource;
                do {
                    randomSource = (int) (Math.random() * numVertices);
                } while (randomSource == i || hasEdge(randomSource, i) || hasEdge(i, randomSource));

                Edge edge = new Edge(randomSource, i, (int) (Math.random() * 10 + 1));
                adjacencyList[randomSource].add(edge);
                numOfEdges++;
            }
        }
    }

    public void addEdge(int source, int destination, int weight) {
        if (hasEdge(source, destination) || hasEdge(destination, source)) {
            destination = findOtherConnection(source, destination);
        }

        Edge edge = new Edge(source, destination, weight);
        adjacencyList[source].add(edge);
        numOfEdges++;
    }

    private boolean hasEdge(int source, int destination) {
        for (Edge edge : adjacencyList[source]) {
            if (edge.getDestination() == destination) {
                return true;
            }
        }
        return false;
    }

    public int findOtherConnection(int source, int destination){
        int newDestination = 0;
        for(int i = 0; i < 10; i++) {
            newDestination = (int) (Math.random() * numVertices);

            if (newDestination == source) continue;  // Skip if new destination is the same as the source.

            boolean hasNewEdge = false;
            for (Edge newEdgeCheck : adjacencyList[destination]) {
                if (newEdgeCheck.getDestination() == source) {
                    hasNewEdge = true;
                    break;
                }
            }

            for (Edge newEdgeCheck : adjacencyList[source]) {
                if (newEdgeCheck.getDestination() == newDestination) {
                    hasNewEdge = true;
                    break;
                }
            }

            if (!hasNewEdge) break; // If no duplicate edges found, exit the loop.
        }

        return newDestination;  // Set the new destination for the edge
    }

    public List<Edge> getNeighbors(int vertex) {
        return adjacencyList[vertex];
    }

    public int getNumVertices() {
        return numVertices;
    }

    public int getNumOfEdges() {
        return numOfEdges;
    }

    public List<Edge>[] getAdjacencyList() {
        return adjacencyList;
    }
}
