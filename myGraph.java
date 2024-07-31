package sample;
import java.util.ArrayList;
public class myGraph {

    private boolean [][] adjacency_mat;
    private final int _vertices;

    // constructors
    public myGraph(boolean [][] graph){
        _vertices = graph.length;
        adjacency_mat = new boolean [_vertices][_vertices];
        for (int i = 0; i<_vertices; i++){
            for (int j = 0; j< _vertices; j++){
                this.adjacency_mat[i][j] = graph[i][j];
            }
        }
    }

    public myGraph(myGraph other){
        this._vertices = other.getVertices();
        for (int i = 0; i<_vertices; i++){
            for (int j = 0; j< _vertices; j++){
                this.adjacency_mat[i][j] = other.getAdjacencyMat()[i][j];
            }
        }
    }

    // getters and setters
    public int getVertices(){
        return _vertices;
    }

    public boolean[][] getAdjacencyMat(){
        return adjacency_mat;
    }

    public void makeEdge(int from, int to){
        adjacency_mat[from][to] = true;
    }

    public void deleteEdge(int from, int to){
        adjacency_mat[from][to] = false;
    }

    // calculating the fitness of a given solution such that 0 is a perfect score.
    public int getFitness(myGraph myGraph, ArrayList<Integer> k) {

        // Initialize fitness score (number of missing edges within the clique)
        int fitness = 0;
        int cliqueSize = k.size(); // Size of the potential clique

        // Loop through each pair of vertices in the provided clique (k)
        for (int i = 0; i < cliqueSize; i++) {
            for (int j = i + 1; j < cliqueSize; j++) {
                // Check if there's no edge between vertices in the clique
                if (!myGraph.getAdjacencyMat()[k.get(i)][k.get(j)]) {
                    fitness++; // Increment fitness for each missing edge
                }
            }
        }

        return  fitness;
    }
}
