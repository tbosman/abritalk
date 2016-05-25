package talkABRI;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

public class GridGraph {
	private int id=0; 
	
	Graph returnGrid(int width, int height){
		Graph graph = new SingleGraph("");
		
		for(int i=0; i<height;  i++){
			for(int j=0; j<width; j++){
				Node n = graph.addNode("n"+j+"x"+i);
				n.setAttribute("xyz", j, i, 0);

			}
		}
		for(int i=0; i<height;  i++){
			for(int j=0; j<width; j++){
				if(i+1 < height){
					graph.addEdge(this.id+++"", "n"+j+"x"+i, "n"+(j)+"x"+(i+1));
				}
				if(j+1 < width){
					graph.addEdge(this.id+++"", "n"+j+"x"+i, "n"+(j+1)+"x"+(i));
				}
								
			}
		}
		
		return graph;
	}
	
	public String getNodeName(int x, int y){
		return "n"+x+"x"+y; 
	}
	
	public static void main(String... args){
		Viewer viewer = new GridGraph().returnGrid(6, 5).display();
		
		viewer.disableAutoLayout();
	}
}
