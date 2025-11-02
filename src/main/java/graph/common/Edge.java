package graph.common;

public class Edge {
    public final int source;
    public final int target;
    public final int weight;

    public Edge(int source, int target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }
}