import grph.Grph;
import grph.in_memory.InMemoryGrph;

public class Paley13Generator {
	
	Grph paley13Generator(){
		Grph g = new InMemoryGrph(); 
		g.ensureNVertices(13);
		
		for(int i=0; i<g.getNumberOfVertices()-1;i++){
			for(int j=i+1; j<g.getNumberOfVertices();j++){
				int aDiff = Math.abs(i-j);
						if(aDiff == 1 ||aDiff == 3 || aDiff == 4){
							g.addUndirectedSimpleEdge(i,j);
						}
				
			}
		}
		return g; 
	}

}
