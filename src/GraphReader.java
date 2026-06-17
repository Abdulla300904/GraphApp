import java.io.*;
import java.util.*;

public class GraphReader {

    public static int[][] readGraph(String filename) throws IOException {

        List<int[]> rows = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(filename));

        String line;

        while ((line = br.readLine()) != null) {

            String[] values = line.split(";");

            int[] row = new int[values.length];

            for (int i = 0; i < values.length; i++) {
                row[i] = Integer.parseInt(values[i]);
            }

            rows.add(row);
        }

        br.close();

        return rows.toArray(new int[0][]);
    }
}