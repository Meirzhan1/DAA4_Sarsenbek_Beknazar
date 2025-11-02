package graph.common;

import java.util.*;

public class Graph {
    private final int V;
    private final Map<Integer, List<Edge>> adj;
    private final Map<Integer, List<Edge>> reverseAdj;

    public Graph(int V) {
        this.V = V;
        this.adj = new HashMap<>();
        this.reverseAdj = new HashMap<>();
        for (int i = 0; i < V; i++) {
            adj.put(i, new ArrayList<>());
            reverseAdj.put(i, new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(u, v, w));
        reverseAdj.get(v).add(new Edge(v, u, w));
    }

    public void addSimpleEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(u, v, w));
    }

    public int getV() {
        return V;
    }

    public List<Edge> getAdj(int v) {
        return adj.getOrDefault(v, Collections.emptyList());
    }

    public List<Edge> getReverseAdj(int v) {
        return reverseAdj.getOrDefault(v, Collections.emptyList());
    }

    public Collection<Integer> getVertices() {
        return adj.keySet();
    }
}