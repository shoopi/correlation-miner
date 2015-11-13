package main.java.nl.tue.ieis.is.correlation.graph;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JScrollPane;

import main.java.nl.tue.ieis.is.correlation.objects.PossibleEdge;

import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;

public class GraphUtil extends JApplet implements MouseWheelListener {
	private static final long serialVersionUID = 1L;
	private SimpleDirectedWeightedGraph<Node, Edge> graph;
	private JGraphXAdapter<Node, Edge> jgraphX;
	private mxGraphComponent graphComponent;
	
	private List<Node> nodeList;
	private Map<PossibleEdge, Integer> edgeList;
	
	public GraphUtil(List<Node> nodeList, Map<PossibleEdge, Integer> edgeList)
			throws HeadlessException {
		super();
		graph = new SimpleDirectedWeightedGraph<Node, Edge>(Edge.class);
		this.nodeList = nodeList;
		this.edgeList = edgeList;
		addNodesToGraph();
		addPossibleEdgesToGraph();
	}
	
	public SimpleDirectedWeightedGraph<Node, Edge> getGraph() {
		return graph;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {		
		if (e.getWheelRotation() < 0) graphComponent.zoomIn();
		else graphComponent.zoomOut();
	}
	
	public JApplet draw (String title) {
		jgraphX = new JGraphXAdapter<Node, Edge>(graph);
        graphComponent = new mxGraphComponent(jgraphX);        

        JApplet frame2 = new JApplet();
        frame2.getContentPane().add(new JScrollPane (graphComponent));
        frame2.addMouseWheelListener(this);
        
        graphComponent.setExportEnabled(true);
        
        frame2.setSize(800, 800);
        frame2.setVisible(true);
        
        try {
	        jgraphX.getModel().beginUpdate();
	        for (mxCell cell : jgraphX.getVertexToCellMap().values()) {
	        	jgraphX.getModel().setGeometry(cell, new mxGeometry(20, 20, 20, 20));
	            jgraphX.updateCellSize(cell);
	        }
        } finally {
        	jgraphX.getModel().endUpdate();
        }
        
        mxGraphComponent graphComponent = new mxGraphComponent(jgraphX);    
        graphComponent.setConnectable(false);
        graphComponent.setAutoExtend(false);
        graphComponent.setDragEnabled(false);
        
        //jgraphX.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_NOLABEL, "1");
        jgraphX.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_STROKEWIDTH, 3);
        jgraphX.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        jgraphX.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_FONTCOLOR, "#EA7201");
        jgraphX.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_FONTSIZE, 13);
        jgraphX.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_FONTFAMILY, "Times New Roman");
        jgraphX.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);

        jgraphX.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_STROKECOLOR, "#FF0000");
        jgraphX.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_FILLCOLOR, "#FFFF00");
        jgraphX.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        jgraphX.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_ROUNDED, true);
        jgraphX.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        jgraphX.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_FONTFAMILY, "Times New Roman");
        jgraphX.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_FONTSIZE, 15);
        jgraphX.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_SPACING, "10");
        
        jgraphX.setAutoSizeCells(true);
        jgraphX.setAutoOrigin(true);
        jgraphX.setCellsDeletable(false);
        jgraphX.setCellsResizable(true);
        jgraphX.setCellsEditable(false);
        jgraphX.setCellsSelectable(true);
        jgraphX.setLabelsVisible(true);
        jgraphX.setLabelsClipped(false);
        jgraphX.setCollapseToPreferredSize(true);
        jgraphX.setSplitEnabled(false);
        jgraphX.setResetEdgesOnConnect(false);
        jgraphX.setResetEdgesOnMove(true);
        jgraphX.setResetEdgesOnResize(true);
        jgraphX.setResetViewOnRootChange(true);
        jgraphX.setCellsDisconnectable(false);
        jgraphX.setConnectableEdges(false);
        jgraphX.setCellsDisconnectable(false);
        jgraphX.setConnectableEdges(false);
        jgraphX.setCellsCloneable(false);
        jgraphX.setCellsEditable(false);
        jgraphX.setAllowDanglingEdges(false);

        mxHierarchicalLayout layout2 = new mxHierarchicalLayout(jgraphX);
        
        layout2.setDisableEdgeStyle(false);
        layout2.setFineTuning(true);
        layout2.setMoveParent(true);
        layout2.setResizeParent(true);
        layout2.setInterRankCellSpacing(100);
        
        Object cell = jgraphX.getDefaultParent();
		jgraphX.getModel().beginUpdate();
		try {
			layout2.execute(cell);
			for(Object c : jgraphX.getChildVertices(cell)) {
				mxGeometry geometry = (mxGeometry) (((mxCell) c).getGeometry()).clone();
		        mxRectangle bounds = jgraphX.getView().getState(((mxCell) c)).getLabelBounds();
		        geometry.setHeight(bounds.getHeight() + 15);
		        geometry.setWidth (bounds.getWidth() + 40);
		        jgraphX.cellsResized(new Object[] { ((mxCell) c) }, new mxRectangle[] { geometry });
			}
		} finally {
			jgraphX.getModel().endUpdate();
		}
		return frame2;
	}


	public void exportModel(String filePath, String format) {
		VertexNameProvider<Node> vertexNameProvider = new VertexNameProvider<Node>() {
			@Override 
			public String getVertexName(Node v) { 
				return v.getActivityName();
				} 
		}; 
		EdgeNameProvider<Edge> edgeNameProvider = new EdgeNameProvider<Edge>() { 
			@Override 
			public String getEdgeName(Edge e) { 
				return (int)e.getOccurance() + ""; 
			} 
		}; 
		
		File file = new File(filePath);
		GraphVizGraphExporter<Node, Edge> exporter = new GraphVizGraphExporter<Node, Edge>(filePath + ".dot", format);
	
		try {
			exporter.exportGraph(graph, vertexNameProvider, edgeNameProvider, file);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void addNodesToGraph() {
		for(Node n : nodeList) {
			graph.addVertex(n);
		}
	}

	private void addPossibleEdgesToGraph() {
		//Map<Edge, Integer> weightedEdges = new HashMap<Edge, Integer>();
		for(Map.Entry<PossibleEdge, Integer> edge : edgeList.entrySet()) {
			Edge e = new Edge(getNodeByName(edge.getKey().getActivity1()),  getNodeByName(edge.getKey().getActivity2()), edge.getValue(), edge.getKey().getProbability());
			graph.setEdgeWeight(e, edge.getValue());
			graph.addEdge((getNodeByName(edge.getKey().getActivity1())),  getNodeByName(edge.getKey().getActivity2()), e); 
			//weightedEdges.put(e, edge.getValue());
		}
	}
	
	private Node getNodeByName(String activityName) {
		Set<Node> nodes = graph.vertexSet();
		for(Node n : nodes) {
			if(n.getActivityName().contentEquals(activityName))
				return n;
		}
		return null;
	}
	
	public BufferedImage generatePicture(String dest) {
		BufferedImage image = mxCellRenderer.createBufferedImage(jgraphX, null, 1, Color.WHITE, true, null);
		//"C:\\Temp\\graph.png"
		try {
			ImageIO.write(image, "PNG", new File(dest));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
}
