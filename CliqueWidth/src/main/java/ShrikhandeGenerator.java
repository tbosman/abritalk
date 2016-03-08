import grph.Grph;
import grph.in_memory.InMemoryGrph;

public class ShrikhandeGenerator {
	
	public Grph shrikhandeGenerator(){
		Grph g = new InMemoryGrph();
		
		int[][] Z4Sq = new int[4][4];
		int idx = 0; 
		for(int i =0; i< 4; i++){
			for(int j =0; j<4 ;j++){
				Z4Sq[i][j] = idx; 
				g.addVertex(idx);
				idx++; 				
			}
		}
		
		for(int i =0; i< 4; i++){
			for(int j =0; j<4 ;j++){
				int l, k , u,v;				
				
				u = Z4Sq[i][j];
									
				k = (i + 1)% 4;
				l = j;
				
				v = Z4Sq[k][l];				
				ensureEdge(u,v,g);
				
				k = (i + 4 - 1)% 4;
				l = j;
				
				v = Z4Sq[k][l];				
				ensureEdge(u,v,g);
				
				k = i;
				l = (j + 4 + 1) % 4;
				
				v = Z4Sq[k][l];				
				ensureEdge(u,v,g);
				
				k = i;
				l = (j + 4 - 1) % 4;
				
				v = Z4Sq[k][l];				
				ensureEdge(u,v,g);
				
				
				k = (i + 4 + 1) % 4;

				l = (j + 4 + 1) % 4;
				
				v = Z4Sq[k][l];				
				ensureEdge(u,v,g);
				
				k = (i + 4 - 1) % 4;

				l = (j + 4 - 1) % 4;
				
				v = Z4Sq[k][l];				
				ensureEdge(u,v,g);
				

				
				
				
			}
		}
		
		return g;
		
	}
	
	private void ensureEdge(int u, int v, Grph g){
		if(!g.areVerticesAdjacent(u, v)){
			g.addUndirectedSimpleEdge(u, v);
		}
	}
	
	
	
	
	public static void main(String... args){
		Grph g = new ShrikhandeGenerator().shrikhandeGenerator();
		System.out.println(g);
		g.display();
	}

}
