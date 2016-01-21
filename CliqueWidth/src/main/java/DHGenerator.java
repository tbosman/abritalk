import java.util.Random;



import toools.set.IntSet;
import grph.Grph;
import grph.in_memory.InMemoryGrph;


public class DHGenerator {
	
	int n; 
	double pPendant; 
	double pTTwin; 
	double pFTwin;
	
	long seed = 0; 
	Random rnd = new Random(seed);
	
	public DHGenerator(int n, double pPendant, double pTTwin) {
		this.n = n;
		this.pPendant = pPendant;
		this.pTTwin = pTTwin;
		this.pFTwin = 1 - pPendant - pTTwin;
			
	}

	public void addPendantTo(int v, Grph g) {
		int w = g.addVertex();
		g.addSimpleEdge(v, w, false);
	}
	
	public int makeFTwin(int v, Grph g) {
		int w = g.addVertex();
		IntSet nn = g.getNeighbours(v);
		for(int n : nn.toIntArray()) {
			g.addUndirectedSimpleEdge(w, n);
		}
		return w;
	}
	
	public int makeTTwin(int v, Grph g) {
		int w = makeFTwin(v, g);
		g.addUndirectedSimpleEdge(v,w);
		return w;
	}
	
	
	public Grph run() {
		Grph g = new InMemoryGrph();
		g.addVertex();
		for(int i=1; i<n; i++) {
			int v = g.getVertices().pickRandomElement(rnd);
			double p = rnd.nextDouble();
			if( p < pPendant) {
				addPendantTo(v,g);				
			}else if( p < pPendant + pTTwin) {
				makeTTwin(v,g);
			}else {
				makeFTwin(v,g);
			}
			
		}
		return g;
	}
}
