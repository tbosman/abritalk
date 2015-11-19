import grph.Grph;
import grph.in_memory.InMemoryGrph;

public class ChvatalGenerator {
	
	public Grph chvatalGenerator(){
		Grph g = new InMemoryGrph();
		
		

		
		g.ensureNVertices(12);
		//Inner circle with spokes 
		for(int i=0; i<8; i++){
			g.addUndirectedSimpleEdge(i, (i+1)%8);			
		}
		for(int i=0; i<4; i++){
			g.addUndirectedSimpleEdge(i, (i+4));
		}
		
		//Outer square
		int base = 8;
		for(int i=0;i<4; i++){
			g.addUndirectedSimpleEdge(base+i, base+((i+1)%4));
		}
		
		//Edges from square to circle
		for(int i=0; i<4; i++){
			g.addUndirectedSimpleEdge(base+i, 2*i);
			g.addUndirectedSimpleEdge(base+i, (2*i+5)%8);
		}
		
		return g; 
		
	}
}
