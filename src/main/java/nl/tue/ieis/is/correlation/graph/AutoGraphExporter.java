package main.java.nl.tue.ieis.is.correlation.graph;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;

public class AutoGraphExporter<V, E> implements GraphExporter<V, E> {
	
	private final Map<String, GraphExporter<V, E>> exporters;
	
	public AutoGraphExporter(final String graphVizDotFile) {
		final Map<String, GraphExporter<V, E>> exporters = new HashMap<String, GraphExporter<V, E>>();
		exporters.put("pdf", new GraphVizGraphExporter<V, E>(graphVizDotFile, "pdf"));
		exporters.put("svg", new GraphVizGraphExporter<V, E>(graphVizDotFile, "svg"));
		exporters.put("png", new GraphVizGraphExporter<V, E>(graphVizDotFile, "png"));
		this.exporters = exporters;
	}

	public Map<String, GraphExporter<V, E>> getExporters() {
		return Collections.unmodifiableMap(this.exporters);
	}

	public void exportGraph(DirectedGraph<V, E> graph, VertexNameProvider<V> vertexLabelProvider,
	                EdgeNameProvider<E> edgeLabelProvider, File targetFile) throws IOException {

		final String name = targetFile.getName();
		final int lastIndexOfDot = name.lastIndexOf('.');
		final String extension = (0 <= lastIndexOfDot && lastIndexOfDot < (name.length() - 1)) ?
	                name.substring(lastIndexOfDot + 1) : null;
        final GraphExporter<V, E> exporter = getExporters().get(extension);
        
        if (exporter != null) {
	        exporter.exportGraph(graph, vertexLabelProvider, edgeLabelProvider, targetFile);
    	} else {
	        System.out.println("Could not find graph exporter for the [" + extension + "] file extension.");
        }
	}
}