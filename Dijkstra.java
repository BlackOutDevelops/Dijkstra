import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Dijkstra {
    int v;                      // Number of Vertices
    int e;                      // Number of Edges
    int sv;                     // Source Vertex
    int currentDistance;        // Current distance from source
    int INT_MAX = 2147483647;

    List<Vertex> vertices = new ArrayList<>();
    ArrayList<Integer> visited = new ArrayList<>();
    ArrayList<Integer> listOfGraph = new ArrayList<>();

    // Constructor for graph
    public Dijkstra(ArrayList<Integer> array) {
        this.v = array.get(0);
        this.sv = array.get(1);
        this.e = array.get(2);
        for (int i = 3; i < array.size(); i++) {
            listOfGraph.add(array.get(i));
        }

        addVertices();
        parseArray(listOfGraph);
    }

    public class Vertex implements Comparable<Vertex> {
        String name;
        String parent;
        Vertex closestVertex;
        Map<Vertex, Integer> connectedVertices = new HashMap<>(4, .5f);

        // Constructor for Vertex
        public Vertex(String name) {
            this.name = name;
            parent = null;
        }

        // Helps set up adjacent vertices to each vertex
        public void setUpAdjacentVertices(ArrayList<Integer> array, Vertex givenVertex) {
            for (int i = 0; i < array.size(); i++) {
                if (i % 3 == 0) {
//                    System.out.println("Edge given : " + givenVertex.name + " Weight added: " + array.get(i+2));
                    if (array.get(i) == Integer.parseInt(givenVertex.name))
                        givenVertex.connectedVertices.put(vertices.get(array.get(i + 1) - 1), array.get(i + 2));
                    else
                        vertices.get(array.get(i + 1) - 1).connectedVertices.put(vertices.get(array.get(i) - 1), array.get(i + 2));
                }
            }
        }

        public int compareTo(Vertex o) {
            return new nameComparator().compare(name, o.name);
        }

        public class nameComparator implements Comparator<Vertex> {
            public int compare(Vertex v1, Vertex v2) {
                String v1s = v1.name;
                String v2s = v2.name;
                Integer a1 = Integer.parseInt(v1s);
                Integer a2 = Integer.parseInt(v2s);
                return a1.compareTo(a2);
            }

            public int compare(String name, String name1) {
                Integer a1 = Integer.parseInt(name);
                Integer a2 = Integer.parseInt(name1);
                return a1.compareTo(a2);
            }
        }
    }

    // Actual implementation of Dijkstra's Algorithm
    public void dijkstraAlgorithm() throws FileNotFoundException {
        Map<Vertex, Integer> table = new TreeMap<>();
        Vertex temp;
        int tempDistance = 0;
        int currentVertex = sv;

        // Updates adjacent nodes parent and distance from source values
        while (visited.size() != v) {
            temp = vertices.get(currentVertex - 1);


            if (currentVertex == sv) {
                temp.parent = String.valueOf(-1);
                table.put(temp, -1);

                for (Map.Entry<Vertex, Integer> entry : temp.connectedVertices.entrySet()) {
//                    System.out.println("Key = " + entry.getKey().name + ", Value: " + entry.getValue());
                    entry.getKey().parent = temp.name;
                    table.put(entry.getKey(), entry.getValue());
                }
            } else {

                for (Map.Entry<Vertex, Integer> entry : temp.connectedVertices.entrySet()) {
//                    System.out.println("Key = " + entry.getKey().name + ", Value: " + entry.getValue());
                    if (entry.getKey().parent == null) {
                        entry.getKey().parent = temp.name;
                        table.put(entry.getKey(), (entry.getValue() + tempDistance));
                    } else if (table.get(entry.getKey()) > (tempDistance + entry.getValue())) {
                        entry.getKey().parent = temp.name;
                        table.put(entry.getKey(), (entry.getValue() + tempDistance));
                    }
                }
            }

            currentDistance = tempDistance;

            // Checks whether the next vertex being traversed was visited or not
            if (checkVisited(Integer.parseInt(temp.closestVertex.name))) {
                visited.add(currentVertex);
                if (visited.size() < v) {
                    currentVertex = Integer.parseInt(findNextVertex(table).name);
                    tempDistance = currentDistance;
                }

            } else {
                visited.add(currentVertex);
//                currentVertex = Integer.parseInt(temp.closestVertex.name);
//                tempDistance += findClosestVertex(temp);
                currentVertex = Integer.parseInt(findNextVertex(table).name);
                tempDistance = currentDistance;
            }


        }

        printTable(table);
    }

    // Separates the array taken from scanner into smaller arrays for each vertex
    public void parseArray(ArrayList<Integer> array) {

        ArrayList<Integer> parsedArray = new ArrayList<>();
        for (int count = 1; count < v + 1; count++) {
            for (int i = 0; i < array.size(); i++) {
                if (array.get(i) == count) {
                    while ((i + 1) % 3 != 0)
                        i++;

                    parsedArray.add(array.get(i - 2));
                    parsedArray.add(array.get(i - 1));
                    parsedArray.add(array.get(i));
                }
            }

            constructVertices(parsedArray, count);
            parsedArray.clear();
        }
    }

    // Fills in the values need per vertex to complete my implementation of Dijkstra's Algorithm
    public void constructVertices(ArrayList<Integer> array, int i) {
        Vertex tempVertex = vertices.get(i - 1);

        tempVertex.setUpAdjacentVertices(array, tempVertex);

        findClosestVertex(tempVertex);
    }

    // Adds all the vertices necessary for graph
    public void addVertices() {
        for (int i = 1; i < v + 1; i++) {
            Vertex newVertex = new Vertex(String.valueOf(i));
            vertices.add(newVertex);
        }
    }

    // Finds the next vertex to be traversed from Dijkstra's Algorithm.
    public Vertex findNextVertex(Map<Vertex, Integer> currentVertices) {
        Vertex tempVertex = null;
        int temp = INT_MAX;
        for (Map.Entry<Vertex, Integer> t : currentVertices.entrySet()) {
            if (t.getValue() < temp && t.getValue() > currentDistance) {
                tempVertex = t.getKey();
                temp = t.getValue();
            } else if (t.getValue() == currentDistance && !checkVisited(Integer.parseInt(t.getKey().name))) {
                tempVertex = t.getKey();
                temp = t.getValue();
            }
        }
        currentDistance = temp;
        return tempVertex;
    }

    // Finds the closest vertex for any given vertex in the graph
    public void findClosestVertex(Vertex vertex) {
        Map<Vertex, Integer> tempHash = vertex.connectedVertices;
        int temp = INT_MAX;
        for (Map.Entry<Vertex, Integer> t : tempHash.entrySet()) {
            if (t.getValue() < temp) {
                vertex.closestVertex = t.getKey();
                temp = t.getValue();
            }
//            System.out.println("Connected Edge: " + t.getKey().name + " Distance: " + t.getValue());
        }
    }

    // Checks the visited array to see if vertex is within it
    public boolean checkVisited(int vertex) {
        return visited.contains(vertex);
    }

    // Simple print function to satisfy output parameters
    public void printTable(Map<Vertex, Integer> table) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter("cop3503-asn2-output-frazer-joshua.txt");

        pw.printf("%d\n", v);
        (table).forEach((key, value) -> pw.printf(key.name + " " + value + " " + key.parent + "\n"));
        pw.close();
    }

    public static void main(String[] args) throws FileNotFoundException {

        File filename = new File("cop3503-asn2-input.txt");
        Scanner s = new Scanner(filename);
        int n;

        ArrayList<Integer> arrayFromFile = new ArrayList<>();
        while (s.hasNext()) {
            if (s.hasNextInt()) {
                n = s.nextInt();
                arrayFromFile.add(n);
            } else
                s.next();
        }

        Dijkstra spanningTree = new Dijkstra(arrayFromFile);
        spanningTree.dijkstraAlgorithm();

        s.close();
    }
}


