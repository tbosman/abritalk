import java.util.Random;

import grph.Grph;
import grph.in_memory.InMemoryGrph;

public class RandomGraphGenerator {
	double p;
	int n;
	Random rand;
	
	RandomGraphGenerator(int n, double p, int seed ){
		this.p = p;
		this.n = n;
		this.rand =  new Random(seed);
	}
	
	
	Grph compute(){
		Grph g = new InMemoryGrph(); 
		
		g.ensureNVertices(n);
		for(int i =0; i< n-1; i++){
			for(int j =i+1; j<n; j++){
				if(rand.nextDouble() <= p){
					g.addUndirectedSimpleEdge(i, j);
				}
			}
		}
		return g;
		
	}

}
