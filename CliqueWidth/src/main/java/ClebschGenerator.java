import grph.Grph;
import grph.in_memory.InMemoryGrph;

public class ClebschGenerator {

	
	public Grph clebschGenerator(){
		Grph g = new InMemoryGrph(); 
		g.ensureNVertices(16);
		for(int v=0; v<16; v++){
			for(int idx=0; idx<4; idx++){
				int w = v ^( 1 << idx); // is v with idx'th least significant bit flipped
				if(!g.areVerticesAdjacent(v, w)){
					g.addUndirectedSimpleEdge(v, w);
				}
			}
			
			int w = ~v & 0xf;
			if(!g.areVerticesAdjacent(v, w)){
				g.addUndirectedSimpleEdge(v, w);
			}
		}
		return g;
	}
	
	public static void main(String... args){
		Grph g = new ClebschGenerator().clebschGenerator();
		System.out.println(g);
		g.display();
	}
}
