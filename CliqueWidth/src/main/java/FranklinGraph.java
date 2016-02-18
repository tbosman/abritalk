import grph.Grph;
import grph.in_memory.InMemoryGrph;


public class FranklinGraph {

	public Grph run() {
		Grph g = new InMemoryGrph();
		g.ensureNVertices(12);
		for(int i=0; i<12;i++) {
			g.addUndirectedSimpleEdge(i, (i+1)%12);
		}
		for(int i=0; i<6;i++) {
			if(i%2 == 1) {
				g.addUndirectedSimpleEdge(i, (i+5)%12);
				
			}else {
				g.addUndirectedSimpleEdge(i, (i+7)%12);
			}
		}
		return g;
	}
}
