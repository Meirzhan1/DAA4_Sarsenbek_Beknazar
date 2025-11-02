package graph.dagsp;

import graph.common.Edge;
import graph.common.Graph;
import graph.common.Metrics;

import java.util.*;
public class DAGShortestPath {
    private final Graph graph;
    private final Metrics metrics;
    private final List<Integer> topoOrder;
    private final int[] dist;
    private final int[] predecessor;
    private static final int INF = Integer.MAX_VALUE / 2;

    public DAGShortestPath(Graph graph, Metrics metrics, List<Integer> topoOrder) {
        this.graph = graph;
        this.metrics = metrics;
        this.topoOrder = topoOrder;
        this.dist = new int[graph.getV()];
        this.predecessor = new int[graph.getV()];

        Arrays.fill(dist, INF);
        Arrays.fill(predecessor, -1);
    }
    public void findShortestPaths(int s) {
        metrics.startTimer();

        dist[s] = 0;
        for (int u : topoOrder) {
            if (dist[u] != INF) {
                for (Edge edge : graph.getAdj(u)) {
                    int v = edge.target;

                    if (dist[v] > dist[u] + edge.weight) {
                        dist[v] = dist[u] + edge.weight;
                        predecessor[v] = u;
                        metrics.incrementRelaxations();
                    }
                }
            }
        }

        metrics.stopTimer();
    }

    public void findLongestPaths(int s) {
        metrics.startTimer();
        Arrays.fill(dist, INF);
        Arrays.fill(predecessor, -1);
        final int N_INF = -INF;
        Arrays.fill(dist, N_INF);
        dist[s] = 0;

        for (int u : topoOrder) {
            if (dist[u] != N_INF) {
                for (Edge edge : graph.getAdj(u)) {
                    int v = edge.target;

                    if (dist[v] < dist[u] + edge.weight) {
                        dist[v] = dist[u] + edge.weight;
                        predecessor[v] = u;
                        metrics.incrementRelaxations();
                    }
                }
            }
        }

        metrics.stopTimer();
    }
    public List<Integer> reconstructPath(int target) {
        LinkedList<Integer> path = new LinkedList<>();
        if (dist[target] == INF || dist[target] == -INF) {
            return path;
        }

        int curr = target;
        while (curr != -1) {
            path.addFirst(curr);
            curr = predecessor[curr];
        }

        if (path.isEmpty() || predecessor[path.getFirst()] != -1) {
            return Collections.emptyList();
        }

        return path;
    }

    public int getDistance(int v) {
        return dist[v];
    }

    public int[] getAllDistances() {
        return dist;
    }
}