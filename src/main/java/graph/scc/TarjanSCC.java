package graph.scc;

import graph.common.Edge;
import graph.common.Graph;
import graph.common.Metrics;

import java.util.*;

public class TarjanSCC {
    private final Graph graph;
    private final Metrics metrics;

    private int time;
    private final int[] disc;
    private final int[] low;
    private final boolean[] onStack;
    private final Stack<Integer> stack;

    private final List<List<Integer>> sccs;
    private final int[] sccId;

    public TarjanSCC(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
        int V = graph.getV();
        this.disc = new int[V];
        this.low = new int[V];
        this.onStack = new boolean[V];
        this.stack = new Stack<>();
        this.sccs = new ArrayList<>();
        this.sccId = new int[V];
        Arrays.fill(disc, -1);
        Arrays.fill(sccId, -1);
    }

    public List<List<Integer>> findSccs() {
        metrics.startTimer();

        for (int v : graph.getVertices()) {
            if (disc[v] == -1) {
                dfs(v);
            }
        }

        metrics.stopTimer();
        return sccs;
    }

    private void dfs(int u) {
        metrics.incrementDfsVisits();

        disc[u] = low[u] = ++time;
        stack.push(u);
        onStack[u] = true;

        for (Edge edge : graph.getAdj(u)) {
            metrics.incrementEdgesProcessed(1);
            int v = edge.target;

            if (disc[v] == -1) {
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        if (low[u] == disc[u]) {
            List<Integer> scc = new ArrayList<>();
            int w;
            int currentSccId = sccs.size();
            do {
                w = stack.pop();
                onStack[w] = false;
                scc.add(w);
                sccId[w] = currentSccId; // Assign the new SCC ID
            } while (w != u);
            sccs.add(scc);
        }
    }

    public Graph buildCondensationGraph() {
        int numScc = sccs.size();
        Graph condensationGraph = new Graph(numScc);

        Map<String, Integer> maxEdgeWeight = new HashMap<>();

        for (int u = 0; u < graph.getV(); u++) {
            int sccUId = sccId[u];

            for (Edge edge : graph.getAdj(u)) {
                int v = edge.target;
                int sccVId = sccId[v];

                if (sccUId != sccVId) {
                    String edgeKey = sccUId + "->" + sccVId;
                    int currentMaxW = maxEdgeWeight.getOrDefault(edgeKey, Integer.MIN_VALUE);

                    if (edge.weight > currentMaxW) {
                        maxEdgeWeight.put(edgeKey, edge.weight);
                    }
                }
            }
        }
        for (Map.Entry<String, Integer> entry : maxEdgeWeight.entrySet()) {
            String[] parts = entry.getKey().split("->");
            int sccUId = Integer.parseInt(parts[0]);
            int sccVId = Integer.parseInt(parts[1]);
            int maxW = entry.getValue();

            condensationGraph.addSimpleEdge(sccUId, sccVId, maxW);
        }

        return condensationGraph;
    }

    public int[] getSccId() {
        return sccId;
    }
}