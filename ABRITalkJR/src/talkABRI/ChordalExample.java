package talkABRI;

import org.graphstream.algorithm.generator.GridGenerator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class ChordalExample {
	
	
	public void start(){
		Graph graph = new SingleGraph("");
		
		RandomEuclideanGenerator reg = new RandomEuclideanGenerator();
		
		GridGenerator grid = new GridGenerator();
		
		
		
		grid.addSink(graph);
		
		grid.begin();
		for(int i=0; i<5; i++){
		grid.nextEvents(); }
		grid.end();
		
		
		graph.display();
		
		
		
		
	}
	public static void main(String... args){
		new ChordalExample().start();
	}

}
