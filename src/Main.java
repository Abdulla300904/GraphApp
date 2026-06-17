public class Main {

    public static void main(String[] args) {

        try {

            if (args.length == 0) {

                System.out.println(
                        "Usage: java Main graph.csv"
                );

                return;
            }

            int[][] matrix =
                    GraphReader.readGraph(args[0]);

            GraphApp graph = new GraphApp(matrix);

            int[] ecc =
                    graph.getEccentricities();

            System.out.println("Exzentrizitäten:");

            for (int i = 0; i < ecc.length; i++) {

                System.out.println(
                        (char)('A' + i)
                                + " -> "
                                + ecc[i]
                );
            }

            System.out.println();
            System.out.println(
                    "Radius: "
                            + graph.getRadius()
            );

            System.out.println(
                    "Durchmesser: "
                            + graph.getDiameter()
            );

            System.out.println(
                    "Zentrum: "
                            + graph.getCenter()
            );

        } catch (Exception e) {

            System.out.println(
                    "Fehler: "
                            + e.getMessage()
            );
        }
    }
}