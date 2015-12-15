import java.util.ArrayList;

import toools.set.DefaultIntSet;
import toools.set.IntSet;

/**
 * Double adjacency list (one entry for every vertex)
 * [Add only] 
 * @author tbn530
 *
 */
public class DAdjacencyList {
	
	IntSet[] aList;
	
	
	public DAdjacencyList(int capacity){
		aList = new IntSet[capacity];
	}
		 
	
	public void addVertex(int id){
		assert aList[id] != null;
		if(aList[id] != null){
			new Error("Vertex already exist").printStackTrace();;
		}
		aList[id] = new DefaultIntSet();
		
	}
	
	public void addEdge(int u, int v){
		if(aList[u] == null ){
			addVertex(u);
		}		
		if(aList[v] == null){
			addVertex(v);
		}
		
		aList[u].add(v);
		aList[v].add(u);		
	}
	
	
	
	public boolean areAdjacent(int u, int v){
		return (aList[u].contains(v));
	}
	
	public IntSet getNeighbours(int v){
		return aList[v];
	}
	
	
	public void clear(){
		aList = new IntSet[aList.length];
	}
	
	
	
	

}
