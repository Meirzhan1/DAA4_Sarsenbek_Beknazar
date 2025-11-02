package graph.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DatasetGenerator {

    private static final Random RANDOM = new Random();
    private static final String DATA_PATH = "data/";

    public static void generateAll() throws IOException {
        new File(DATA_PATH).mkdirs();

        // 1. SMALL DAG (Sparse) - 10 вершин
        generateAndSaveDAG(10, 12, "small_dag_1.json");

        // 2. SMALL Cycle (Sparse) - 10 вершин
        generateAndSaveMixed(10, 15, 1, "small_cycle_1.json");

        // 3. SMALL Mixed (Dense) - 10 вершин
        generateAndSaveMixed(10, 30, 2, "small_mixed_1.json");

        // 4. MEDIUM DAG (Sparse) - 25 вершин
        generateAndSaveDAG(25, 30, "medium_dag_1.json");

        // 5. MEDIUM SCC (Sparse) - 25 вершин, 2 больших SCC
        generateAndSaveSCC(25, 40, 2, 10, "medium_scc_1.json");

        // 6. MEDIUM Dense (Dense) - 25 вершин
        generateAndSaveMixed(25, 150, 5, "medium_dense_1.json");

        // 7. LARGE DAG (Sparse) - 50 вершин
        generateAndSaveDAG(50, 70, "large_dag_1.json");

        // 8. LARGE SCC (Sparse) - 50 вершин, 3 больших SCC
        generateAndSaveSCC(50, 90, 3, 15, "large_scc_1.json");

        // 9. LARGE Dense (Dense) - 50 вершин
        generateAndSaveMixed(50, 50 * 49 / 2 / 2, 5, "large_dense_1.json");

        System.out.println("Generated 9 datasets in the '" + DATA_PATH + "' folder.");
    }

    private static void saveGraph(int V, List<Edge> edges, int source, String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("directed", true);
        data.put("n", V);

        List<Map<String, Integer>> edgeList = new ArrayList<>();
        for (Edge edge : edges) {
            Map<String, Integer> edgeMap = new HashMap<>();
            edgeMap.put("u", edge.source);
            edgeMap.put("v", edge.target);
            edgeMap.put("w", edge.weight);
            edgeList.add(edgeMap);
        }
        data.put("edges", edgeList);
        data.put("source", source);
        data.put("weight_model", "edge");

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(DATA_PATH + fileName), data);
    }

    private static void generateAndSaveDAG(int V, int E, String fileName) throws IOException {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < E; i++) {
            int u = RANDOM.nextInt(V);
            int v = u + RANDOM.nextInt(V - u);
            if (u == v) continue;
            int w = 1 + RANDOM.nextInt(10);
            edges.add(new Edge(u, v, w));
        }
        List<Edge> uniqueEdges = new ArrayList<>();
        Map<String, Edge> edgeMap = new HashMap<>();
        for (Edge edge : edges) {
            String key = edge.source + "->" + edge.target;
            if (!edgeMap.containsKey(key)) {
                edgeMap.put(key, edge);
                uniqueEdges.add(edge);
            }
        }

        saveGraph(V, uniqueEdges, 0, fileName);
    }

    private static void generateAndSaveMixed(int V, int E, int numCycles, String fileName) throws IOException {
        List<Edge> edges = new ArrayList<>();

        for (int i = 0; i < E - numCycles; i++) {
            int u = RANDOM.nextInt(V);
            int v = RANDOM.nextInt(V);
            if (u == v) continue;
            int w = 1 + RANDOM.nextInt(10);
            edges.add(new Edge(u, v, w));
        }

        for (int i = 0; i < numCycles; i++) {
            int start = RANDOM.nextInt(V);
            int middle = (start + 1 + RANDOM.nextInt(V - 1)) % V;
            edges.add(new Edge(start, middle, 1));
            edges.add(new Edge(middle, start, 1));
        }

        List<Edge> uniqueEdges = new ArrayList<>();
        Map<String, Edge> edgeMap = new HashMap<>();
        for (Edge edge : edges) {
            String key = edge.source + "->" + edge.target;
            if (!edgeMap.containsKey(key)) {
                edgeMap.put(key, edge);
                uniqueEdges.add(edge);
            }
        }

        saveGraph(V, uniqueEdges, 0, fileName);
    }

    private static void generateAndSaveSCC(int V, int E, int numScc, int sccSize, String fileName) throws IOException {
        List<Edge> edges = new ArrayList<>();

        int currentV = 0;
        for (int s = 0; s < numScc; s++) {
            int startNode = currentV;
            int endNode = currentV + sccSize;

            for (int i = startNode; i < endNode; i++) {
                int next = (i + 1);
                if (next >= endNode) next = startNode; // Замыкаем цикл
                edges.add(new Edge(i, next, 1));
            }

            for (int k = 0; k < sccSize * 2; k++) {
                int u = startNode + RANDOM.nextInt(sccSize);
                int v = startNode + RANDOM.nextInt(sccSize);
                if (u != v) {
                    edges.add(new Edge(u, v, 1 + RANDOM.nextInt(5)));
                }
            }
            if (s > 0) {
                int prevEnd = currentV - 1;
                edges.add(new Edge(prevEnd, startNode + RANDOM.nextInt(sccSize), 1));
            }

            currentV += sccSize;
        }
        int lastNode = currentV - 1;
        int remainingV = V - currentV;
        for (int i = 0; i < remainingV; i++) {
            currentV++;
            edges.add(new Edge(lastNode, currentV - 1, 1)); // Связываем
            lastNode = currentV - 1;
        }
        int remainingEdges = E - edges.size();
        for (int i = 0; i < remainingEdges; i++) {
            int u = RANDOM.nextInt(V);
            int v = RANDOM.nextInt(V);
            if (u != v) {
                edges.add(new Edge(u, v, 1 + RANDOM.nextInt(10)));
            }
        }
        List<Edge> uniqueEdges = new ArrayList<>();
        Map<String, Edge> edgeMap = new HashMap<>();
        for (Edge edge : edges) {
            String key = edge.source + "->" + edge.target;
            if (!edgeMap.containsKey(key)) {
                edgeMap.put(key, edge);
                uniqueEdges.add(edge);
            }
        }

        saveGraph(V, uniqueEdges, 0, fileName);
    }

    public static void main(String[] args) throws IOException {
        generateAll();
    }
}