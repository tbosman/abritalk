import java.util.ArrayList;
import java.util.Stack;

import CliqueWidth.CliqueWidth.tools.UFPartition;
import de.ruedigermoeller.serialization.dson.generators.DartDsonGen;
import grph.Grph;
import toools.set.DefaultIntSet;
import toools.set.IntSet;
import toools.set.IntSets;

public class VCLowerBound {
	
	Grph g; 
	UFPartition<Integer> components;
	
//	IntSet cupMinVertices; // \subset V 
	ArrayList<IntSet> pVertexCovers = new ArrayList<IntSet>(); // list \{S  \subset V \} 
	
	
	DAdjacencyList minPairs; 
	IntSet minList = new DefaultIntSet();
	
	int minContractionCost;
	int bestBound; 
	
	public VCLowerBound(Grph g, UFPartition components) {
		super();
		this.g = g;
		this.components = components;
		this.minPairs =new DAdjacencyList(g.getVertices().getGreatest()+1);
		
		
		getAllMinimalContractions();
	} 
	
	
	public void getAllMinimalContractions(){
		//TODO
		int cu = 0, cv = 0, minW = Integer.MAX_VALUE; 

		int[] vertices = g.getVertices().toIntArray();
		for(int i = 0; i<vertices.length-1;i++) {
			for(int j=i+1; j<vertices.length;j++) {
				
				int u = vertices[i];
				int v = vertices[j];
				IntSet reqV = getRequiredVertices(u,v);				
				IntSet reqC = getRequiredComponents(reqV);
				int w = getRequiredWidth(u, v, reqV, reqC); 
				if(w < minW){
					minPairs.clear();
					minList.clear();
					minW = w;
				}
				
				if(w <= minW){
					minPairs.addEdge(u, v);
					minList.add(u);
					minList.add(v);
				}
			}
			
		}
		
		minContractionCost = minW;
		bestBound = minW; 
		
		
	}
	
	
	public void run(){ 
		
		pVertexCovers.add(new DefaultIntSet());
		

		
		
		
		IntSet allVertices = new DefaultIntSet();
		
		IntSet newCover;
		
		for(int v: minList.toIntArray()){
			ArrayList<IntSet> newPVertexCovers = new ArrayList<IntSet>(); 
			allVertices.add(v);
			for(IntSet cover : pVertexCovers){
				if(!mustNotInclude(v, cover, allVertices)){
					newCover = cover.clone(); 
					newCover.add(v);
					int newCoverCost = getMinCostForCover(newCover) ;
					
//					System.out.println("cover: "+newCover+"\t cost: "+newCoverCost);
					if(newCoverCost >= minContractionCost || true){
						newPVertexCovers.add(newCover);

					}
				}
				
				
				if(!mustInclude(v, cover, allVertices)){
					newCover = cover.clone();					
					int newCoverCost = getMinCostForCover(newCover) ;
//					System.out.println("cover: "+newCover+"\t cost: "+newCoverCost);
					if(newCoverCost >= minContractionCost || true){
						newPVertexCovers.add(newCover);
					}
				}
			}
			
			
			pVertexCovers = newPVertexCovers;
		}
		
		
		int cost;
		for(IntSet cover: pVertexCovers){
			cost = getMinCostForCover(cover);
			if(cost > bestBound){
				bestBound = cost; 
			}
			
			System.out.println(cover+"\t cost: "+cost+"\t bb: "+bestBound);
		}
	}
	
	
	
	
	/**
	 * 
	 * @param cover
	 * @return smallest cost of contraction excluding minimum contractions in original graph, given cover is removed 
	 */
	public int getMinCostForCover(IntSet cover){
		int cu = 0, cv = 0, minW = Integer.MAX_VALUE;
		
		IntSet nonCoverVertices = IntSets.difference(g.getVertices(), cover);
		
		int[] vertices = nonCoverVertices.toIntArray();
		for(int i = 0; i<vertices.length-1;i++) {
			for(int j=i+1; j<vertices.length;j++) {
				int u = vertices[i];
				int v = vertices[j];
				if(minList.contains(u) && minList.contains(v)){
					if(minPairs.areAdjacent(u, v)) {
					continue;
					}
				}
				IntSet reqV = IntSets.difference(getRequiredVertices(u,v), cover);				
//				reqV.removeAll(cover);				
				IntSet reqC = getRequiredComponents(reqV);
				IntSet compV = getComponentVertices(reqC);
				compV = IntSets.difference(compV, cover);
				
				
				
				int w = compV.size() - (reqC.size() == 1 || (reqV.size() == 2 && !g.areVerticesAdjacent(u,v))?1:0); 
				if(w < minW){
					minW = w; 
				}
				

			}
			
		}
		
		return minW; 
	}
	
	
	/**
	 * 
	 * @param v newly added vertex this it
	 * @param C partial cover 
	 * @param U all vertices up until this it
	 * @return Must include if there exist an edge to a vertex w not in C but in U, otherwise never becomes a cover
	 */
	public boolean mustInclude(int v, IntSet C, IntSet U){ 
		IntSet vNeighbours = minPairs.getNeighbours(v);
		vNeighbours.retainAll(U);
		if(!C.contains(vNeighbours)){
			return true;
		}
		return false; 
	}
	
	/**
	 * 
	 * @param v newly added vertex this it
	 * @param C partial cover 
	 * @param U all vertices up until this it
	 * @return Mustnt include if every neighbour of v already in cover, cover wouldnt be minimal 
	 */
	public boolean mustNotInclude(int v, IntSet C, IntSet U){
		IntSet vNeighbours = minPairs.getNeighbours(v);		
		
		
		if(C.contains(vNeighbours)){
			return true;
		}		 
		return false;
	}
	
	
	
	
	//TODO extract these 3 methods (reqV, reqC, reqW) to one class for all algos
	private IntSet getRequiredVertices(int u, int v) {
		IntSet reqVertices = new DefaultIntSet(); 
		reqVertices.addAll(u, v);
		IntSet nU = g.getNeighbours(u);
		IntSet nV = g.getNeighbours(v);
		reqVertices.addAll(IntSets.difference(IntSets.union(g.getNeighbours(u), g.getNeighbours(v)), IntSets.intersection(g.getNeighbours(u), g.getNeighbours(v)) ) );
						
		return reqVertices;		
	}
	
	private IntSet getRequiredComponents(IntSet reqVertices) {
		IntSet reqComponents = new DefaultIntSet();
		for(int i : reqVertices.toIntArray()) {
			reqComponents.add(components.find(i));
		}
		return reqComponents;
	}
	
	private IntSet getComponentVertices(IntSet reqComponents){
		IntSet componentVertices = new DefaultIntSet();
		for(int c : reqComponents.toIntArray()){
			for(int v : components.getBlock(c)){
				componentVertices.add(v);
			}
		}
		return componentVertices;
	}
	
	private int getRequiredWidth(int u, int v, IntSet reqVertices, IntSet reqComponents) {
		int width =0;
		for(int component : reqComponents.toIntArray()) {
			width += components.size(component);
		}
		
		width -= (reqComponents.size() == 1 || !(reqVertices.size()==2  && g.areVerticesAdjacent(u, v))   ?1:0);
		
		return width; 
	}
	
	
	
}
