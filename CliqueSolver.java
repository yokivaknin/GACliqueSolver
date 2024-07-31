package sample;
import java.awt.*;
import java.util.*;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.JFrame;


import javax.swing.JFrame;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

public class CliqueSolver {

    private final myGraph graph;
    private final int cliqueSize;
    private final int populationSize;
    private final int maxGenerations;
    private double mutationRate;

    private ArrayList<Integer> bestCliqueFound;
    //disaster parameters to jump out of local minima's.
    private final double disasterKillPercentage;
    private  final int timeDisasterOccur;

    // Array to store fitness of best solution per generation
    private final ArrayList<Integer> bestFitnessHistory;


    public CliqueSolver(myGraph graph, int populationSize, int maxGenerations,
                        double mutationRate, int cliqueSize, double disasterKillPercentage, int timeDisasterOccur) {
        this.graph = graph;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.mutationRate = mutationRate;
        this.cliqueSize = cliqueSize;
        this.bestFitnessHistory = new ArrayList<>();
        this.disasterKillPercentage = disasterKillPercentage;
        this.timeDisasterOccur = timeDisasterOccur;
    }

    public int solve() {

        //find the vertices that are candidates to be a part of a clique.
        ArrayList<Integer> candidates;
        candidates = findCandidates(graph);

        // Initialize population with random cliques
        ArrayList<ArrayList<Integer>> population = initializePopulation(candidates);

        //remember what rate of mutation to return to.
        double originalMutationRate = mutationRate;

        int lowestFitnessFount = Integer.MAX_VALUE;
        bestCliqueFound = new ArrayList<>(population.get(0));

        // Iterate for the specified number of generations
        for (int generation = 0; generation < maxGenerations; generation++) {
            // Selection - Tournament selection
            ArrayList<ArrayList<Integer>> selected = tournamentSelection(population);

            //creating offsprings from the selected group.
            ArrayList<ArrayList<Integer>> offspring = crossover(selected);

            //mutation control mechanism
            if((generation >= 100) &&
                    (bestFitnessHistory.get(bestFitnessHistory.size()-1).equals(bestFitnessHistory.get(bestFitnessHistory.size()-2))) &&
                    (mutationRate < 0.95)){

                mutationRate += 0.0001;

            }else if((generation >= 100) &&
                    (!bestFitnessHistory.get(bestFitnessHistory.size()-1).equals(bestFitnessHistory.get(bestFitnessHistory.size()-2))) &&
                    mutationRate>0.01){

                mutationRate = originalMutationRate;
            }
/*
            if((generation >= 100) &&
                    (bestFitnessHistory.get(bestFitnessHistory.size()-1) >= lowestFitnessFount) &&
                    (mutationRate < 0.95)){

                mutationRate += 0.0001;

            }else if((generation >= 100) &&
                    (bestFitnessHistory.get(bestFitnessHistory.size()-1) < lowestFitnessFount) &&
                    mutationRate>0.01){

                mutationRate = originalMutationRate;
            }
*/
            // Mutation - Random bit mutation
            mutation(offspring, candidates);

            // Apply disaster at specified intervals
            if (generation % timeDisasterOccur == 0 && generation != 0) {
                applyDisaster(population, disasterKillPercentage, candidates);
            }

            // Replacement - Generational replacement
            population = generationalReplacement(population, offspring);

            // Get and store fitness of best solution in this generation
            ArrayList<Integer> bestCliqueInCurrentGeneration = population.get(0);

            //print every 100 generation status.
            if(generation%100 == 0)
                System.out.println("in generation: " + generation + " best Clique is: " + bestCliqueInCurrentGeneration + " with fitness of: " + graph.getFitness(graph,bestCliqueInCurrentGeneration));

            //find best fitness for this generation.
            int bestFitnessInCurrentGeneration = graph.getFitness(graph, bestCliqueInCurrentGeneration);
            bestFitnessHistory.add(bestFitnessInCurrentGeneration);

            //save the best fitness found over all.
            if(bestFitnessInCurrentGeneration < lowestFitnessFount){
                lowestFitnessFount = bestFitnessInCurrentGeneration;
                bestCliqueFound = population.get(0);
            }


            //if clique has been found.
            if(bestFitnessInCurrentGeneration == 0)
                break;
        }
        System.out.println(bestFitnessHistory + " \n\n" );
        System.out.println("the lowest fitness found over all was: " + lowestFitnessFount + " for the clique: " + bestCliqueFound);

        // Return the clique with the best fitness
        return lowestFitnessFount;
    }

    public ArrayList<Integer> getBestCliqueFound(){
        return bestCliqueFound;
    }

    public ArrayList<Integer> getBestFitnessHistory(){
        return bestFitnessHistory;
    }

    public static void plotFitnessHistory(ArrayList<Integer> bestFitnessHistory) {
        XYSeries series = new XYSeries("Fitness");

        for (int i = 0; i < bestFitnessHistory.size(); i++) {
            series.add(i + 1, bestFitnessHistory.get(i)); // Generation number starts from 1
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Fitness Over Generations",
                "Generation",
                "Fitness",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        XYPlot plot = chart.getXYPlot();
        LogarithmicAxis logAxis = new LogarithmicAxis("Generation");
        plot.setDomainAxis(logAxis);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        JFrame frame = new JFrame();
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

    //find the vertices that are candidates to be a part of a clique.
    private  ArrayList<Integer> findCandidates(myGraph graph){
        ArrayList<Integer> candidates = new ArrayList<>();
        for (int i = 0; i< graph.getVertices(); i++){
            int count = 0;
            for (int j = 0; j<graph.getVertices(); j++){
                if(graph.getAdjacencyMat()[i][j] == true && count < (cliqueSize - 1)){
                    count++;
                }if(count == (cliqueSize - 1)){
                    candidates.add(i);
                    break;
                }
            }
        }

        return candidates;
    }

    private ArrayList<ArrayList<Integer>> initializePopulation(ArrayList<Integer> candidates) {
        ArrayList<ArrayList<Integer>> population = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < populationSize; i++) {
            ArrayList<Integer> clique = new ArrayList<>();
            while (clique.size() < cliqueSize) {
                int vertexIndexInCandidates = random.nextInt(candidates.size());
                if (!clique.contains(candidates.get(vertexIndexInCandidates))) {
                    clique.add(candidates.get(vertexIndexInCandidates));
                }
            }
            population.add(clique);
        }
        return population;
    }

    private ArrayList<ArrayList<Integer>> tournamentSelection(ArrayList<ArrayList<Integer>> population) {
        ArrayList<ArrayList<Integer>> selected = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < populationSize; i++) {
            int competitor1 = random.nextInt(populationSize);
            int competitor2 = random.nextInt(populationSize);
            while (competitor1 == competitor2) {
                competitor2 = random.nextInt(populationSize);
            }
            ArrayList<Integer> contender1 = population.get(competitor1);
            ArrayList<Integer> contender2 = population.get(competitor2);
            selected.add(graph.getFitness(graph, contender1) < graph.getFitness(graph, contender2) ? contender1 : contender2);
        }
        return selected;
    }

    private void singlePointCrossover(ArrayList<ArrayList<Integer>> offspring, ArrayList<Integer> parent1, ArrayList<Integer> parent2){
        Random random = new Random();

        int crossoverPoint = random.nextInt(parent1.size() - 2) + 1;
        ArrayList<Integer> child1 = new ArrayList<>(parent1.subList(0, crossoverPoint));
        child1.addAll(parent2.subList(crossoverPoint, parent2.size()));
        ArrayList<Integer> child2 = new ArrayList<>(parent2.subList(0, crossoverPoint));
        child2.addAll(parent1.subList(crossoverPoint, parent1.size()));

        // Repair children to remove duplicates
        child1 = repairChild(child1, parent1, parent2);
        child2 = repairChild(child2, parent1, parent2);

        offspring.add(child1);
        offspring.add(child2);
    }

    private ArrayList<ArrayList<Integer>> crossover(ArrayList<ArrayList<Integer>> selected) {
        ArrayList<ArrayList<Integer>> offspring = new ArrayList<>();

        for (int i = 0; i < populationSize - 1; i += 2) {
            ArrayList<Integer> parent1 = selected.get(i);
            ArrayList<Integer> parent2 = selected.get(i + 1);

            if(this.cliqueSize>5)
                doublePointCrossover(offspring,parent1,parent2);
            else
                singlePointCrossover(offspring,parent1,parent2);
        }

        return offspring;
    }

    private void doublePointCrossover(ArrayList<ArrayList<Integer>> offspring, ArrayList<Integer> parent1, ArrayList<Integer> parent2) {
        Random random = new Random();
        int size = parent1.size();

        // Ensure crossover points are not trivial
        int crossoverPoint1 = random.nextInt(size - 2) + 1;
        int crossoverPoint2;
        do {
            crossoverPoint2 = random.nextInt(size - 2) + 1;
        } while (crossoverPoint2 == crossoverPoint1 || Math.abs(crossoverPoint1 - crossoverPoint2) < 2);

        if (crossoverPoint1 > crossoverPoint2) {
            int temp = crossoverPoint1;
            crossoverPoint1 = crossoverPoint2;
            crossoverPoint2 = temp;
        }

        ArrayList<Integer> child1 = new ArrayList<>(parent1.subList(0, crossoverPoint1));
        child1.addAll(parent2.subList(crossoverPoint1, crossoverPoint2));
        child1.addAll(parent1.subList(crossoverPoint2, size));

        ArrayList<Integer> child2 = new ArrayList<>(parent2.subList(0, crossoverPoint1));
        child2.addAll(parent1.subList(crossoverPoint1, crossoverPoint2));
        child2.addAll(parent2.subList(crossoverPoint2, size));

        ArrayList<Integer> child3 = new ArrayList<>(parent1.subList(0, crossoverPoint1));
        child3.addAll(parent2.subList(crossoverPoint1, crossoverPoint2));
        child3.addAll(parent1.subList(crossoverPoint2, size));

        // Ensure no duplicates in the children
        child1 = repairChild(child1, parent1, parent2);
        child2 = repairChild(child2, parent1, parent2);
        child3 = repairChild(child3, parent1, parent2);

        offspring.add(child1);
        offspring.add(child2);
        offspring.add(child3);
    }

    // Repair function to ensure no duplicates in the child
    private ArrayList<Integer> repairChild(ArrayList<Integer> child, ArrayList<Integer> parent1, ArrayList<Integer> parent2) {

        Set <Integer> childVertices = new HashSet<>(child);
        if (childVertices.size() == child.size())
            return child;

        Set<Integer> parentVertices = new HashSet<>(parent1);
        parentVertices.addAll(parent2);

        parentVertices.removeAll(childVertices);

        ArrayList <Integer> verticesLeft = new ArrayList<>(parentVertices);

        for(int i = 0; i<child.size(); i++){
            if(childVertices.contains(child.get(i)))
                childVertices.remove(child.get(i));
            else {
                child.set(i,verticesLeft.remove(0));
            }
        }

        return child;
    }

    private void mutation(ArrayList<ArrayList<Integer>> offspring, ArrayList<Integer> candidates) {
        Random random = new Random();
        for (ArrayList<Integer> clique : offspring) {
            for (int i = 0; i < clique.size(); i++) {
                if (random.nextDouble() < mutationRate) {
                    int vertexIndexInCandidates = random.nextInt(candidates.size());

                    int dontTryForEver = 0;
                    while (clique.contains(candidates.get(vertexIndexInCandidates)) && dontTryForEver<100){
                        vertexIndexInCandidates = random.nextInt(candidates.size());
                        dontTryForEver++;
                    }

                    if(dontTryForEver<100)
                        clique.set(i, candidates.get(vertexIndexInCandidates));
                }
            }
        }
    }

    private ArrayList<ArrayList<Integer>> generationalReplacement(ArrayList<ArrayList<Integer>> population, ArrayList<ArrayList<Integer>> offspring) {
        ArrayList<ArrayList<Integer>> combined = new ArrayList<>(population);
        combined.addAll(offspring);
        Collections.sort(combined, (c1, c2) -> graph.getFitness(graph, c1) - graph.getFitness(graph, c2));
        return new ArrayList<>(combined.subList(0, populationSize));
    }

    private void applyDisaster(ArrayList<ArrayList<Integer>> population, double disasterKillPercentage, ArrayList<Integer> candidates) {
        Random random = new Random();
        int killCount = (int) (population.size() * disasterKillPercentage);
        for (int i = 0; i < killCount; i++) {
            population.remove(random.nextInt(population.size()));
        }
        while (population.size() < populationSize) {
            ArrayList<Integer> newClique = initializeClique(candidates);
            population.add(newClique);
        }
    }

    private ArrayList<Integer> initializeClique(ArrayList<Integer> candidates) {
        ArrayList<Integer> clique = new ArrayList<>();
        Random random = new Random();
        while (clique.size() < cliqueSize) {
            int vertexIndexInCandidates = random.nextInt(candidates.size());
            if (!clique.contains(candidates.get(vertexIndexInCandidates))) {
                clique.add(candidates.get(vertexIndexInCandidates));
            }
        }
        return clique;
    }

    public static boolean[][] CreateAdjacencyMatrix (int vertex, int cliqueSize, double edgeSparsity){

        Random random = new Random();

        boolean[][] toReturnMatrix = new boolean[vertex][vertex];
        Set<Integer> cliqueVertices = new HashSet<>();

        // randomly selecting the vertices for te clique.
        while (cliqueVertices.size()<cliqueSize){
            cliqueVertices.add(random.nextInt(vertex));
        }


        //creating the clique.
        for(Integer ver1 : cliqueVertices){
            for (Integer ver2 : cliqueVertices){
                toReturnMatrix[ver1][ver2] = true;
            }
        }

        // for every other edge possible we decide randomly if it is in the graph or not.
        // Using the edgeSparsity to determine the connectivity of the graph.
        for(int i = 0; i<vertex; i++){
            for (int j = i+1; j<vertex; j++){
                if(!cliqueVertices.contains(i) || !cliqueVertices.contains(j)){
                    if(random.nextDouble() <= edgeSparsity){
                        toReturnMatrix[i][j] = true;
                        toReturnMatrix[j][i] = true;
                    }else {
                        toReturnMatrix[i][j] = false;
                        toReturnMatrix[j][i] = false;
                    }
                }
            }
        }

        //all the edges from a vertex to itself not exist.
        //it is important later for the findCandidates function.
        for(int i = 0; i<vertex; i++){
            toReturnMatrix[i][i] = false;
        }

        System.out.println("the right clique is: \n" + cliqueVertices + " \n\n ");

        return toReturnMatrix;
    }

    private static void plotTimeComparison(ArrayList<Long> AVGTimeForGA, ArrayList<Long> AVGTimeForTrivial, int VertexCount) {
        XYSeries gaSeries = new XYSeries("GA Solver");
        XYSeries trivialSeries = new XYSeries("Trivial Solver");

        int vertexCount = VertexCount;
        for (int i = 0; i < AVGTimeForGA.size(); i++) {
            // Add a small offset to avoid zero values
            gaSeries.add(vertexCount, AVGTimeForGA.get(i) + 1);
            trivialSeries.add(vertexCount, AVGTimeForTrivial.get(i) + 1);
            vertexCount++;
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(gaSeries);
        dataset.addSeries(trivialSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Time Comparison of GA Solver vs Trivial Solver",
                "Number of Vertices",
                "Time (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        // Set logarithmic axis for time
        LogarithmicAxis yAxis = new LogarithmicAxis("Time (ms)");
        plot.setRangeAxis(yAxis);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.RED);
        plot.setRenderer(renderer);

        JFrame frame = new JFrame("Comparison Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
