package sample;
import java.util.ArrayList;
import java.util.List;
public class TrivialCliqueSolver {
    // Function to check if a given set of vertices forms a clique
    private static boolean isClique(boolean[][] adjacencyMatrix, List<Integer> vertices) {
        int size = vertices.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (!adjacencyMatrix[vertices.get(i)][vertices.get(j)]) {
                    return false;
                }
            }
        }
        return true;
    }

    // Recursive function to find cliques of a given size
    private static boolean findCliqueRecursive(boolean[][] adjacencyMatrix, int n, int k, int start, List<Integer> current) {
        if (current.size() == k) {
            return isClique(adjacencyMatrix, current);
        }

        for (int i = start; i < n; i++) {
            current.add(i);
            if (findCliqueRecursive(adjacencyMatrix, n, k, i + 1, current)) {
                return true;
            }
            current.remove(current.size() - 1);
        }

        return false;
    }

    // Main function to find a clique of a given size
    public static ArrayList<Integer> findClique(boolean[][] adjacencyMatrix, int cliqueSize) {
        List<Integer> current = new ArrayList<>();
        if (findCliqueRecursive(adjacencyMatrix, adjacencyMatrix.length, cliqueSize, 0, current)) {
            return new ArrayList<>(current);
        }
        return new ArrayList<>(); // Return an empty list if no clique is found
    }

}
