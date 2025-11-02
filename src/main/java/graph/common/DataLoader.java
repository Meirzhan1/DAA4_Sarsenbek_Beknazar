package graph.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DataLoader {
    public static class GraphData {
        public boolean directed;
        public int n;
        public List<Map<String, Integer>> edges;
        public int source;
        public String weight_model;
    }
    public static Graph loadGraph(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        GraphData data = mapper.readValue(new File(filePath), GraphData.class);

        Graph graph = new Graph(data.n);

        for (Map<String, Integer> edgeData : data.edges) {
            int u = edgeData.get("u");
            int v = edgeData.get("v");
            int w = edgeData.getOrDefault("w", 1);

            graph.addEdge(u, v, w);
        }

        System.out.println("Loaded graph from " + filePath + ": V=" + graph.getV() + ", Model=" + data.weight_model);
        return graph;
    }
}