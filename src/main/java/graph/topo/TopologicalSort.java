package graph.topo;

import graph.common.Edge;
import graph.common.Graph;
import graph.common.Metrics;

import java.util.*;

public class TopologicalSort {
    private final Graph graph;
    private final Metrics metrics;
    private final int[] inDegree;

    public TopologicalSort(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
        this.inDegree = new int[graph.getV()];

        // 1. Предварительный расчет in-degree для всех вершин
        for (int u : graph.getVertices()) {
            for (Edge edge : graph.getAdj(u)) {
                inDegree[edge.target]++;
            }
        }
    }

    /**
     * Выполняет топологическую сортировку с использованием алгоритма Кана (BFS-based).
     * @return Список вершин в топологическом порядке. Пустой список, если цикл.
     */
    public List<Integer> sort() {
        metrics.startTimer();
        List<Integer> topOrder = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();

        // 2. Инициализация очереди: добавление всех вершин с in-degree = 0
        for (int v = 0; v < graph.getV(); v++) {
            if (inDegree[v] == 0) {
                queue.add(v);
                metrics.incrementQueuePushes();
            }
        }

        while (!queue.isEmpty()) {
            // 3. Извлечение вершины
            int u = queue.poll();
            metrics.incrementQueuePops();
            topOrder.add(u);

            // 4. Уменьшение in-degree смежных вершин
            for (Edge edge : graph.getAdj(u)) {
                metrics.incrementEdgesProcessed(1);
                int v = edge.target;
                inDegree[v]--;

                // 5. Если in-degree стало 0, добавляем вершину в очередь
                if (inDegree[v] == 0) {
                    queue.add(v);
                    metrics.incrementQueuePushes();
                }
            }
        }

        metrics.stopTimer();

        // Проверка на цикл: если количество отсортированных вершин меньше общего числа вершин, значит есть цикл
        if (topOrder.size() != graph.getV()) {
            // В контексте SCC, это означает ошибку в построении конденсационного графа,
            // т.к. конденсационный граф должен быть DAG.
            System.err.println("Cycle detected in the graph! Topological sort is not possible.");
            return Collections.emptyList();
        }

        return topOrder;
    }
}