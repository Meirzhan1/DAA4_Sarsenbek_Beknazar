package graph.common;

import graph.common.Graph;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GraphUtils {
    public static void exportToDot(Graph graph, String filename) throws IOException {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph G {\n");
        dot.append("    rankdir=LR; // Left to Right layout\n");

        // 1. Nodes (Vertices)
        for (int i = 0; i < graph.getV(); i++) {
            dot.append("    ").append(i).append(" [label=\"").append(i).append("\"];\n");
        }

        // 2. Edges: Using direct public field access (.target, .weight)
        for (int u = 0; u < graph.getV(); u++) {
            for (Edge edge : graph.getAdj(u)) {
                dot.append("    ").append(u)
                        .append(" -> ").append(edge.target)
                        .append(" [label=\"").append(edge.weight).append("\"];\n");
            }
        }
        dot.append("}\n");

        Files.write(Paths.get(filename), dot.toString().getBytes());
    }

    public static void exportCondensationToDot(Graph cGraph, String filename, List<List<Integer>> sccs) throws IOException {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph CondensationGraph {\n");
        dot.append("    rankdir=LR;\n");

        for (int i = 0; i < cGraph.getV(); i++) {
            String label = "SCC " + i + "\\n{" + sccs.get(i).toString().replaceAll("[\\[\\]\\s]", "") + "}";
            dot.append("    ").append(i).append(" [shape=box, label=\"").append(label).append("\"];\n");
        }
        for (int u = 0; u < cGraph.getV(); u++) {
            for (Edge edge : cGraph.getAdj(u)) {
                dot.append("    ").append(u)
                        .append(" -> ").append(edge.target)
                        .append(" [label=\"").append(edge.weight).append("\"];\n");
            }
        }
        dot.append("}\n");

        Files.write(Paths.get(filename), dot.toString().getBytes());
    }
}