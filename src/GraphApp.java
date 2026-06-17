import java.util.*;

public class GraphApp {

    private int[][] matrix;
    private int n;

    public GraphApp(int[][] matrix) {
        this.matrix = matrix;
        this.n = matrix.length;
    }

    public int[] bfs(int start) {

        int[] distance = new int[n];
        Arrays.fill(distance, -1);

        Queue<Integer> queue = new LinkedList<>();

        distance[start] = 0;
        queue.add(start);

        while (!queue.isEmpty()) {

            int current = queue.poll();

            for (int i = 0; i < n; i++) {

                if (matrix[current][i] != 0 &&
                        distance[i] == -1) {

                    distance[i] = distance[current] + 1;
                    queue.add(i);
                }
            }
        }

        return distance;
    }

    public int[] getEccentricities() {

        int[] ecc = new int[n];

        for (int i = 0; i < n; i++) {

            int[] distances = bfs(i);

            int max = 0;

            for (int d : distances) {
                max = Math.max(max, d);
            }

            ecc[i] = max;
        }

        return ecc;
    }

    public int getRadius() {

        int[] ecc = getEccentricities();

        int radius = Integer.MAX_VALUE;

        for (int e : ecc) {
            radius = Math.min(radius, e);
        }

        return radius;
    }

    public int getDiameter() {

        int[] ecc = getEccentricities();

        int diameter = 0;

        for (int e : ecc) {
            diameter = Math.max(diameter, e);
        }

        return diameter;
    }

    public List<Character> getCenter() {

        int radius = getRadius();

        int[] ecc = getEccentricities();

        List<Character> center = new ArrayList<>();

        for (int i = 0; i < n; i++) {

            if (ecc[i] == radius) {
                center.add((char) ('A' + i));
            }
        }

        return center;
    }
}