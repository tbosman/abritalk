import grph.Grph;
import grph.in_memory.InMemoryGrph;

public class FruchtGenerator {

	Grph fruchtGenerator(){
		Grph g = new InMemoryGrph();
		g.ensureNVertices(12);
		
		for(int v=0; v<12; v++){
			g.addUndirectedSimpleEdge(v, (v+1)%12);
			
		}
		
		int[] LCF = new int[] {(-5),(-2),(-4),2,5,(-2),2,5,(-2),(-5),4,2};
		for(int v=0; v<12;v++){
			int w = (v+12 + LCF[v])%12;
			if(!g.areVerticesAdjacent(v, w)){
				g.addUndirectedSimpleEdge(v, w);
			}
		}
		
		return g;
		
	}
	
	public static void main(String... args){
		Grph g = new FruchtGenerator().fruchtGenerator();
		System.out.println(g);
		g.display();
	}
}
