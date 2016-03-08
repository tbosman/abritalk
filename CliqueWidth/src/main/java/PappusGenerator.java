import grph.Grph;
import grph.in_memory.InMemoryGrph;

public class PappusGenerator {
	
	
	public Grph pappusGenerator(){ 
		Grph g = new InMemoryGrph(); 
		g.ensureNVertices(18);
		
		
		for(int v=0; v<18; v++){
			g.addUndirectedSimpleEdge(v, (v+1)%18);
			
		}
		
		int[] LCF = new int[] {5,7,-7,7,-7,-5,5,7,-7,7,-7,-5,5,7,-7,7,-7,-5};
		for(int v=0; v<18;v++){
			int w = (v+18 + LCF[v])%18;
			if(!g.areVerticesAdjacent(v, w)){
				g.addUndirectedSimpleEdge(v, w);
			}
		}
		
		return g;
	}
	
	
	public static void main(String... args){
		Grph g = new PappusGenerator().pappusGenerator();
		System.out.println(g);
		g.display();
	}

}
