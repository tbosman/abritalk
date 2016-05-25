package talkABRI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.IncompleteGridGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.view.Viewer;

import grph.algo.topology.ChordalTopologyGenerator;


public class STPExample {
	Random rnd; 
	
	
	public Path addToSTP(Graph graph, HashSet<Node> inSTP, Dijkstra dijk, Node addNode){
		dijk.setSource(addNode);
		dijk.compute();
		double minLength = Double.MAX_VALUE; 
		Node minNode = null;
		for(Node n : inSTP){
			if(dijk.getPathLength(n) < minLength){
				minNode = n;
				minLength = dijk.getPathLength(n);
			}
		}
		
		Path p = dijk.getPath(minNode);
		
		for(Node n : p.getEachNode()){
			inSTP.add(n);
		}
		
		for(Edge e : p.getEachEdge()){
			e.setAttribute("ui.class", "intree");
		}
		return p; 
	}
	
	
	public int spHeur(Graph graph, HashSet<Node> terminals, Node root, Node nextTerm){
		Dijkstra dijk = new Dijkstra(null, null, "weight");
		
		dijk.init(graph);
		
		
		HashSet<Edge> inSTPEdges = new HashSet<Edge>();
		HashSet<Node> inSTP = new HashSet<Node>();
		
		Iterator<Node> termIt = terminals.iterator();
//		Node root = termIt.next();
		
		dijk.setSource(root);
		dijk.compute();
		
//		Node nextTerm = termIt.next(); 
		Path p = dijk.getPath(nextTerm);
		
		for(Node n : p.getEachNode()){
			inSTP.add(n);
		}
		
		for(Edge e : p.getEachEdge()){
			e.setAttribute("ui.class", "intree");
		}
		
		while(!inSTP.containsAll(terminals)){
//			HashSet<Node> notInSTP = (HashSet<Node>) terminals.clone();
			for(Node n : terminals){
				if(!inSTP.contains(n)){
					addToSTP(graph, inSTP, dijk, n);
					break;
					
				}
			}
			
		}
		
		return 0;
		
	}

	public void start(){


		rnd = new Random(0); 

		int gWidth = 50; 
		int gHeight = 25;
		Graph graph = new GridGraph().returnGrid(gWidth, gHeight);
//		Graph graph = new SingleGraph("grph");
//		Generator gen = new DorogovtsevMendesGenerator();
//		
//		
//		IncompleteGridGenerator igrid =  new IncompleteGridGenerator();
//		gen.addSink(graph);
//		gen.begin();
//		for(int i=1; i<=300;i++){
//			gen.nextEvents();
//		}
//		gen.end();
		

		String css = "node {size: 5px;}"+
				"node .terminal {size:10px; fill-color: red;}"+
				"edge .intree {size:2px; fill-color: blue;}"
		;
		graph.addAttribute("ui.stylesheet", css);



		double pEdge = 0.3; 
		//		
		//		for(Edge e: graph.getEdgeSet()){
		//			if(rnd.nextDouble()<pEdge){
		//			graph.removeEdge(e);
		//			}
		//
		//		}
		double pNode =0.6;
		//		for(Node n : graph.getNodeSet()){
		//			if(rnd.nextDouble() < pNode){
		//				if(n.getDegree() > 1){
		//					
		//					 Iterator<Node> nbrs = n.getNeighborNodeIterator();
		//					Node nb1 = nbrs.next();
		//					Node nb2 = nbrs.next();
		//					if(!nb1.hasEdgeBetween(nb2)){
		//						graph.addEdge(nb1+"-"+nb2, nb1, nb2);
		//					}
		//				}
		//				graph.removeNode(n);
		//				
		//			}
		//		}


		for(Node n : graph.getNodeSet()){
			if(rnd.nextDouble() < pNode){

				for(Edge e: n.getEdgeSet()){
					if(rnd.nextDouble()<pEdge){
						graph.removeEdge(e);
					}
				}
				
				if(n.getDegree() == 2){
					Iterator<Node> nbrs = n.getNeighborNodeIterator();
					Node nb1 = nbrs.next();
					Node nb2 = nbrs.next();
					if(!nb1.hasEdgeBetween(nb2)){
						graph.addEdge(nb1+"-"+nb2, nb1, nb2);
					}
				}
				
				if(n.getDegree()<=2){
					graph.removeNode(n);
				}


			}
		}


		for(Edge e : graph.getEdgeSet()){
			double weight = 1 + 3*rnd.nextDouble();
			e.setAttribute("weight", weight);
			e.setAttribute("layout.weight", weight);
		}
		

		Viewer view = graph.display();
		view.disableAutoLayout();
		
		
		Layout sb = new SpringBox();
		sb.setForce(sb.getForce()*1.1);
		
		
		
		
		HashSet<Node> terminals = new HashSet<Node>();
		rnd = new Random(4);
		for(Node n: graph.getNodeSet()){
			if(rnd.nextDouble()<(7.0/graph.getNodeCount())){
				terminals.add(n);
				n.setAttribute("ui.class", "terminal");
//				n.setAttribute("layout.weight", 10);

			}
		}
		
		
		view.enableAutoLayout( sb);
		
		
		
//		view.disablseAutoLayout();
		
		int a=1;
		
		graph.addAttribute("ui.screenshot", "src/talkABRI/pics/steinertree1.png");
		
		for(Node n : terminals){
			spHeur(graph, terminals, n, terminals.iterator().next());
			a++;
			if(a>4){
				break;
			}
		}
		
		
		
		Collection<Edge> edgeSet = graph.getEdgeSet();
		ArrayList<String> removeEdges = new ArrayList<String>();
		
		
		
		for(Edge e : edgeSet){
			
			

			if(e.getAttribute("ui.class") == null){
				removeEdges.add(e.getId());				

			}			
		}
		
		for(String e: removeEdges){
			graph.removeEdge(e);
			
		}
		
		for(Node n : graph.getNodeSet().toArray(new Node[0])){
			if(n.getDegree() == 0){
				graph.removeNode(n);
			}
		}

	
		
//		view.enableAutoLayout(sb);

		
	}

	public static void main(String...strings){
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		new STPExample().start(); 
	}

}
