package com.mygdx.dijkstra.models;

import java.util.*;

public class GraphAlgorithms {
    private final Graph graph;
    private final int[] distances;
    private final int vertices;
    private final int[][] iterations;
    private int[][] precursor;
    private final ArrayList<Integer> dijkstraConnections;

    public GraphAlgorithms(Graph graph){
        this.graph = graph;
        this.vertices = graph.getNumVertices();
        this.distances = new int[vertices];
        this.iterations = new int[vertices][vertices];
        this.dijkstraConnections = new ArrayList<>();
        dijkstra(0);
    }
    public void dijkstra(int source) {
        int count = 0;
        boolean[] visited = new boolean[vertices];

        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[source] = 0;

        //priorityQueue from chatgpt conversation on the 13th of July 3.32pm
        PriorityQueue<Node> minHeap = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                return Integer.compare(node1.getDistance(), node2.getDistance());
            }
        });

        precursor = new int[vertices][vertices];
        minHeap.add(new Node(source, 0));

        while (!minHeap.isEmpty()) {

            Node currentNode = minHeap.poll();
            int vertex = currentNode.getVertex();
            if (!dijkstraConnections.contains(vertex)) dijkstraConnections.add(vertex);

            if (visited[vertex]) continue;

            visited[vertex] = true;
            List<Edge> neighbors = graph.getNeighbors(vertex);

            for (Edge edge : neighbors) {
                int neighbor = edge.getDestination();
                if (visited[neighbor]) continue;

                int weight = edge.getWeight();
                int newDistance = distances[vertex] + weight;

                if (newDistance < distances[neighbor]) {
                    precursor[count][neighbor] = vertex;
                    distances[neighbor] = newDistance;
                    minHeap.add(new Node(neighbor, newDistance));
                }
            }
            if (count + 1 < vertices) System.arraycopy(precursor[count], 0, precursor[count + 1], 0, vertices);
            System.arraycopy(distances, 0, iterations[count], 0, vertices);
            count++;
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public int[] getDistances() {
        return distances;
    }

    public int getVertices() {
        return vertices;
    }

    public int[][] getIterations() {
        return iterations;
    }

    public int[][] getPrecursor() {
        return precursor;
    }

    public ArrayList<Integer> getDijkstraConnections() {
        return dijkstraConnections;
    }
}
