package main.java.nl.tue.ieis.is.correlation.graph;

import java.io.File;
import java.io.IOException;

import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;

public interface GraphExporter<V, E> {

        public void exportGraph(final DirectedGraph<V, E> graph, VertexNameProvider<V> vertexLabelProvider,
                        EdgeNameProvider<E> edgeLabelProvider, File targetFile) throws IOException;

}