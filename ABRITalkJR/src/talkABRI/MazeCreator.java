package talkABRI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.graphstream.algorithm.Kruskal;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

public class MazeCreator {
	
	
	
	public void start(){
		Graph graph = new GridGraph().returnGrid(30, 15);
		
		Random rnd = new Random(3);
		for(Edge e : graph.getEachEdge()){
			e.setAttribute("weight", rnd.nextInt(100));
		}
		
		
		
		String css = "edge .notintree {size:1px;fill-color:gray;} " +
                "edge .intree {size:15px;fill-color:black;}"+
				"node {size: 15px;}" +
                "node #n0x0 {fill-color: green;}" +
                "node #n10x19 {fill-color: blue;}"
				;
		
		
		graph.addAttribute("ui.stylesheet", css);
		
		
		Kruskal krusk = new Kruskal("ui.class", "intree", "notintree");
		krusk.init(graph);
		krusk.compute();
		
		
		Viewer view = graph.display();
		view.disableAutoLayout();
		
		
		Collection<Edge> edgeSet = graph.getEdgeSet();
		ArrayList<String> removeEdges = new ArrayList<String>();
		for(Edge e : edgeSet){
			System.out.println(e.getAttribute("ui.class").toString());
			if(e.getAttribute("ui.class").equals("notintree")){

				removeEdges.add(e.getId());				
			}
		}
		
		double pEdge = 0.1;
		for(String e: removeEdges){
			if(rnd.nextDouble() < pEdge){
				graph.getEdge(e).setAttribute("ui.class", "intree");
			}else{
			graph.removeEdge(e);
			}
		}
		
		
		view.enableAutoLayout();
		
		view.disableAutoLayout();
		
	}
	
	
	public static void main(String...strings){
		new MazeCreator().start();
	}

}
