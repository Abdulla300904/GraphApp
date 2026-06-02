import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GraphApp {

    private static int[][] adjanzMatrix;
    private static int anzahlKnoten;
    private static String[] knotenNamen; // Speichert A, B, C... aus der CSV-Headerzeile
    private static int[][] distanzMatrix;
    private static int[] exzentrizitaeten;

    private static final int INF = Integer.MAX_VALUE / 2; // Schutz vor Überlauf bei Additionen

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Verwendung: java GraphApp.java <pfad_zur_csv> [Startknoten] [Zielknoten]");
            return;
        }

        String csvPfad = args[0];

        try {
            // 1. CSV einlesen
            gesamteCsvEinlesen(csvPfad);

            // 2. Distanzen berechnen (Floyd-Warshall Algorithmus)
            berechneDistanzMatrix();

            // 3. Exzentrizitäten, Radius, Durchmesser, Zentrum berechnen
            berechneMetriken();

            // 4. Bonus für Note 1: Zusatzfunktionen ausführen, wenn Knoten übergeben wurden
            if (args.length >= 3) {
                String start = args[1];
                String ziel = args[2];
                System.out.println("\n--- Bonus: Kürzeste Wege ---");
                fuehreBFSaus(start, ziel);
                fuehreDijkstraAus(start, ziel);
            }

        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Datei: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Fehler: " + e.getMessage());
        }
    }

    /**
     * Liest die CSV-Datei ein und baut die Adjazenzmatrix auf.
     * Kompatibel mit Semikolon und dynamischer Knotengröße.
     */
    private static void gesamteCsvEinlesen(String pfad) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(pfad));
        String zeile = br.readLine();

        if (zeile == null) {
            br.close();
            throw new IllegalArgumentException("Die CSV-Datei ist leer.");
        }

        // Header auslesen (Knotennamen)
        String[] header = zeile.split(";");
        anzahlKnoten = header.length - 1; // Erste Spalte ist meistens leer oder beschriftet
        knotenNamen = new String[anzahlKnoten];
        System.arraycopy(header, 1, knotenNamen, 0, anzahlKnoten);

        adjanzMatrix = new int[anzahlKnoten][anzahlKnoten];

        int zeilenIndex = 0;
        while ((zeile = br.readLine()) != null) {
            if (zeile.trim().isEmpty()) continue;
            String[] teile = zeile.split(";");

            for (int spaltenIndex = 0; spaltenIndex < anzahlKnoten; spaltenIndex++) {
                // Teile[0] ist der Zeilenname, danach kommen die Gewichte
                String wert = teile[spaltenIndex + 1].trim();
                int gewicht = Integer.parseInt(wert);

                // In der Adjazenzmatrix: 0 bedeutet oft "keine Kante", außer auf der Hauptdiagonale
                if (gewicht == 0 && zeilenIndex != spaltenIndex) {
                    adjanzMatrix[zeilenIndex][spaltenIndex] = INF;
                } else {
                    adjanzMatrix[zeilenIndex][spaltenIndex] = gewicht;
                }
            }
            zeilenIndex++;
        }
        br.close();

        System.out.println("Graph erfolgreich geladen. Knoten: " + anzahlKnoten);
    }

    /**
     * Berechnet die kürzesten Distanzen zwischen allen Paaren (Floyd-Warshall).
     * Ohne Frameworks gelöst.
     */
    private static void berechneDistanzMatrix() {
        distanzMatrix = new int[anzahlKnoten][anzahlKnoten];

        // Initialisierung
        for (int i = 0; i < anzahlKnoten; i++) {
            System.arraycopy(adjanzMatrix[i], 0, distanzMatrix[i], 0, anzahlKnoten);
        }

        // Kern des Floyd-Warshall Algorithmus
        for (int k = 0; k < anzahlKnoten; k++) {
            for (int i = 0; i < anzahlKnoten; i++) {
                for (int j = 0; j < anzahlKnoten; j++) {
                    if (distanzMatrix[i][k] + distanzMatrix[k][j] < distanzMatrix[i][j]) {
                        distanzMatrix[i][j] = distanzMatrix[i][k] + distanzMatrix[k][j];
                    }
                }
            }
        }

        // Ausgabe der Distanzmatrix
        System.out.println("\n--- Distanzmatrix ---");
        for (int i = 0; i < anzahlKnoten; i++) {
            for (int j = 0; j < anzahlKnoten; j++) {
                System.out.print((distanzMatrix[i][j] == INF ? "INF" : distanzMatrix[i][j]) + "\t");
            }
            System.out.println();
        }
    }

    /**
     * Bestimmt Exzentrizitäten, Radius, Durchmesser und Zentrum.
     */
    private static void berechneMetriken() {
        exzentrizitaeten = new int[anzahlKnoten];
        int radius = INF;
        int durchmesser = 0;

        System.out.println("\n--- Exzentrizitäten ---");
        for (int i = 0; i < anzahlKnoten; i++) {
            int maxDist = 0;
            for (int j = 0; j < anzahlKnoten; j++) {
                if (distanzMatrix[i][j] != INF && distanzMatrix[i][j] > maxDist) {
                    maxDist = distanzMatrix[i][j];
                }
            }
            exzentrizitaeten[i] = maxDist;
            System.out.println("Knoten " + knotenNamen[i] + ": " + maxDist);

            if (maxDist > durchmesser) durchmesser = maxDist;
            if (maxDist < radius) radius = maxDist;
        }

        System.out.println("\n--- Graphen-Metriken ---");
        System.out.println("Radius (r): " + radius);
        System.out.println("Durchmesser (d): " + durchmesser);

        // Zentrum bestimmen (Knoten, deren Exzentrizität gleich dem Radius ist)
        List<String> zentrum = new ArrayList<>();
        for (int i = 0; i < anzahlKnoten; i++) {
            if (exzentrizitaeten[i] == radius) {
                zentrum.add(knotenNamen[i]);
            }
        }
        System.out.println("Zentrum: " + zentrum);
    }

    private static int findeKnotenIndex(String name) {
        for (int i = 0; i < knotenNamen.length; i++) {
            if (knotenNamen[i].equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    /**
     * BONUS FEATURE 1: Breitensuche (BFS) für ungewichtete Pfade
     */
    private static void fuehreBFSaus(String startName, String zielName) {
        int start = findeKnotenIndex(startName);
        int ziel = findeKnotenIndex(zielName);
        if (start == -1 || ziel == -1) return;

        Queue<Integer> queue = new LinkedList<>();
        boolean[] besucht = new boolean[anzahlKnoten];
        int[] vorgaenger = new int[anzahlKnoten];
        Arrays.fill(vorgaenger, -1);

        queue.add(start);
        besucht[start] = true;

        while (!queue.isEmpty()) {
            int aktuell = queue.poll();
            if (aktuell == ziel) break;

            for (int i = 0; i < anzahlKnoten; i++) {
                // Eine Kante existiert, wenn das Gewicht in der Adjazenzmatrix nicht INF und nicht 0 ist
                if (adjanzMatrix[aktuell][i] != INF && adjanzMatrix[aktuell][i] != 0 && !besucht[i]) {
                    besucht[i] = true;
                    vorgaenger[i] = aktuell;
                    queue.add(i);
                }
            }
        }

        System.out.print("BFS Pfad (" + startName + " -> " + zielName + "): ");
        rekonstruierePfad(vorgaenger, ziel);
    }

    /**
     * BONUS FEATURE 2: Dijkstra-Algorithmus für gewichtete kürzeste Pfade
     */
    private static void fuehreDijkstraAus(String startName, String zielName) {
        int start = findeKnotenIndex(startName);
        int ziel = findeKnotenIndex(zielName);
        if (start == -1 || ziel == -1) return;

        int[] dist = new int[anzahlKnoten];
        int[] vorgaenger = new int[anzahlKnoten];
        boolean[] besucht = new boolean[anzahlKnoten];

        Arrays.fill(dist, INF);
        Arrays.fill(vorgaenger, -1);
        dist[start] = 0;

        for (int i = 0; i < anzahlKnoten; i++) {
            // Finde den unbesuchten Knoten mit der minimalen Distanz
            int u = -1;
            for (int j = 0; j < anzahlKnoten; j++) {
                if (!besucht[j] && (u == -1 || dist[j] < dist[u])) {
                    u = j;
                }
            }

            if (dist[u] == INF) break;
            besucht[u] = true;

            // Distanzen updaten
            for (int v = 0; v < anzahlKnoten; v++) {
                if (adjanzMatrix[u][v] != INF && adjanzMatrix[u][v] != 0) {
                    if (dist[u] + adjanzMatrix[u][v] < dist[v]) {
                        dist[v] = dist[u] + adjanzMatrix[u][v];
                        vorgaenger[v] = u;
                    }
                }
            }
        }

        System.out.print("Dijkstra Pfad (" + startName + " -> " + zielName + ") mit Gesamtkosten " + dist[ziel] + ": ");
        rekonstruierePfad(vorgaenger, ziel);
    }

    private static void rekonstruierePfad(int[] vorgaenger, int ziel) {
        List<String> pfad = new ArrayList<>();
        for (int at = ziel; at != -1; at = vorgaenger[at]) {
            pfad.add(knotenNamen[at]);
        }
        Collections.reverse(pfad);
        if (pfad.size() == 1 && vorgaenger[ziel] == -1) {
            System.out.println("Kein Pfad vorhanden.");
        } else {
            System.out.println(String.join(" -> ", pfad));
        }
    }
}