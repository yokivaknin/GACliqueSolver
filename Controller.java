package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    private TextField populationSizeTextField;
    @FXML
    private TextField maxGenerationsTextField;
    @FXML
    private TextField mutationRateTextField;
    @FXML
    private TextField cliqueSizeTextField;
    @FXML
    private TextField disasterKillPercentageTextField;
    @FXML
    private TextField timeDisasterOccurTextField;
    @FXML
    private TextField edgeSparsityTextField;
    @FXML
    private TextField amountOfVerticesTextField;
    @FXML
    private CheckBox generateGraphImageID;

    @FXML
    private Button runButton;
    @FXML
    private Button stopButton;

    //private CliqueSolver solver;
    private List<Thread> solverThreadsList = new ArrayList<>();

    @FXML
    public void initialize() {
        runButton.setOnAction(event -> runAlgorithm());
        stopButton.setOnAction(event -> stopAlgorithm());
    }

    public void chekArgumentsValidity(int populationSize, int maxGenerations, double mutationRate, int cliqueSize,
                                      double disasterKillPercentage, int timeDisasterOccur, double edgeSparsity, int amountOfVertices)
            throws IllegalArgumentException {

        if (populationSize <= 2) {
            throw new IllegalArgumentException("Population size needs to be an integer larger than 1");
        }
        if (maxGenerations <= 0) {
            throw new IllegalArgumentException("Max generations need to be an integer larger than 0");
        }
        if (mutationRate < 0 || mutationRate > 1) {
            throw new IllegalArgumentException("Mutation rate needs to be a double between 0 and 1");
        }
        if (cliqueSize <= 2) {
            throw new IllegalArgumentException("Clique size needs to be an integer larger than 1");
        }
        if (disasterKillPercentage < 0 || disasterKillPercentage > 1) {
            throw new IllegalArgumentException("Disaster kill percentage needs to be a double between 0 and 1");
        }
        if (timeDisasterOccur < 0 || timeDisasterOccur >= maxGenerations) {
            throw new IllegalArgumentException("Time disaster occur needs to be a non-negative integer and smaller then the max generations");
        }
        if (edgeSparsity < 0 || edgeSparsity > 1) {
            throw new IllegalArgumentException("Edge sparsity needs to be a double between 0 and 1");
        }
        if (amountOfVertices <= 0) {
            throw new IllegalArgumentException("Amount of vertices needs to be an integer larger than 0");
        }
    }


    private void runAlgorithm() {
        try {
            // Retrieve text field values
            int populationSize = Integer.parseInt(populationSizeTextField.getText());
            int maxGenerations = Integer.parseInt(maxGenerationsTextField.getText());
            double mutationRate = Double.parseDouble(mutationRateTextField.getText());
            int cliqueSize = Integer.parseInt(cliqueSizeTextField.getText());
            double disasterKillPercentage = Double.parseDouble(disasterKillPercentageTextField.getText());
            int timeDisasterOccur = Integer.parseInt(timeDisasterOccurTextField.getText());
            double edgeSparsity = Double.parseDouble(edgeSparsityTextField.getText());
            int amountOfVertices = Integer.parseInt(amountOfVerticesTextField.getText());
            boolean generateGraphImageFlag = generateGraphImageID.isSelected();

            //checking the validity of the args gotten from the user.
            chekArgumentsValidity(populationSize,maxGenerations,mutationRate,cliqueSize
                    ,disasterKillPercentage,timeDisasterOccur,edgeSparsity,amountOfVertices);

            // Create graph and solver
            boolean[][] adjacencyMatrix = CliqueSolver.CreateAdjacencyMatrix(amountOfVertices, cliqueSize, edgeSparsity);
            myGraph graph = new myGraph(adjacencyMatrix);


            // Run the solver in a new thread
            Thread solverThread = new Thread(() -> {

               CliqueSolver solver = new CliqueSolver(graph, populationSize, maxGenerations, mutationRate, cliqueSize, disasterKillPercentage, timeDisasterOccur);

                //time the GA solution for comparing with the trivial solution.
                long startTime = System.currentTimeMillis();
                long endTime;
                int bestClique = solver.solve();

                endTime = System.currentTimeMillis();


                // Print the found clique
                Platform.runLater(() -> {
                    showMessage("Best clique fitness found : " + bestClique + "\n in: " + (endTime - startTime) + " milliseconds.");});


                    //plot the fitness compere to the number of generations.
                CliqueSolver.plotFitnessHistory(solver.getBestFitnessHistory());

                // if the user want to visualise the graph.
                if(generateGraphImageFlag){
                    // Create a sample graph
                    Graph<Integer, DefaultEdge> graphToVisualise = new DefaultUndirectedGraph<>(DefaultEdge.class);

                    // Add all vertices first
                    for (int i = 0; i < adjacencyMatrix.length; i++) {
                        graphToVisualise.addVertex(i);
                    }

                    //Add all edges
                    for (int i = 0; i < adjacencyMatrix.length; i++){
                        for (int j = i; j < adjacencyMatrix.length; j++){
                            if(adjacencyMatrix[i][j] == true)
                                graphToVisualise.addEdge(i,j);
                        }
                    }

                    GraphVisualizer.displayGraph(graphToVisualise, solver.getBestCliqueFound());
                }

            });

            solverThread.setDaemon(true);
            solverThread.start();

            //keep track of all threads opened.
            solverThreadsList.add(solverThread);

        } catch (NumberFormatException e) {
            // Handle invalid number format
            showAlert("Please enter valid numeric values.");
        } catch (IllegalArgumentException e) {
            // Handle other invalid inputs
            showAlert(e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showMessage (String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("results");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void stopAlgorithm() {
        for (Thread solverThread : solverThreadsList) {
            if (solverThread != null) {
                solverThread.stop(); // killing the thread
            }
        }
        solverThreadsList.clear(); // Clear the list of threads
    }
}
