package graph;

import graph.common.*;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPath;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class GraphAlgorithmsTest {
    @Test
    void testTarjanSCC_SimpleCycle() {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 1, 1);
        graph.addEdge(3, 4, 1);

        TarjanSCC tarjan = new TarjanSCC(graph, new Metrics());
        List<List<Integer>> sccs = tarjan.findSccs();

        assertEquals(3, sccs.size(), "Should be 3 SCCs");

        boolean sccFound = sccs.stream()
                .anyMatch(scc -> scc.size() == 3 && scc.contains(1) && scc.contains(2) && scc.contains(3));

        assertTrue(sccFound, "SCC {1, 2, 3} should be found");

        Graph cGraph = tarjan.buildCondensationGraph();
        assertEquals(3, cGraph.getV(), "Condensation graph should have 3 vertices (SCCs)");
    }

    @Test
    void testTopologicalSort_SimpleDAG() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        TopologicalSort topoSort = new TopologicalSort(graph, new Metrics());
        List<Integer> order = topoSort.sort();

        assertEquals(4, order.size(), "Topological sort should return 4 vertices");
        assertEquals(0, order.get(0), "First vertex should be 0 (in-degree 0)");
        assertTrue(order.indexOf(1) < order.indexOf(3), "1 must be before 3");
        assertTrue(order.indexOf(2) < order.indexOf(3), "2 must be before 3");
    }

    @Test
    void testDAGShortestAndLongestPath() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 2);
        graph.addEdge(2, 3, 5);

        List<Integer> topoOrder = List.of(0, 1, 2, 3);
        int source = 0;
        int target = 3;

        Metrics shortestMetrics = new Metrics();
        DAGShortestPath shortestPath = new DAGShortestPath(graph, shortestMetrics, topoOrder);
        shortestPath.findShortestPaths(source);

        assertEquals(5, shortestPath.getDistance(target), "Shortest path to 3 should be 5.");
        assertEquals(List.of(0, 2, 3), shortestPath.reconstructPath(target), "Shortest path should be 0->2->3");

        Metrics longestMetrics = new Metrics();
        DAGShortestPath longestPath = new DAGShortestPath(graph, longestMetrics, topoOrder);
        longestPath.findLongestPaths(source);

        assertEquals(6, longestPath.getDistance(target), "Longest path to 3 should be 6.");
        assertEquals(List.of(0, 2, 3), longestPath.reconstructPath(target), "Longest path should be 0->2->3");
    }
}