import grph.Grph;
import toools.set.IntSet;

public class GrphTools {
	
	/**
	 * makes vertex numbering contiguous
	 * @param g
	 */
	public static void NormalizeVertexRange(Grph g){
		for(int v =0; v<= g.getVertices().getGreatest(); v++){
			if(!g.containsVertex(v)){
				g.addVertex(v); 
				IntSet mNeighbours = g.getNeighbours(g.getVertices().getGreatest());
				for(int n: mNeighbours.toIntArray()) {
					g.addUndirectedSimpleEdge(v, n);
				}
				g.removeVertex(g.getVertices().getGreatest());
			}
		}
		
		
	}

}
