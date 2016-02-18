import grph.Grph;
import grph.in_memory.InMemoryGrph;


public class FlowerSnarkGenerator {

	public FlowerSnarkGenerator() {
		// TODO Auto-generated constructor stub
	}
	
	public Grph run(int n) {
		
		Grph g = new InMemoryGrph();
		
		int[] B = new int[n];
		int[] C = new int[n];
		int[] D = new int[n];
		for(int i=0; i<n;i++) {
			int v = g.addVertex();
			int w = g.addVertex();
			g.addUndirectedSimpleEdge(v, w);
			B[i] = w;
			w = g.addVertex();
			g.addUndirectedSimpleEdge(v, w);
			C[i] = w;
			w = g.addVertex();
			g.addUndirectedSimpleEdge(v, w);
			D[i] = w;			
		}
		
		for(int i=0; i<n;i++) {
			g.addUndirectedSimpleEdge(B[i], B[(i+1)%n]);			
		}
		for(int i=0; i<n-1;i++) {
			g.addUndirectedSimpleEdge(C[i], C[i+1%n]);			
			g.addUndirectedSimpleEdge(D[i], D[i+1%n]);
		}
		g.addUndirectedSimpleEdge(C[n-1], D[0]);
		g.addUndirectedSimpleEdge(D[n-1], C[0]);
		
		
		return g;
		
	}

}
