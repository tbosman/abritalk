package talkABRI;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.graphstream.algorithm.Kruskal;
import org.graphstream.algorithm.flow.FordFulkersonAlgorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.Quality;
import org.graphstream.stream.file.FileSinkImages.Resolution;
import org.graphstream.stream.file.FileSinkImages.Resolutions;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.LinLog;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.layout.springbox.implementations.SpringBoxNodeParticle;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;

public class MazeCreator {


	private String prefix;


	public String getNodeName(int x, int y){
		return "n"+x+"x"+y; 
	}

	public void leftHandRule(Graph graph, int startX, int startY, 
			String end){
		int x = startX; 
		int y = startY;

		String curNode = getNodeName(x,y);
		String lastNode = curNode;

		// Directions index from left (0) clockwise to down (3)
		String[] nbrs = new String[4]; 
		nbrs[0] = getNodeName(x-1, y);
		nbrs[1] = getNodeName(x, y+1);
		nbrs[2] = getNodeName(x+1, y);
		nbrs[3] = getNodeName(x, y-1); 

		int lastMove = 1; 

		int nextMove = (lastMove + 3)%4; 

		graph.addAttribute("ui.screenshot", ".\\src\\talkABRI\\pics\\"+prefix+"dfs_0.png");
		int i=1;

		while(!curNode.equals(end)){
			nbrs[0] = getNodeName(x-1, y);
			nbrs[1] = getNodeName(x, y+1);
			nbrs[2] = getNodeName(x+1, y);
			nbrs[3] = getNodeName(x, y-1); 


			while(graph.getNode(nbrs[nextMove])== null || !graph.getNode(curNode).hasEdgeBetween(nbrs[nextMove]) || 
					(graph.getNode(nbrs[nextMove]).hasAttribute("ui.class") &&  graph.getNode(nbrs[nextMove]).getAttribute("ui.class").equals("block")) ){
				//change move one quarter clock wise 
				nextMove = (nextMove+1)%4;
			}
			//			if(graph.getNode(nbrs[nextMove])!= null && graph.getNode(curNode).hasEdgeBetween(nbrs[nextMove])){			
			lastNode = curNode;			
			curNode = nbrs[nextMove];


			graph.getNode(lastNode).setAttribute("ui.style", "fill-color:#999977;stroke-mode:none;");
			if(!curNode.equals(end)){
				graph.getNode(curNode).setAttribute("ui.style", "fill-color:#eeee77;stroke-mode:plain; stroke-color:black; stroke-width:3px;");
			}
			//update x, y
			switch(nextMove){
			case 0: 
				x--;
				break;
			case 1:
				y++;
				break;
			case 2:
				x++;
				break;
			case 3: 
				y--;
				break;
			}

			//set move priority to turn left compared to current direction
			nextMove = (nextMove+3)%4;

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(graph.getNode(curNode).getDegree()!=2){
				graph.addAttribute("ui.screenshot", ".\\src\\talkABRI\\pics\\"+prefix+"dfs_"+i++ +".png");
			}

		}
	}


	public void start() throws IOException{

		prefix = "gridmov";

		int gWidth = 30; 
		int gHeight = 15;
		Graph graph = new GridGraph().returnGrid(30, 15);

		Random rnd = new Random(3);
		for(Edge e : graph.getEachEdge()){
			e.setAttribute("weight", rnd.nextInt(100));
		}


		graph.removeNode("n16x7");
		graph.removeNode("n14x7");
		graph.removeNode("n15x8");
		graph.removeNode("n16x8");
		graph.removeNode("n14x8");


		String css = "graph {fill-color:black;}"
				+ "edge .notintree {size:1px;fill-color:gray;} " +
				"edge .intree {size:10px;fill-color:white;shape: blob;}"+
				"node {size: 10px; fill-color: white;}" +
				"node .leaf {size: 10px;}"+
				"node #n0x0 {fill-color: red; size: 10px; shape: box;stroke-mode:plain; stroke-color: white;}" +
				//                "node #n19x10 {fill-color: blue;size: 10px; shape: cross; stroke-mode:plain;  stroke-color: white;}"+
				"node #n15x7 {fill-color: blue;size: 10px; shape: box; stroke-mode:plain;  stroke-color: white;}"+
				"edge .leafedge {size: 10px; fill-color: white; shape: line;}"+				
				"sprite { text-size: 15px; text-color: white;}" 
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
			prefix = "treemovsplit";
			System.out.println(e.getAttribute("ui.class").toString());
			if(e.getAttribute("ui.class").equals("notintree")){

				removeEdges.add(e.getId());				
			}
		}

		//		for(Edge e : edgeSet){
		//			prefix = "gridmov";
		//
		//			System.out.println(e.getAttribute("ui.class").toString());
		//			if(e.getAttribute("ui.class").equals("notintree")){
		//
		//				e.setAttribute("ui.class", "intree");				
		//			}
		//		}
		//		




		double pEdge = 0.0;
		for(String e: removeEdges){
			graph.removeEdge(e);
			//			continue;
			//			if(rnd.nextDouble() < pEdge){
			//				graph.getEdge(e).setAttribute("ui.class", "intree");
			//			}else{
			//			graph.removeEdge(e);
			//			}
		}
		for(Node n : graph.getNodeSet()){
			if(n.getDegree() == 1){
				n.getEachEdge().iterator().next().setAttribute("ui.class", "leafedge");
				n.setAttribute("ui.class", "leaf");
			}
		}



				FordFulkersonAlgorithm ford = new FordFulkersonAlgorithm();
				rnd = new Random(3);
				prefix = "treedecompmovdfs";
				
				
				for(int s =1; s< 20; s++){
					int x = rnd.nextInt(gWidth-1);
					int y = rnd.nextInt(gHeight-1);
					String node = "n"+x+"x"+y;
					String other = "n"+(x+1)+"x"+(y);
					
					ford.init(graph, node, other);
					if(ford.getMaximumFlow() <= 1 && !graph.getNode(node).hasEdgeFrom(other)){
						Edge e = graph.addEdge(node+"-"+other, node, other);
						e.setAttribute("ui.class", "intree");
					}
					
				}


		SpriteManager sman = new SpriteManager(graph);
		Sprite s = sman.addSprite("S1");
		s.attachToNode("n0x0");


		s.setPosition(-1, 0, 0);

		s.setAttribute("ui.label", "IN");

		s = sman.addSprite("S2");
		s.attachToNode("n15x7");

		s.setPosition(1, 0, 0);

		s.setAttribute("ui.label", "OUT");



		//		leftHandRule(graph, 0,1, "n0x0");



		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");

		graph.addAttribute("ui.screenshot", ".\\src\\talkABRI\\pics\\"+prefix+"_0.png");

		Layout sb = new SpringBox();
		sb.setForce(sb.getForce()*1.1);



		
		
		Node splitNode = graph.getNode(getNodeName(8, 5));

		splitNode.setAttribute("ui.style", "fill-color: purple;");
		splitNode.setAttribute("ui.class", "block");
		
		splitNode = graph.getNode(getNodeName(0, 3));
		
		splitNode.setAttribute("ui.style", "fill-color: purple;");
		splitNode.setAttribute("ui.class", "block");

		splitNode = graph.getNode(getNodeName(13, 9));

		splitNode.setAttribute("ui.style", "fill-color: purple;");
		splitNode.setAttribute("ui.class", "block");
		
		view.enableAutoLayout( sb);


		graph.addAttribute("ui.screenshot", ".\\src\\talkABRI\\pics\\"+prefix+"_split1.png");
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
				leftHandRule(graph, 0,1, "n15x7");

//		Node splitNode2 = graph.getNode(getNodeName(7, 13));
//
//		splitNode2.setAttribute("ui.style", "fill-color: purple;");
//
//		graph.addAttribute("ui.screenshot", ".\\src\\talkABRI\\pics\\"+prefix+"_split2.png");
//
//		
//		try {
//			Thread.sleep(250);
//		} catch (InterruptedException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
		int a=1;
		
		graph.removeNode(splitNode);
//		graph.removeNode(splitNode2);
//		graph.addAttribute("ui.screenshot", ".\\src\\talkABRI\\pics\\"+prefix+"_split2.png");


		//		Thread ssThread = new  Thread(){
		//		
		//			public void run(){
		//				
		//				
		//			}
		//		
		//		
		//		};
		//		
		//		
		//		ssThread.start();
		//		
		//		try {
		//			ssThread.join();
		//		} catch (InterruptedException e1) {
		//			// TODO Auto-generated catch block
		//			e1.printStackTrace();
		//		}

		for(int i=1; i<10;i++){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			graph.addAttribute("ui.screenshot", ".\\src\\talkABRI\\pics\\"+prefix+"_"+i+".png");	
			sb.shake();


		}
		for(int i=10; i<30;i++){

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			graph.addAttribute("ui.screenshot", ".\\src\\talkABRI\\pics\\"+prefix+"_"+i+".png");	
			sb.shake();
			view.enableAutoLayout(sb);
		}



		view.disableAutoLayout();


		//
		//		 OutputPolicy outputPolicy = OutputPolicy.BY_LAYOUT_STEP;
		//		 String prefix = ".\\src\\talkABRI\\treemov_";
		//		 OutputType type = OutputType.JPG;
		//		 Resolution resolution = Resolutions.HD720;
		//		 
		//		 FileSinkImages fsi = new FileSinkImages( type, resolution );
		//		 
		//		 // Create the source
		//		 
		//		 FileSourceDGS dgs = new FileSourceDGS();
		//		  
		//		 // Optional configuration
		//		 
		////		 fsi.setStyleSheet(
		////		 	"graph { padding: 50px; fill-color: black; }" +
		////		 	"node { fill-color: #3d5689; }" +
		////		 	"edge { fill-color: white; }");
		//		 
		//		 fsi.setStyleSheet(
		//		 	"graph { padding: 10px; fill-color: white; }");
		//
		//		 
		//		 fsi.setStyleSheet(css);
		//		 
		//		 fsi.setOutputPolicy( outputPolicy );
		//		 fsi.setLayoutPolicy( LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE );
		////		 fsi.setQuality(Quality.HIGH);
		////		 fsi.addLogo( "path/to/logo", x, y );
		//		  
		//
		//		 
		//		 // Images production
		//		 
		////		 dgs.addSink( fsi );
		//		  graph.addSink(fsi);
		//		  
		////		  sb.addSink(fsi);
		//		  
		//		 fsi.begin(prefix);
		////		 dgs.begin( ".\\src\\talkABRI\\dgs.dgs" );
		//		 
		////			view.enableAutoLayout( sb);
		//			
		//		 	
		//
		//			for(int i=0;i<50;i++){
		//			sb.compute();
		//			}
		//			
		//		 
		////		 
		////		 fsi.outputNewImage();
		////			graph.stepBegins(System.currentTimeMillis());
		////
		////
		////			try {
		////				Thread.sleep(1000);
		////			} catch (InterruptedException e1) {
		////				// TODO Auto-generated catch block
		////				e1.printStackTrace();
		////			}
		////			graph.stepBegins(System.currentTimeMillis());
		////
		////			graph.stepBegins(System.currentTimeMillis());
		//
		////		 while( dgs.nextStep() );
		////		 dgs.end();
		//		 fsi.end();
		//		
		//		
		//		

	}


	public static void main(String...strings) throws IOException{
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		new MazeCreator().start();
	}

}
