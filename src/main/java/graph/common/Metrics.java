package graph.common;

public class Metrics {
    private long startTime;
    private long endTime;

    private int dfsVisits = 0;
    private int edgesProcessed = 0;

    private int queuePushes = 0;
    private int queuePops = 0;

    private int relaxations = 0;

    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    public void stopTimer() {
        this.endTime = System.nanoTime();
    }

    public long getElapsedTimeNanos() {
        return endTime - startTime;
    }

    public long getElapsedTimeMillis() {
        return (endTime - startTime) / 1_000_000;
    }

    public void incrementDfsVisits() {
        dfsVisits++;
    }

    public void incrementEdgesProcessed(int count) {
        edgesProcessed += count;
    }
    public void incrementQueuePushes() {
        queuePushes++;
    }

    public void incrementQueuePops() {
        queuePops++;
    }

    public void incrementRelaxations() {
        relaxations++;
    }
    public int getDfsVisits() {
        return dfsVisits;
    }

    public int getEdgesProcessed() {
        return edgesProcessed;
    }

    public int getQueuePushes() {
        return queuePushes;
    }

    public int getQueuePops() {
        return queuePops;
    }

    public int getRelaxations() {
        return relaxations;
    }

    @Override
    public String toString() {
        return String.format(
                "Time (ms): %d, DFS Visits: %d, Edges Processed: %d, Queue Pushes: %d, Queue Pops: %d, Relaxations: %d",
                getElapsedTimeMillis(), dfsVisits, edgesProcessed, queuePushes, queuePops, relaxations
        );
    }
}