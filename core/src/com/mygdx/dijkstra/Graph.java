package com.mygdx.dijkstra;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private int numVertices;
    List<Edge>[] adjacencyList;
    int numOfEdges;

    public Graph(int numVertices, int mode) {
        this.numVertices = numVertices;
        adjacencyList = new List[numVertices];

        for (int i = 0; i < numVertices; i++) adjacencyList[i] = new ArrayList<>();
        createGraph(numVertices, mode);
    }

    public void createGraph(int vertices, int mode) {
        double probability;
        switch (mode) {
            case 1:
                probability = 0.3;
                for (int i = 0; i < vertices; i++) {
                    ArrayList<Integer> connected = new ArrayList<>();
                    for (int j = i + 1; j < vertices; j++) {
                        if (probability < Math.random() && connected.size() < vertices / 2) {
                            double weight = Math.random() * 10 + 1;
                            this.addEdge(i, j, (int) weight);
                            connected.add(1);
                        }
                    }
                }
                break;
            case 2:
                probability = 0.3;
                for (int i = 0; i < vertices; i++) {
                    ArrayList<Integer> connected = new ArrayList<>();
                    boolean isConnected = false; // Flag to check if the current node is connected

                    // Ensure that each node is connected to at least one other node
                    if (i != 0) {
                        int randomNode = (int) (Math.random() * i); // Choose a random node from previous nodes
                        double weight = Math.random() * 10 + 1;
                        this.addEdge(i, randomNode, (int) weight);
                        connected.add(randomNode);
                        isConnected = true;
                    }

                    // Connect the remaining nodes with a given probability
                    for (int j = i + 1; j < vertices; j++) {
                        if ((isConnected || probability < Math.random()) && connected.size() < vertices/2) {
                            double weight = Math.random() * 10 + 1;
                            this.addEdge(i, j, (int) weight);
                            connected.add(j);
                        }
                    }
                }
        }
        if(0 == this.getNeighbors(0).size()){
            int randomNode = (int) (Math.random() * vertices);
            this.addEdge(0, randomNode, (int) (Math.random() * 10 + 1));
        }
    }

    public void addEdge(int source, int destination, int weight) {
        Edge edge = new Edge(source, destination, weight);
        adjacencyList[source].add(edge);
        numOfEdges++;
    }

    public void removeEdge(int source, int destination, int weight) {
        Edge edge = new Edge(source, destination, weight);
        adjacencyList[source].remove(edge);
        numOfEdges--;
    }

    public List<Edge> getNeighbors(int vertex) {
        return adjacencyList[vertex];
    }

    public int getNumVertices() {
        return numVertices;
    }
}
