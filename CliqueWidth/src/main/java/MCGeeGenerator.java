import grph.Grph;
import grph.in_memory.InMemoryGrph;

public class MCGeeGenerator {

	
	Grph run(){
		Grph g = new InMemoryGrph();
		g.ensureNVertices(24);
		
		for(int i=0,  j=0; i<24; i++, j++){
			g.addUndirectedSimpleEdge(i, (i+1)%24);
			
			
			j = j%3;
			int[] offsets = {7,12,17}; 
			
				int offset = offsets[j];
				int n = (i+offset) % 24;
				if(!g.areVerticesAdjacent(i, n))
					g.addUndirectedSimpleEdge(i, n);
			
		}
		return g;
	}
}
