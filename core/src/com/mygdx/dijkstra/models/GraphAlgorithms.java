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
        dijkstra();
    }
    public void dijkstra() {
        int count = 0, source = 0;
        boolean[] visited = new boolean[vertices];

        Arrays.fill(distances, Integer.MAX_VALUE); //fill distances with infinity to initialize it
        distances[source] = 0; //set distance to zero at source node

        //got the priorityQueue idea from chatgpt conversation on the 13th of July 3.32
        Comparator<Node> nodeComparator = new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                return Integer.compare(node1.getDistance(), node2.getDistance());
            }
        };
        PriorityQueue<Node> minHeap = new PriorityQueue<>(nodeComparator);

        precursor = new int[vertices][vertices];
        minHeap.add(new Node(source, 0));

        //loop until all graph are found
        while (!minHeap.isEmpty()) {
            //get the node with the lowest distances + current node
            Node currentNode = minHeap.poll();
            int vertex = currentNode.getVertex();
            if (!dijkstraConnections.contains(vertex)) dijkstraConnections.add(vertex);

            // if node has already been visited go to next one otherwise set visited
            if (visited[vertex]) continue;

            visited[vertex] = true;
            List<Edge> neighbors = graph.getNeighbors(vertex);

            //Calculate distance to each node and add to queue (which orders them then by distance)
            for (Edge edge : neighbors) {
                int neighbor = edge.getDestination();
                if (visited[neighbor]) continue;

                int weight = edge.getWeight();
                int newDistance = distances[vertex] + weight;

                //check if there has already been a shorter connection via a different node
                if (newDistance < distances[neighbor]) {
                    for(int i = count; i < vertices; i++){
                        precursor[i][neighbor] = vertex;
                    }
                    distances[neighbor] = newDistance;
                    minHeap.add(new Node(neighbor, newDistance));
                }

            }
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
