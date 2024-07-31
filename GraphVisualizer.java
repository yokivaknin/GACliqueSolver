package sample;
import javax.swing.JFrame;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class GraphVisualizer {

    public static void displayGraph(Graph<Integer, DefaultEdge> graph, ArrayList<Integer> nodesToColor) {
        // Create a JGraphX adapter for the JGraphT graph
        JGraphXAdapter<Integer, DefaultEdge> graphAdapter = new JGraphXAdapter<>(graph);

        // Create a layout for the graph
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter); // Change to mxCircleLayout for better organization
        layout.execute(graphAdapter.getDefaultParent());

        // Color specified nodes in red
        Map<Integer, mxICell> vertexToCellMap = graphAdapter.getVertexToCellMap();
        for (Integer node : nodesToColor) {
            mxICell cell = vertexToCellMap.get(node);
            if (cell != null) {
                graphAdapter.setCellStyle("fillColor=red", new Object[]{cell});
            }
        }

        // Create a swing component that holds the graph and its layout
        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);
        graphComponent.setConnectable(false);
        graphComponent.getGraph().setAllowDanglingEdges(false);

        // Create a frame to display the graph
        JFrame frame = new JFrame();
        frame.getContentPane().add(graphComponent);
        frame.setTitle("Graph Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Set a preferred size
        frame.setVisible(true);

        // Add a listener to resize and re-center the graph when the window is resized
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                // Scale the graph to fit the window size
                Dimension newSize = frame.getContentPane().getSize();
                graphComponent.getGraphControl().setPreferredSize(newSize);
                graphComponent.getGraphControl().revalidate();
                graphComponent.zoomAndCenter();
            }
        });

        // Initial centering and scaling
        graphComponent.zoomAndCenter();
    }

}
