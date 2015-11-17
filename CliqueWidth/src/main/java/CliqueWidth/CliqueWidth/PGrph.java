package CliqueWidth.CliqueWidth;

import java.util.ArrayList;

import CliqueWidth.CliqueWidth.tools.UFPartition;
import grph.in_memory.InMemoryGrph;

/**
 * Graph datastructure which additional functionality:
 * - Supports vertex contraction 
 * - Stores a partition of the vertex set
 * - Supports retrieval of new vertex after contraction based on old vertex id 
 */
public class PGrph extends InMemoryGrph{
	
	private UFPartition<Integer> vertexComponents = new UFPartition<Integer>();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1774227125426383208L;
	
	
	@Override
	public void addVertex(int v){
		super.addVertex(v);
		vertexComponents.makeSet(v);
	}
	
	@Override
	public int addVertex(){
		int v = super.addVertex();
		vertexComponents.makeSet(v);
		return v;
	}
	
	@Override
	public void contractVertices(int a, int b) {
		super.contractVertices(b, a);
	}
	
	

}
