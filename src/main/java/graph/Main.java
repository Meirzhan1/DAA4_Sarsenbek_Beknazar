package graph;

import graph.common.*;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPath;

import java.io.IOException;
import java.util.List;

public class Main {
    private static final String DATA_PATH = "data/";

    private static final String[] DATASETS = {
            DATA_PATH + "small_dag_1.json",
            DATA_PATH + "small_cycle_1.json",
            DATA_PATH + "small_mixed_1.json",
            DATA_PATH + "medium_dag_1.json",
            DATA_PATH + "medium_scc_1.json",
            DATA_PATH + "medium_dense_1.json",
            DATA_PATH + "large_dag_1.json",
            DATA_PATH + "large_scc_1.json",
            DATA_PATH + "large_dense_1.json",
            DATA_PATH + "tasks (1).json"
    };

    public static void main(String[] args) {
        try {
            DatasetGenerator.generateAll();
        } catch (IOException e) {
            System.err.println("Error generating datasets: " + e.getMessage());
            return;
        }

        for (String filePath : DATASETS) {
            System.out.println("=================================================");
            System.out.println("Processing: " + filePath);

            try {
                Graph graph = DataLoader.loadGraph(filePath);

                String baseName = filePath.substring(filePath.lastIndexOf('/') + 1).replace(".json", "");
                String dotFileName = DATA_PATH + baseName + "_original.dot";
                GraphUtils.exportToDot(graph, dotFileName);
                System.out.println("Exported Original Graph to: " + dotFileName);

                int sourceNode;
                if (filePath.endsWith("tasks (1).json")) {
                    sourceNode = 4;
                } else {
                    sourceNode = 0;
                }

                runGraphAlgorithms(graph, sourceNode, baseName);

            } catch (IOException e) {
                System.err.println("FAILED to load or parse " + filePath + ": " + e.getMessage());
            }
        }
    }

    private static void runGraphAlgorithms(Graph graph, int sourceNode, String baseName) {
        Metrics sccMetrics = new Metrics();
        TarjanSCC tarjan = new TarjanSCC(graph, sccMetrics);
        List<List<Integer>> sccs = tarjan.findSccs();
        System.out.println("\n[SCC & Condensation]");
        System.out.printf("Found %d SCCs. Metrics: %s\n", sccs.size(), sccMetrics);

        Graph condensationGraph = tarjan.buildCondensationGraph();
        System.out.println("Condensation Graph V=" + condensationGraph.getV());

        // --- 2. Export Condensation Graph (using GraphUtils) ---
        String cDotFileName = DATA_PATH + "condensed_" + baseName + ".dot";
        try {
            GraphUtils.exportCondensationToDot(condensationGraph, cDotFileName, sccs);
            System.out.println("Exported Condensation Graph to: " + cDotFileName);
        } catch (IOException e) {
            System.err.println("Failed to export condensation graph: " + e.getMessage());
        }

        Metrics topoMetrics = new Metrics();
        TopologicalSort topoSort = new TopologicalSort(condensationGraph, topoMetrics);
        List<Integer> topoOrder = topoSort.sort();

        System.out.println("\n[Topological Sort]");
        if (!topoOrder.isEmpty()) {
            System.out.printf("Order of SCCs: %s\n", topoOrder);
        } else {
            System.out.println("Graph is not a DAG (or has cycle error)");
            return;
        }
        System.out.printf("Metrics: %s\n", topoMetrics);
        int[] sccIdMap = tarjan.getSccId();
        int sccSourceId = sccIdMap[sourceNode];
        int targetNode = graph.getV() - 1;
        int sccTargetId = sccIdMap[targetNode];


        if (sccSourceId == -1 || sccTargetId == -1) {
            System.err.println("Error: Source or Target node not processed by Tarjan.");
            return;
        }
        Metrics shortestPathMetrics = new Metrics();
        DAGShortestPath shortestPath = new DAGShortestPath(condensationGraph, shortestPathMetrics, topoOrder);
        shortestPath.findShortestPaths(sccSourceId);

        List<Integer> path = shortestPath.reconstructPath(sccTargetId);

        System.out.println("\n[Shortest Path]");
        System.out.printf("Shortest distance from Source Node %d (SCC %d) to Target Node %d (SCC %d): %d\n",
                sourceNode, sccSourceId, targetNode, sccTargetId, shortestPath.getDistance(sccTargetId));
        System.out.printf("Path in SCCs: %s\n", path);
        System.out.printf("Metrics: %s\n", shortestPathMetrics);

        Metrics longestPathMetrics = new Metrics();
        DAGShortestPath longestPath = new DAGShortestPath(condensationGraph, longestPathMetrics, topoOrder);
        longestPath.findLongestPaths(sccSourceId);

        List<Integer> criticalPath = longestPath.reconstructPath(sccTargetId);

        System.out.println("\n[Longest Path / Critical Path]");
        System.out.printf("Critical path length from Source Node %d (SCC %d) to Target Node %d (SCC %d): %d\n",
                sourceNode, sccSourceId, targetNode, sccTargetId, longestPath.getDistance(sccTargetId));
        System.out.printf("Path in SCCs: %s\n", criticalPath);
        System.out.printf("Metrics: %s\n", longestPathMetrics);
    }
}