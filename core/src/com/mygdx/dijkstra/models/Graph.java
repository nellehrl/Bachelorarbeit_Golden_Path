package com.mygdx.dijkstra.models;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final int numVertices;
    private final List<Edge>[] adjacencyList;
    private int numOfEdges;;

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

            // Ensure that each node is connected to at least one other node before
            if (i != 0) {
                int randomNode = (int) (Math.random() * i);
                double weight = Math.random() * 10 + 1;
                while (randomNode == i){
                    randomNode = (int) (Math.random() * i);
                }
                this.addEdge(i, randomNode, (int) weight);
                connected.add(randomNode);
            }

            // Connect the remaining nodes with a given probability
            for (int j = i + 1; j < vertices; j++) {
                if (probability < Math.random() && connected.size() < numVertices/2) {
                    double weight = Math.random() * 10 + 1;
                    this.addEdge(i, j, (int) weight);
                    connected.add(j);
                }
            }
        }
        checkIfStartIsConnected();
    }

    public void checkIfStartIsConnected(){
        if (0 == this.getNeighbors(0).size()) {
            int randomNode = (int) (Math.random() * numVertices);
            this.addEdge(0, randomNode, (int) (Math.random() * 10 + 1));
        }
    }

    public void addEdge(int source, int destination, int weight) {
        for (Edge e : adjacencyList[destination]) {
            if (e.getDestination() == source) {
              destination = findOtherConnection(source, destination);
            }
        }
        for (Edge e : adjacencyList[source]) {
            if (e.getDestination() == destination) {
                destination = findOtherConnection(source, destination);
            }
        }
        Edge edge = new Edge(source, destination, weight);
        adjacencyList[source].add(edge);
        numOfEdges++;
    }

    public int findOtherConnection(int source, int destination){
        int newDestination = 0;
        for(int i = 0; i < 10; i++) {
            newDestination = (int) (Math.random() * numVertices);
            if (newDestination != source) {
                for (Edge newEdgeCheck : adjacencyList[destination]) {
                    if (newEdgeCheck.getDestination() == source) {
                        break;
                    }
                }
                for (Edge newEdgeCheck : adjacencyList[source]) {
                    if (newEdgeCheck.getDestination() == newDestination) {
                        break;
                    }
                }
                break;
            }
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
