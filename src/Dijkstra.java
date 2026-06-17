import java.util.*;

public class Dijkstra {

    public static int[] shortestPath(int[][] graph, int start) {

        int n = graph.length;

        int[] dist = new int[n];

        Arrays.fill(dist, Integer.MAX_VALUE);

        dist[start] = 0;

        boolean[] visited = new boolean[n];

        for (int count = 0; count < n - 1; count++) {

            int u = -1;

            for (int i = 0; i < n; i++) {

                if (!visited[i] &&
                        (u == -1 || dist[i] < dist[u])) {

                    u = i;
                }
            }

            visited[u] = true;

            for (int v = 0; v < n; v++) {

                if (graph[u][v] > 0 &&
                        !visited[v] &&
                        dist[u] != Integer.MAX_VALUE &&
                        dist[u] + graph[u][v] < dist[v]) {

                    dist[v] = dist[u] + graph[u][v];
                }
            }
        }

        return dist;
    }
}