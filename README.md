# README

## Project Title: Efficient Algorithm Exploration for NP Problems Using Genetic Algorithms

### Overview
This project was developed in my free time between academic semesters. The primary objective was to explore more efficient algorithms for solving NP problems, with a specific focus on the use of Genetic Algorithms (GA). The project consists of several components that work together to find cliques within a graph, a well-known NP problem.

### Project Components

1. **Graph Representation (myGraph.java)**
   - This class represents the graph using an adjacency matrix.
   - It provides methods for adding and removing edges and calculating the fitness of a solution in terms of the number of missing edges within a proposed clique.

2. **Clique Finder (TrivialCliqueSolver.java)**
   - This component contains methods to identify cliques of a specified size within the graph.
   - It uses a recursive approach to check if a given set of vertices forms a clique.
   - The TrivialCliqueSolver is not accessible from the UI and was developed solely for benchmarking the efficiency of the Genetic Algorithm.
     This component serves as a baseline for comparison in the results section below.

3. **Graph Visualization (GraphVisualizer.java)**
   - Utilizes JGraphT and JGraphX libraries to visually represent the graph and highlight specific nodes.
   - The graph is displayed in a JFrame, with nodes involved in the identified clique highlighted in red.

4. **User Interface (Main.fxml)**
   - A JavaFX-based UI for inputting parameters like population size, mutation rate, and more.
   - Includes controls to start and stop the genetic algorithm and options to display the graph.

### Purpose and Motivation
The project's purpose is to delve into the application of Genetic Algorithms (GA) for efficiently solving NP problems. By experimenting with various parameters and techniques, the aim is to understand and optimize the performance of GAs in finding cliques within a graph. This exploration helps in gaining insights into the potential of GAs in tackling complex computational problems.

### How to Run
1. Clone the repository.
2. Make sure you have the necessary dependencies installed (JGraphT, JGraphX, JavaFX).
3. Compile and run the project using your preferred Java IDE or build tool.
4. Use the provided UI to set parameters and start the algorithm.

### results

1.The following graph illustrates the average execution time (in milliseconds) on the y-axis (note that the y-axis is logaritmic) versus the number of vertices in the graph on the x-axis, based on 50 runs for each algorithm. The parameters for these tests were set as follows:

- Population size: 100
- Edge sparsity: 0.15
- Mutation rate: 0.2
- Clique size: 10
- Disaster kill percentage: 0.9
- Time disaster occurs: 1000

![ES0 15POP100MU0 2CS10DK1000AVG50](https://github.com/user-attachments/assets/5ab75431-4345-48d3-a84b-929ac0b75676)

2. The following graph depicts the fitness of the best individual in each generation on the y-axis, with the number of generations represented on the x-axis (note that the x-axis is logarithmic). The parameters for these tests were set as follows:

Population size: 100
Edge sparsity: 0.15
Mutation rate: 0.3
Clique size: 20
Disaster kill percentage: 0.9
Time disaster occurs: 1000
Number of vertices: 1500

* The search space for a clique of size 20 from 1500 vertices is approximately 1/(1500 choose 20) = 8.3 * 10^-46.

* The jumps observed every 1000 generations are due to the algorithm attempting to "escape" local minima.
* 
![ES0 15POP100MU0 3CS20EV1500DK0 9](https://github.com/user-attachments/assets/c06a7897-7d1a-4bc2-8016-643d4ec64002)


### Contributions
Contributions to the project are welcome. If you find any issues or have suggestions for improvements, please feel free to open an issue or submit a pull request.

### License
This project is licensed under the MIT License. See the LICENSE file for more details.

### Acknowledgements
Special thanks to the open-source community for providing the tools and libraries that made this project possible.

### Contact
For any inquiries or further information, please contact me at [yokivaknin17@gmail.com].

---

Feel free to customize and expand upon this README as needed for your project's specific details and requirements!
